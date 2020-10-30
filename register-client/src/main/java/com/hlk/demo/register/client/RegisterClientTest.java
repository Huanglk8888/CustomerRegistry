package com.hlk.demo.register.client;

/**
 * register-client组件的测试类
 * @author huanglk
 *
 */
public class RegisterClientTest {

	public static void main(String[] args) throws Exception {
		RegisterClient registerClient = new RegisterClient();
		registerClient.start();
		
		Thread.sleep(5000);  
		
		registerClient.shutdown();
	}
	
}
