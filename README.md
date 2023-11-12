[Основную документацию можно найти тут](https://clck.ru/35aWZB)

[Что бы создать комнату, для начала получите ключ авторизации тут](https://clck.ru/35aWZw)

[Инициализация](READ-sdk-initialization.md)

[Сценарии использования](READ-sdk-scenarios.md)

[Лицензия на использование](https://clck.ru/35F8h3)

## Как подключить JAZZ SDK
### 1 Шаг. В settings.gradle, аналогично заполняем блок repositories, обязательно не забыть про jcenter():
```groovy
dependencyResolutionManagement {
   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
   repositories {
     // Здесь указан репозиторий с которого можно скачать все необходимые зависимости,
     // что бы не делать excludeGroup достаточно поставить наш репозиторий выше других
     maven { url "https://jazz-sdk.nx.s2b.tech/maven2/" }

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

### 2 Шаг. Добавляем необходимые опции в build.gradle в модуле app перед sync project:

* Проверяем блок plugins, в нем должны быть необходимые плагины, как на скрине ниже:
```groovy
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}
```
* Дополняем блок Android:
  * Указываем compileSdk, должен быть выше 30
  * Подключаем viewBinding и мержим ресурсы(Как на скрине ниже)
```groovy
    buildFeatures {
        viewBinding true
    }
    packagingOptions {
        pickFirst '**/*.so'
        pickFirst 'META-INF/kotlinx_coroutines_core.version'
    }
 ```
* Дополняем блок dependencies:
```groovy
    //Jazz SDK
    implementation("com.sdkit.android:jazz-public-sdk:23.10.2.90")
    implementation("com.sdkit.jazz:jazzcastlib:1.15.3")

    //region TODO Не правильно подтягиваются из pom зависимости, подключенные через bom
    implementation(platform('androidx.compose:compose-bom:2022.11.00'))
    implementation(platform('com.google.firebase:firebase-bom:32.0.0'))
    //endregion
 ```

Так же добавляем блок configurations.all {}
```groovy
configurations.all {
    resolutionStrategy.dependencySubstitution {
        // Тут перенаправляем все core что требуется внутри платформы в fataar артефакт
        substitute(module("ru.sberdevices.core:analytics"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:analytics_api"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:di_api"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:di"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:di_graph"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:utils"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:utils_api"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:logging_api"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:logging"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:platform"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:platform_api"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:designsystem"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:viewmodels"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:coroutines"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:coroutines_api"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:config_api"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:config"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:network_api"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))
        substitute(module("ru.sberdevices.core:network"))
                .using(module("com.sdkit.android.core:core-ext:" + getJazzVersion()))

        substitute(module("ru.sberdevices.core:font"))
                .using(module("com.sdkit.android.core:core-font:" + getJazzVersion()))

    }
}
```


#### Дополнительный материал:

[Инициализация](READ-sdk-initialization.md)

[Сценарии использования](READ-sdk-scenarios.md)