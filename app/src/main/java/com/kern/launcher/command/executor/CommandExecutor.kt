package com.kern.launcher.command.executor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.widget.Toast
import com.kern.launcher.data.repository.AppRepository
import com.kern.launcher.data.repository.CommandHistoryRepository
import com.kern.launcher.data.repository.HiddenAppRepository
import com.kern.launcher.model.Command
import com.kern.launcher.service.intent.IntentFactory

class CommandExecutor(
    private val context: Context,
    private val appRepository: AppRepository,
    private val historyRepository: CommandHistoryRepository,
    private val hiddenAppRepository: HiddenAppRepository
) {
    suspend fun execute(
        command: Command,
        onOpenSettings: () -> Unit = {},
        onOpenHelp: () -> Unit = {},
        onOpenHiddenApps: () -> Unit = {},
        onToggleTuiView: () -> Unit = {}
    ): Boolean {
        return try {
            when (command) {
                is Command.AppLaunch -> {
                    val intent = IntentFactory.createAppLaunchIntent(context, command.app)
                    if (intent != null) {
                        try {
                            context.startActivity(intent)
                            appRepository.incrementAppUsage(command.app.packageName, command.app.activityName)
                            historyRepository.recordCommand(command.app.label)
                            true
                        } catch (e: Exception) {
                            Toast.makeText(context, "App '${command.app.label}' is no longer installed", Toast.LENGTH_SHORT).show()
                            false
                        }
                    } else {
                        Toast.makeText(context, "App '${command.app.label}' is no longer installed", Toast.LENGTH_SHORT).show()
                        false
                    }
                }
                is Command.AiSearch -> {
                    context.startActivity(IntentFactory.createAiSearchIntent(context, command.prompt, command.provider))
                    historyRepository.recordCommand(if (command.prompt.isNotBlank()) "ai ${command.prompt}" else "ai")
                    true
                }
                is Command.LazyLogsAdd -> {
                    try {
                        context.startActivity(IntentFactory.createLazyLogsIntent(context, command.rawText))
                        historyRepository.recordCommand(if (command.rawText.isNotBlank()) "log ${command.rawText}" else "log")
                        true
                    } catch (e: Exception) {
                        Toast.makeText(context, "LazyLogs app is not installed to handle deep link", Toast.LENGTH_SHORT).show()
                        false
                    }
                }
                is Command.DeepLinkUri -> {
                    try {
                        context.startActivity(IntentFactory.createDeepLinkUriIntent(command.uriString))
                        historyRepository.recordCommand(command.uriString)
                        true
                    } catch (e: Exception) {
                        Toast.makeText(context, "No app found to open deep link: ${command.uriString}", Toast.LENGTH_SHORT).show()
                        false
                    }
                }
                is Command.GoogleSearch -> {
                    context.startActivity(IntentFactory.createGoogleSearchIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "g ${command.query}" else "g")
                    true
                }
                is Command.GoogleMaps -> {
                    context.startActivity(IntentFactory.createGoogleMapsIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "maps ${command.query}" else "maps")
                    true
                }
                is Command.YoutubeSearch -> {
                    context.startActivity(IntentFactory.createYoutubeSearchIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "yt ${command.query}" else "yt")
                    true
                }
                is Command.SpotifySearch -> {
                    context.startActivity(IntentFactory.createSpotifySearchIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "spot ${command.query}" else "spot")
                    true
                }
                is Command.PlayStoreSearch -> {
                    context.startActivity(IntentFactory.createPlayStoreSearchIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "play ${command.query}" else "play")
                    true
                }
                is Command.GithubSearch -> {
                    context.startActivity(IntentFactory.createGithubSearchIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "gh ${command.query}" else "gh")
                    true
                }
                is Command.WikipediaSearch -> {
                    context.startActivity(IntentFactory.createWikipediaSearchIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "wiki ${command.query}" else "wiki")
                    true
                }
                is Command.RedditSearch -> {
                    context.startActivity(IntentFactory.createRedditSearchIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "reddit ${command.query}" else "reddit")
                    true
                }
                is Command.TwitterSearch -> {
                    context.startActivity(IntentFactory.createTwitterSearchIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "x ${command.query}" else "x")
                    true
                }
                is Command.DuckDuckGoSearch -> {
                    context.startActivity(IntentFactory.createDuckDuckGoSearchIntent(context, command.query))
                    historyRepository.recordCommand(if (command.query.isNotBlank()) "ddg ${command.query}" else "ddg")
                    true
                }
                is Command.Calculator -> {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Calculator Result", command.result)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Result: ${command.result} (copied to clipboard)", Toast.LENGTH_SHORT).show()
                    historyRepository.recordCommand("calc ${command.expression}")
                    true
                }
                is Command.Timer -> {
                    try {
                        val timerIntent = IntentFactory.createTimerIntent(command.durationSeconds, command.rawInput)
                        context.startActivity(timerIntent)
                        Toast.makeText(context, "Timer set for ${command.rawInput}", Toast.LENGTH_SHORT).show()
                        historyRepository.recordCommand("timer ${command.rawInput}")
                        true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        try {
                            val fallbackIntent = Intent(AlarmClock.ACTION_SHOW_TIMERS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(fallbackIntent)
                            Toast.makeText(context, "Opened Clock app (${command.rawInput})", Toast.LENGTH_SHORT).show()
                            historyRepository.recordCommand("timer ${command.rawInput}")
                            true
                        } catch (e2: Exception) {
                            Toast.makeText(context, "No Clock app found to set timer", Toast.LENGTH_SHORT).show()
                            false
                        }
                    }
                }
                is Command.AppInfoSettings -> {
                    try {
                        context.startActivity(IntentFactory.createAppInfoIntent(command.packageName))
                        historyRepository.recordCommand("info ${command.appLabel}")
                        true
                    } catch (e: Exception) {
                        Toast.makeText(context, "App '${command.appLabel}' is no longer installed", Toast.LENGTH_SHORT).show()
                        false
                    }
                }
                is Command.HideApp -> {
                    hiddenAppRepository.hideApp(command.app.packageName, command.app.label)
                    Toast.makeText(context, "App '${command.app.label}' is now hidden.", Toast.LENGTH_SHORT).show()
                    historyRepository.recordCommand("hide ${command.app.label}")
                    true
                }
                is Command.UnhideApp -> {
                    hiddenAppRepository.unhideApp(command.packageName)
                    Toast.makeText(context, "App '${command.label}' is now visible.", Toast.LENGTH_SHORT).show()
                    historyRepository.recordCommand("unhide ${command.label}")
                    true
                }
                is Command.ShowHiddenApps -> {
                    onOpenHiddenApps()
                    true
                }
                is Command.ToggleTuiView -> {
                    onToggleTuiView()
                    true
                }
                is Command.OpenSettings -> {
                    onOpenSettings()
                    true
                }
                is Command.Help -> {
                    onOpenHelp()
                    true
                }
                is Command.CustomAlias -> {
                    execute(command.resolvedCommand, onOpenSettings, onOpenHelp, onOpenHiddenApps, onToggleTuiView)
                }
                is Command.Unknown -> {
                    if (command.rawQuery.isNotBlank()) {
                        context.startActivity(IntentFactory.createGoogleSearchIntent(context, command.rawQuery))
                        historyRepository.recordCommand(command.rawQuery)
                        true
                    } else false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to execute command: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
