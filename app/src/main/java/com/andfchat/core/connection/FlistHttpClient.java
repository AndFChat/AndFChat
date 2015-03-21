/*******************************************************************************
 *     This file is part of AndFChat.
 *
 *     AndFChat is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     AndFChat is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with AndFChat.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package com.andfchat.core.connection;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import roboguice.util.Ln;
import android.os.AsyncTask;

public class FlistHttpClient {

    private final static String logInUri = "http://www.f-list.net/json/getApiTicket.php?account={1}&password={2}";
    private final static String addBookmarkUri = "http://www.f-list.net/json/api/bookmark-add.php?account={1}&ticket={2}&name={3}";
    private final static String removeBookmarkUri = "http://www.f-list.net/json/api/bookmark-remove.php?account={1}&ticket={2}&name={3}";


    public static void logIn(String account, String password, FeedbackListener feedbackListener) {
        sendMessage(feedbackListener, logInUri, account, password);
    }

    public static void addBookmark(String account, String ticket, String name, FeedbackListener feedbackListener) {
        sendMessage(feedbackListener, addBookmarkUri, account, ticket, name);
    }

    public static void removeBookmark(String account, String ticket, String name, FeedbackListener feedbackListener) {
        sendMessage(feedbackListener, removeBookmarkUri, account, ticket, name);
    }

    private static void sendMessage(FeedbackListener feedbackListener, String url, String... arguments) {
        for (int i = 1; i < arguments.length + 1; i++) {
            try {
                url = url.replace("{" + i + "}", URLEncoder.encode(arguments[i - 1], "utf-8"));
            } catch (UnsupportedEncodingException e) {
                Ln.e(e);
            }
        }

        Ln.d("Open url");
        HttpWebCall webCall = new HttpWebCall(url, feedbackListener);
        webCall.execute();
    }

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static class HttpWebCall extends AsyncTask<Void, Void, Void> {

        private final String uri;
        private final FeedbackListener feedbackListener;

        public HttpWebCall(String uri, FeedbackListener feedbackListener) {
            this.uri = uri;
            this.feedbackListener = feedbackListener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(uri);

                HttpResponse response = client.execute(request);
                String responseString = convertStreamToString(response.getEntity().getContent());
                Ln.d("Got Response: " + responseString);
                if (feedbackListener != null) {
                    feedbackListener.onResponse(responseString);
                }

                return null;
            } catch (Exception ex) {
                ex.printStackTrace();
                if (feedbackListener != null) {
                    feedbackListener.onError(ex);
                }
            }
            return null;
        }
    }
}
