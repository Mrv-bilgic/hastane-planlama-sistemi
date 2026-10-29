package com.hastane.depo;

import com.hastane.model.Doktor;
import java.nio.file.Path;

/** Satır biçimi: id;ad;soyad;brans;musait;mevcutHastaId */
public class DoktorDeposu extends DosyaDeposu<Doktor> {

    public DoktorDeposu(Path dosya) {
        super(dosya);
        yukle();
    }

    @Override
    protected String satiraDonustur(Doktor d) {
        return String.join(AYRAC, d.getId(), temizle(d.getAd()), temizle(d.getSoyad()),
                temizle(d.getBrans()), String.valueOf(d.isMusait()),
                d.getMevcutHastaId() == null ? BOS : d.getMevcutHastaId());
    }

    @Override
    protected Doktor satirdanOlustur(String[] a) {
        return new Doktor(a[0], a[1], a[2], a[3], Boolean.parseBoolean(a[4]), bosIseNull(a[5]));
    }
}