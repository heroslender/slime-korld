package io.github.luizotavio.slimekorld.storage

import io.github.luizotavio.slimekorld.SlimeDelegator
import io.github.luizotavio.slimekorld.SlimeWorld
import io.github.luizotavio.slimekorld.exception.SlimeStorageException
import io.github.luizotavio.slimekorld.impl.LegacySlimeWorld
import io.github.luizotavio.slimekorld.stream.SlimeOutputStream
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import java.io.File
import java.io.FileOutputStream

class LegacySlimeStorage(
    file: File
) : AbstractSlimeWorldStorage(file) {

    override fun save(slimeWorld: SlimeWorld) {
        val path = File(path, "${slimeWorld.name}.slime")

        val outputStream = SlimeOutputStream(
            FileOutputStream(path)
        )

        val world = slimeWorld.getWorld() as CraftWorld

        try {
            outputStream.writeAll(
                ArrayList(world.handle.chunkProviderServer.chunks.values()),
                true
            )
        } catch (exception: Exception) {
            throw SlimeStorageException("Failed to save SlimeWorld")
        }
    }

    override fun refresh(slimeWorld: SlimeWorld): World {
        if (slimeWorld !is LegacySlimeWorld) {
            throw SlimeStorageException("SlimeWorld must be of type LegacySlimeWorld")
        }

        val target = SlimeDelegator.createSlimeWorld(
            slimeWorld.file,
            slimeWorld.name
        ) ?: throw SlimeStorageException("Could not create SlimeWorld")

        slimeWorld.setWorld(
            target.getWorld()
        )

        return target.getWorld()
    }
}