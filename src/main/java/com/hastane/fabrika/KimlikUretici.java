package com.hastane.fabrika;

import com.hastane.model.Kimlikli;
import java.util.Collection;

/** "H001", "D002" gibi okunabilir kimlikler üretir; mevcut en büyük numaranın bir fazlasını verir. */
public final class KimlikUretici {

    private KimlikUretici() {
    }

    public static String sonraki(String onek, Collection<? extends Kimlikli> mevcutlar) {
        int enBuyuk = 0;
        for (Kimlikli k : mevcutlar) {
            String id = k.getId();
            if (id.startsWith(onek)) {
                try {
                    enBuyuk = Math.max(enBuyuk, Integer.parseInt(id.substring(onek.length())));
                } catch (NumberFormatException ignored) {
                    // biçim dışı kimlikler atlanır
                }
            }
        }
        return String.format("%s%03d", onek, enBuyuk + 1);
    }
}