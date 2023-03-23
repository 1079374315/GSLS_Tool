package com.gsls.gsls_tool.bean;

public class JsonRootBean {
    private int status;
    private String message;
    private String request_id;
    private Object result;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "JsonRootBean{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", request_id='" + request_id + '\'' +
                ", result=" + result +
                '}';
    }
}
