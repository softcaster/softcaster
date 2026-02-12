/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

/**
 *
 * @author emy
 */
public class JdbcParam {

    private Object rawValue = null;

    public void setStringValue(String value) {
        rawValue = value;
    }

    public String getStringValue() {
        if (rawValue instanceof String string) {
            return string;
        } else {
            return null;
        }
    }

    public void setDoubleValue(Double value) {
        rawValue = value;
    }

    public Double getDoubleValue() {
        if (rawValue instanceof Double aDouble) {
            return aDouble;
        } else {
            return null;
        }
    }

    public void setIntegerValue(Integer value) {
        rawValue = value;
    }

    public Integer getIntegerValue() {
        if (rawValue instanceof Integer integer) {
            return integer;
        } else {
            return null;
        }
    }

    public void setLongValue(Long value) {
        rawValue = value;
    }

    public Long getLongValue() {
        if (rawValue instanceof Long aLong) {
            return aLong;
        } else {
            return null;
        }
    }

    public void setDateValue(java.sql.Date value) {
        rawValue = value;
    }

    public java.sql.Date getDateValue() {
        if (rawValue instanceof java.sql.Date date) {
            return date;
        } else {
            return null;
        }
    }

    public void setBooleanValue(Boolean value) {
        rawValue = value;
    }

    public Boolean getBooleanValue() {
        if (rawValue instanceof Boolean aBoolean) {
            return aBoolean;
        } else {
            return null;
        }
    }

    /**
     * @return the valueType
     */
    public int getValueType() {
        if (rawValue instanceof java.sql.Date) {
            return DATA_TYPE.DATE;
        } else if (rawValue instanceof String) {
            return DATA_TYPE.TEXT;
        } else if (rawValue instanceof Integer) {
            return DATA_TYPE.INTEGER;
        } else if (rawValue instanceof Long) {
            return DATA_TYPE.LONG;
        } else if (rawValue instanceof Double) {
            return DATA_TYPE.REAL;
        } else if (rawValue instanceof Boolean) {
            return DATA_TYPE.BOOL;
        } else {
            return DATA_TYPE.INVALID;
        }
    }
}
