package es.politictest.app

import android.app.Application
import com.google.android.gms.ads.MobileAds

class PoliticTestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicialización en segundo plano de MobileAds
        MobileAds.initialize(this) {}
    }
}
