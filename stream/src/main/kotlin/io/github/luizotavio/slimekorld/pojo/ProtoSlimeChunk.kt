package io.github.luizotavio.slimekorld.pojo

import io.github.luizotavio.slimekorld.SlimeReaderUtil
import io.github.luizotavio.slimekorld.stream.SlimeInputStream
import net.minecraft.server.v1_8_R3.*
import java.io.IOException
import java.util.*

class ProtoSlimeChunk(
    val coords: ChunkCoordIntPair,
    val sections: Array<ChunkSection?>,
    val biomes: ByteArray,
    val heightMap: IntArray,
) {

    companion object {
        private val HEIGHTMAP_ENTRIES = 256
        private val BIOMES_LENGTH = 256
        private val SECTIONS_PER_CHUNK = 16
        private val BLOCKS_LENGTH = 4096

        @Throws(IOException::class)
        fun read(inputStream: SlimeInputStream, coords: ChunkCoordIntPair): ProtoSlimeChunk {
            val heightMap: IntArray = inputStream.readIntArray(HEIGHTMAP_ENTRIES)
            val biomes: ByteArray = inputStream.readByteArray(BIOMES_LENGTH)

            // Read sections
            val populatedSections: BitSet = inputStream.readBitSet(SECTIONS_PER_CHUNK / 8)
            val sections = arrayOfNulls<ChunkSection>(SECTIONS_PER_CHUNK)

            for (y in 0 until SECTIONS_PER_CHUNK) {
                if (!populatedSections[y]) {
                    continue
                }

                val yPos = y shl 4
                val section = ChunkSection(yPos, true) // skyLight

                inputStream.readNibbleArray(section.emittedLightArray)

                val blocks: ByteArray = inputStream.readByteArray(BLOCKS_LENGTH)
                val data: NibbleArray = inputStream.readNibbleArray()

                SlimeReaderUtil.readBlockIds(section.idArray, blocks, data)

                inputStream.readNibbleArray(section.skyLightArray)

                // Skip custom extra data
                inputStream.skipBytes(inputStream.readInt())

                section.recalcBlockCounts()

                sections[y] = section
            }

            return ProtoSlimeChunk(coords, sections, biomes, heightMap)
        }
    }

    private var tileEntities: MutableList<NBTTagCompound>? = null
    private var entities: MutableList<NBTTagCompound>? = null

    fun addTileEntity(compound: NBTTagCompound) {
        if (tileEntities == null) {
            tileEntities = ArrayList()
        }
        tileEntities!!.add(compound)
    }

    fun addEntity(compound: NBTTagCompound) {
        if (entities == null) {
            entities = ArrayList()
        }
        entities!!.add(compound)
    }

    /**
     * Adds the entities of this proto chunk to the specified
     * Minecraft chunk.
     *
     * @param world the world the chunk is in
     * @param chunk the chunk to add the entities to
     */
    private fun loadEntities(world: World, chunk: Chunk) {
        if (entities != null) {
            for (compound in entities!!) {
                var entity = EntityTypes.a(compound, world)
                chunk.g(true)
                if (entity == null) {
                    continue
                }
                chunk.a(entity)

                // Add riding entities
                var riding = compound
                while (riding.hasKeyOfType("Riding", 10)) {
                    val other = EntityTypes.a(riding.getCompound("Riding"), world) ?: break
                    chunk.a(other)
                    entity!!.mount(other)
                    entity = other
                    riding = riding.getCompound("Riding")
                }
            }
        }
        if (tileEntities != null) {
            for (compound in tileEntities!!) {
                val tileEntity = TileEntity.c(compound)
                if (tileEntity != null) {
                    chunk.a(tileEntity)
                }
            }
        }
    }

    /**
     * Converts this proto chunk into a Minecraft chunk.
     *
     * @param world the world the chunk is in
     * @return the loaded chunk
     */
    fun load(world: World): Chunk {
        val chunk = Chunk(world, coords.x, coords.z)
        chunk.a(heightMap)
        chunk.d(true) // TerrainPopulated
        chunk.e(true) // LightPopulated
        chunk.c(0) // InhabitedTime
        chunk.a(sections)
        chunk.a(biomes)
        loadEntities(world, chunk)
        return chunk
    }
}