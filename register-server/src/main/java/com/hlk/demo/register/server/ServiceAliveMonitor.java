package com.hlk.demo.register.server;

import java.util.Map;

/**
 * 微服务存活状态监控组件
 *
 * @author huanglk
 */
public class ServiceAliveMonitor {
    /**
     * 检查服务实例是否存活的间隔
     */
    private static final Long CHECK_ALIVE_INTERVAL = 60 * 1000L;

    /**
     * 负责监控微服务存活状态的后台线程
     */
    private Daemon daemon;

    public ServiceAliveMonitor() {

        this.daemon = new Daemon();
        // 只要设置了这个标志位，就代表这个线程是一个daemon线程，后台线程
        // 非daemon线程，我们一般叫做工作线程
        // 如果工作线程（main线程）都结束了，daemon线程是不会阻止jvm进程退出的
        // daemon线程会跟着jvm进程一起退出
        daemon.setDaemon(true);
        daemon.setName("ServiceAliveMonitor");
    }

    /**
     * 启动后台线程
     */
    public void start() {
        daemon.start();
    }

    /**
     * 负责监控微服务存活状态的后台线程
     *
     * @author zhonghuashishan
     */
    private class Daemon extends Thread {
        private ServiceRegistry serviceRegistry = ServiceRegistry.getInstance();

        @Override
        public void run() {
            Map<String, Map<String, ServiceInstance>> registryMap = null;
            while (true) {
                try {
                    registryMap = serviceRegistry.getRegistry();
                    for (String serviceName : registryMap.keySet()) {
                        Map<String, ServiceInstance> serviceInstanceMap =
                                registryMap.get(serviceName);

                        for (ServiceInstance serviceInstance : serviceInstanceMap.values()) {
                            // 说明服务实例距离上一次发送心跳已经超过90秒了
                            // 认为这个服务就死了
                            // 从注册表中摘除这个服务实例
                            if (!serviceInstance.isAlive()) {
                                serviceRegistry.remove(serviceName, serviceInstance.getServiceInstanceId());
                                // 更新自我保护机制的阈值
                                synchronized(SelfProtectionPolicy.class) {
                                    SelfProtectionPolicy selfProtectionPolicy = SelfProtectionPolicy.getInstance();
                                    selfProtectionPolicy.setExpectedHeartbeatRate(
                                            selfProtectionPolicy.getExpectedHeartbeatRate() - 2);
                                    selfProtectionPolicy.setExpectedHeartbeatThreshold(
                                            (long)(selfProtectionPolicy.getExpectedHeartbeatRate() * 0.85));
                                }
                            }
                        }
                    }

                    Thread.sleep(CHECK_ALIVE_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
