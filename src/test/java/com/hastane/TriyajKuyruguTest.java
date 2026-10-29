package com.hastane;

import static com.hastane.TestVerisi.SIMDI;
import static com.hastane.TestVerisi.hasta;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Hasta;
import com.hastane.oncelik.AcilTriyajStratejisi;
import com.hastane.oncelik.TriyajKuyrugu;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TriyajKuyruguTest {

    @Test
    @DisplayName("Kuyruk yalnızca bekleyen hastaları doğru öncelik sırasıyla verir")
    void yalnizcaBekleyenlerSiralanir() {
        Hasta yesil = hasta("H1", 30, AciliyetSeviyesi.YESIL, 10);
        Hasta sari = hasta("H2", 30, AciliyetSeviyesi.SARI, 10);
        Hasta kirmizi = hasta("H3", 30, AciliyetSeviyesi.KIRMIZI, 10);
        Hasta muayenede = hasta("H4", 30, AciliyetSeviyesi.KIRMIZI, 90);
        muayenede.muayeneyeAl("D001");

        List<Hasta> sirali = new TriyajKuyrugu(new AcilTriyajStratejisi())
                .sirala(List.of(yesil, muayenede, sari, kirmizi), SIMDI);

        assertEquals(3, sirali.size());
        assertEquals(List.of("H3", "H2", "H1"), sirali.stream().map(Hasta::getId).toList());
    }

    @Test
    @DisplayName("Bekleyen hasta yoksa sıradaki boş döner")
    void bosKuyruk() {
        TriyajKuyrugu kuyruk = new TriyajKuyrugu(new AcilTriyajStratejisi());
        assertTrue(kuyruk.siradaki(List.of(), SIMDI).isEmpty());
    }
}