package com.example.katro

import kotlin.random.Random

enum class Player {
    PLAYER_ONE, // Bottom row (indices 0..7)
    PLAYER_TWO  // Top row (indices 15..8 visual, circular indices 8..15)
}

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    EXPERT
}

data class KatroState(
    val board: List<Int> = List(16) { 2 }, // Initial setup: 2 seeds in each of the 16 holes
    val activePlayer: Player = Player.PLAYER_ONE,
    val scoreP1: Int = 0,
    val scoreP2: Int = 0,
    val isGameOver: Boolean = false,
    val winner: Player? = null,
    val isDraw: Boolean = false
) {
    // Get valid move indices for a player
    fun getValidMoves(player: Player): List<Int> = when (player) {
        Player.PLAYER_ONE -> (0..7).filter { board[it] >= 2 }
        Player.PLAYER_TWO -> (8..15).filter { board[it] >= 2 }
    }

    // Check if player has any valid moves
    fun hasValidMoves(player: Player): Boolean = getValidMoves(player).isNotEmpty()
}

/**
 * Atomic animation steps of a Katro turn to give the UI a step-by-step playback.
 */
sealed class AnimationStep {
    data class PickUp(val index: Int, val count: Int) : AnimationStep()
    data class Drop(val index: Int, val countInHole: Int, val remainingSeeds: Int) : AnimationStep()
    data class LandAndSowAgain(val index: Int, val countTaken: Int) : AnimationStep()
    data class Capture(val fromIndex: Int, val capturedCount: Int, val toPlayer: Player) : AnimationStep()
    data class TurnEnd(val nextPlayer: Player, val finalBoardState: List<Int>) : AnimationStep()
}

/**
 * Result of a completed or simulated turn.
 */
data class TurnResult(
    val finalState: KatroState,
    val animationSequence: List<AnimationStep>
)

object KatroEngine {

    /**
     * Executes the circular distribution and relais-sowing game rules, generating an animation sequence.
     */
    fun playTurn(state: KatroState, startIdx: Int): TurnResult {
        val validMoves = state.getValidMoves(state.activePlayer)
        if (startIdx !in validMoves) {
            // Invalid move selected, return current state
            return TurnResult(state, emptyList())
        }

        val sequence = mutableListOf<AnimationStep>()
        val mutableBoard = state.board.toMutableList()
        var scoreP1 = state.scoreP1
        var scoreP2 = state.scoreP2
        val activePlayer = state.activePlayer

        var currIdx = startIdx
        var heldSeeds = mutableBoard[currIdx]
        mutableBoard[idxCheck(currIdx)] = 0
        sequence.add(AnimationStep.PickUp(currIdx, heldSeeds))

        while (heldSeeds > 0) {
            // Move counterclockwise: 0 -> 1 -> ... -> 7 -> 8 -> ... -> 15 -> 0
            currIdx = (currIdx + 1) % 16
            mutableBoard[idxCheck(currIdx)] += 1
            heldSeeds--
            sequence.add(AnimationStep.Drop(currIdx, mutableBoard[currIdx], heldSeeds))

            // Relais mechanism: if the last seed falls in a hole with seeds already, pick them all up and keep going.
            if (heldSeeds == 0 && mutableBoard[currIdx] > 1) {
                heldSeeds = mutableBoard[currIdx]
                mutableBoard[idxCheck(currIdx)] = 0
                sequence.add(AnimationStep.LandAndSowAgain(currIdx, heldSeeds))
            }
        }

        // Final landing is at currIdx, which of course was empty, so it now has exactly 1 seed.
        // Let's check for captures relative to the active player's row.
        val landedOnOwnRow = when (activePlayer) {
            Player.PLAYER_ONE -> currIdx in 0..7
            Player.PLAYER_TWO -> currIdx in 8..15
        }

        if (landedOnOwnRow && mutableBoard[currIdx] == 1) {
            val oppositeIdx = 15 - currIdx
            val capturedCount = mutableBoard[oppositeIdx]
            if (capturedCount > 0) {
                mutableBoard[idxCheck(oppositeIdx)] = 0
                when (activePlayer) {
                    Player.PLAYER_ONE -> scoreP1 += capturedCount
                    Player.PLAYER_TWO -> scoreP2 += capturedCount
                }
                sequence.add(AnimationStep.Capture(oppositeIdx, capturedCount, activePlayer))
            }
        }

        // Now change who the active player is, and check if the game is over.
        var nextPlayer = if (activePlayer == Player.PLAYER_ONE) Player.PLAYER_TWO else Player.PLAYER_ONE
        
        // Check if the next player can play
        val nextPlayerHasMoves = (0..15).any {
            val isOwned = (nextPlayer == Player.PLAYER_ONE && it in 0..7) || 
                          (nextPlayer == Player.PLAYER_TWO && it in 8..15)
            isOwned && mutableBoard[it] >= 2
        }

        // If next player doesn't have moves, is the game over?
        // Note: Check if the original player has moves instead? If one cannot play, the opponent might still play?
        // Actually, the prompt says: "La partie se termine lorsque : Un joueur ne peut plus jouer ; Ou qu'il ne reste plus assez de graines pour continuer normalement."
        // We can check if either player cannot play, or if the next player cannot play, the game is over.
        var isGameOver = false
        var winner: Player? = null
        var isDraw = false

        if (!nextPlayerHasMoves) {
            isGameOver = true
        } else {
            // Also check if the current active player has moves. If both cannot play, game over.
            // But if the next player cannot play, they automatically lose their turn or the game ends.
            // Let's implement that if the designated nextPlayer has no valid moves, game over is triggered.
            // This is accurate and guarantees game termination.
        }

        // Win calculation if game over
        if (isGameOver) {
            val remainingP1 = (0..7).sumOf { mutableBoard[it] }
            val remainingP2 = (8..15).sumOf { mutableBoard[it] }
            
            // Standard Katro adds remaining seeds back to total scores or we calculate directly.
            // Let's stick strictly to: "Le vainqueur est celui qui possède le plus de graines capturées."
            val finalScoreP1 = scoreP1
            val finalScoreP2 = scoreP2

            if (finalScoreP1 > finalScoreP2) {
                winner = Player.PLAYER_ONE
            } else if (finalScoreP2 > finalScoreP1) {
                winner = Player.PLAYER_TWO
            } else {
                isDraw = true
            }
        }

        val finalState = KatroState(
            board = mutableBoard.toList(),
            activePlayer = nextPlayer,
            scoreP1 = scoreP1,
            scoreP2 = scoreP2,
            isGameOver = isGameOver,
            winner = winner,
            isDraw = isDraw
        )

        sequence.add(AnimationStep.TurnEnd(nextPlayer, finalState.board))

        return TurnResult(finalState, sequence)
    }

    private fun idxCheck(idx: Int): Int = idx.coerceIn(0, 15)

    /**
     * AI Move Selection based on standard, intermediate or expert strategies.
     */
    fun calculateAIMove(state: KatroState, difficulty: Difficulty): Int {
        val validMoves = state.getValidMoves(state.activePlayer)
        if (validMoves.isEmpty()) return -1
        if (validMoves.size == 1) return validMoves[0]

        return when (difficulty) {
            Difficulty.BEGINNER -> {
                // Beginner: pure random selection
                validMoves[Random.nextInt(validMoves.size)]
            }
            Difficulty.INTERMEDIATE -> {
                // Intermediate: Evaluates all direct moves and picks the one maximizing IMMEDIATE captures.
                var bestMove = validMoves[0]
                var maxCaptured = -1

                for (move in validMoves) {
                    val result = playTurn(state, move)
                    val captured = result.finalState.scoreP2 - state.scoreP2 // assuming P2 is AI
                    if (captured > maxCaptured) {
                        maxCaptured = captured
                        bestMove = move
                    }
                }
                
                // If no capture possible, pick a move that leads to more sowing steps or random
                if (maxCaptured == 0) {
                    validMoves[Random.nextInt(validMoves.size)]
                } else {
                    bestMove
                }
            }
            Difficulty.EXPERT -> {
                // Expert: Look ahead. AI (P2) wants to maximize custom score metric, which is:
                // scoreP2 - scoreP1 + total seeds on AI's side - opponent's capture vulnerability.
                var bestMove = validMoves[0]
                var bestUtility = Double.NEGATIVE_INFINITY

                for (move in validMoves) {
                    val aiTurn = playTurn(state, move)
                    val aiNextState = aiTurn.finalState
                    
                    // Basic evaluation of the resulting state
                    val aiCapture = aiNextState.scoreP2 - state.scoreP2
                    
                    // Simulate Player 1's best response to this resulting state
                    var worstP1Capture = 0
                    val opponentMoves = aiNextState.getValidMoves(Player.PLAYER_ONE)
                    for (opMove in opponentMoves) {
                        val p1Turn = playTurn(aiNextState, opMove)
                        val p1Capture = p1Turn.finalState.scoreP1 - aiNextState.scoreP1
                        if (p1Capture > worstP1Capture) {
                            worstP1Capture = p1Capture
                        }
                    }

                    // Total AI seeds remaining vs player 1 seeds remaining
                    val aiSeeds = (8..15).sumOf { aiNextState.board[it] }
                    val p1Seeds = (0..7).sumOf { aiNextState.board[it] }
                    
                    // Calculate weight of current move
                    val utility = (aiCapture * 10.0) - (worstP1Capture * 8.0) + (aiSeeds * 0.5) - (p1Seeds * 0.2) + (aiTurn.animationSequence.size * 0.1)
                    
                    if (utility > bestUtility) {
                        bestUtility = utility
                        bestMove = move
                    }
                }
                bestMove
            }
        }
    }
}
