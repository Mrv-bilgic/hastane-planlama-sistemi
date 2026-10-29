package com.hastane.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Hasta extends Kisi {

    private final int yas;
    private final String sikayet;
    private AciliyetSeviyesi aciliyet;
    private final LocalDateTime gelisZamani;
    private HastaDurumu durum;
    private String doktorId; // muayene eden doktor (yoksa null)

    public Hasta(String id, String ad, String soyad, int yas, String sikayet,
                 AciliyetSeviyesi aciliyet, LocalDateTime gelisZamani,
                 HastaDurumu durum, String doktorId) {
        super(id, ad, soyad);
        if (yas < 0 || yas > 130) {
            throw new IllegalArgumentException("Yaş 0-130 arasında olmalı");
        }
        if (aciliyet == null || gelisZamani == null || durum == null) {
            throw new IllegalArgumentException("Aciliyet, geliş zamanı ve durum zorunludur");
        }
        this.yas = yas;
        this.sikayet = sikayet == null ? "" : sikayet.trim();
        this.aciliyet = aciliyet;
        this.gelisZamani = gelisZamani;
        this.durum = durum;
        this.doktorId = doktorId;
    }

    /** Muayeneye alınır: yalnızca bekleyen hasta için geçerli. */
    public void muayeneyeAl(String doktorId) {
        if (durum != HastaDurumu.BEKLIYOR) {
            throw new IllegalStateException(getTamAd() + " beklemede değil");
        }
        this.durum = HastaDurumu.MUAYENEDE;
        this.doktorId = doktorId;
    }

    public void taburcuEt() {
        if (durum != HastaDurumu.MUAYENEDE) {
            throw new IllegalStateException(getTamAd() + " muayenede değil");
        }
        this.durum = HastaDurumu.TABURCU;
    }

    /** Yeniden triyaj: hastanın durumu kötüleşir/iyileşirse. */
    public void aciliyetGuncelle(AciliyetSeviyesi yeni) {
        if (yeni == null) {
            throw new IllegalArgumentException("Aciliyet boş olamaz");
        }
        this.aciliyet = yeni;
    }

    public long beklemeDakikasi(LocalDateTime simdi) {
        long dk = Duration.between(gelisZamani, simdi).toMinutes();
        return Math.max(0, dk);
    }

    public boolean riskliYasGrubu() {
        return yas >= 65 || yas <= 5;
    }

    public int getYas() { return yas; }
    public String getSikayet() { return sikayet; }
    public AciliyetSeviyesi getAciliyet() { return aciliyet; }
    public LocalDateTime getGelisZamani() { return gelisZamani; }
    public HastaDurumu getDurum() { return durum; }
    public String getDoktorId() { return doktorId; }

    @Override
    public String rolAdi() {
        return "Hasta";
    }

    @Override
    public String ozet() {
        return String.format("%s | %d yaş | %s | %s | %s",
                super.ozet(), yas, sikayet, aciliyet, durum);
    }
}