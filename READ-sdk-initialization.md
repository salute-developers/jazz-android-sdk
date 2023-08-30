## Инициализация SDK
### Шаг 1. В Application на onCreate прописываем installJazzPublicSdk():

```kotlin
   class MainApplication : Application() {

   override fun onCreate() {
      super.onCreate()
      installJazzPublicSdk()
   }
}
```

<details><summary>(Дополнительно) Расширенные настройки installJazzPublicSdk()</summary>

  ```kotlin
installJazzPublicSdk(
   // (Опционально) Если хотите увидеть логи sdk, но только в дебаг сборках -  LogMode.LOG_DEBUG_ONLY
   coreLoggingDependencies = object : CoreLoggingDependencies {
      override val logMode: LoggerFactory.LogMode = LoggerFactory.LogMode.LOG_DEBUG_ONLY
   },
   // (Опционально) Если нужно включить дополнительные Фичи Jazz SDK
   coreConfigDependencies = object : CoreConfigDependencies {
      override val featureFlagManager: FeatureFlagManager
         get() = FeatureFlagManager.fromList(
            object : VideoCallsFeatureFlags {
               // Тут подключить нужные флаги или выключить что-то
            }
         )
   },
   coreAnalyticsDependencies = object : CoreAnalyticsDependencies {
      override val coreAnalytics: CoreAnalytics
         get() = object : CoreAnalytics {
            override fun logError(error: Throwable) {}
            override fun logEvent(name: String, events: List<Analytics.EventParam>) {}
            override fun logMessage(message: String) {}
            override fun setUp(userId: String, params: Map<String, String>) {}
         }
   },
   // (Опционально) Если нужно сконфигурировать Jazz SDK под свои нужды
   jazzPlatformDependencies = object : JazzPlatformDependencies by DefaultJazzSdkPlatformDependencies() {
      // К примеру если нужно установить определенный хост на который будут подключаться пользователи
      override val testStandDomainUrlResolver: TestStandDomainUrlResolver
         get() = object : TestStandDomainUrlResolver {
            override fun resolve(stand: TestStand): String {
               return "https://google.com"
            }
         }
   }
)
  ```

</details>

### Шаг 2. Если переопределили WorkerFactory для WorkManager в своем приложении - нужно поддержать JazzWorkerFactory

<details><summary>Как поддержать JazzWorkerFactory</summary>

#### Добавьте фабрику воркеров SDK JazzIntegrationClientApi.jazzWorkerFactory в свою конфигурацию WorkManager. Это можно сделать следующим образом:

```kotlin
   class MyApplication() : Application(), Configuration.Provider {

   override fun getWorkManagerConfiguration(): Configuration {
      val factory = DelegatingWorkerFactory().apply {
         addFactory(appWorkerFactory) // ваша фабрика
         addFactory(getJazzIntegrationClientApi().jazzWorkerFactory) // фабрика JazzSDK
      }
      return Configuration.Builder()
         .setWorkerFactory(factory)
         // здесь могут быть другие настройки
         .build()
   }
}
 ```

</details>

[Сценарии использования](READ-sdk-scenarios.md)