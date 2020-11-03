package com.hlk.demo.register.client;

import com.hlk.demo.register.client.CachedServiceRegistry.RecentlyChangedServiceInstance;

import java.util.LinkedList;

/**
 * 增量注册表
 * @author huanglk
 *
 */
public class DeltaRegistry {

	private LinkedList<RecentlyChangedServiceInstance> recentlyChangedQueue;
	private Long serviceInstanceTotalCount;
	
	public DeltaRegistry(LinkedList<RecentlyChangedServiceInstance> recentlyChangedQueue,
                         Long serviceInstanceTotalCount) {
		this.recentlyChangedQueue = recentlyChangedQueue;
		this.serviceInstanceTotalCount = serviceInstanceTotalCount;
	}
	
	public LinkedList<RecentlyChangedServiceInstance> getRecentlyChangedQueue() {
		return recentlyChangedQueue;
	}
	public void setRecentlyChangedQueue(LinkedList<RecentlyChangedServiceInstance> recentlyChangedQueue) {
		this.recentlyChangedQueue = recentlyChangedQueue;
	}
	public Long getServiceInstanceTotalCount() {
		return serviceInstanceTotalCount;
	}
	public void setServiceInstanceTotalCount(Long serviceInstanceTotalCount) {
		this.serviceInstanceTotalCount = serviceInstanceTotalCount;
	}
	
}
