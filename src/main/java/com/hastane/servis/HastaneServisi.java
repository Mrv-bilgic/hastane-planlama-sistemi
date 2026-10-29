package com.hastane.servis;

import com.hastane.cizelge.AmeliyathaneCizelgeleyici;
import com.hastane.cizelge.CizelgeSonucu;
import com.hastane.depo.Depo;
import com.hastane.fabrika.KimlikUretici;
import com.hastane.fabrika.KisiFabrikasi;
import com.hastane.model.AciliyetSeviyesi;
import com.hastane.model.Ameliyat;
import com.hastane.model.Ameliyathane;
import com.hastane.model.Doktor;
import com.hastane.model.Hasta;
import com.hastane.model.Kisi;
import com.hastane.oncelik.OncelikStratejisi;
import com.hastane.oncelik.TriyajKuyrugu;
import com.hastane.olay.HastaneOlayi;
import com.hastane.olay.OlayTuru;
import com.hastane.olay.OlayYayinci;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Uygulamanın iş kurallarını yöneten servis.
 * DIP: somut dosya sınıflarını değil Depo arayüzünü, somut stratejiyi değil
 * TriyajKuyrugu/OncelikStratejisi soyutlamasını alır. Bağımlılıklar Main'de verilir.
 */
public class HastaneServisi {

    private final Depo<Hasta> hastaDeposu;
    private final Depo<Doktor> doktorDeposu;
    private final Depo<Ameliyathane> salonDeposu;
    private final Depo<Ameliyat> ameliyatDeposu;
    private final TriyajKuyrugu kuyruk;
    private final AmeliyathaneCizelgeleyici cizelgeleyici;
    private final OlayYayinci yayinci;
    private final KisiFabrikasi fabrika;
    private final Clock saat;

    public HastaneServisi(Depo<Hasta> hastaDeposu, Depo<Doktor> doktorDeposu,
                          Depo<Ameliyathane> salonDeposu, Depo<Ameliyat> ameliyatDeposu,
                          TriyajKuyrugu kuyruk, AmeliyathaneCizelgeleyici cizelgeleyici,
                          OlayYayinci yayinci, Clock saat) {
        this.hastaDeposu = hastaDeposu;
        this.doktorDeposu = doktorDeposu;
        this.salonDeposu = salonDeposu;
        this.ameliyatDeposu = ameliyatDeposu;
        this.kuyruk = kuyruk;
        this.cizelgeleyici = cizelgeleyici;
        this.yayinci = yayinci;
        this.saat = saat;
        this.fabrika = new KisiFabrikasi(saat);
    }

    private LocalDateTime simdi() {
        return LocalDateTime.now(saat);
    }

    private void olay(OlayTuru tur, String mesaj) {
        yayinci.yayinla(new HastaneOlayi(tur, mesaj, simdi()));
    }

    // ---------------- Kayıt işlemleri ----------------

    public Hasta hastaKaydet(String ad, String soyad, int yas, String sikayet, AciliyetSeviyesi aciliyet) {
        String id = KimlikUretici.sonraki("H", hastaDeposu.tumu());
        Hasta h = fabrika.yeniHasta(id, ad, soyad, yas, sikayet, aciliyet);
        hastaDeposu.kaydet(h);
        if (aciliyet == AciliyetSeviyesi.KIRMIZI) {
            olay(OlayTuru.KRITIK_HASTA, "Kırmızı alan hastası geldi: " + h.getTamAd() + " (" + sikayet + ")");
        }
        return h;
    }

    public Doktor doktorEkle(String ad, String soyad, String brans) {
        String id = KimlikUretici.sonraki("D", doktorDeposu.tumu());
        Doktor d = fabrika.yeniDoktor(id, ad, soyad, brans);
        doktorDeposu.kaydet(d);
        return d;
    }

    public Ameliyathane salonEkle(String ad) {
        String id = KimlikUretici.sonraki("S", salonDeposu.tumu());
        Ameliyathane s = new Ameliyathane(id, ad, true);
        salonDeposu.kaydet(s);
        return s;
    }

    // ---------------- Triyaj / muayene ----------------

    public List<Hasta> bekleyenHastalar() {
        return kuyruk.sirala(hastaDeposu.tumu(), simdi());
    }

    public double skor(Hasta h) {
        return kuyruk.skor(h, simdi());
    }

    /** Müsait doktor, kuyruktaki en öncelikli hastayı çağırır. */
    public Optional<Hasta> siradakiHastayiCagir(String doktorId) {
        Doktor d = doktorBul(doktorId);
        if (!d.isMusait()) {
            throw new IllegalStateException(d.getTamAd() + " zaten bir hastayla ilgileniyor");
        }
        Optional<Hasta> siradaki = kuyruk.siradaki(hastaDeposu.tumu(), simdi());
        siradaki.ifPresent(h -> {
            h.muayeneyeAl(d.getId());
            d.hastaAta(h.getId());
            hastaDeposu.kaydet(h);
            doktorDeposu.kaydet(d);
            olay(OlayTuru.HASTA_CAGRILDI, h.getTamAd() + " -> " + d.getTamAd());
        });
        return siradaki;
    }

    public Hasta muayeneyiBitir(String doktorId) {
        Doktor d = doktorBul(doktorId);
        if (d.getMevcutHastaId() == null) {
            throw new IllegalStateException(d.getTamAd() + " şu an muayene yapmıyor");
        }
        Hasta h = hastaBul(d.getMevcutHastaId());
        h.taburcuEt();
        d.serbestBirak();
        hastaDeposu.kaydet(h);
        doktorDeposu.kaydet(d);
        return h;
    }

    public void aciliyetGuncelle(String hastaId, AciliyetSeviyesi yeni) {
        Hasta h = hastaBul(hastaId);
        h.aciliyetGuncelle(yeni);
        hastaDeposu.kaydet(h);
        if (yeni == AciliyetSeviyesi.KIRMIZI) {
            olay(OlayTuru.KRITIK_HASTA, h.getTamAd() + " kırmızıya yükseltildi");
        }
    }

    public void stratejiDegistir(OncelikStratejisi strateji) {
        kuyruk.setStrateji(strateji);
    }

    public OncelikStratejisi aktifStrateji() {
        return kuyruk.getStrateji();
    }

    // ---------------- Ameliyathane ----------------

    public Ameliyat ameliyatTalebiEkle(String hastaId, String doktorId, String islem,
                                       int sureDk, AciliyetSeviyesi oncelik) {
        hastaBul(hastaId);
        doktorBul(doktorId);
        String id = KimlikUretici.sonraki("A", ameliyatDeposu.tumu());
        Ameliyat a = new Ameliyat(id, hastaId, doktorId, islem, sureDk, oncelik);
        ameliyatDeposu.kaydet(a);
        return a;
    }

    /** Bekleyen tüm ameliyat taleplerini verilen güne yerleştirir. */
    public CizelgeSonucu ameliyatlariPlanla(LocalDate gun) {
        List<Ameliyat> bekleyenler = new ArrayList<>();
        List<Ameliyat> mevcut = new ArrayList<>();
        for (Ameliyat a : ameliyatDeposu.tumu()) {
            if (!a.planlandiMi()) {
                bekleyenler.add(a);
            } else if (a.getBaslangic().toLocalDate().equals(gun)) {
                mevcut.add(a);
            }
        }
        CizelgeSonucu sonuc = cizelgeleyici.planla(gun, bekleyenler, salonDeposu.tumu(), mevcut);
        for (Ameliyat a : sonuc.planlananlar()) {
            ameliyatDeposu.kaydet(a);
        }
        if (!sonuc.planlananlar().isEmpty()) {
            olay(OlayTuru.AMELIYAT_PLANLANDI, gun + " için " + sonuc.planlananlar().size() + " ameliyat planlandı");
        }
        return sonuc;
    }

    /**
     * Salon arızalanınca o günün planı iptal edilir ve tüm ameliyatlar kalan
     * salonlara ÖNCELİK SIRASIYLA yeniden yerleştirilir (acil olan öne alınır).
     */
    public CizelgeSonucu salonArizaBildir(String salonId, LocalDate gun) {
        Ameliyathane s = salonBul(salonId);
        s.setAktif(false);
        salonDeposu.kaydet(s);
        olay(OlayTuru.SALON_ARIZA, s.getAd() + " devre dışı, plan güncelleniyor");

        for (Ameliyat a : ameliyatDeposu.tumu()) {
            if (a.planlandiMi() && a.getBaslangic().toLocalDate().equals(gun)) {
                a.planiIptalEt();
                ameliyatDeposu.kaydet(a);
            }
        }
        CizelgeSonucu sonuc = ameliyatlariPlanla(gun);
        olay(OlayTuru.PLAN_GUNCELLENDI, "Yeniden planlanan: " + sonuc.planlananlar().size()
                + ", sığmayan: " + sonuc.planlanamayanlar().size());
        return sonuc;
    }

    public void salonuAktifEt(String salonId) {
        Ameliyathane s = salonBul(salonId);
        s.setAktif(true);
        salonDeposu.kaydet(s);
    }

    public List<Ameliyat> gunlukPlan(LocalDate gun) {
        List<Ameliyat> liste = new ArrayList<>();
        for (Ameliyat a : ameliyatDeposu.tumu()) {
            if (a.planlandiMi() && a.getBaslangic().toLocalDate().equals(gun)) {
                liste.add(a);
            }
        }
        liste.sort(Comparator.comparing(Ameliyat::getSalonId).thenComparing(Ameliyat::getBaslangic));
        return liste;
    }

    public List<Ameliyat> planlanmamisAmeliyatlar() {
        return ameliyatDeposu.tumu().stream().filter(a -> !a.planlandiMi()).toList();
    }

    // ---------------- Listeleme / arama ----------------

    /** Hasta ve doktorlar tek listede: üst tip (Kisi) üzerinden çok biçimli kullanım. */
    public List<Kisi> tumKisiler() {
        List<Kisi> liste = new ArrayList<>(doktorDeposu.tumu());
        liste.addAll(hastaDeposu.tumu());
        return liste;
    }

    public List<Doktor> doktorlar() { return doktorDeposu.tumu(); }
    public List<Ameliyathane> salonlar() { return salonDeposu.tumu(); }
    public List<Hasta> hastalar() { return hastaDeposu.tumu(); }
    public boolean veriVarMi() { return doktorDeposu.say() > 0 || hastaDeposu.say() > 0; }

    public Hasta hastaBul(String id) {
        return hastaDeposu.bul(id).orElseThrow(() -> new IllegalArgumentException("Hasta bulunamadı: " + id));
    }

    public Doktor doktorBul(String id) {
        return doktorDeposu.bul(id).orElseThrow(() -> new IllegalArgumentException("Doktor bulunamadı: " + id));
    }

    public Ameliyathane salonBul(String id) {
        return salonDeposu.bul(id).orElseThrow(() -> new IllegalArgumentException("Salon bulunamadı: " + id));
    }
}