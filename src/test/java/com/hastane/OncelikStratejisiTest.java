package com.hastane;

import static com.hastane.TestVerisi.SIMDI;
import static com.hastane.TestVerisi.hasta;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Hasta;
import com.hastane.oncelik.AcilTriyajStratejisi;
import com.hastane.oncelik.GelisSirasiStratejisi;
import com.hastane.oncelik.OncelikStratejisi;
import com.hastane.oncelik.TriyajKuyrugu;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OncelikStratejisiTest {

    private final OncelikStratejisi triyaj = new AcilTriyajStratejisi();

    @Test
    @DisplayName("Kırmızı hasta, uzun bekleyen yaşlı sarı hastadan bile önce gelir")
    void kirmiziHerZamanOnde() {
        Hasta kirmizi = hasta("H1", 30, AciliyetSeviyesi.KIRMIZI, 0);
        Hasta sari = hasta("H2", 80, AciliyetSeviyesi.SARI, 300);
        assertTrue(triyaj.skorHesapla(kirmizi, SIMDI) > triyaj.skorHesapla(sari, SIMDI));
    }

    @Test
    @DisplayName("Aynı seviyede uzun bekleyen hasta öne geçer (aging)")
    void beklemeSuresiOnceligiArtirir() {
        Hasta yeni = hasta("H1", 30, AciliyetSeviyesi.YESIL, 5);
        Hasta eski = hasta("H2", 30, AciliyetSeviyesi.YESIL, 60);
        assertTrue(triyaj.skorHesapla(eski, SIMDI) > triyaj.skorHesapla(yeni, SIMDI));
    }

    @Test
    @DisplayName("65 yaş üstü ve 5 yaş altı hastalar ek puan alır")
    void riskliYasGrubuEkPuanAlir() {
        Hasta genc = hasta("H1", 30, AciliyetSeviyesi.SARI, 10);
        Hasta yasli = hasta("H2", 70, AciliyetSeviyesi.SARI, 10);
        Hasta cocuk = hasta("H3", 3, AciliyetSeviyesi.SARI, 10);
        double fark = triyaj.skorHesapla(yasli, SIMDI) - triyaj.skorHesapla(genc, SIMDI);
        assertEquals(15.0, fark, 0.001);
        assertEquals(triyaj.skorHesapla(yasli, SIMDI), triyaj.skorHesapla(cocuk, SIMDI), 0.001);
    }

    @Test
    @DisplayName("Strateji değişince sıralama değişir: geliş sırasında aciliyet dikkate alınmaz")
    void stratejiDegisinceSiralamaDegisir() {
        Hasta erkenYesil = hasta("H1", 30, AciliyetSeviyesi.YESIL, 50);
        Hasta gecKirmizi = hasta("H2", 30, AciliyetSeviyesi.KIRMIZI, 1);
        List<Hasta> hastalar = List.of(erkenYesil, gecKirmizi);

        TriyajKuyrugu kuyruk = new TriyajKuyrugu(triyaj);
        assertEquals("H2", kuyruk.sirala(hastalar, SIMDI).get(0).getId());

        kuyruk.setStrateji(new GelisSirasiStratejisi());
        assertEquals("H1", kuyruk.sirala(hastalar, SIMDI).get(0).getId());
    }
}