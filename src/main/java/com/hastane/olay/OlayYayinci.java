package com.hastane.olay;

import java.util.ArrayList;
import java.util.List;

/** Observer kalıbındaki "Subject": dinleyicileri tutar ve olayı hepsine iletir. */
public class OlayYayinci {

    private final List<OlayDinleyici> dinleyiciler = new ArrayList<>();

    public void aboneEkle(OlayDinleyici d) {
        dinleyiciler.add(d);
    }

    public void aboneCikar(OlayDinleyici d) {
        dinleyiciler.remove(d);
    }

    public void yayinla(HastaneOlayi olay) {
        for (OlayDinleyici d : dinleyiciler) {
            d.olayOlustu(olay);
        }
    }
}