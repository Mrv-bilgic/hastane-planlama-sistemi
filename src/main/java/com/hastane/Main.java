package com.hastane;

import com.hastane.cizelge.AmeliyathaneCizelgeleyici;
import com.hastane.depo.AmeliyatDeposu;
import com.hastane.depo.AmeliyathaneDeposu;
import com.hastane.depo.DoktorDeposu;
import com.hastane.depo.HastaDeposu;
import com.hastane.oncelik.AcilTriyajStratejisi;
import com.hastane.oncelik.TriyajKuyrugu;
import com.hastane.olay.DosyaGunlukDinleyici;
import com.hastane.olay.KonsolBildirimDinleyici;
import com.hastane.olay.OlayYayinci;
import com.hastane.servis.HastaneServisi;
import com.hastane.ui.KonsolMenusu;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Scanner;

/** Uygulamanın giriş noktası: tüm nesneler burada oluşturulup birbirine bağlanır. */
public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Path veriKlasoru = Path.of("data");

        OlayYayinci yayinci = new OlayYayinci();
        yayinci.aboneEkle(new KonsolBildirimDinleyici());
        yayinci.aboneEkle(new DosyaGunlukDinleyici(veriKlasoru.resolve("olay_gunlugu.txt")));

        HastaneServisi servis = new HastaneServisi(
                new HastaDeposu(veriKlasoru.resolve("hastalar.txt")),
                new DoktorDeposu(veriKlasoru.resolve("doktorlar.txt")),
                new AmeliyathaneDeposu(veriKlasoru.resolve("salonlar.txt")),
                new AmeliyatDeposu(veriKlasoru.resolve("ameliyatlar.txt")),
                new TriyajKuyrugu(new AcilTriyajStratejisi()),
                AmeliyathaneCizelgeleyici.varsayilan(),
                yayinci,
                Clock.systemDefaultZone());

        new KonsolMenusu(servis, new Scanner(System.in, StandardCharsets.UTF_8)).baslat();
    }
}