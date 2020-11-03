package com.hlk.demo.register.client;

import java.util.HashMap;
import java.util.Map;

/**
 * 完整的服务实例的信息
 * @author huanglk
 *
 */
public class Applications {

	private Map<String, Map<String, ServiceInstance>> registry = 
			new HashMap<String, Map<String, ServiceInstance>>();  

	public Applications() {
		
	}
	
	public Applications(Map<String, Map<String, ServiceInstance>> registry) {
		this.registry = registry;
	}

	public Map<String, Map<String, ServiceInstance>> getRegistry() {
		return registry;
	}
	public void setRegistry(Map<String, Map<String, ServiceInstance>> registry) {
		this.registry = registry;
	}
	
}
