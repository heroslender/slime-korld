package io.github.luizotavio.slimekorld.nbt

import io.github.luizotavio.slimekorld.generator.EmptyChunkGenerator
import io.github.luizotavio.slimekorld.loader.SlimeChunkLoader
import net.minecraft.server.v1_8_R3.*
import java.io.File

class SlimeNBTManager(
    file: File,
    name: String,
    private val slimeFile: File,
    private val data: WorldData
) : WorldNBTStorage(file, name, false) {

    override fun createChunkLoader(provider: WorldProvider?): IChunkLoader {
        EmptyChunkGenerator.setGenerator(uuid)

        return SlimeChunkLoader(slimeFile)
    }

    override fun getWorldData(): WorldData = data

    override fun save(entityhuman: EntityHuman) {}

    override fun load(entityhuman: EntityHuman): NBTTagCompound? = null

    override fun saveWorldData(worlddata: WorldData?) {}

    override fun saveWorldData(worlddata: WorldData?, nbttagcompound: NBTTagCompound?) {}
}