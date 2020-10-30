package com.hlk.demo.register.client;

/**
 * 客户端缓存注册表
 * @author huanglk
 */
public class ClientCachedServiceRegistry {
    private Daemon daemon;


    public ClientCachedServiceRegistry() {
        this.daemon = new Daemon();
    }

    private class Daemon extends Thread{

    }
}
