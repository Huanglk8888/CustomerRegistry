package com.hlk.demo.register.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 客户端缓存注册表
 *
 * @author huanglk
 */
public class CachedServiceRegistry {
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
    private FetchDeltaRegistryWorker fetchDeltaRegistryWorker;
    /**
     * RegisterClient
     */
    private RegisterClient registerClient;

    /**
     * http通信组件
     */
    private HttpSender httpSender;

    public CachedServiceRegistry(RegisterClient registerClient,
                                 HttpSender httpSender) {
        this.fetchDeltaRegistryWorker = new FetchDeltaRegistryWorker();
        this.registerClient = registerClient;
        this.httpSender = httpSender;
    }


    /**
     * 初始化
     */
    public void initialize() {
        // 启动全量拉取注册表的线程
        FetchFullRegistryWorker fetchFullRegistryWorker =
                new FetchFullRegistryWorker();
        fetchFullRegistryWorker.start();
        // 启动增量拉取注册表的线程
        this.fetchDeltaRegistryWorker.start();
    }

    /**
     * 销毁这个组件
     */
    public void destroy() {
        this.fetchDeltaRegistryWorker.interrupt();
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
     * 合并增量注册表到本地缓存注册表里去
     *
     * @param deltaRegistry
     */
    private void mergeDeltaRegistry(LinkedList<RecentlyChangedServiceInstance> deltaRegistry) {
        for (RecentlyChangedServiceInstance recentlyChangedItem : deltaRegistry) {
            // 如果是注册操作的话
            if (ServiceInstanceOperation.REGISTER.equals(recentlyChangedItem.serviceInstanceOperation)) {
                Map<String, ServiceInstance> serviceInstanceMap =
                        registry.get(recentlyChangedItem.serviceInstance.getServiceName());
                if (serviceInstanceMap == null) {
                    serviceInstanceMap = new HashMap<String, ServiceInstance>();
                    registry.put(recentlyChangedItem.serviceInstance.getServiceName(), serviceInstanceMap);
                }

                ServiceInstance serviceInstance = serviceInstanceMap.get(
                        recentlyChangedItem.serviceInstance.getServiceInstanceId());
                if (serviceInstance == null) {
                    serviceInstanceMap.put(
                            recentlyChangedItem.serviceInstance.getServiceInstanceId(),
                            recentlyChangedItem.serviceInstance);
                }
            }
            // 如果是删除操作的话
            else if (ServiceInstanceOperation.REMOVE.equals(
                    recentlyChangedItem.serviceInstanceOperation)) {
                Map<String, ServiceInstance> serviceInstanceMap = registry.get(
                        recentlyChangedItem.serviceInstance.getServiceName());
                if (serviceInstanceMap != null) {
                    serviceInstanceMap.remove(recentlyChangedItem.serviceInstance.getServiceInstanceId());
                }
            }
        }
    }

    /**
     * 最近变更的实例信息
     *
     * @author zhonghuashishan
     */
    static class RecentlyChangedServiceInstance {

        /**
         * 服务实例
         */
        ServiceInstance serviceInstance;
        /**
         * 发生变更的时间戳
         */
        Long changedTimestamp;
        /**
         * 变更操作
         */
        String serviceInstanceOperation;

        public RecentlyChangedServiceInstance(
                ServiceInstance serviceInstance,
                Long changedTimestamp,
                String serviceInstanceOperation) {
            this.serviceInstance = serviceInstance;
            this.changedTimestamp = changedTimestamp;
            this.serviceInstanceOperation = serviceInstanceOperation;
        }

        @Override
        public String toString() {
            return "RecentlyChangedServiceInstance [serviceInstance=" + serviceInstance + ", changedTimestamp="
                    + changedTimestamp + ", serviceInstanceOperation=" + serviceInstanceOperation + "]";
        }

    }

    /**
     * 全量拉取注册表的后台线程
     *
     * @author zhonghuashishan
     */
    private class FetchFullRegistryWorker extends Thread {
        @Override
        public void run() {
            // 拉取全量注册表
            registry = httpSender.fetchFullRegistry();
        }
    }

    /**
     * 增量拉取注册表的后台线程
     *
     * @author zhonghuashishan
     */
    private class FetchDeltaRegistryWorker extends Thread {
        @Override
        public void run() {
            while (registerClient.isRunning()) {
                try {
                    Thread.sleep(SERVICE_REGISTRY_FETCH_INTERVAL);

                    // 拉取回来的是最近3分钟变化的服务实例
                    DeltaRegistry deltaRegistry = httpSender.fetchDeltaRegistry();

                    // 一类是注册，一类是删除
                    // 如果是注册的话，就判断一下这个服务实例是否在这个本地缓存的注册表中
                    // 如果不在的话，就放到本地缓存注册表里去
                    // 如果是删除的话，就看一下，如果服务实例存在，就给删除了

                    // 我们这里其实是要大量的修改本地缓存的注册表，所以此处需要加锁
                    synchronized (registry) {
                        mergeDeltaRegistry(deltaRegistry.getRecentlyChangedQueue());
                    }
                    // 再检查一下，跟服务端的注册表的服务实例的数量相比，是否是一致的
                    // 封装一下增量注册表的对象，也就是拉取增量注册表的时候，一方面是返回那个数据
                    // 另外一方面，是要那个对应的register-server端的服务实例的数量
                    Long serverSideTotalCount = deltaRegistry.getServiceInstanceTotalCount();
                    Long clientSideTotalCount = 0L;
                    for (Map<String, ServiceInstance> serviceInstanceMap : registry.values()) {
                        clientSideTotalCount += serviceInstanceMap.size();
                    }

                    if (!serverSideTotalCount.equals(clientSideTotalCount)) {
                        // 重新拉取全量注册表进行纠正
                        registry = httpSender.fetchFullRegistry();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 服务实例操作
     *
     * @author zhonghuashishan
     */
    class ServiceInstanceOperation {

        /**
         * 注册
         */
        public static final String REGISTER = "register";
        /**
         * 删除
         */
        public static final String REMOVE = "REMOVE";

    }

}
