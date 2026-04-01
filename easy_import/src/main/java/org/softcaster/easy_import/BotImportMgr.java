/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

/**
 *
 * @author softc
 */
public class BotImportMgr {
    private static final String URL_STI = "https://www.simpletoolsforinvestors.eu/data/definitions/";

    private static final String[] codes = {"1843962.xml",
        "1844124.xml",
        "1844069.xml",
        "1844168.xml",
        "1844094.xml",
        "1844089.xml",
        "1844155.xml",
        "1844077.xml",
        "1844064.xml",
        "1844093.xml",
        "1844054.xml",
        "1843996.xml",
        "1844034.xml",
        "1844042.xml",
        "1843979.xml"
    };

    private HttpURLConnection getConnection(String _url) throws MalformedURLException, IOException {
        URL url = new URL(_url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/xml, application/json");

        return connection;
    }

    private void loadXml(String info) throws IOException {
        String url = URL_STI + info;
        HttpURLConnection conn = getConnection(url);
        //System.out.println(conn.getResponseCode());
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())))) {
            Stream<String> lines = br.lines();
            lines.forEach(System.out::println);
            //String response = br.readLine();
        }
    }
    
    public void dumpBotXml() throws IOException{
        for(String s :codes) {
            loadXml(s);
        }
    }
}
