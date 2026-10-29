package com.hastane.depo;

import com.hastane.model.Kimlikli;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Veriyi yalnızca bellekte tutan depo; birim testlerinde dosya yerine kullanılır (LSP). */
public class BellekDeposu<T extends Kimlikli> implements Depo<T> {

    private final Map<String, T> veriler = new LinkedHashMap<>();

    @Override
    public void kaydet(T nesne) {
        veriler.put(nesne.getId(), nesne);
    }

    @Override
    public Optional<T> bul(String id) {
        return Optional.ofNullable(veriler.get(id));
    }

    @Override
    public List<T> tumu() {
        return new ArrayList<>(veriler.values());
    }

    @Override
    public boolean sil(String id) {
        return veriler.remove(id) != null;
    }
}