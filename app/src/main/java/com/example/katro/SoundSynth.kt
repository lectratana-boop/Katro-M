package com.example.katro

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundSynth {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isMuted: Boolean = false

    private fun playTone(
        frequency1: Double,
        frequency2: Double = 0.0,
        durationMs: Int,
        decay: Boolean = true,
        volume: Float = 0.8f
    ) {
        if (isMuted) return

        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val samples = FloatArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    
                    // Generate basic waveforms
                    var wave1 = sin(2.0 * Math.PI * frequency1 * t)
                    val wave2 = if (frequency2 > 0) sin(2.0 * Math.PI * frequency2 * t) else 0.0
                    
                    var value = (wave1 + wave2) / (if (frequency2 > 0) 2.0 else 1.0)
                    
                    if (decay) {
                        // Exponential decay of volume
                        val progress = i.toDouble() / numSamples
                        val decayFactor = Math.exp(-progress * 5.0)
                        value *= decayFactor
                    }

                    samples[i] = (value * volume).toFloat()
                }

                // Translate FloatArray into PCM 16-bit Byte data
                val bufferSize = numSamples * 2
                val pcmBuffer = ByteArray(bufferSize)
                for (i in 0 until numSamples) {
                    val floatVal = samples[i].coerceIn(-1.0f, 1.0f)
                    val shortVal = (floatVal * 32767.0f).toInt().toShort()
                    pcmBuffer[i * 2] = (shortVal.toInt() and 0xff).toByte()
                    pcmBuffer[i * 2 + 1] = ((shortVal.toInt() shr 8) and 0xff).toByte()
                }

                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STATIC
                )

                audioTrack.write(pcmBuffer, 0, pcmBuffer.size)
                audioTrack.play()
                
                // Release audioTrack resources after playback finishes
                scope.launch {
                    kotlinx.coroutines.delay(durationMs + 200L)
                    try {
                        audioTrack.stop()
                        audioTrack.release()
                    } catch (e: Exception) {
                        // Safe cleanup
                    }
                }
            } catch (e: Exception) {
                // Fail-safe
            }
        }
    }

    /**
     * Plays a realistic, crisp wood click sound of seed landing in a hole.
     */
    fun playSeedDrop() {
        // High-frequency short wood clock click
        playTone(frequency1 = 520.0, frequency2 = 300.0, durationMs = 45, decay = true, volume = 0.5f)
    }

    /**
     * Plays a melodic, satisfying capture bell sound.
     */
    fun playCapture() {
        // Bright G-major harmony chime
        playTone(frequency1 = 784.0, frequency2 = 987.77, durationMs = 300, decay = true, volume = 0.7f)
    }

    /**
     * Plays a sequence of notes representing a triumph or victory chord.
     */
    fun playVictory() {
        scope.launch {
            // Rising arpeggio (C5 - E5 - G5 - C6)
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            for (note in notes) {
                playTone(frequency1 = note, frequency2 = 0.0, durationMs = 250, decay = true, volume = 0.8f)
                kotlinx.coroutines.delay(120)
            }
        }
    }
}
