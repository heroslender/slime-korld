package io.github.luizotavio.slimekorld.factory

import io.github.luizotavio.slimekorld.SlimeWorld
import io.github.luizotavio.slimekorld.generator.EmptyChunkGenerator
import io.github.luizotavio.slimekorld.impl.LegacySlimeWorld
import io.github.luizotavio.slimekorld.nbt.SlimeNBTManager
import io.github.luizotavio.slimekorld.util.asDifficulty
import io.github.luizotavio.slimekorld.util.asGameMode
import net.minecraft.server.v1_8_R3.*
import net.minecraft.server.v1_8_R3.WorldType
import org.bukkit.*
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboardManager
import java.io.File
import java.util.concurrent.ThreadLocalRandom


class LegacySlimeFactory : SlimeWorldFactory {

    companion object {
        val MINECRAFT_SERVER = MinecraftServer.getServer()
        val WORLD_SERVERS = MINECRAFT_SERVER.worlds
        val RANDOM = ThreadLocalRandom.current()
    }

    override fun createSlimeWorld(
        file: File,
        name: String,
        gameMode: GameMode,
        difficulty: Difficulty,
        spawnLocation: Location?
    ): SlimeWorld {
        var dimension = CraftWorld.CUSTOM_DIMENSION_OFFSET + WORLD_SERVERS.size
        var used = false
        do {
            for (server in WORLD_SERVERS) {
                used = server.dimension == dimension
                if (used) {
                    dimension++
                    break
                }
            }
        } while (used)

        val worldSettings = WorldSettings(
            RANDOM.nextLong(),
            asGameMode(gameMode),
            false,
            false,
            WorldType.CUSTOMIZED
        )

        val data = WorldData(worldSettings, name)

        data.difficulty = asDifficulty(difficulty)

        val slimeManager = SlimeNBTManager(
            File(Bukkit.getWorldContainer(), name),
            name,
            file,
            data
        )

        val internal = WorldServer(
            MINECRAFT_SERVER,
            slimeManager,
            slimeManager.worldData,
            dimension,
            MINECRAFT_SERVER.methodProfiler,
            World.Environment.NORMAL,
            EmptyChunkGenerator()
        ).b() as WorldServer

        if (spawnLocation != null) {
            internal.B(
                BlockPosition(spawnLocation.blockX, spawnLocation.blockY, spawnLocation.blockZ)
            )
        }

        internal.scoreboard = (Bukkit.getScoreboardManager() as CraftScoreboardManager).mainScoreboard.handle
        internal.tracker = EntityTracker(internal)
        internal.addIWorldAccess(WorldManager(MINECRAFT_SERVER, internal))
        WORLD_SERVERS.add(internal)

        val world = LegacySlimeWorld(
            file,
            name,
        )

        world.setWorld(internal.world)

        return world
    }

    override fun createWorlds(directory: File): List<SlimeWorld> {
        val worlds = mutableListOf<SlimeWorld>()

        for (file in directory.listFiles()) {
            if (file.isDirectory) continue

            val slimeWorld = createSlimeWorld(file)

            worlds.add(slimeWorld)
        }

        return worlds
    }
}