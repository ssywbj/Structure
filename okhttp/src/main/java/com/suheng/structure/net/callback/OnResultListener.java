package com.suheng.structure.net.callback;

public interface OnResultListener<RightResult, ErrorResult> {
    void onRightResult(RightResult data);

    void onErrorResult(int code, String msg, ErrorResult data);
}
