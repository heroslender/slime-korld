package io.github.luizotavio.slimekorld.stream

import com.github.luben.zstd.Zstd
import net.minecraft.server.v1_8_R3.*
import java.io.*
import java.util.*
import kotlin.math.ceil

class SlimeOutputStream(
    outputStream: OutputStream
): DataOutputStream(
    outputStream
) {

    companion object {
        internal val FILE_HEADER = 0xB10B.toShort()
    }

    @Throws(Exception::class)
    fun writeAll(chunks: MutableList<Chunk>, hasEntities: Boolean) {
        chunks.removeIf { it.sections.all { section -> Objects.isNull(section) } }

        writeShort(FILE_HEADER.toInt())
        writeByte(1)

        val lowestX = chunks.stream()
            .mapToInt { it.locX }
            .min()
            .orElse(1)

        val lowestZ = chunks.stream()
            .mapToInt { it.locZ }
            .min()
            .orElse(1)

        writeShort(lowestX)
        writeShort(lowestZ)

        val highestX = chunks.stream()
            .mapToInt { value: Chunk -> value.locX }
            .max()
            .orElse(1)

        val highestZ = chunks.stream()
            .mapToInt { value: Chunk -> value.locZ }
            .max()
            .orElse(1)

        val depth = highestZ - lowestZ + 1
        val width = highestX - lowestX + 1

        writeShort(width)
        writeShort(depth)

        val chunkBitset = BitSet(width * depth)

        chunks.sortWith(Comparator.comparingInt { it.locZ * Int.MAX_VALUE + it.locX })

        for (chunk in chunks) {
            chunkBitset[(chunk.locZ - lowestZ) * width + (chunk.locX - lowestX)] = true
        }

        val chunkSize = ceil(width * depth / 8.0).toInt()

        writeBitSetAsBytes(this, chunkBitset, chunkSize)

        val chunkData = writeCustomChunkFormat(chunks)
        val tileData = writeTileEntities(chunks)

        val compressChunkData = Zstd.compress(chunkData)
        val compressTileEntities = Zstd.compress(tileData)

        writeInt(compressChunkData.size)
        writeInt(chunkData.size)
        write(compressChunkData)
        writeInt(compressTileEntities.size)
        writeInt(tileData.size)
        write(compressTileEntities)

        writeBoolean(hasEntities)

        if (hasEntities) {
            val entityData = writeEntities(chunks)
            val compressed = Zstd.compress(entityData)

            writeInt(compressed.size)
            writeInt(entityData.size)
            write(compressChunkData)
        }
    }

    @Throws(IOException::class)
    private fun writeBitSetAsBytes(outStream: DataOutputStream, set: BitSet, fixedSize: Int) {
        val array = set.toByteArray()
        outStream.write(array)
        val chunkMaskPadding = fixedSize - array.size
        for (i in 0 until chunkMaskPadding) {
            outStream.write(0)
        }
    }

    private fun writeEntities(chunks: List<Chunk>): ByteArray {
        val compound = NBTTagCompound()
        val nbtTagList = NBTTagList()
        for (chunk in chunks) {
            val entities: MutableList<Entity> = Vector()
            for (behavior in chunk.getEntitySlices()) {
                entities.addAll(behavior)
            }
            for (entity in entities) {
                val data = NBTTagCompound()
                entity.e(data)
                nbtTagList.add(data)
            }
        }
        compound["entities"] = nbtTagList
        return writeCompound(compound)
    }

    private fun writeCompound(compound: NBTTagCompound): ByteArray {
        val outputStream = ByteArrayOutputStream()
        try {
            DataOutputStream(outputStream).use { stream ->
                NBTCompressedStreamTools.a(
                    compound,
                    stream as DataOutput
                )
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return outputStream.toByteArray()
    }

    private fun writeTileEntities(chunks: List<Chunk>): ByteArray {
        val compound = NBTTagCompound()
        val list = NBTTagList()
        for (chunk in chunks) {
            for (tileEntity in chunk.getTileEntities().values) {
                val target = NBTTagCompound()
                tileEntity.b(target)
                list.add(target)
            }
        }
        compound["tiles"] = list
        return writeCompound(compound)
    }

    @Throws(IOException::class)
    private fun writeCustomChunkFormat(chunks: List<Chunk>): ByteArray {
        val outputStream = ByteArrayOutputStream(16384)
        val out = DataOutputStream(outputStream)
        for (chunk in chunks) {
            for (value in chunk.heightMap) out.writeInt(value)
            out.write(chunk.biomeIndex)
            val sections = chunk.sections
            val sectionBitmask = BitSet(16)
            for (i in 0..15) {
                sectionBitmask[i] = i < sections.size && sections[i] != null
            }
            writeBitSetAsBytes(out, sectionBitmask, 2)
            for (section in sections) {
                if (section != null) {
                    val objects = toByteArray(section)
                    out.write(section.emittedLightArray.a())
                    out.write(objects[0] as ByteArray)
                    out.write((objects[1] as NibbleArray).a())
                    out.write(section.skyLightArray.a())
                    out.writeShort(0)
                }
            }
        }
        return outputStream.toByteArray()
    }

    private fun toByteArray(chunkSection: ChunkSection): Array<Any> {
        val ids = chunkSection.idArray
        val bytes = ByteArray(ids.size)
        val array = NibbleArray()
        for (k in ids.indices) {
            val c0 = ids[k]
            val l = k and 15
            val i1 = k shr 8 and 15
            val j1 = k shr 4 and 15
            bytes[k] = (c0.code shr 4 and 255).toByte()
            array.a(l, i1, j1, c0.code and 15)
        }
        return arrayOf(bytes, array)
    }


}