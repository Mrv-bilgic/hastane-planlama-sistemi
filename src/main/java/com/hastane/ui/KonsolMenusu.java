package com.hastane.ui;

import com.hastane.cizelge.CizelgeSonucu;
import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Ameliyat;
import com.hastane.model.Ameliyathane;
import com.hastane.model.Doktor;
import com.hastane.model.Hasta;
import com.hastane.model.Kisi;
import com.hastane.oncelik.AcilTriyajStratejisi;
import com.hastane.oncelik.GelisSirasiStratejisi;
import com.hastane.servis.HastaneServisi;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** Yalnızca kullanıcıyla konuşur (SRP); iş kuralları HastaneServisi'nde. */
public class KonsolMenusu {

    private final HastaneServisi servis;
    private final Scanner girdi;

    public KonsolMenusu(HastaneServisi servis, Scanner girdi) {
        this.servis = servis;
        this.girdi = girdi;
    }

    public void baslat() {
        System.out.println("=========================================");
        System.out.println("   HASTANE PLANLAMA SİSTEMİ");
        System.out.println("=========================================");
        boolean devam = true;
        while (devam) {
            menuYazdir();
            String secim = oku("Seçiminiz");
            try {
                devam = isle(secim);
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("  Hata: " + e.getMessage());
            }
            if (devam) {
                oku("\n(Devam etmek için Enter'a basın)");
            }
        }
        System.out.println("Veriler kaydedildi. Güle güle!");
    }

    private void menuYazdir() {
        System.out.println();
        System.out.println("--- HASTA / ACİL ---");
        System.out.println(" 1) Hasta kaydet");
        System.out.println(" 2) Bekleyen hastaları öncelik sırasıyla listele");
        System.out.println(" 3) Doktor sıradaki hastayı çağırsın");
        System.out.println(" 4) Muayeneyi bitir (taburcu)");
        System.out.println(" 5) Hastanın aciliyetini güncelle");
        System.out.println(" 6) Önceliklendirme stratejisini değiştir");
        System.out.println("--- PERSONEL / SALON ---");
        System.out.println(" 7) Doktor ekle");
        System.out.println(" 8) Ameliyathane ekle");
        System.out.println(" 9) Tüm kişileri listele");
        System.out.println("--- AMELİYATHANE ÇİZELGESİ ---");
        System.out.println("10) Ameliyat talebi ekle");
        System.out.println("11) Ameliyatları planla");
        System.out.println("12) Günlük ameliyat planını göster");
        System.out.println("13) Salon arıza bildir (plan güncellenir)");
        System.out.println("14) Salonu tekrar aktif et");
        System.out.println("--- DİĞER ---");
        System.out.println("15) Örnek veri yükle");
        System.out.println(" 0) Çıkış");
    }

    private boolean isle(String secim) {
        switch (secim) {
            case "1" -> hastaKaydet();
            case "2" -> bekleyenleriListele();
            case "3" -> hastaCagir();
            case "4" -> muayeneBitir();
            case "5" -> aciliyetGuncelle();
            case "6" -> stratejiDegistir();
            case "7" -> doktorEkle();
            case "8" -> salonEkle();
            case "9" -> kisileriListele();
            case "10" -> ameliyatTalebi();
            case "11" -> planla();
            case "12" -> planGoster();
            case "13" -> arizaBildir();
            case "14" -> salonAktifEt();
            case "15" -> ornekVeriYukle();
            case "0" -> { return false; }
            default -> System.out.println("  Geçersiz seçim.");
        }
        return true;
    }

    // ---------- işlemler ----------

    private void hastaKaydet() {
        String ad = oku("Ad");
        String soyad = oku("Soyad");
        int yas = sayiOku("Yaş");
        String sikayet = oku("Şikâyet");
        AciliyetSeviyesi a = aciliyetOku();
        Hasta h = servis.hastaKaydet(ad, soyad, yas, sikayet, a);
        System.out.println("  Kaydedildi: " + h.ozet());
    }

    private void bekleyenleriListele() {
        List<Hasta> liste = servis.bekleyenHastalar();
        System.out.println("  Strateji: " + servis.aktifStrateji().ad());
        if (liste.isEmpty()) {
            System.out.println("  Bekleyen hasta yok.");
            return;
        }
        int sira = 1;
        for (Hasta h : liste) {
            System.out.printf("  %2d. skor=%6.1f  %s%n", sira++, servis.skor(h), h.ozet());
        }
    }

    private void hastaCagir() {
        doktorlariYazdir();
        String id = oku("Doktor kimliği (ör. D001)");
        Optional<Hasta> h = servis.siradakiHastayiCagir(id);
        System.out.println(h.map(x -> "  Çağrılan hasta: " + x.ozet())
                .orElse("  Bekleyen hasta yok."));
    }

    private void muayeneBitir() {
        doktorlariYazdir();
        Hasta h = servis.muayeneyiBitir(oku("Doktor kimliği"));
        System.out.println("  Taburcu edildi: " + h.getTamAd());
    }

    private void aciliyetGuncelle() {
        bekleyenleriListele();
        String id = oku("Hasta kimliği (ör. H001)");
        servis.aciliyetGuncelle(id, aciliyetOku());
        System.out.println("  Güncellendi.");
    }

    private void stratejiDegistir() {
        System.out.println("  1) Acil triyaj   2) Geliş sırası");
        String s = oku("Seçim");
        servis.stratejiDegistir("2".equals(s) ? new GelisSirasiStratejisi() : new AcilTriyajStratejisi());
        System.out.println("  Aktif strateji: " + servis.aktifStrateji().ad());
    }

    private void doktorEkle() {
        Doktor d = servis.doktorEkle(oku("Ad"), oku("Soyad"), oku("Branş"));
        System.out.println("  Eklendi: " + d.ozet());
    }

    private void salonEkle() {
        Ameliyathane s = servis.salonEkle(oku("Salon adı"));
        System.out.println("  Eklendi: " + s);
    }

    private void kisileriListele() {
        // Çok biçimlilik: liste Kisi tipinde, ozet() her nesnenin kendi sınıfındaki sürümüyle çalışır.
        for (Kisi k : servis.tumKisiler()) {
            System.out.println("  " + k.ozet());
        }
        for (Ameliyathane s : servis.salonlar()) {
            System.out.println("  " + s);
        }
    }

    private void ameliyatTalebi() {
        String hastaId = oku("Hasta kimliği");
        doktorlariYazdir();
        String doktorId = oku("Doktor kimliği");
        String islem = oku("İşlem adı");
        int sure = sayiOku("Süre (dakika)");
        AciliyetSeviyesi a = aciliyetOku();
        Ameliyat am = servis.ameliyatTalebiEkle(hastaId, doktorId, islem, sure, a);
        System.out.println("  Talep eklendi: " + am);
    }

    private void planla() {
        LocalDate gun = gunOku();
        CizelgeSonucu s = servis.ameliyatlariPlanla(gun);
        sonucYazdir(s);
    }

    private void planGoster() {
        LocalDate gun = gunOku();
        List<Ameliyat> plan = servis.gunlukPlan(gun);
        if (plan.isEmpty()) {
            System.out.println("  Bu gün için plan yok.");
        }
        plan.forEach(a -> System.out.println("  " + a));
        List<Ameliyat> bekleyen = servis.planlanmamisAmeliyatlar();
        if (!bekleyen.isEmpty()) {
            System.out.println("  Henüz planlanmamış:");
            bekleyen.forEach(a -> System.out.println("    " + a));
        }
    }

    private void arizaBildir() {
        servis.salonlar().forEach(s -> System.out.println("  " + s));
        String id = oku("Arızalı salon kimliği (ör. S001)");
        CizelgeSonucu s = servis.salonArizaBildir(id, gunOku());
        sonucYazdir(s);
    }

    private void salonAktifEt() {
        servis.salonlar().forEach(s -> System.out.println("  " + s));
        servis.salonuAktifEt(oku("Salon kimliği"));
        System.out.println("  Salon aktif.");
    }

    private void ornekVeriYukle() {
        if (servis.veriVarMi()) {
            System.out.println("  Zaten kayıtlı veri var; örnek veri yüklenmedi.");
            return;
        }
        Doktor d1 = servis.doktorEkle("Ayşe", "Kaya", "Genel Cerrahi");
        Doktor d2 = servis.doktorEkle("Mehmet", "Demir", "Ortopedi");
        servis.doktorEkle("Zeynep", "Aydın", "Acil Tıp");
        servis.salonEkle("Ameliyathane 1");
        servis.salonEkle("Ameliyathane 2");
        Hasta h1 = servis.hastaKaydet("Ali", "Yılmaz", 72, "Göğüs ağrısı", AciliyetSeviyesi.KIRMIZI);
        Hasta h2 = servis.hastaKaydet("Elif", "Şahin", 28, "Bilek burkulması", AciliyetSeviyesi.YESIL);
        Hasta h3 = servis.hastaKaydet("Can", "Öztürk", 4, "Yüksek ateş", AciliyetSeviyesi.SARI);
        Hasta h4 = servis.hastaKaydet("Fatma", "Çelik", 45, "Karın ağrısı", AciliyetSeviyesi.SARI);
        servis.ameliyatTalebiEkle(h4.getId(), d1.getId(), "Apendektomi", 90, AciliyetSeviyesi.KIRMIZI);
        servis.ameliyatTalebiEkle(h2.getId(), d2.getId(), "Bilek tespiti", 60, AciliyetSeviyesi.YESIL);
        servis.ameliyatTalebiEkle(h1.getId(), d1.getId(), "Kolesistektomi", 120, AciliyetSeviyesi.SARI);
        servis.ameliyatTalebiEkle(h3.getId(), d2.getId(), "Kırık redüksiyonu", 45, AciliyetSeviyesi.SARI);
        System.out.println("  Örnek veri yüklendi: 3 doktor, 2 salon, 4 hasta, 4 ameliyat talebi.");
    }

    // ---------- yardımcılar ----------

    private void sonucYazdir(CizelgeSonucu s) {
        System.out.println("  Planlanan (" + s.planlananlar().size() + "):");
        s.planlananlar().stream()
                .sorted(Comparator.comparing(Ameliyat::getSalonId)
                        .thenComparing(Ameliyat::getBaslangic))
                .forEach(a -> System.out.println("    " + a));
        if (!s.planlanamayanlar().isEmpty()) {
            System.out.println("  Gün içine sığmayan (" + s.planlanamayanlar().size() + "):");
            s.planlanamayanlar().forEach(a -> System.out.println("    " + a));
        }
    }

    private void doktorlariYazdir() {
        servis.doktorlar().forEach(d -> System.out.println("  " + d.ozet()));
    }

    private String oku(String etiket) {
        System.out.print(etiket + ": ");
        if (!girdi.hasNextLine()) {
            return "0";
        }
        return girdi.nextLine().trim();
    }

    private int sayiOku(String etiket) {
        while (true) {
            try {
                return Integer.parseInt(oku(etiket));
            } catch (NumberFormatException e) {
                System.out.println("  Lütfen bir sayı girin.");
            }
        }
    }

    private AciliyetSeviyesi aciliyetOku() {
        System.out.println("  3) Kırmızı  2) Sarı  1) Yeşil");
        return AciliyetSeviyesi.puandan(sayiOku("Aciliyet"));
    }

    private LocalDate gunOku() {
        String s = oku("Gün (YYYY-AA-GG, boş bırakırsanız yarın)");
        if (s.isBlank()) {
            return LocalDate.now().plusDays(1);
        }
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Tarih biçimi hatalı: " + s);
        }
    }
}