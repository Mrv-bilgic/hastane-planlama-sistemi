package com.hastane.fabrika;

import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Doktor;
import com.hastane.model.Hasta;
import com.hastane.model.HastaDurumu;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * FACTORY tasarım kalıbı: Kisi nesnelerinin nasıl ve hangi başlangıç
 * değerleriyle (geliş zamanı, durum, müsaitlik) oluşturulacağı tek yerde toplanır.
 */
public class KisiFabrikasi {

    private final Clock saat;

    public KisiFabrikasi(Clock saat) {
        this.saat = saat;
    }

    public Hasta yeniHasta(String id, String ad, String soyad, int yas,
                           String sikayet, AciliyetSeviyesi aciliyet) {
        return new Hasta(id, ad, soyad, yas, sikayet, aciliyet,
                LocalDateTime.now(saat).withNano(0), HastaDurumu.BEKLIYOR, null);
    }

    public Doktor yeniDoktor(String id, String ad, String soyad, String brans) {
        return new Doktor(id, ad, soyad, brans, true, null);
    }
}