package com.sd.jazz_testapp

import android.app.Application
import com.sdkit.jazz.client.integration.api.model.JazzTokenConfiguration
import com.sdkit.jazz.sdk.di.JazzSdk
import com.sdkit.jazz.sdk.di.JazzTokenConfigurationProvider
import com.sdkit.jazz.sdk.di.installJazzSdk
import com.sdkit.jazz.sdk.domain.dependencies.JazzCoreAnalyticsDependencies
import com.sdkit.jazz.sdk.domain.dependencies.JazzCoreLoggingDependencies
import com.sdkit.jazz.sdk.domain.dependencies.JazzLoggerFactory

class MainApplication : Application() {
    // https://public.repo.dp.s2b.tech/repo/public/repository/jazz-maven/com/sdkit/jazz/jazz-public-sdk/25.07.1.3/jazz-public-sdk-25.07.1.3.pom
    // https://office.dp.s2b.tech/repo/public/repository/jazz-maven/com/sdkit/jazz/jazz-public-bom/25.07.1.3/jazz-public-bom-25.07.1.3.pom

    override fun onCreate() {
        super.onCreate()

        // Устанавливаем необходимые зависимости для Jazz
        installJazzSdk(
            jazzConfig = JazzSdk.JazzConfig.Simple(
                JazzTokenConfigurationProvider.create {
                    JazzTokenConfiguration(
                        secretKey = "", // Получить ключ в https://developers.sber.ru
                        liveTimeDurationInSeconds = 180,
                        userId = "",
                    )
                },
            ),
            coreConfig = JazzSdk.CoreConfig(
                context = applicationContext,
                analyticsDependencies = object : JazzCoreAnalyticsDependencies {},
                loggingDependencies = JazzCoreLoggingDependencies(
                    jazzLogMode = JazzLoggerFactory.LogMode.LOG_ALWAYS
                ),
            ),
        )
    }
}