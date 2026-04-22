/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.easy_pricer_srv.util;

import jakarta.servlet.ServletContext;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author emy
 */
@Service("commonData")
public class CommonData {

    private static String osName = null;
    @Autowired
    ServletContext servletContext;

    private String createUrl(String regione, String sigla, String comune, String indirizzo) {
        String url = "https://www.tuttocitta.it/cap/";

        // rimuovo spazi in testa e in coda
        regione = regione.trim();
        // sostituisco spazio con -
        regione = regione.replaceAll(" ", "-").toLowerCase();

        comune = comune.trim();
        comune = comune.replaceAll(" ", "-").toLowerCase();

        indirizzo = indirizzo.trim();
        indirizzo = indirizzo.replaceAll(" ", "-").toLowerCase();

        sigla = sigla.trim();

        url = url + regione + "/" + sigla.toLowerCase() + "/" + comune + "/" + indirizzo;
        return url;
    }

    public String getOsName() {
        if (osName == null) {
            osName = System.getProperty("os.name");
        }

        return osName;
    }

    public boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    public String getJsonPathSeparator() {
        String fileSep = "\\";
        if (!isWindows()) {
            fileSep = "/";
        }

        return fileSep;
    }

    public String getPathSeparator() {
        return System.getProperty("path.separator");
    }

    public String getJsonRealPathFileName(String fileName) {

        String token = "\\";
        if (!isWindows()) {
            token = "/";
        }

        StringTokenizer st = new StringTokenizer(servletContext.getRealPath("public"), token);
        String jsonRealPath = "";
        while (st.hasMoreElements()) {
            String next = (String) st.nextElement();
            next += "\\\\";
            jsonRealPath += next;
        }

        jsonRealPath += fileName;

        return jsonRealPath;
    }

    public String getJsonContextFileName(String fileName, String folder) {
        String jsonContext = "http://" + servletContext.getVirtualServerName() + ":8080" + servletContext.getContextPath() + "/public/" + folder + "/" + fileName;
        return jsonContext;
    }

    public void uploadFile(byte[] data, String fileName) throws FileNotFoundException, IOException {
        String localFileName = System.getenv().get("PUBLIC_ASSETS") + "\\upload\\" + fileName;
        try (FileOutputStream fileOuputStream = new FileOutputStream(localFileName)) {
            fileOuputStream.write(data);
        }
    }

    public static String getJsonError(String error) {
        String jsonError = "{\"Error\"" + ":" + "\"" + error + "\"}";
        return jsonError;
    }

    public static String getJsonResult(String message) {
        String jsonError = "{\"Result\"" + ":" + "\"" + message + "\"}";
        return jsonError;
    }
 
}
