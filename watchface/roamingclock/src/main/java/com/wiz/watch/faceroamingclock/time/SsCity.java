package com.wiz.watch.faceroamingclock.time;

import java.io.Serializable;

public class SsCity implements Serializable {
    private String id;
    private String name = "";
    private String gmt;

    public SsCity() {
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

    public String getGmt() {
        return gmt;
    }

    public void setGmt(String gmt) {
        this.gmt = gmt;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof SsCity) && (id != null) && (id.equals(((SsCity) o).getId()));
    }

    @Override
    public int hashCode() {
        return 1;
    }

}
