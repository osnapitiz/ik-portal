package com.pmt.ikportal;

import com.pmt.ikportal.service.LeaveService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaveServiceTest {

    @Test
    void haftaIciBesGunSayar() {
        // 2026-09-14 Pazartesi - 2026-09-18 Cuma
        assertEquals(5, LeaveService.countWorkDays(
                LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 18)));
    }

    @Test
    void haftaSonuHaricTutulur() {
        // 2026-09-14 Pazartesi - 2026-09-21 Pazartesi -> 6 is gunu
        assertEquals(6, LeaveService.countWorkDays(
                LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 21)));
    }

    @Test
    void sadeceHaftaSonuSifirDoner() {
        // 2026-09-19 Cumartesi - 2026-09-20 Pazar
        assertEquals(0, LeaveService.countWorkDays(
                LocalDate.of(2026, 9, 19), LocalDate.of(2026, 9, 20)));
    }

    @Test
    void tekGunlukIzin() {
        assertEquals(1, LeaveService.countWorkDays(
                LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 15)));
    }
}
