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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Ahmed El-mawaziny
 */
public class MtGatewayHttpPostExample {

    public static final String URL = "https://sgw01.cm.nl/gateway.ashx";

    public static void main(String[] args) throws ProtocolException, IOException {
        String xml = createXml(9168, "Dopay", "Pgt33D2Q", 0, "Dopay", "English with عربي",
                "00201003009331");
        System.out.println(xml);
//        String response = doHttpPost(URL, xml);
//        System.out.println("Response: " + response);
        Object[] sendPost = sendPost(URL, xml);
        System.out.println(Arrays.toString(sendPost));
    }

    private static String createXml(int customerId, String username,
            String password, int tariff, String senderName, String body,
            String msisdn) {
        try {
            ByteArrayOutputStream xml = new ByteArrayOutputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            DocumentBuilder docBuilder = factory.newDocumentBuilder();

            DOMImplementation impl = docBuilder.getDOMImplementation();
            Document doc = impl.createDocument(null, "MESSAGES", null);

            Element root = doc.getDocumentElement();

            Element customerElement = doc.createElement("CUSTOMER");
            customerElement.setAttribute("ID", "" + customerId);
            root.appendChild(customerElement);

            Element userElement = doc.createElement("USER");
            userElement.setAttribute("LOGIN", username);
            userElement.setAttribute("PASSWORD", password);
            root.appendChild(userElement);

            Element tariffElement = doc.createElement("TARIFF");
            Text tariffValue = doc.createTextNode("TARIFF");
            tariffValue.setNodeValue("" + tariff);
            tariffElement.appendChild(tariffValue);
            root.appendChild(tariffElement);

            Element msgElement = doc.createElement("MSG");
            root.appendChild(msgElement);

            Element fromElement = doc.createElement("FROM");
            Text fromValue = doc.createTextNode("FROM");
            fromValue.setNodeValue(senderName);
            fromElement.appendChild(fromValue);
            msgElement.appendChild(fromElement);

            Element dcsElement = doc.createElement("DCS");
            Text dcsValue = doc.createTextNode("DCS");
            dcsValue.setNodeValue("8");
            dcsElement.appendChild(dcsValue);
            msgElement.appendChild(dcsElement);
            
            
            Element bodyElement = doc.createElement("BODY");
            Text bodyValue = doc.createTextNode("BODY");
            bodyValue.setNodeValue(body);
            bodyElement.appendChild(bodyValue);
            bodyElement.setAttribute("TYPE", "TEXT");
            msgElement.appendChild(bodyElement);
            Element toElement = doc.createElement("TO");
            Text toValue = doc.createTextNode("TO");
            toValue.setNodeValue(msisdn);
            toElement.appendChild(toValue);
            msgElement.appendChild(toElement);
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Source src = new DOMSource(doc);
            Result dest = new StreamResult(xml);
            aTransformer.transform(src, dest);
            return xml.toString();
        } catch (TransformerException | ParserConfigurationException ex) {
            System.err.println(ex);
            return ex.toString();
        }
    }

    private static String doHttpPost(String urlString, String requestString) {
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            BufferedReader rd;
            String response;
            try (OutputStreamWriter wr
                    = new OutputStreamWriter(conn.getOutputStream())) {
                wr.write(requestString);
                wr.flush();
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                response = "";
                while ((line = rd.readLine()) != null) {
                    response += line;
                }
            }
            rd.close();
            return response;
        } catch (IOException ex) {
            System.err.println(ex);
            return ex.toString();
        }
    }
    
    
    private final static String USER_AGENT = "Mozilla/5.0";
    public static Object[] sendPost(String url, String params)
            throws MalformedURLException, ProtocolException, IOException {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
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
