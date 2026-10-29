package com.hastane.model;

/**
 * Hasta ve Doktor için ortak soyut üst sınıf.
 */
public abstract class Kisi implements Kimlikli {

    private final String id;
    private String ad;
    private String soyad;

    protected Kisi(String id, String ad, String soyad) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Kimlik boş olamaz");
        }
        this.id = id;
        setAd(ad);
        this.soyad = soyad == null ? "" : soyad.trim();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getAd() {
        return ad;
    }

    public final void setAd(String ad) {
        if (ad == null || ad.isBlank()) {
            throw new IllegalArgumentException("Ad boş olamaz");
        }
        this.ad = ad.trim();
    }

    public String getSoyad() {
        return soyad;
    }

    public String getTamAd() {
        return (ad + " " + soyad).trim();
    }

    /** Her alt sınıf kendi rol adını verir (soyut metot). */
    public abstract String rolAdi();

    /** Alt sınıflar ezerek (override) kendi özetini üretir — çok biçimlilik. */
    public String ozet() {
        return String.format("[%s] %s: %s", id, rolAdi(), getTamAd());
    }

    @Override
    public String toString() {
        return ozet();
    }
}