package com.sd.jazz_testapp

import android.app.Application
import com.sdkit.jazz.sdk.di.DefaultJazzSdkPlatformDependencies
import com.sdkit.jazz.sdk.di.JazzSdk
import com.sdkit.jazz.sdk.di.installJazzPublicSdk
import com.sdkit.jazz.sdk.domain.dependencies.JazzCoreAnalyticsDependencies
import com.sdkit.jazz.sdk.domain.dependencies.JazzCoreLoggingDependencies
import com.sdkit.jazz.sdk.domain.dependencies.JazzLoggerFactory
import com.sdkit.jazz.sdk.domain.dependencies.JazzSdkFeatureFlags
import ru.sberdevices.vc.platform.api.di.JazzPlatformDependencies
import ru.sberdevices.vc.platform.api.domain.dependencies.VideoCallsFeatureFlags

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Устанавливаем необходимые зависимости для Jazz
        installJazzPublicSdk(
            jazzConfig = JazzSdk.JazzConfig(
                // Здесь устанавливаем платформенные зависимости Jazz
                // Обязательно нужно пробросить SECRET_KEY из смартмаркета
                platformDependencies = object : JazzPlatformDependencies by DefaultJazzSdkPlatformDependencies() {
                    override val videoCallsFeatureFlags: VideoCallsFeatureFlags = JazzSdkFeatureFlags()
                }
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