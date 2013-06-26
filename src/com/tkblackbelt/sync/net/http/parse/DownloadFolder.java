package com.tkblackbelt.sync.net.http.parse;

import org.jsoup.select.Elements;

public final class DownloadFolder extends Downloadable {

    public DownloadFolder(Elements elements) {
        path = elements.get(0).attr("href");
        name = elements.get(0).html();
        path = path.replaceFirst("/", "");
        name = name.substring(0, name.length() - 1);
    }

    public DownloadFolder(String path, String name) {
        this.path = path;
        this.name = name;
    }

    @Override
    public int compareTo(Downloadable another) {
        if (another instanceof DownloadFile)
            return -1;
        else
            return this.name.compareTo(another.getName());
    }
}
