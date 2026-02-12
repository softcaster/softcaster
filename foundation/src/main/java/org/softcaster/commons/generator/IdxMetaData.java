/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author emy
 */
public class IdxMetaData {

    private Map<String, List<String>> idxMap = null;

    public IdxMetaData() {
        idxMap = new HashMap<>();
    }

    /**
     * @return the idxMap
     */
    public Map<String, List<String>> getIdxMap() {
        return idxMap;
    }

    public void add(String key, String column) {
        if (idxMap.containsKey(key)) {
            List<String> columns = idxMap.get(key);
            columns.add(column);
        } else {
            List<String> columns = new ArrayList<>();
            columns.add(column);
            idxMap.put(key, columns);
        }
    }
}
