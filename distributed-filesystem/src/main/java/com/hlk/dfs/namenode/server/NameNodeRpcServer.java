package com.hlk.dfs.namenode.server;

/**
 * NameNode的rpc服务的接口
 *
 * @author huanglk
 */
public class NameNodeRpcServer {

    private FSNamesystem namesystem;

    public NameNodeRpcServer(FSNamesystem namesystem) {
        this.namesystem = namesystem;
    }

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @return 是否创建成功
     * @throws Exception
     */
    public Boolean mkdir(String path) throws Exception {
        return this.namesystem.mkdir(path);
    }

    /**
     * 启动这个rpc server
     */
    public void start() {
        System.out.println("开始监听指定的rpc server的端口号，来接收请求");
    }
}
