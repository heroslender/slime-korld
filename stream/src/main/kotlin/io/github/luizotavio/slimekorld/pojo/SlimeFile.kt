package io.github.luizotavio.slimekorld.pojo

import gnu.trove.map.hash.TLongObjectHashMap
import io.github.luizotavio.slimekorld.stream.SlimeInputStream
import io.github.luizotavio.slimekorld.stream.SlimeOutputStream
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.minecraft.server.v1_8_R3.NBTTagList
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.ceil

class SlimeFile(
    val version: Int,
    val minX: Short,
    val minZ: Short,
    val width: Short,
    val depth: Short,
    val populatedChunks: BitSet,
    val chunkData: ByteArray,
    val entities: NBTTagList,
    val tileEntities: NBTTagList
) {

    companion object {
        val SUPPORTED_VERSIONS = setOf(1, 3)

        @Throws(IOException::class)
        fun read(file: File): SlimeFile {
            SlimeInputStream(file.inputStream()).use {
                val header = it.readShort()

                if (header != SlimeOutputStream.FILE_HEADER) {
                    throw IOException("Invalid header: $header")
                }

                val version: Int = it.read()

                if (!SUPPORTED_VERSIONS.contains(version)) {
                    throw IOException("Unsupported version: $version")
                }

                // Lowest chunk coordinates
                val minX: Short = it.readShort()
                val minZ: Short = it.readShort()

                // X-axis and Z-axis length respectively
                val width = it.readShort()
                val depth = it.readShort()

                val bitSetLength = ceil(width * depth / 8.0).toInt()

                val populatedChunks: BitSet = it.readBitSet(bitSetLength)
                val chunkData: ByteArray = it.readCompressed()
                val tileEntities: NBTTagCompound = it.readCompressedCompound()

                var entities = NBTTagCompound()

                if (version == 3 || version == 1) {
                    val hasEntities: Boolean = it.readBoolean()
                    if (hasEntities) {
                        entities = it.readCompressedCompound()
                    }

                    // Skip extra data
                    it.skipCompressed()
                }
                return SlimeFile(
                    version,
                    minX,
                    minZ,
                    width,
                    depth,
                    populatedChunks,
                    chunkData,
                    entities.getList("entities", 10),
                    tileEntities.getList("tiles", 10)
                )
            }
        }

    }

    val protoChunks = TLongObjectHashMap<ProtoSlimeChunk>()

    init {
        createProtoChunks()
    }

    private fun getChunkCoords(bitIndex: Int): ChunkCoordIntPair {
        return ChunkCoordIntPair(
            bitIndex % width + minX, bitIndex / width + minZ
        )
    }

    /**
     * Gets the proto chunk at the specified block coordinates.
     *
     * @param x the x-coordinate
     * @param z the z-coordinate
     * @return the proto chunk, or `null` if not populated
     */
    fun getProtoChunkAt(x: Int, z: Int): ProtoSlimeChunk? {
        return protoChunks[LongHash.toLong(x shr 4, z shr 4)]
    }

    @Throws(IOException::class)
    private fun createProtoChunks() {
        val stream = SlimeInputStream(
            ByteArrayInputStream(chunkData)
        )
        for (i in 0 until populatedChunks.length()) {
            if (!populatedChunks[i]) {
                // Non-populated chunk
                continue
            }

            val coords = getChunkCoords(i)

            protoChunks.put(
                LongHash.toLong(coords.x, coords.z),
                ProtoSlimeChunk.read(stream, coords)
            )
        }

        loadEntities()
    }

    private fun loadEntities() {
        // Add each entity to its proto chunk
        for (i in 0 until entities.size()) {
            val entityData = entities[i]
            val position = entityData.getList("Pos", 6)
            val x = position.d(0).toInt()
            val z = position.d(2).toInt()
            val chunk = getProtoChunkAt(x, z)
            chunk?.addEntity(entityData)
        }

        // Add each tile entity to its proto chunk
        for (i in 0 until tileEntities.size()) {
            val tileData = tileEntities[i]
            val x = tileData.getInt("x")
            val z = tileData.getInt("z")
            val chunk = getProtoChunkAt(x, z)
            chunk?.addTileEntity(tileData)
        }
    }

}