package io.github.luizotavio.slimekorld

import org.bukkit.Difficulty
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import java.io.File

abstract class SlimeWorld(
    val file: File,
    val name: String,
    val gameMode: GameMode,
    val difficulty: Difficulty,
    val spawnLocation: Location? = null
) {

    abstract fun getWorld(): World

    abstract fun refresh(): World

    abstract fun save()

    abstract fun delete()

}
