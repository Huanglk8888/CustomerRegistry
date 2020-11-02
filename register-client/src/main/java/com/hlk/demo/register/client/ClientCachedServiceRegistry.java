package com.hlk.demo.register.client;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端缓存注册表
 *
 * @author huanglk
 */
public class ClientCachedServiceRegistry {
    /**
     * 服务注册表拉取间隔时间
     */
    private static final Long SERVICE_REGISTRY_FETCH_INTERVAL = 30 * 1000L;

    /**
     * 客户端缓存的服务注册表
     */
    private Map<String, Map<String, ServiceInstance>> registry =
            new HashMap<String, Map<String, ServiceInstance>>();

    /**
     * 负责定时拉取注册表到客户端进行缓存的后台线程
     */
    private Daemon daemon;
    /**
     * RegisterClient
     */
    private RegisterClient registerClient;

    /**
     * http通信组件
     */
    private HttpSender httpSender;

    public ClientCachedServiceRegistry(RegisterClient registerClient,
                                       HttpSender httpSender) {
        this.daemon = new Daemon();
        this.registerClient = registerClient;
        this.httpSender = httpSender;
    }


    /**
     * 初始化
     */
    public void initialize() {
        this.daemon.start();
    }

    /**
     * 销毁这个组件
     */
    public void destroy() {
        this.daemon.interrupt();
    }

    /**
     * 获取服务注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }

    /**
     * 负责定时拉取注册表到本地来进行缓存
     *
     * @author zhonghuashishan
     */
    private class Daemon extends Thread {
        @Override
        public void run() {
            while (registerClient.isRunning()) {

                try {
                    registry = httpSender.fetchServiceRegistry();
                    Thread.sleep(SERVICE_REGISTRY_FETCH_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
