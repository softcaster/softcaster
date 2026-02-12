/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.imports;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 *
 * @author ep
 */
public class ImportConfig {

    private char separator = ',';
    private Path filePath = null;
    private Charset charset = StandardCharsets.ISO_8859_1;
    private int startData = 0; // linea da cui cominciano i dati

    /**
     * @return the separator
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * @param separator the separator to set
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    /**
     * @return the filePath
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * @return the charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @param charset the charset to set
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * @return the startData
     */
    public int getStartData() {
        return startData;
    }

    /**
     * @param startData the startData to set
     */
    public void setStartData(int startData) {
        this.startData = startData;
    }

}
