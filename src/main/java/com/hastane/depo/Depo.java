package com.hastane.depo;

import com.hastane.model.Kimlikli;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY kalıbı: verinin nerede tutulduğu (dosya, bellek, veritabanı)
 * servis katmanından gizlenir. Servis bu arayüze bağımlıdır (DIP).
 */
public interface Depo<T extends Kimlikli> {

    void kaydet(T nesne);

    Optional<T> bul(String id);

    List<T> tumu();

    boolean sil(String id);

    default int say() {
        return tumu().size();
    }
}