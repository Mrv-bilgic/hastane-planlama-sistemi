package com.hastane.olay;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Tüm olayları kalıcı bir günlük dosyasına yazar. */
public class DosyaGunlukDinleyici implements OlayDinleyici {

    private final Path dosya;

    public DosyaGunlukDinleyici(Path dosya) {
        this.dosya = dosya;
    }

    @Override
    public void olayOlustu(HastaneOlayi olay) {
        String satir = olay.zaman().withNano(0) + " | " + olay.tur() + " | " + olay.mesaj()
                + System.lineSeparator();
        try {
            if (dosya.getParent() != null) {
                Files.createDirectories(dosya.getParent());
            }
            Files.writeString(dosya, satir, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Günlük yazılamadı: " + e.getMessage());
        }
    }
}