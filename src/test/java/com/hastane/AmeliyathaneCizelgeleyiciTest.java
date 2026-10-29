package com.hastane;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hastane.cizelge.AmeliyathaneCizelgeleyici;
import com.hastane.cizelge.CizelgeSonucu;
import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Ameliyat;
import com.hastane.model.Ameliyathane;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AmeliyathaneCizelgeleyiciTest {

    private static final LocalDate GUN = LocalDate.of(2026, 11, 3);
    private final AmeliyathaneCizelgeleyici cizelgeleyici = AmeliyathaneCizelgeleyici.varsayilan();

    private static Ameliyat ameliyat(String id, String doktor, int sure, AciliyetSeviyesi o) {
        return new Ameliyat(id, "H" + id, doktor, "işlem", sure, o);
    }

    @Test
    @DisplayName("Tek salonda ameliyatlar çakışmaz ve aralarında 30 dk temizlik olur")
    void ayniSalondaCakismaYok() {
        Ameliyathane s1 = new Ameliyathane("S001", "Salon 1", true);
        Ameliyat a1 = ameliyat("A1", "D1", 60, AciliyetSeviyesi.SARI);
        Ameliyat a2 = ameliyat("A2", "D2", 60, AciliyetSeviyesi.SARI);

        CizelgeSonucu sonuc = cizelgeleyici.planla(GUN, List.of(a1, a2), List.of(s1), List.of());

        assertEquals(2, sonuc.planlananlar().size());
        LocalDateTime ilkBitis = a1.getBaslangic().isBefore(a2.getBaslangic()) ? a1.bitis() : a2.bitis();
        LocalDateTime ikinciBaslangic = a1.getBaslangic().isBefore(a2.getBaslangic())
                ? a2.getBaslangic() : a1.getBaslangic();
        assertEquals(ilkBitis.plusMinutes(30), ikinciBaslangic);
    }

    @Test
    @DisplayName("Aynı doktor aynı anda iki salonda ameliyata atanmaz")
    void doktorAyniAndaIkiYerdeOlmaz() {
        List<Ameliyathane> salonlar = List.of(
                new Ameliyathane("S001", "Salon 1", true),
                new Ameliyathane("S002", "Salon 2", true));
        Ameliyat a1 = ameliyat("A1", "D1", 90, AciliyetSeviyesi.SARI);
        Ameliyat a2 = ameliyat("A2", "D1", 60, AciliyetSeviyesi.SARI);

        cizelgeleyici.planla(GUN, List.of(a1, a2), salonlar, List.of());

        boolean cakisiyor = a1.getBaslangic().isBefore(a2.bitis()) && a2.getBaslangic().isBefore(a1.bitis());
        assertFalse(cakisiyor);
    }

    @Test
    @DisplayName("Kırmızı öncelikli ameliyat günün ilk saatine yerleşir; farklı doktorlar paralel salonlara dağılır")
    void acilAmeliyatIlkSaate() {
        List<Ameliyathane> salonlar = List.of(
                new Ameliyathane("S001", "Salon 1", true),
                new Ameliyathane("S002", "Salon 2", true));
        Ameliyat yesil = ameliyat("A1", "D1", 120, AciliyetSeviyesi.YESIL);
        Ameliyat kirmizi = ameliyat("A2", "D2", 60, AciliyetSeviyesi.KIRMIZI);
        Ameliyat sari = ameliyat("A3", "D3", 60, AciliyetSeviyesi.SARI);

        cizelgeleyici.planla(GUN, List.of(yesil, sari, kirmizi), salonlar, List.of());

        assertEquals(GUN.atTime(LocalTime.of(8, 0)), kirmizi.getBaslangic());
        assertEquals(GUN.atTime(LocalTime.of(8, 0)), sari.getBaslangic());
        assertNotEquals(kirmizi.getSalonId(), sari.getSalonId());
    }

    @Test
    @DisplayName("Mesaiye sığmayan ameliyat planlanamayanlara düşer, pasif salon kullanılmaz")
    void sigmayanVePasifSalon() {
        List<Ameliyathane> salonlar = List.of(
                new Ameliyathane("S001", "Salon 1", true),
                new Ameliyathane("S002", "Arızalı salon", false));
        Ameliyat uzun1 = ameliyat("A1", "D1", 400, AciliyetSeviyesi.SARI);
        Ameliyat uzun2 = ameliyat("A2", "D2", 400, AciliyetSeviyesi.SARI);

        CizelgeSonucu sonuc = cizelgeleyici.planla(GUN, List.of(uzun1, uzun2), salonlar, List.of());

        assertEquals(1, sonuc.planlananlar().size());
        assertEquals(1, sonuc.planlanamayanlar().size());
        assertEquals("S001", sonuc.planlananlar().get(0).getSalonId());
        assertTrue(sonuc.planlanamayanlar().get(0).getSalonId() == null);
    }
}