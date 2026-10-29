package com.hastane.depo;

import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Hasta;
import com.hastane.model.HastaDurumu;
import java.nio.file.Path;
import java.time.LocalDateTime;

/** Satır biçimi: id;ad;soyad;yas;sikayet;aciliyet;gelisZamani;durum;doktorId */
public class HastaDeposu extends DosyaDeposu<Hasta> {

    public HastaDeposu(Path dosya) {
        super(dosya);
        yukle();
    }

    @Override
    protected String satiraDonustur(Hasta h) {
        return String.join(AYRAC, h.getId(), temizle(h.getAd()), temizle(h.getSoyad()),
                String.valueOf(h.getYas()), temizle(h.getSikayet()), h.getAciliyet().name(),
                h.getGelisZamani().toString(), h.getDurum().name(),
                h.getDoktorId() == null ? BOS : h.getDoktorId());
    }

    @Override
    protected Hasta satirdanOlustur(String[] a) {
        return new Hasta(a[0], a[1], a[2], Integer.parseInt(a[3]), a[4],
                AciliyetSeviyesi.valueOf(a[5]), LocalDateTime.parse(a[6]),
                HastaDurumu.valueOf(a[7]), bosIseNull(a[8]));
    }
}