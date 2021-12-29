package io.github.luizotavio.slimekorld.factory

import io.github.luizotavio.slimekorld.SlimeWorld
import java.io.File

class LegacySlimeFactory : SlimeWorldFactory {
    override fun createSlimeWorld(file: File, name: String): SlimeWorld {
        TODO("Not yet implemented")
    }

    override fun createWorlds(directory: File): List<SlimeWorld> {
        TODO("Not yet implemented")
    }
}