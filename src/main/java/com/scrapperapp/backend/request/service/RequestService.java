package com.scrapperapp.backend.request.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestService implements IRequestService {

    private static Logger log = LoggerFactory.getLogger(RequestService.class);

    @Override
    public String getUrlContent(String targetURL) {

        log.info("getting content of url {}", targetURL);
        HttpURLConnection connection = null;

        try {
            //Create connection
/*            if(targetURL.startsWith("http")){
                System.setProperty("https.proxyPort", "80");
            }
            if(targetURL.startsWith("https")){
                System.setProperty("https.proxyPort", "443");
            }*/
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setUseCaches(false);
            connection.setDoOutput(false);

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\r');
            }
            rd.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("error getting site {}, error: msg {} cause {} ",
                    targetURL, e.getMessage(), e.getCause());
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
