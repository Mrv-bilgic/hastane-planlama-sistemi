package com.hastane.depo;

import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Ameliyat;
import java.nio.file.Path;
import java.time.LocalDateTime;

/** Satır biçimi: id;hastaId;doktorId;islem;sure;oncelik;salonId;baslangic */
public class AmeliyatDeposu extends DosyaDeposu<Ameliyat> {

    public AmeliyatDeposu(Path dosya) {
        super(dosya);
        yukle();
    }

    @Override
    protected String satiraDonustur(Ameliyat a) {
        return String.join(AYRAC, a.getId(), a.getHastaId(), a.getDoktorId(),
                temizle(a.getIslemAdi()), String.valueOf(a.getSureDakika()), a.getOncelik().name(),
                a.getSalonId() == null ? BOS : a.getSalonId(),
                a.getBaslangic() == null ? BOS : a.getBaslangic().toString());
    }

    @Override
    protected Ameliyat satirdanOlustur(String[] f) {
        Ameliyat a = new Ameliyat(f[0], f[1], f[2], f[3], Integer.parseInt(f[4]),
                AciliyetSeviyesi.valueOf(f[5]));
        if (!BOS.equals(f[6]) && !BOS.equals(f[7])) {
            a.planla(f[6], LocalDateTime.parse(f[7]));
        }
        return a;
    }
}