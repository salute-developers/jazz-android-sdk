package com.sd.jazz_testapp

import android.app.Application
import com.sdkit.jazz.sdk.di.DefaultJazzSdkPlatformDependencies
import com.sdkit.jazz.sdk.di.installJazzPublicSdk
import com.sdkit.jazz.sdk.domain.dependencies.JazzCoreAnalyticsDependencies
import com.sdkit.jazz.sdk.domain.dependencies.JazzCoreConfigDependencies
import com.sdkit.jazz.sdk.domain.dependencies.JazzCoreDependencies
import com.sdkit.jazz.sdk.domain.dependencies.JazzCoreLoggingDependencies
import com.sdkit.jazz.sdk.domain.dependencies.JazzLoggerFactory
import com.sdkit.jazz.sdk.domain.dependencies.JazzSdkFeatureFlags

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Устанавливаем необходимые зависимости для Jazz
        installJazzPublicSdk(
            corePlatformDependencies = JazzCoreDependencies(applicationContext),
            // Здесь устанавливаем платформенные зависимости Jazz
            // Обязательно нужно пробросить SECRET_KEY из смартмаркета
            jazzPlatformDependencies = DefaultJazzSdkPlatformDependencies(),
            // (Опционально) Если хотите увидеть логи sdk, но только в дебаг сборках -  LogMode.LOG_DEBUG_ONLY
            coreLoggingDependencies = JazzCoreLoggingDependencies(
                jazzLogMode = JazzLoggerFactory.LogMode.LOG_ALWAYS
            ),
            // (Опционально) Если нужно включить дополнительные Фичи Jazz SDK
            coreConfigDependencies = JazzCoreConfigDependencies(
                featureFlags = JazzSdkFeatureFlags(
                    // Тут подключить нужные флаги или выключить что-то
                    isChatEnabled = false,
                    isConferenceTransferByNetworkEnabled = true,
                    isRoomCheckEnabled = true,
                )
            ),
            coreAnalyticsDependencies = object : JazzCoreAnalyticsDependencies {},
        )
    }
}