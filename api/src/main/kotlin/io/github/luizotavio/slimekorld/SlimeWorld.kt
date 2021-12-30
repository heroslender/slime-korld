package io.github.luizotavio.slimekorld

import org.bukkit.World
import java.io.File

abstract class SlimeWorld(
    val file: File,
    val name: String
) {

    abstract fun getWorld(): World

    abstract fun refresh(): World

    abstract fun save()

    abstract fun delete()

}
