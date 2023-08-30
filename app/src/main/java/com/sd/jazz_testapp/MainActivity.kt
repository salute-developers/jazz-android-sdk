package com.sd.jazz_testapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sd.jazz_testapp.databinding.ActivityMainBinding
import com.sdkit.jazz.client.integration.api.domain.JazzTokenProvider
import com.sdkit.jazz.client.integration.api.model.AudioDevice
import com.sdkit.jazz.client.integration.api.model.CreateVideoCallArguments
import com.sdkit.jazz.client.integration.api.model.JazzTokenConfiguration
import com.sdkit.jazz.client.integration.api.model.JoinVideoCallArguments
import com.sdkit.jazz.client.integration.api.model.ScheduledConferenceResult
import com.sdkit.jazz.sdk.di.JazzSdkTokenProvider
import com.sdkit.jazz.sdk.di.JazzTokenConfigurationProvider
import com.sdkit.jazz.sdk.utils.getJazzIntegrationClientApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    // Что бы получить ваш ключ, пройдите сюда -> https://developers.sber.ru/docs/ru/jazz/sdk/sdk-key
    // Так же добавить этот JazzTokenProvider можно в DefaultJazzPlatformDependencies,
    // которые прокидываются в MainApplication
    val jazzSdkTokenProvider = JazzSdkTokenProvider(provider = object: JazzTokenConfigurationProvider {
        override fun getConfiguration(): JazzTokenConfiguration {
            return JazzTokenConfiguration(
                secretKey = "ВАШ КЛЮЧ",
                liveTimeDurationInSeconds = 180
            )
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createConferenceButton.setOnClickListener {
            getJazzIntegrationClientApi().jazzIntegrationClient.createConference(
                arguments = CreateVideoCallArguments(
                    roomType = "MEETING",
                    roomName = "Новая видеовстреча",
                    userName = "Пользователь",
                    cameraEnabled = false,
                    micEnabled = false,
                    // Закрытая или открытая встреча
                    withGuests = true,
                    // Встреча с комнатой ожидания
                    lobbyEnabled = false,
                    autoRecord = null,
                    audioDevice = AudioDevice.DEFAULT,
                    jazzTokenProvider = object : JazzTokenProvider {
                        override suspend fun getToken(): String? {
                            return jazzSdkTokenProvider.getToken()
                        }
                    }
                ),
            )
        }

        binding.scheduleJoinConferenceButton.setOnClickListener {
            scheduleAndJoinConference()
        }
    }

    fun scheduleAndJoinConference() {
        lifecycleScope.launchWhenResumed {
            val scheduled = withContext(Dispatchers.IO) {
                getJazzIntegrationClientApi().jazzIntegrationClient.scheduleConference(
                    roomType = "MEETING",
                    name = "Новая видеовстреча",
                    // Закрытая или открытая встреча
                    withGuests = true,
                    // Встреча с комнатой ожидания
                    lobbyEnabled = false,
                    autoRecord = false,
                    jazzTokenProvider = object : JazzTokenProvider {
                        override suspend fun getToken(): String? {
                            return jazzSdkTokenProvider.getToken()
                        }
                    }
                )
            }

            when (scheduled) {
                is ScheduledConferenceResult.Success -> {
                    // Сохраняем запланированую встречу
                    // Можем разослать ее всем участникам
                    // Выполняем присоединение к запланированной встрече
                    val joinArgs = JoinVideoCallArguments(
                        userName = "Имя участника",
                        roomCode = scheduled.conference.code,
                        password = scheduled.conference.password,
                        micEnabled = false,
                        cameraEnabled = false
                    )
                    getJazzIntegrationClientApi().jazzIntegrationClient.joinConference(joinArgs)
                }

                is ScheduledConferenceResult.Error -> {
                    val message = "Title: ${scheduled.title}, description: ${scheduled.description}"
                    Log.e("joinConference", message, scheduled.throwable)
                }
            }
        }
    }
}