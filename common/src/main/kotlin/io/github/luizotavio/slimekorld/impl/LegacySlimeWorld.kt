package io.github.luizotavio.slimekorld.impl

import io.github.luizotavio.slimekorld.SlimeWorld
import io.github.luizotavio.slimekorld.exception.SlimeNotFoundException
import org.bukkit.*
import java.io.File

class LegacySlimeWorld(
    file: File,
    name: String,
    gameMode: GameMode = GameMode.SURVIVAL,
    difficulty: Difficulty = Difficulty.NORMAL,
    spawnLocation: Location? = null
) : SlimeWorld(
    file,
    name,
    gameMode,
    difficulty,
    spawnLocation
) {

    private lateinit var world: World

    override fun getWorld(): World {
        if (!::world.isInitialized) {
            world = Bukkit.getWorld(name) ?: throw SlimeNotFoundException(name)
        }

        return world
    }

    override fun refresh(): World {
        TODO("not implemented")
    }

    override fun save() {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    internal fun setWorld(world: World) {
        this.world = world
    }
}