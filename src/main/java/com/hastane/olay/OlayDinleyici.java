package com.hastane.olay;

/** OBSERVER tasarım kalıbı: olaylardan haberdar olmak isteyen her sınıf bunu uygular. */
public interface OlayDinleyici {
    void olayOlustu(HastaneOlayi olay);
}