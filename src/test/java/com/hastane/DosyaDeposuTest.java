package com.hastane;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hastane.depo.AmeliyatDeposu;
import com.hastane.depo.HastaDeposu;
import com.hastane.fabrika.KimlikUretici;
import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Ameliyat;
import com.hastane.model.Hasta;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DosyaDeposuTest {

    @Test
    @DisplayName("Kaydedilen veriler dosyadan yeniden okununca aynen geri gelir (kalıcılık)")
    void verilerKaliciOlarakSaklanir() throws IOException {
        Path klasor = Files.createTempDirectory("hastane-test");
        Path hastaDosyasi = klasor.resolve("hastalar.txt");
        Path ameliyatDosyasi = klasor.resolve("ameliyatlar.txt");

        Hasta h = TestVerisi.hasta("H001", 72, AciliyetSeviyesi.KIRMIZI, 15);
        new HastaDeposu(hastaDosyasi).kaydet(h);
        Ameliyat a = new Ameliyat("A001", "H001", "D001", "Apendektomi; acil", 90, AciliyetSeviyesi.KIRMIZI);
        a.planla("S001", LocalDateTime.of(2026, 11, 3, 8, 0));
        new AmeliyatDeposu(ameliyatDosyasi).kaydet(a);

        // Uygulama kapanıp açılmış gibi: yeni depo nesneleri dosyadan okur
        Hasta okunan = new HastaDeposu(hastaDosyasi).bul("H001").orElseThrow();
        Ameliyat okunanA = new AmeliyatDeposu(ameliyatDosyasi).bul("A001").orElseThrow();

        assertEquals(h.getTamAd(), okunan.getTamAd());
        assertEquals(72, okunan.getYas());
        assertEquals(AciliyetSeviyesi.KIRMIZI, okunan.getAciliyet());
        assertEquals(h.getGelisZamani(), okunan.getGelisZamani());
        assertTrue(okunanA.planlandiMi());
        assertEquals("S001", okunanA.getSalonId());
        assertEquals("Apendektomi, acil", okunanA.getIslemAdi()); // ayraç güvenli hale getirildi
    }

    @Test
    @DisplayName("Silme kalıcıdır ve kimlik üretici sıradaki numarayı verir")
    void silmeVeKimlikUretimi() throws IOException {
        Path dosya = Files.createTempDirectory("hastane-test").resolve("hastalar.txt");
        HastaDeposu depo = new HastaDeposu(dosya);
        depo.kaydet(TestVerisi.hasta("H001", 30, AciliyetSeviyesi.YESIL, 0));
        depo.kaydet(TestVerisi.hasta("H007", 30, AciliyetSeviyesi.YESIL, 0));

        assertEquals("H008", KimlikUretici.sonraki("H", depo.tumu()));
        assertTrue(depo.sil("H001"));
        assertFalse(new HastaDeposu(dosya).bul("H001").isPresent());
        assertEquals(1, new HastaDeposu(dosya).say());
    }
}