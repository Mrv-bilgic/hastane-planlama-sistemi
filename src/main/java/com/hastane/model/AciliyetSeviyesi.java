package com.hastane.model;

/** Acil servis triyaj renk kodları. */
public enum AciliyetSeviyesi {
    KIRMIZI(3, "Kırmızı - hayati tehlike"),
    SARI(2, "Sarı - acil ama stabil"),
    YESIL(1, "Yeşil - ayaktan / acil değil");

    private final int puan;
    private final String aciklama;

    AciliyetSeviyesi(int puan, String aciklama) {
        this.puan = puan;
        this.aciklama = aciklama;
    }

    public int getPuan() {
        return puan;
    }

    public String getAciklama() {
        return aciklama;
    }

    /** Menüden girilen 1/2/3 değerini seviyeye çevirir. */
    public static AciliyetSeviyesi puandan(int puan) {
        for (AciliyetSeviyesi s : values()) {
            if (s.puan == puan) {
                return s;
            }
        }
        throw new IllegalArgumentException("Geçersiz aciliyet: " + puan + " (1, 2 veya 3 olmalı)");
    }
}