package com.suheng.structure.enums;

import java.util.Locale;

public enum OzWitch {
    //1.如果打算定义自己的方法，那么必须有enum实例序列的最后添加一个分号；2.先定义enum实例，再定义任何方法或属性，否则在编译时会报错。
    WEST("West00"), NORTH("North11"), EAST("East22"), SOUTH("South33");

    private final String mDescription;

    OzWitch(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    @Override
    public String toString() {
        String id = name();
        String lower = id.substring(1).toLowerCase(Locale.getDefault());
        return id.charAt(0) + lower;
    }

}
