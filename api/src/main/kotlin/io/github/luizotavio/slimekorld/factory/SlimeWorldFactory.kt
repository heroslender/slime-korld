package io.github.luizotavio.slimekorld.factory

import io.github.luizotavio.slimekorld.SlimeWorld
import io.github.luizotavio.slimekorld.exception.SlimeAlreadyExistsException
import io.github.luizotavio.slimekorld.exception.SlimeFactoryException
import io.github.luizotavio.slimekorld.exception.SlimeNotFoundException
import org.bukkit.Difficulty
import org.bukkit.GameMode
import org.bukkit.Location
import java.io.File
import kotlin.jvm.Throws

interface SlimeWorldFactory {

    @Throws(SlimeFactoryException::class, SlimeNotFoundException::class, SlimeAlreadyExistsException::class)
    fun createSlimeWorld(
        file: File,
        name: String = file.nameWithoutExtension,
        gameMode: GameMode = GameMode.SURVIVAL,
        difficulty: Difficulty = Difficulty.NORMAL,
        spawnLocation: Location? = null
    ): SlimeWorld

    @Throws(SlimeFactoryException::class, SlimeNotFoundException::class)
    fun createWorlds(directory: File): List<SlimeWorld>

}