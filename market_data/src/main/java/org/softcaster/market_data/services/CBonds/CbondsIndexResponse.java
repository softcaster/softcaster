/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.CBonds;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CbondsIndexResponse {

    @JsonProperty("type_id")
    private String typeId;

    @JsonProperty("type_id.numeric")
    private Integer typeIdNumeric;

    @JsonProperty("CbondsIndexValue")
    private String cbondsIndexValue;

    @JsonProperty("CbondsIndexValue.numeric")
    private Double cbondsIndexValueNumeric;

    @JsonProperty("group_id")
    private String groupId;

    @JsonProperty("group_id.numeric")
    private Integer groupIdNumeric;

    @JsonProperty("group2_id")
    private String group2Id;

    @JsonProperty("group2_id.numeric")
    private Integer group2IdNumeric;

    @JsonProperty("datetime")
    private String datetime;

    @JsonProperty("datetime.numeric")
    private Long datetimeNumeric;

    // Costruttore vuoto (obbligatorio per Jackson)
    public CbondsIndexResponse() {
    }

    // Getter e Setter (puoi generarli con il tuo IDE o usare @Data di Lombok)
    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public Integer getTypeIdNumeric() {
        return typeIdNumeric;
    }

    public void setTypeIdNumeric(Integer typeIdNumeric) {
        this.typeIdNumeric = typeIdNumeric;
    }

    public String getCbondsIndexValue() {
        return cbondsIndexValue;
    }

    public void setCbondsIndexValue(String cbondsIndexValue) {
        this.cbondsIndexValue = cbondsIndexValue;
    }

    public Double getCbondsIndexValueNumeric() {
        return cbondsIndexValueNumeric;
    }

    public void setCbondsIndexValueNumeric(Double cbondsIndexValueNumeric) {
        this.cbondsIndexValueNumeric = cbondsIndexValueNumeric;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getGroupIdNumeric() {
        return groupIdNumeric;
    }

    public void setGroupIdNumeric(Integer groupIdNumeric) {
        this.groupIdNumeric = groupIdNumeric;
    }

    public String getGroup2Id() {
        return group2Id;
    }

    public void setGroup2Id(String group2Id) {
        this.group2Id = group2Id;
    }

    public Integer getGroup2IdNumeric() {
        return group2IdNumeric;
    }

    public void setGroup2IdNumeric(Integer group2IdNumeric) {
        this.group2IdNumeric = group2IdNumeric;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Long getDatetimeNumeric() {
        return datetimeNumeric;
    }

    public void setDatetimeNumeric(Long datetimeNumeric) {
        this.datetimeNumeric = datetimeNumeric;
    }
}
