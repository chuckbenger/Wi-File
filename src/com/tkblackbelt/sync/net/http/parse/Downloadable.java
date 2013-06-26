package com.tkblackbelt.sync.net.http.parse;

public abstract class Downloadable implements Comparable<Downloadable> {

    protected String path;
    protected String name;

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Downloadable{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
