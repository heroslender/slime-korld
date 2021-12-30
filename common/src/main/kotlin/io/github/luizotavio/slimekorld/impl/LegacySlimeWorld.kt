package io.github.luizotavio.slimekorld.impl

import io.github.luizotavio.slimekorld.SlimeDelegator
import io.github.luizotavio.slimekorld.SlimeWorld
import io.github.luizotavio.slimekorld.exception.SlimeNotFoundException
import org.bukkit.Bukkit
import org.bukkit.World
import java.io.File

class LegacySlimeWorld(
    file: File,
    name: String,
) : SlimeWorld(
    file,
    name
) {

    private lateinit var world: World

    override fun getWorld(): World {
        if (!::world.isInitialized) {
            world = Bukkit.getWorld(name) ?: throw SlimeNotFoundException(name)
        }

        return world
    }

    override fun refresh(): World {
        if (::world.isInitialized) {
            Bukkit.unloadWorld(world, false)

            val storage = SlimeDelegator.getStorage()

            this.world = storage.refresh(this)
        }

        return world
    }

    override fun save() = SlimeDelegator.save(this)

    override fun delete() {
        Bukkit.unloadWorld(world, false)

        val path = world.worldFolder

        if (path.exists()) {
            path.deleteRecursively()
        }
    }

    internal fun setWorld(world: World) {
        this.world = world
    }
}