/*
 * Copyright 2015 QFast Ahmed El-mawaziny.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qfast.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 * @author Ahmed El-mawaziny
 */
public class HttpRequest {

    private final static String USER_AGENT = "Mozilla/5.0";

    public static Object[] sendGet(String url) throws MalformedURLException, IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            StringBuilder response = new StringBuilder(16);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return new Object[]{responseCode, response.toString()};
        }
    }

    public static Object[] sendPost(String url, String params)
            throws MalformedURLException, ProtocolException, IOException {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(true);

        try (OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream())) {
            wr.write(params);
            wr.flush();
        }

        int responseCode = con.getResponseCode();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            StringBuilder response = new StringBuilder(16);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return new Object[]{responseCode, response.toString()};
        }
    }
}
