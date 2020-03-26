package com.suheng.structure.common.eventbus;

public class BasicEvent {
    private static final int CODE_SUCCESS = 0;

    /***业务类型，如：请求或回应，具体在对应的event中定义*/
    private int type;
    /***业务状态码，非0表示失败*/
    private int code = CODE_SUCCESS;
    /***业务状态描述*/
    private String msg;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return code == CODE_SUCCESS;
    }

}
