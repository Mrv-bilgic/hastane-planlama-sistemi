package com.hastane.model;

public class Ameliyathane implements Kimlikli {

    private final String id;
    private final String ad;
    private boolean aktif; // arıza/temizlik durumunda false

    public Ameliyathane(String id, String ad, boolean aktif) {
        if (id == null || id.isBlank() || ad == null || ad.isBlank()) {
            throw new IllegalArgumentException("Salon kimliği ve adı zorunludur");
        }
        this.id = id;
        this.ad = ad.trim();
        this.aktif = aktif;
    }

    @Override
    public String getId() { return id; }
    public String getAd() { return ad; }
    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", id, ad, aktif ? "aktif" : "DEVRE DIŞI");
    }
}