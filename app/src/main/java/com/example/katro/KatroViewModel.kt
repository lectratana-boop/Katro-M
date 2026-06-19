package com.example.katro

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class KatroScreen {
    HOME,
    GAME,
    HOW_TO_PLAY,
    STATS,
    SETTINGS,
    ABOUT
}

class KatroViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("katro_prefs", Context.MODE_PRIVATE)

    // UI state states
    var currentScreen by mutableStateOf(KatroScreen.HOME)
    var language by mutableStateOf(prefs.getString("lang", "fr") ?: "fr")
    var soundEnabled by mutableStateOf(prefs.getBoolean("sound_enabled", true))
    var fastAnimations by mutableStateOf(prefs.getBoolean("fast_anim", false))
    var isAiMode by mutableStateOf(prefs.getBoolean("ai_mode", false))
    var aiDifficulty by mutableStateOf(
        Difficulty.valueOf(prefs.getString("ai_diff", Difficulty.INTERMEDIATE.name) ?: Difficulty.INTERMEDIATE.name)
    )

    // Current logic board and scoring state
    var gameState by mutableStateOf(KatroState())

    // Animation representation for the UI board
    var currentUiBoard by mutableStateOf(List(16) { 2 })
    var isAnimating by mutableStateOf(false)
    var activeAnimationHoleIdx by mutableStateOf<Int?>(null)
    var animationHeldSeedsCount by mutableStateOf(0)
    var turnDurationTimer by mutableStateOf(0L) // in seconds
    private var gameStartTime = System.currentTimeMillis()

    // Pass and Play Rotate Board option
    var rotateBoardForP2 by mutableStateOf(prefs.getBoolean("rotate_p2", true))

    // Statistics persisted local state
    var statsGamesPlayed by mutableStateOf(prefs.getInt("stats_games", 0))
    var statsWinsP1 by mutableStateOf(prefs.getInt("stats_wins_p1", 0))
    var statsWinsP2 by mutableStateOf(prefs.getInt("stats_wins_p2", 0))
    var statsDraws by mutableStateOf(prefs.getInt("stats_draws", 0))
    var statsAverageTimeSec by mutableStateOf(prefs.getLong("stats_avg_time", 0L))
    var statsMaxCaptureSingleTurn by mutableStateOf(prefs.getInt("stats_max_cap", 0))

    // Achievements badges status
    var badgeFirstWin by mutableStateOf(prefs.getBoolean("badge_first_win", false))
    var badgeTenWins by mutableStateOf(prefs.getBoolean("badge_10_wins", false))
    var badgeFiftyWins by mutableStateOf(prefs.getBoolean("badge_50_wins", false))
    var badgeLegendaryCapture by mutableStateOf(prefs.getBoolean("badge_legend_cap", false))
    var badgeLoyalPlayer by mutableStateOf(prefs.getBoolean("badge_loyal_player", false))

    init {
        // Sync synthesizer mute flag
        SoundSynth.isMuted = !soundEnabled
        
        // Attempt to auto-resume active match
        loadAndRestoreGame()
    }

    fun startNewGame(vsAI: Boolean) {
        isAiMode = vsAI
        prefs.edit().putBoolean("ai_mode", vsAI).apply()
        
        gameState = KatroState()
        currentUiBoard = gameState.board
        isAnimating = false
        activeAnimationHoleIdx = null
        animationHeldSeedsCount = 0
        gameStartTime = System.currentTimeMillis()
        turnDurationTimer = 0L
        
        currentScreen = KatroScreen.GAME
        saveGame()
    }

    /**
     * Executes the circular distribution with step-by-step visual animation.
     */
    fun performMove(holeIndex: Int) {
        if (isAnimating || gameState.isGameOver) return

        // Verify it belongs to active player
        val validMoves = gameState.getValidMoves(gameState.activePlayer)
        if (holeIndex !in validMoves) return

        viewModelScope.launch {
            isAnimating = true
            val turnResult = KatroEngine.playTurn(gameState, holeIndex)
            val steps = turnResult.animationSequence

            val delayMs = if (fastAnimations) 120L else 300L

            for (step in steps) {
                when (step) {
                    is AnimationStep.PickUp -> {
                        activeAnimationHoleIdx = step.index
                        animationHeldSeedsCount = step.count
                        val boardCopy = currentUiBoard.toMutableList()
                        boardCopy[step.index] = 0
                        currentUiBoard = boardCopy
                        SoundSynth.playSeedDrop()
                        delay(delayMs)
                    }
                    is AnimationStep.Drop -> {
                        activeAnimationHoleIdx = step.index
                        animationHeldSeedsCount = step.remainingSeeds
                        val boardCopy = currentUiBoard.toMutableList()
                        boardCopy[step.index] = step.countInHole
                        currentUiBoard = boardCopy
                        SoundSynth.playSeedDrop()
                        delay(delayMs)
                    }
                    is AnimationStep.LandAndSowAgain -> {
                        activeAnimationHoleIdx = step.index
                        animationHeldSeedsCount = step.countTaken
                        val boardCopy = currentUiBoard.toMutableList()
                        boardCopy[step.index] = 0
                        currentUiBoard = boardCopy
                        SoundSynth.playSeedDrop()
                        delay((delayMs * 1.5).toLong())
                    }
                    is AnimationStep.Capture -> {
                        activeAnimationHoleIdx = step.fromIndex
                        val boardCopy = currentUiBoard.toMutableList()
                        boardCopy[step.fromIndex] = 0
                        currentUiBoard = boardCopy
                        
                        // Check if this counts as a Legendary capture (>= 6 seeds)
                        if (step.capturedCount >= 6) {
                            badgeLegendaryCapture = true
                            prefs.edit().putBoolean("badge_legend_cap", true).apply()
                        }
                        // Update Max Capture in session
                        if (step.capturedCount > statsMaxCaptureSingleTurn) {
                            statsMaxCaptureSingleTurn = step.capturedCount
                            prefs.edit().putInt("stats_max_cap", step.capturedCount).apply()
                        }

                        SoundSynth.playCapture()
                        delay(delayMs * 2L)
                    }
                    is AnimationStep.TurnEnd -> {
                        // Clear active states
                        activeAnimationHoleIdx = null
                        animationHeldSeedsCount = 0
                    }
                }
            }

            // Sync with final logical state
            gameState = turnResult.finalState
            currentUiBoard = gameState.board
            isAnimating = false

            if (gameState.isGameOver) {
                SoundSynth.playVictory()
                handleGameFinished()
            } else {
                saveGame()
                
                // If it's now AI's turn, trigger next move
                if (isAiMode && gameState.activePlayer == Player.PLAYER_TWO) {
                    delay(800) // slight thinking pause for AI
                    triggerAiTurn()
                }
            }
        }
    }

    private fun triggerAiTurn() {
        val aiMove = KatroEngine.calculateAIMove(gameState, aiDifficulty)
        if (aiMove != -1) {
            performMove(aiMove)
        }
    }

    private fun handleGameFinished() {
        val durationSec = (System.currentTimeMillis() - gameStartTime) / 1000L
        
        // Update games played
        val newCountGames = statsGamesPlayed + 1
        statsGamesPlayed = newCountGames
        prefs.edit().putInt("stats_games", newCountGames).apply()

        // Loyal Player check (15 games)
        if (newCountGames >= 15) {
            badgeLoyalPlayer = true
            prefs.edit().putBoolean("badge_loyal_player", true).apply()
        }

        // Calculate average time
        val totalPrevTimeSec = statsAverageTimeSec * (newCountGames - 1)
        val newAvgTime = (totalPrevTimeSec + durationSec) / newCountGames
        statsAverageTimeSec = newAvgTime
        prefs.edit().putLong("stats_avg_time", newAvgTime).apply()

        // Handle Winners
        if (gameState.isDraw) {
            statsDraws++
            prefs.edit().putInt("stats_draws", statsDraws).apply()
        } else {
            when (gameState.winner) {
                Player.PLAYER_ONE -> {
                    statsWinsP1++
                    prefs.edit().putInt("stats_wins_p1", statsWinsP1).apply()
                    
                    // First Win
                    if (!badgeFirstWin) {
                        badgeFirstWin = true
                        prefs.edit().putBoolean("badge_first_win", true).apply()
                    }
                    // Win thresholds
                    if (statsWinsP1 >= 10) {
                        badgeTenWins = true
                        prefs.edit().putBoolean("badge_10_wins", true).apply()
                    }
                    if (statsWinsP1 >= 50) {
                        badgeFiftyWins = true
                        prefs.edit().putBoolean("badge_50_wins", true).apply()
                    }
                }
                Player.PLAYER_TWO -> {
                    statsWinsP2++
                    prefs.edit().putInt("stats_wins_p2", statsWinsP2).apply()
                }
                else -> {}
            }
        }

        // Delete active save file
        prefs.edit().remove("active_save").apply()
    }

    fun changeLanguage(lang: String) {
        language = lang
        prefs.edit().putString("lang", lang).apply()
    }

    fun toggleSounds() {
        soundEnabled = !soundEnabled
        SoundSynth.isMuted = !soundEnabled
        prefs.edit().putBoolean("sound_enabled", soundEnabled).apply()
    }

    fun toggleFastAnimations() {
        fastAnimations = !fastAnimations
        prefs.edit().putBoolean("fast_anim", fastAnimations).apply()
    }

    fun toggleRotateP2() {
        rotateBoardForP2 = !rotateBoardForP2
        prefs.edit().putBoolean("rotate_p2", rotateBoardForP2).apply()
    }

    fun setAiDifficultySetting(diff: Difficulty) {
        aiDifficulty = diff
        prefs.edit().putString("ai_diff", diff.name).apply()
    }

    fun resetStatistics() {
        statsGamesPlayed = 0
        statsWinsP1 = 0
        statsWinsP2 = 0
        statsDraws = 0
        statsAverageTimeSec = 0L
        statsMaxCaptureSingleTurn = 0
        
        badgeFirstWin = false
        badgeTenWins = false
        badgeFiftyWins = false
        badgeLegendaryCapture = false
        badgeLoyalPlayer = false

        prefs.edit()
            .putInt("stats_games", 0)
            .putInt("stats_wins_p1", 0)
            .putInt("stats_wins_p2", 0)
            .putInt("stats_draws", 0)
            .putLong("stats_avg_time", 0L)
            .putInt("stats_max_cap", 0)
            .putBoolean("badge_first_win", false)
            .putBoolean("badge_10_wins", false)
            .putBoolean("badge_50_wins", false)
            .putBoolean("badge_legend_cap", false)
            .putBoolean("badge_loyal_player", false)
            .apply()
    }

    // Save and load state (Autosave functionality)
    private fun saveGame() {
        try {
            val json = JSONObject()
            json.put("board", JSONArray(gameState.board))
            json.put("activePlayer", gameState.activePlayer.name)
            json.put("scoreP1", gameState.scoreP1)
            json.put("scoreP2", gameState.scoreP2)
            json.put("isGameOver", gameState.isGameOver)
            json.put("isAi", isAiMode)
            
            prefs.edit().putString("active_save", json.toString()).apply()
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    private fun loadAndRestoreGame() {
        val saveStr = prefs.getString("active_save", null) ?: return
        try {
            val json = JSONObject(saveStr)
            val jsonBoard = json.getJSONArray("board")
            val boardList = mutableListOf<Int>()
            for (i in 0 until jsonBoard.length()) {
                boardList.add(jsonBoard.getInt(i))
            }
            val activePlayer = Player.valueOf(json.optString("activePlayer", Player.PLAYER_ONE.name))
            val scoreP1 = json.optInt("scoreP1", 0)
            val scoreP2 = json.optInt("scoreP2", 0)
            val isGameOver = json.optBoolean("isGameOver", false)
            
            isAiMode = json.optBoolean("isAi", false)
            
            // Reconstruct logic state
            gameState = KatroState(
                board = boardList,
                activePlayer = activePlayer,
                scoreP1 = scoreP1,
                scoreP2 = scoreP2,
                isGameOver = isGameOver,
                winner = null,
                isDraw = false
            )
            currentUiBoard = gameState.board
        } catch (e: Exception) {
            // Clear corrupted save
            prefs.edit().remove("active_save").apply()
        }
    }

    /**
     * Determines if an active game exists and can be resumed.
     */
    fun hasOngoingGame(): Boolean {
        val saveStr = prefs.getString("active_save", null)
        return saveStr != null && !gameState.isGameOver
    }

    fun resumeExistingGame() {
        loadAndRestoreGame()
        currentScreen = KatroScreen.GAME
        
        // If AI's turn immediately on resume, resume AI move triggers
        if (isAiMode && gameState.activePlayer == Player.PLAYER_TWO && !isAnimating) {
            viewModelScope.launch {
                delay(800)
                triggerAiTurn()
            }
        }
    }
}
