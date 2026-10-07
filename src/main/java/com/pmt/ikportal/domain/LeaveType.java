package com.pmt.ikportal.domain;

public enum LeaveType {

    YILLIK_IZIN("Yıllık İzin", true),
    MAZERET_IZNI("Mazeret İzni", false),
    HASTALIK_IZNI("Hastalık (Rapor) İzni", false),
    UCRETSIZ_IZIN("Ücretsiz İzin", false),
    DOGUM_IZNI("Doğum İzni", false),
    BABALIK_IZNI("Babalık İzni", false),
    EVLILIK_IZNI("Evlilik İzni", false),
    OLUM_IZNI("Ölüm İzni", false),
    IDARI_IZIN("İdari İzin", false);

    private final String label;
    private final boolean deductedFromAnnualBalance;

    LeaveType(String label, boolean deductedFromAnnualBalance) {
        this.label = label;
        this.deductedFromAnnualBalance = deductedFromAnnualBalance;
    }

    public String getLabel() { return label; }

    public boolean isDeductedFromAnnualBalance() { return deductedFromAnnualBalance; }
}
