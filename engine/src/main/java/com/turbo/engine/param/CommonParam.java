package com.turbo.engine.param;

public class CommonParam {
    private String tenant;
    private String caller;

    public CommonParam(String tenant, String caller) {
        this.tenant = tenant;
        this.caller = caller;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

}
