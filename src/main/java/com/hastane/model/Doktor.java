package com.hastane.model;

public class Doktor extends Kisi {

    private final String brans;
    private boolean musait;
    private String mevcutHastaId;

    public Doktor(String id, String ad, String soyad, String brans,
                  boolean musait, String mevcutHastaId) {
        super(id, ad, soyad);
        if (brans == null || brans.isBlank()) {
            throw new IllegalArgumentException("Branş boş olamaz");
        }
        this.brans = brans.trim();
        this.musait = musait;
        this.mevcutHastaId = mevcutHastaId;
    }

    public void hastaAta(String hastaId) {
        if (!musait) {
            throw new IllegalStateException(getTamAd() + " şu an müsait değil");
        }
        this.mevcutHastaId = hastaId;
        this.musait = false;
    }

    public void serbestBirak() {
        this.mevcutHastaId = null;
        this.musait = true;
    }

    public String getBrans() { return brans; }
    public boolean isMusait() { return musait; }
    public String getMevcutHastaId() { return mevcutHastaId; }

    @Override
    public String rolAdi() {
        return "Doktor";
    }

    @Override
    public String ozet() {
        return String.format("%s | %s | %s", super.ozet(), brans,
                musait ? "müsait" : "muayenede (" + mevcutHastaId + ")");
    }
}