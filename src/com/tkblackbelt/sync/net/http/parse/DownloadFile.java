package com.tkblackbelt.sync.net.http.parse;

import org.jsoup.select.Elements;

public final class DownloadFile extends Downloadable {

    private String size;

    public DownloadFile(Elements elements) {
        path = elements.get(0).attr("href");
        name = elements.get(0).html();
        size = elements.get(1).html();
    }

    public DownloadFile(String path, String name, String size) {
        this.path = path;
        this.name = name;
        this.size = size;
    }


    @Override
    public String toString() {
        return "DownloadFile{" +
                "size='" + size + '\'' + super.toString() +
                '}';
    }


    @Override
    public int compareTo(Downloadable another) {
        if (another instanceof DownloadFolder)
            return 1;
        else
            return name.compareTo(another.getName());
    }
}
