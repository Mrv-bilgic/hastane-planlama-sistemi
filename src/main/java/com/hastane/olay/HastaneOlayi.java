package com.hastane.olay;

import java.time.LocalDateTime;

public record HastaneOlayi(OlayTuru tur, String mesaj, LocalDateTime zaman) {
}