package com.hastane.depo;

import com.hastane.model.Kimlikli;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Verileri ";" ile ayrılmış metin dosyasında KALICI olarak saklar.
 * TEMPLATE METHOD kalıbı: okuma/yazma iskeleti burada, satırın nasıl
 * oluşturulacağı/çözüleceği alt sınıflarda (HastaDeposu, DoktorDeposu...).
 */
public abstract class DosyaDeposu<T extends Kimlikli> implements Depo<T> {

    protected static final String AYRAC = ";";
    protected static final String BOS = "-";

    private final Path dosya;
    private final Map<String, T> onbellek = new LinkedHashMap<>();

    protected DosyaDeposu(Path dosya) {
        this.dosya = dosya;
    }

    /** Alt sınıf kurucusunun sonunda çağrılır. */
    protected final void yukle() {
        onbellek.clear();
        if (!Files.exists(dosya)) {
            return;
        }
        try {
            for (String satir : Files.readAllLines(dosya, StandardCharsets.UTF_8)) {
                if (satir.isBlank()) {
                    continue;
                }
                T nesne = satirdanOlustur(satir.split(AYRAC, -1));
                onbellek.put(nesne.getId(), nesne);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Dosya okunamadı: " + dosya, e);
        }
    }

    private void diskeYaz() {
        List<String> satirlar = new ArrayList<>();
        for (T nesne : onbellek.values()) {
            satirlar.add(satiraDonustur(nesne));
        }
        try {
            if (dosya.getParent() != null) {
                Files.createDirectories(dosya.getParent());
            }
            Files.write(dosya, satirlar, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Dosyaya yazılamadı: " + dosya, e);
        }
    }

    @Override
    public void kaydet(T nesne) {
        onbellek.put(nesne.getId(), nesne);
        diskeYaz();
    }

    @Override
    public Optional<T> bul(String id) {
        return Optional.ofNullable(onbellek.get(id));
    }

    @Override
    public List<T> tumu() {
        return new ArrayList<>(onbellek.values());
    }

    @Override
    public boolean sil(String id) {
        boolean silindi = onbellek.remove(id) != null;
        if (silindi) {
            diskeYaz();
        }
        return silindi;
    }

    // --- alt sınıfların dolduracağı "şablon adımları" ---
    protected abstract String satiraDonustur(T nesne);

    protected abstract T satirdanOlustur(String[] alanlar);

    // --- yardımcılar ---
    protected static String temizle(String metin) {
        if (metin == null) {
            return BOS;
        }
        return metin.replace(AYRAC, ",").replace("\n", " ").replace("\r", " ");
    }

    protected static String bosIseNull(String alan) {
        return BOS.equals(alan) ? null : alan;
    }
}