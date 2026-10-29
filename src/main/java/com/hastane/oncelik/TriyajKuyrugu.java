package com.hastane.oncelik;

import com.hastane.model.Hasta;
import com.hastane.model.HastaDurumu;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * ALGORİTMA: Öncelik kuyruğu (PriorityQueue / ikili yığın, heap).
 * Skor bekleme süresine bağlı olduğu için zamanla değişir; bu yüzden
 * kuyruk her sorguda o anki skorlarla yeniden kurulur: O(n log n).
 */
public class TriyajKuyrugu {

    private OncelikStratejisi strateji;

    public TriyajKuyrugu(OncelikStratejisi strateji) {
        setStrateji(strateji);
    }

    public void setStrateji(OncelikStratejisi strateji) {
        if (strateji == null) {
            throw new IllegalArgumentException("Strateji boş olamaz");
        }
        this.strateji = strateji;
    }

    public OncelikStratejisi getStrateji() {
        return strateji;
    }

    /** Bekleyen hastaları öncelik sırasına dizer (ilk eleman = ilk çağrılacak). */
    public List<Hasta> sirala(Collection<Hasta> hastalar, LocalDateTime simdi) {
        Comparator<Hasta> karsilastirici = Comparator
                .comparingDouble((Hasta h) -> strateji.skorHesapla(h, simdi)).reversed()
                .thenComparing(Hasta::getGelisZamani); // eşitlikte erken gelen önce

        PriorityQueue<Hasta> kuyruk = new PriorityQueue<>(karsilastirici);
        for (Hasta h : hastalar) {
            if (h.getDurum() == HastaDurumu.BEKLIYOR) {
                kuyruk.offer(h);
            }
        }
        List<Hasta> sirali = new ArrayList<>();
        while (!kuyruk.isEmpty()) {
            sirali.add(kuyruk.poll());
        }
        return sirali;
    }

    public Optional<Hasta> siradaki(Collection<Hasta> hastalar, LocalDateTime simdi) {
        List<Hasta> sirali = sirala(hastalar, simdi);
        return sirali.isEmpty() ? Optional.empty() : Optional.of(sirali.get(0));
    }

    public double skor(Hasta hasta, LocalDateTime simdi) {
        return strateji.skorHesapla(hasta, simdi);
    }
}