package io.github.luizotavio.slimekorld.util

import net.minecraft.server.v1_8_R3.EnumDifficulty
import net.minecraft.server.v1_8_R3.WorldSettings
import org.bukkit.Difficulty
import org.bukkit.GameMode

fun asGameMode(gameMode: GameMode): WorldSettings.EnumGamemode {
    return when (gameMode) {
        GameMode.SURVIVAL -> WorldSettings.EnumGamemode.SURVIVAL
        GameMode.CREATIVE -> WorldSettings.EnumGamemode.CREATIVE
        GameMode.ADVENTURE -> WorldSettings.EnumGamemode.ADVENTURE
        GameMode.SPECTATOR -> WorldSettings.EnumGamemode.SPECTATOR
    }
}

fun asDifficulty(difficulty: Difficulty): EnumDifficulty {
    return when (difficulty) {
        Difficulty.PEACEFUL -> EnumDifficulty.PEACEFUL
        Difficulty.EASY -> EnumDifficulty.EASY
        Difficulty.NORMAL -> EnumDifficulty.NORMAL
        Difficulty.HARD -> EnumDifficulty.HARD
    }
}