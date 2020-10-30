package com.hlk.demo.register.client;

/**
 * 注册请求
 * @author huanglk
 *
 */
public class RegisterRequest {
    private String serviceName;
    private String ip;
    private String hostname;
    private int port;
    private String serviceInstanceId;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("RegisterRequest{");
        sb.append("serviceName='").append(serviceName).append('\'');
        sb.append(", ip='").append(ip).append('\'');
        sb.append(", hostname='").append(hostname).append('\'');
        sb.append(", port=").append(port);
        sb.append(", serviceInstanceId='").append(serviceInstanceId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
