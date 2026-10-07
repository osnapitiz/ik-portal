package com.pmt.ikportal.domain;

public enum LeaveStatus {

    BEKLIYOR("Onay Bekliyor", "badge-pending"),
    ONAYLANDI("Onaylandı", "badge-approved"),
    REDDEDILDI("Reddedildi", "badge-rejected"),
    IPTAL("İptal Edildi", "badge-cancelled");

    private final String label;
    private final String cssClass;

    LeaveStatus(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() { return label; }

    public String getCssClass() { return cssClass; }
}
