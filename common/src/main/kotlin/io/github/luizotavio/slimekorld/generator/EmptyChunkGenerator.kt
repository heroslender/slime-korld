package io.github.luizotavio.slimekorld.generator

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.generator.ChunkGenerator
import java.util.*

class EmptyChunkGenerator : ChunkGenerator() {

    companion object {
        /**
         * Sets the generator of the world specified by `worldUuid`
         * to [EmptyChunkGenerator].
         *
         * This method should be called from [ServerNBTManager.createChunkLoader],
         * right before `WorldServer#k()` accesses [WorldServer.generator].
         *
         * @param worldUuid the UUID of the world
         */
        fun setGenerator(worldUuid: UUID?) {
            val craftWorld = Bukkit.getWorld(worldUuid) as CraftWorld
            craftWorld.handle.generator = EmptyChunkGenerator()
        }
    }

    override fun generateBlockSections(
        world: World,
        random: Random?,
        x: Int,
        z: Int,
        biomes: BiomeGrid?
    ): Array<ByteArray?> {
        // Leave all sections as null, meaning they aren't populated.
        return arrayOfNulls(world.maxHeight / 16)
    }
}