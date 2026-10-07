package com.pmt.ikportal.service;

/** Icinde bulunulan yil icin yillik izin ozeti. */
public class LeaveBalance {

    private final int entitledDays;
    private final int usedDays;
    private final int pendingDays;
    private final int seniorityYears;
    private final boolean entitled;

    public LeaveBalance(int entitledDays, int usedDays, int pendingDays, int seniorityYears, boolean entitled) {
        this.entitledDays = entitledDays;
        this.usedDays = usedDays;
        this.pendingDays = pendingDays;
        this.seniorityYears = seniorityYears;
        this.entitled = entitled;
    }

    public static LeaveBalance unknown() {
        return new LeaveBalance(0, 0, 0, 0, false);
    }

    public int getEntitledDays() { return entitledDays; }

    public int getUsedDays() { return usedDays; }

    public int getPendingDays() { return pendingDays; }

    public int getSeniorityYears() { return seniorityYears; }

    public boolean isEntitled() { return entitled; }

    public int getRemainingDays() {
        return Math.max(0, entitledDays - usedDays - pendingDays);
    }

    public int getUsedPercent() {
        if (entitledDays <= 0) {
            return 0;
        }
        return Math.min(100, (int) Math.round((usedDays + pendingDays) * 100.0 / entitledDays));
    }
}
