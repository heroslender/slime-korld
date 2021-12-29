package io.github.luizotavio.slimekorld

import io.github.luizotavio.slimekorld.factory.SlimeWorldFactory
import io.github.luizotavio.slimekorld.storage.AbstractSlimeWorldStorage
import java.io.File

class SlimeDelegator {

    internal class SlimeFacade(
        private val abstractSlimeWorldStorage: AbstractSlimeWorldStorage,
        private val slimeWorldFactory: SlimeWorldFactory
    ) {

        fun createSlimeWorld(name: String, file: File): SlimeWorld? {
            var slimeWorld: SlimeWorld? = null

            try {
                slimeWorld = slimeWorldFactory.createSlimeWorld(file, name)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return slimeWorld
        }

        fun createSlimeWorld(file: File): SlimeWorld? {
            return createSlimeWorld(file.nameWithoutExtension, file)
        }

        fun save(slimeWorld: SlimeWorld) {
            abstractSlimeWorldStorage.save(slimeWorld)
        }

        fun exists(name: String): Boolean {
            return abstractSlimeWorldStorage.exists(name)
        }
    }

}