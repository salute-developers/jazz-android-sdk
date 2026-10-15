## Сценарии использования Jazz SDK

<details><summary>Простой сценарий создания встречи и присоединения к ней</summary>

```kotlin
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createConferenceButton.setOnClickListener {
            getJazzIntegrationClientApi().jazzIntegrationClient.createConference()
        }
    }
}
```
</details>

<details><summary>Сценарий создания запланированной встречи и присоединения к ней</summary>

`joinConference` не возвращается, пока конференция не закончится, и внутри открывает `VideoCallsActivity`. Вызов нельзя оборачивать в `repeatOnLifecycle(RESUMED)` или `launchWhenResumed`: при старте экрана звонка `MainActivity` уходит в `onPause`, корутина отменяется, и подключение обрывается. Используйте `lifecycleScope.launch` или `viewModelScope` — они живут дольше, чем состояние `RESUMED`.

```kotlin
fun scheduleAndJoinConference() {
    lifecycleScope.launch {
        val scheduled = withContext(Dispatchers.IO) {
            JazzSdk.getIntegrationClientApi().jazzIntegrationClient.scheduleConference(
                CreateVideoCallArguments(
                    roomType = "MEETING",
                    roomName = "Новая видеовстреча",
                    withGuests = true,
                    lobbyEnabled = false,
                    autoRecord = false,
                    summarizationEnabled = false,
                    jazzTokenProvider = jazzTokenProvider,
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
                val joinArgs = JoinVideoCallArguments(
                    userName = "Имя участника",
                    conferenceConnectionArguments = ConferenceConnectionArguments.RoomCode(
                        roomCode = scheduled.conference.code,
                        password = scheduled.conference.password,
                    ),
                    micEnabled = false,
                    cameraEnabled = false,
                )
                JazzSdk.getIntegrationClientApi().jazzIntegrationClient.joinConference(joinArgs)
            }

            is ConferenceResult.Error.Scheduled -> {
                val message = "Title: ${scheduled.title}, description: ${scheduled.description}"
                Log.e("joinConference", message, scheduled.throwable)
            }
        }
    }
}
```

</details>