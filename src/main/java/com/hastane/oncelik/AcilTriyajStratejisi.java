package com.hastane.oncelik;

import com.hastane.model.Hasta;
import java.time.LocalDateTime;

/**
 * Acil servis triyajı:
 *   skor = aciliyet puanı x 100
 *        + bekleme süresi (en fazla 120 dk) x 0,5   -> "yaşlanma" (aging), bekleyen öne geçer
 *        + 15 (65 yaş üstü veya 5 yaş altıysa)
 * Bekleme ve yaş katkısı en fazla 75 puan olduğu için üst seviye
 * (ör. KIRMIZI) her zaman alt seviyenin (SARI) önünde kalır.
 */
public class AcilTriyajStratejisi implements OncelikStratejisi {

    static final int SEVIYE_CARPANI = 100;
    static final int AZAMI_BEKLEME_DK = 120;
    static final double BEKLEME_KATSAYISI = 0.5;
    static final int RISKLI_YAS_PUANI = 15;

    @Override
    public double skorHesapla(Hasta hasta, LocalDateTime simdi) {
        double skor = hasta.getAciliyet().getPuan() * SEVIYE_CARPANI;
        long bekleme = Math.min(hasta.beklemeDakikasi(simdi), AZAMI_BEKLEME_DK);
        skor += bekleme * BEKLEME_KATSAYISI;
        if (hasta.riskliYasGrubu()) {
            skor += RISKLI_YAS_PUANI;
        }
        return skor;
    }

    @Override
    public String ad() {
        return "Acil triyaj (aciliyet + bekleme + yaş)";
    }
}