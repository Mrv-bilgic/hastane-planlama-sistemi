package com.hastane.cizelge;

import com.hastane.model.Ameliyat;
import java.util.List;

/** Çizelgeleme çıktısı: yerleşen ve gün içine sığmayan ameliyatlar. */
public record CizelgeSonucu(List<Ameliyat> planlananlar, List<Ameliyat> planlanamayanlar) {

    public CizelgeSonucu {
        planlananlar = List.copyOf(planlananlar);
        planlanamayanlar = List.copyOf(planlanamayanlar);
    }
}