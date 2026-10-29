package com.hastane.olay;

/** Kritik olayları konsolda uyarı olarak gösterir. */
public class KonsolBildirimDinleyici implements OlayDinleyici {

    @Override
    public void olayOlustu(HastaneOlayi olay) {
        if (olay.tur() == OlayTuru.KRITIK_HASTA || olay.tur() == OlayTuru.SALON_ARIZA) {
            System.out.println("  !!! UYARI: " + olay.mesaj());
        }
    }
}