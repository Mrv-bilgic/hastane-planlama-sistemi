package com.hastane.oncelik;

import com.hastane.model.Hasta;
import java.time.LocalDateTime;

/** Poliklinik mantığı: ilk gelen ilk muayene olur (FIFO). */
public class GelisSirasiStratejisi implements OncelikStratejisi {

    @Override
    public double skorHesapla(Hasta hasta, LocalDateTime simdi) {
        return hasta.beklemeDakikasi(simdi);
    }

    @Override
    public String ad() {
        return "Geliş sırası (poliklinik)";
    }
}