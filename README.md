[Основную документацию можно найти тут](https://clck.ru/35aWZB)

[Чтобы создать комнату, для начала получите ключ авторизации тут](https://clck.ru/35aWZw)

[Инициализация](READ-sdk-initialization.md)

[Сценарии использования](READ-sdk-scenarios.md)

[Лицензия на использование](https://clck.ru/35F8h3)

## Как подключить JAZZ SDK

Версии и настройки ниже совпадают с этим тестовым приложением: SDK `25.07.1.4`, `compileSdk` / `targetSdk` 36, `minSdk` 26, Java 17.

### 1. Репозиторий в settings.gradle

Репозиторий Jazz ставим выше `google()` и `mavenCentral()`. Так подтягиваются нужные зависимости, и не требуется `excludeGroup` для `com.facebook.react`. `jcenter()` оставляем: часть транзитивных артефактов SDK по-прежнему лежит там.

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url "https://public.repo.dp.s2b.tech/repo/public/repository/jazz-maven/"
        }

        google() {
            content { excludeGroup("com.facebook.react") }
        }
        mavenCentral() {
            content { excludeGroup("com.facebook.react") }
        }
        jcenter() {
            content { excludeGroup("com.facebook.react") }
        }
    }
}
```

### 2. Модуль app, build.gradle

Плагины:

```groovy
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}
```

`viewBinding` нужен примеру с `ActivityMainBinding`. `packaging` с `pickFirsts` нужен, чтобы сборка не падала на одинаковых `.so` и версии coroutines из разных зависимостей. В AGP 8 блок называется `packaging`, не `packagingOptions`.

```groovy
android {
    compileSdk 36

    defaultConfig {
        minSdk 26
        targetSdk 36
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = '17'
    }

    buildFeatures {
        viewBinding true
    }
    packaging {
        jniLibs {
            pickFirsts += ['**/*.so']
        }
        resources {
            pickFirsts += ['META-INF/kotlinx_coroutines_core.version']
        }
    }
}
```

Зависимости. Версии `jazz-public-sdk` и `jazz-public-bom` должны совпадать. `lifecycle-runtime-ktx` нужен для `lifecycleScope`, из которого вызываются `createConference` и `joinConference`.

```groovy
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.8.7'

implementation("com.sdkit.jazz:jazz-public-sdk:25.07.1.4") {
    exclude(group: "ru.sberbank.sdakit.sbercast")
}
implementation(platform("com.sdkit.jazz:jazz-public-bom:25.07.1.4"))
```

### 3. Инициализация в Application

В `AndroidManifest.xml` у `<application>` должен быть указан класс, в котором вызывается `installJazzSdk`. Ключ берётся в [личном кабинете](https://clck.ru/35aWZw).

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        installJazzSdk(
            jazzConfig = JazzSdk.JazzConfig.Simple(
                JazzTokenConfigurationProvider.create {
                    JazzTokenConfiguration(
                        secretKey = "",
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
```

`joinConference` и `createConference` — долгие `suspend`-функции: они не возвращаются, пока конференция не закончится, и сами открывают `VideoCallsActivity`. Вызывайте их из `lifecycleScope.launch` или `viewModelScope`. Не оборачивайте в `repeatOnLifecycle(RESUMED)` и не используйте `launchWhenResumed`: при старте экрана звонка текущая activity уходит в `onPause`, корутина отменяется, и подключение обрывается.

Примеры вызовов: [сценарии использования](READ-sdk-scenarios.md). Если в приложении своя `WorkerFactory` для WorkManager, подключите фабрику Jazz так, как описано в [инициализации](READ-sdk-initialization.md).
