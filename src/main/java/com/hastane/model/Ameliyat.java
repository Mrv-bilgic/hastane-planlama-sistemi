package com.hastane.model;

import java.time.LocalDateTime;

/** Bir ameliyat talebi; çizelgeleyici salon ve saat atadığında "planlanmış" olur. */
public class Ameliyat implements Kimlikli {

    private final String id;
    private final String hastaId;
    private final String doktorId;
    private final String islemAdi;
    private final int sureDakika;
    private final AciliyetSeviyesi oncelik;
    private String salonId;
    private LocalDateTime baslangic;

    public Ameliyat(String id, String hastaId, String doktorId, String islemAdi,
                    int sureDakika, AciliyetSeviyesi oncelik) {
        if (sureDakika <= 0) {
            throw new IllegalArgumentException("Süre pozitif olmalı");
        }
        if (id == null || hastaId == null || doktorId == null || oncelik == null) {
            throw new IllegalArgumentException("Ameliyat bilgileri eksik");
        }
        this.id = id;
        this.hastaId = hastaId;
        this.doktorId = doktorId;
        this.islemAdi = islemAdi == null ? "" : islemAdi.trim();
        this.sureDakika = sureDakika;
        this.oncelik = oncelik;
    }

    public void planla(String salonId, LocalDateTime baslangic) {
        this.salonId = salonId;
        this.baslangic = baslangic;
    }

    public void planiIptalEt() {
        this.salonId = null;
        this.baslangic = null;
    }

    public boolean planlandiMi() {
        return salonId != null && baslangic != null;
    }

    public LocalDateTime bitis() {
        return planlandiMi() ? baslangic.plusMinutes(sureDakika) : null;
    }

    @Override
    public String getId() { return id; }
    public String getHastaId() { return hastaId; }
    public String getDoktorId() { return doktorId; }
    public String getIslemAdi() { return islemAdi; }
    public int getSureDakika() { return sureDakika; }
    public AciliyetSeviyesi getOncelik() { return oncelik; }
    public String getSalonId() { return salonId; }
    public LocalDateTime getBaslangic() { return baslangic; }

    @Override
    public String toString() {
        String plan = planlandiMi()
                ? String.format("%s | %s - %s", salonId,
                baslangic.toLocalTime(), bitis().toLocalTime())
                : "planlanmadı";
        return String.format("[%s] %s (%d dk, %s) hasta=%s doktor=%s -> %s",
                id, islemAdi, sureDakika, oncelik, hastaId, doktorId, plan);
    }
}