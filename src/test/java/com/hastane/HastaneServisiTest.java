package com.hastane;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hastane.cizelge.AmeliyathaneCizelgeleyici;
import com.hastane.depo.BellekDeposu;
import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Ameliyat;
import com.hastane.model.Ameliyathane;
import com.hastane.model.Doktor;
import com.hastane.model.Hasta;
import com.hastane.model.HastaDurumu;
import com.hastane.oncelik.AcilTriyajStratejisi;
import com.hastane.oncelik.TriyajKuyrugu;
import com.hastane.olay.HastaneOlayi;
import com.hastane.olay.OlayTuru;
import com.hastane.olay.OlayYayinci;
import com.hastane.servis.HastaneServisi;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Servis, dosya yerine BellekDeposu ile test edilir (DIP sayesinde mümkün). */
class HastaneServisiTest {

    private HastaneServisi servis;
    private final List<HastaneOlayi> gelenOlaylar = new ArrayList<>();

    @BeforeEach
    void hazirla() {
        gelenOlaylar.clear();
        OlayYayinci yayinci = new OlayYayinci();
        yayinci.aboneEkle(gelenOlaylar::add);
        servis = new HastaneServisi(new BellekDeposu<>(), new BellekDeposu<>(),
                new BellekDeposu<>(), new BellekDeposu<>(),
                new TriyajKuyrugu(new AcilTriyajStratejisi()),
                AmeliyathaneCizelgeleyici.varsayilan(), yayinci, TestVerisi.SAAT);
    }

    @Test
    @DisplayName("Doktor en öncelikli hastayı çağırır; meşgulken tekrar çağıramaz")
    void hastaCagirma() {
        Doktor d = servis.doktorEkle("Ayşe", "Kaya", "Acil");
        servis.hastaKaydet("Ali", "Y", 30, "baş ağrısı", AciliyetSeviyesi.YESIL);
        Hasta kritik = servis.hastaKaydet("Veli", "Z", 50, "göğüs ağrısı", AciliyetSeviyesi.KIRMIZI);

        Hasta cagrilan = servis.siradakiHastayiCagir(d.getId()).orElseThrow();

        assertEquals(kritik.getId(), cagrilan.getId());
        assertEquals(HastaDurumu.MUAYENEDE, cagrilan.getDurum());
        assertFalse(d.isMusait());
        assertThrows(IllegalStateException.class, () -> servis.siradakiHastayiCagir(d.getId()));

        servis.muayeneyiBitir(d.getId());
        assertTrue(d.isMusait());
        assertEquals(HastaDurumu.TABURCU, cagrilan.getDurum());
    }

    @Test
    @DisplayName("Kırmızı hasta kaydı Observer ile dinleyicilere bildirilir")
    void kritikHastaOlayiYayinlanir() {
        servis.hastaKaydet("Ali", "Y", 30, "kesik", AciliyetSeviyesi.YESIL);
        assertTrue(gelenOlaylar.isEmpty());

        servis.hastaKaydet("Veli", "Z", 60, "bilinç kaybı", AciliyetSeviyesi.KIRMIZI);
        assertEquals(1, gelenOlaylar.size());
        assertEquals(OlayTuru.KRITIK_HASTA, gelenOlaylar.get(0).tur());
    }

    @Test
    @DisplayName("Salon arızasında ameliyatlar diğer salona yeniden planlanır")
    void arizadaYenidenPlanlama() {
        LocalDate gun = LocalDate.of(2026, 11, 3);
        Ameliyathane s1 = servis.salonEkle("Salon 1");
        Ameliyathane s2 = servis.salonEkle("Salon 2");
        Doktor d1 = servis.doktorEkle("A", "B", "Cerrahi");
        Doktor d2 = servis.doktorEkle("C", "D", "Ortopedi");
        Hasta h1 = servis.hastaKaydet("H", "1", 40, "x", AciliyetSeviyesi.SARI);
        Hasta h2 = servis.hastaKaydet("H", "2", 40, "y", AciliyetSeviyesi.SARI);
        servis.ameliyatTalebiEkle(h1.getId(), d1.getId(), "op1", 60, AciliyetSeviyesi.SARI);
        servis.ameliyatTalebiEkle(h2.getId(), d2.getId(), "op2", 60, AciliyetSeviyesi.SARI);
        servis.ameliyatlariPlanla(gun);
        assertEquals(2, servis.gunlukPlan(gun).size());

        servis.salonArizaBildir(s1.getId(), gun);

        List<Ameliyat> plan = servis.gunlukPlan(gun);
        assertEquals(2, plan.size());
        assertTrue(plan.stream().allMatch(a -> a.getSalonId().equals(s2.getId())));
        assertFalse(servis.salonBul(s1.getId()).isAktif());
    }

    @Test
    @DisplayName("Geçersiz durum geçişleri ve hatalı veriler reddedilir (kapsülleme)")
    void gecersizIslemlerReddedilir() {
        Hasta h = TestVerisi.hasta("H1", 30, AciliyetSeviyesi.YESIL, 0);
        assertThrows(IllegalStateException.class, h::taburcuEt);
        assertThrows(IllegalArgumentException.class,
                () -> servis.hastaKaydet("Ali", "Y", 200, "x", AciliyetSeviyesi.SARI));
        assertThrows(IllegalArgumentException.class, () -> AciliyetSeviyesi.puandan(5));
        assertThrows(IllegalArgumentException.class, () -> servis.siradakiHastayiCagir("D999"));
    }
}