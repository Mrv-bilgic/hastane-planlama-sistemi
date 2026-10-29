package com.hastane.cizelge;

import com.hastane.model.Ameliyat;
import com.hastane.model.Ameliyathane;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * ALGORİTMA: Açgözlü (greedy) çakışmasız aralık çizelgeleme.
 *  1) Bekleyen ameliyatlar önce aciliyete, sonra süreye göre (uzun olan önce) sıralanır.
 *  2) Her ameliyat için tüm aktif salonlarda "en erken boş başlangıç" aranır.
 *     Aday saatler: gün başlangıcı + o salondaki / o doktordaki ameliyatların bitişleri.
 *  3) Salon çakışmaz (arada temizlik süresi var), doktor aynı anda iki ameliyatta olmaz,
 *     ameliyat mesai bitişini aşmaz. En erken uygun salon seçilir.
 *  4) Yer bulunamayanlar "planlanamayanlar" listesine düşer.
 */
public class AmeliyathaneCizelgeleyici {

    private final LocalTime gunBaslangici;
    private final LocalTime gunBitisi;
    private final int temizlikDakikasi;

    public AmeliyathaneCizelgeleyici(LocalTime gunBaslangici, LocalTime gunBitisi, int temizlikDakikasi) {
        if (!gunBaslangici.isBefore(gunBitisi) || temizlikDakikasi < 0) {
            throw new IllegalArgumentException("Geçersiz mesai ayarı");
        }
        this.gunBaslangici = gunBaslangici;
        this.gunBitisi = gunBitisi;
        this.temizlikDakikasi = temizlikDakikasi;
    }

    public static AmeliyathaneCizelgeleyici varsayilan() {
        return new AmeliyathaneCizelgeleyici(LocalTime.of(8, 0), LocalTime.of(18, 0), 30);
    }

    /**
     * @param gun         planlanacak gün
     * @param bekleyenler henüz planlanmamış ameliyatlar
     * @param salonlar    tüm salonlar (pasif olanlar atlanır)
     * @param mevcutPlan  o gün zaten planlanmış ameliyatlar (dokunulmaz)
     */
    public CizelgeSonucu planla(LocalDate gun, List<Ameliyat> bekleyenler,
                                List<Ameliyathane> salonlar, List<Ameliyat> mevcutPlan) {
        List<Ameliyat> plan = new ArrayList<>(mevcutPlan);
        List<Ameliyat> planlananlar = new ArrayList<>();
        List<Ameliyat> planlanamayanlar = new ArrayList<>();

        List<Ameliyat> sirali = new ArrayList<>(bekleyenler);
        sirali.sort(Comparator
                .comparingInt((Ameliyat a) -> a.getOncelik().getPuan()).reversed()
                .thenComparing(Comparator.comparingInt(Ameliyat::getSureDakika).reversed())
                .thenComparing(Ameliyat::getId));

        LocalDateTime gunBas = gun.atTime(gunBaslangici);
        LocalDateTime gunSon = gun.atTime(gunBitisi);

        for (Ameliyat a : sirali) {
            LocalDateTime enIyiZaman = null;
            Ameliyathane enIyiSalon = null;

            for (Ameliyathane salon : salonlar) {
                if (!salon.isAktif()) {
                    continue;
                }
                LocalDateTime t = enErkenBosZaman(a, salon.getId(), plan, gunBas, gunSon);
                if (t != null && (enIyiZaman == null || t.isBefore(enIyiZaman))) {
                    enIyiZaman = t;
                    enIyiSalon = salon;
                }
            }

            if (enIyiSalon != null) {
                a.planla(enIyiSalon.getId(), enIyiZaman);
                plan.add(a);
                planlananlar.add(a);
            } else {
                planlanamayanlar.add(a);
            }
        }
        return new CizelgeSonucu(planlananlar, planlanamayanlar);
    }

    private LocalDateTime enErkenBosZaman(Ameliyat a, String salonId, List<Ameliyat> plan,
                                          LocalDateTime gunBas, LocalDateTime gunSon) {
        TreeSet<LocalDateTime> adaylar = new TreeSet<>();
        adaylar.add(gunBas);
        for (Ameliyat p : plan) {
            if (p.getSalonId().equals(salonId)) {
                adaylar.add(p.bitis().plusMinutes(temizlikDakikasi));
            }
            if (p.getDoktorId().equals(a.getDoktorId())) {
                adaylar.add(p.bitis());
            }
        }
        for (LocalDateTime bas : adaylar) {
            if (bas.isBefore(gunBas)) {
                continue;
            }
            LocalDateTime son = bas.plusMinutes(a.getSureDakika());
            if (son.isAfter(gunSon)) {
                return null; // adaylar sıralı: sonrakiler de sığmaz
            }
            if (uygunMu(a, salonId, bas, son, plan)) {
                return bas;
            }
        }
        return null;
    }

    private boolean uygunMu(Ameliyat a, String salonId, LocalDateTime bas, LocalDateTime son,
                            List<Ameliyat> plan) {
        for (Ameliyat p : plan) {
            boolean ayniSalon = p.getSalonId().equals(salonId);
            boolean ayniDoktor = p.getDoktorId().equals(a.getDoktorId());
            if (ayniSalon && cakisir(bas, son.plusMinutes(temizlikDakikasi),
                    p.getBaslangic(), p.bitis().plusMinutes(temizlikDakikasi))) {
                return false;
            }
            if (ayniDoktor && cakisir(bas, son, p.getBaslangic(), p.bitis())) {
                return false;
            }
        }
        return true;
    }

    /** [b1, s1) ile [b2, s2) aralıkları kesişiyor mu? */
    static boolean cakisir(LocalDateTime b1, LocalDateTime s1, LocalDateTime b2, LocalDateTime s2) {
        return b1.isBefore(s2) && b2.isBefore(s1);
    }
}