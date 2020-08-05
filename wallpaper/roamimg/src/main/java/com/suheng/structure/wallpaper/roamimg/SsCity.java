package com.suheng.structure.wallpaper.roamimg;

import java.io.Serializable;

public class SsCity implements Serializable {
    private String id;
    private String name = "";
    private String gmt;
    private int isDefaultAndUnDelete = UN_DEFAULT_AND_UNDELETE;
    private int offset;
    private String Pinyin;

    public static final int UN_DEFAULT_AND_UNDELETE = 0;

    public SsCity() {
    }

    public SsCity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        if (null == name) {
            name = "";
        }
        return name;
    }

    public void setName(String name) {
        if (null == name) {
            name = "";
        }
        this.name = name;
    }

    public String getPinyin() {
        return Pinyin;
    }

    public void setPinyin(String pinyin) {
        Pinyin = pinyin;
    }

    public String getGmt() {
        return gmt;
    }

    public void setGmt(String gmt) {
        this.gmt = gmt;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getIsDefaultAndUnDelete() {
        return isDefaultAndUnDelete;
    }

    public void setIsDefaultAndUnDelete(int isDefaultAndUnDelete) {
        this.isDefaultAndUnDelete = isDefaultAndUnDelete;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SsCity && id != null && id.equals(((SsCity) o).getId())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 1;
    }

}
