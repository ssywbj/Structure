package com.suheng.structure.module1.request;

import com.suheng.structure.net.request.normal.StringTask;

public class StringTaskImpl extends StringTask<String> {

    @Override
    protected String parseResult(String result) {
        return result;
    }

    @Override
    protected String getURL() {
        return null;
    }

}
