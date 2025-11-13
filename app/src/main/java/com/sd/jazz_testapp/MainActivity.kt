package com.sd.jazz_testapp

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sd.jazz_testapp.databinding.ActivityMainBinding
import com.sdkit.jazz.client.integration.api.domain.JazzTokenProvider
import com.sdkit.jazz.client.integration.api.model.AudioDevice
import com.sdkit.jazz.client.integration.api.model.ConferenceConnectionArguments
import com.sdkit.jazz.client.integration.api.model.ConferenceResult
import com.sdkit.jazz.client.integration.api.model.CreateVideoCallArguments
import com.sdkit.jazz.client.integration.api.model.JazzTokenConfiguration
import com.sdkit.jazz.client.integration.api.model.JoinVideoCallArguments
import com.sdkit.jazz.sdk.di.JazzSdk
import com.sdkit.jazz.sdk.di.JazzSdkTokenProvider
import com.sdkit.jazz.sdk.di.JazzTokenConfigurationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    // Что бы получить ваш ключ, пройдите сюда -> https://clck.ru/35aWZw
    // Так же добавить этот JazzTokenProvider можно в DefaultJazzPlatformDependencies,
    // которые прокидываются в MainApplication
    val jazzSdkTokenProvider = JazzSdkTokenProvider(provider = object : JazzTokenConfigurationProvider {
        override fun getConfiguration(): JazzTokenConfiguration {
            return JazzTokenConfiguration(
                userId = "test",
                secretKey = binding.sdkKeyEditText.text.toString(),
                liveTimeDurationInSeconds = 180,
                userId = "",
            )
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sdkKeyDocLinkTextView.movementMethod = LinkMovementMethod.getInstance()

        binding.createConferenceButton.setOnClickListener {
            lifecycleScope.launch {
                JazzSdk.getIntegrationClientApi().jazzIntegrationClient.createConference(
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
                        summarizationEnabled = null,
                        autoRecord = null,
                        audioDevice = AudioDevice.DEFAULT,
                        jazzTokenProvider = object : JazzTokenProvider {
                            override suspend fun getToken(): String? {
                                return jazzSdkTokenProvider.getToken()
                            }
                        },
                        isRoom3dEnabled = false,
                        sipEnabled = false,
                    ),
                )
            }
        }

        binding.scheduleJoinConferenceButton.setOnClickListener {
            scheduleAndJoinConference()
        }
    }

    fun scheduleAndJoinConference() {
        lifecycleScope.launchWhenResumed {
            val scheduled = withContext(Dispatchers.IO) {
                JazzSdk.getIntegrationClientApi().jazzIntegrationClient.scheduleConference(
                    CreateVideoCallArguments(
                        roomType = "MEETING",
                        roomName = "Новая видеовстреча",
                        // Закрытая или открытая встреча
                        withGuests = true,
                        // Встреча с комнатой ожидания
                        lobbyEnabled = false,
                        autoRecord = false,
                        summarizationEnabled = false,
                        jazzTokenProvider = object : JazzTokenProvider {
                            override suspend fun getToken(): String? {
                                return jazzSdkTokenProvider.getToken()
                            }
                        },
                        cameraEnabled = false,
                        sipEnabled = false,
                        isRoom3dEnabled = false,
                        micEnabled = false,
                        userName = "",
                    )

                )
            }

            when (scheduled) {
                is ConferenceResult.Success.Scheduled -> {
                    // Сохраняем запланированую встречу
                    // Можем разослать ее всем участникам
                    // Выполняем присоединение к запланированной встрече
                    val joinArgs = JoinVideoCallArguments(
                        userName = "Имя участника",
                        conferenceConnectionArguments = ConferenceConnectionArguments.RoomCode(
                            roomCode = scheduled.conference.code,
                            password = scheduled.conference.password,
                        ),
                        micEnabled = false,
                        cameraEnabled = false
                    )
                    JazzSdk.getIntegrationClientApi().jazzIntegrationClient.joinConference(joinArgs)
                }

                is ConferenceResult.Error.Scheduled -> {
                    val message = "Title: ${scheduled.title}, description: ${scheduled.description}"
                    Log.e("joinConference", message, scheduled.throwable)
                }
                else -> Unit
            }
        }
    }
}