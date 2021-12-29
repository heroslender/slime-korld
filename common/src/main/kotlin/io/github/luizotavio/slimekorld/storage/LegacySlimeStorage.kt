package io.github.luizotavio.slimekorld.storage

import io.github.luizotavio.slimekorld.SlimeWorld
import java.io.File

class LegacySlimeStorage(
    file: File
) : AbstractSlimeWorldStorage(file) {

    private val files: MutableMap<String, File> = hashMapOf()

    override fun save(slimeWorld: SlimeWorld) {
        TODO("Not yet implemented")
    }

    override fun exists(name: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun refresh(slimeWorld: SlimeWorld) {
        TODO("Not yet implemented")
    }
}