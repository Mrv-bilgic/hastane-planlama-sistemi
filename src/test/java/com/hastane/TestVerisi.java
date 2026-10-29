package com.hastane;

import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Hasta;
import com.hastane.model.HastaDurumu;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/** Testlerde ortak kullanılan sabit saat ve hasta üretici. */
final class TestVerisi {

    static final Clock SAAT = Clock.fixed(Instant.parse("2026-11-02T10:00:00Z"), ZoneOffset.UTC);
    static final LocalDateTime SIMDI = LocalDateTime.now(SAAT);

    private TestVerisi() {
    }

    static Hasta hasta(String id, int yas, AciliyetSeviyesi a, int kacDakikaOnceGeldi) {
        return new Hasta(id, "Ad" + id, "Soyad", yas, "şikâyet", a,
                SIMDI.minusMinutes(kacDakikaOnceGeldi), HastaDurumu.BEKLIYOR, null);
    }
}