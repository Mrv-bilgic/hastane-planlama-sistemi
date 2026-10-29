package com.hastane.oncelik;

import com.hastane.model.Hasta;
import java.time.LocalDateTime;

/**
 * STRATEGY tasarım kalıbı: hastaların sıralanma kuralı değiştirilebilir.
 * Yeni bir kural eklemek için bu arayüzü uygulayan yeni sınıf yazmak yeterli
 * (OCP: mevcut kod değişmez).
 */
public interface OncelikStratejisi {

    /** Puan ne kadar yüksekse hasta o kadar önce çağrılır. */
    double skorHesapla(Hasta hasta, LocalDateTime simdi);

    String ad();
}