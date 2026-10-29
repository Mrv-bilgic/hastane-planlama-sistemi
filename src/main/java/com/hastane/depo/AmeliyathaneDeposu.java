package com.hastane.depo;

import com.hastane.model.Ameliyathane;
import java.nio.file.Path;

/** Satır biçimi: id;ad;aktif */
public class AmeliyathaneDeposu extends DosyaDeposu<Ameliyathane> {

    public AmeliyathaneDeposu(Path dosya) {
        super(dosya);
        yukle();
    }

    @Override
    protected String satiraDonustur(Ameliyathane s) {
        return String.join(AYRAC, s.getId(), temizle(s.getAd()), String.valueOf(s.isAktif()));
    }

    @Override
    protected Ameliyathane satirdanOlustur(String[] a) {
        return new Ameliyathane(a[0], a[1], Boolean.parseBoolean(a[2]));
    }
}