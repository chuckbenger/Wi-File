package com.tkblackbelt.sync.net.http;

import android.os.AsyncTask;
import com.tkblackbelt.sync.net.http.parse.DownloadFile;
import com.tkblackbelt.sync.net.http.parse.DownloadFolder;
import com.tkblackbelt.sync.net.http.parse.Downloadable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.tkblackbelt.sync.core.MyLog.E;

public class HtmlDownloadTask extends AsyncTask<Void, Void, ArrayList<Downloadable>> {

    public interface HtmlDownloadListener {
        public void onHtmlReceived(ArrayList<Downloadable> resultSet);

        public void onHtmlDownloadError();
    }

    private final String address;
    private final HtmlDownloadListener callback;
    private final static int FILE_NODE_SIZE = 3;
    private final static int FOLDER_NODE_SIZE = 2;

    public HtmlDownloadTask(String address, HtmlDownloadListener callback) {
        this.address = address;
        this.callback = callback;
    }

    @Override
    protected ArrayList<Downloadable> doInBackground(Void... params) {
        ArrayList<Downloadable> files;

        try {

            Document doc = Jsoup.connect(address).timeout(1000 * 10).get();

            Elements elementsByTag = doc.select("b");

            files = new ArrayList<Downloadable>();
            parseElements(elementsByTag, files);

        } catch (IOException e) {
            E("Error downloading files: " + e.getMessage());
            files = null;
        }
        return files;
    }

    /**
     * Parses the elements from html and adds create download files and folders from them
     *
     * @param elementsSelected A list of selected elements
     * @param files            The result set the nodes will be added to
     */
    private void parseElements(Elements elementsSelected, ArrayList<Downloadable> files) {

        for (Element element : elementsSelected) {
            Elements elements = element.children();

            if (elements.size() == FILE_NODE_SIZE) {
                DownloadFile file = new DownloadFile(elements);
                files.add(file);
            } else if (elements.size() == FOLDER_NODE_SIZE) {
                DownloadFolder folder = new DownloadFolder(elements);
                files.add(folder);
            }
        }
        Collections.sort(files);
    }

    @Override
    protected void onPostExecute(ArrayList<Downloadable> resultSet) {
        if (resultSet == null)
            callback.onHtmlDownloadError();
        else
            callback.onHtmlReceived(resultSet);
    }
}








