package io.github.luizotavio.slimekorld.stream

import com.github.luben.zstd.Zstd
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.minecraft.server.v1_8_R3.NibbleArray
import java.io.*
import java.util.*

class SlimeInputStream(
    inputStream: InputStream
): DataInputStream(inputStream) {

    /**
     * The number of bytes needed to store a [NibbleArray].
     */
    private val NIBBLE_ARRAY_LENGTH = 2048

    @Throws(IOException::class)
    fun readIntArray(count: Int): IntArray {
        val arr = IntArray(count)
        for (i in 0 until count) {
            arr[i] = readInt()
        }
        return arr
    }

    @Throws(IOException::class)
    fun readByteArray(length: Int): ByteArray {
        val arr = ByteArray(length)
        val readByteCount = read(arr)
        if (readByteCount == -1) {
            throw EOFException()
        }
        return arr
    }

    /**
     * Reads and parses the chunk nibble array.
     *
     * @return the nibble array
     * @throws IOException if the bytes cannot be read
     * @see .NIBBLE_ARRAY_LENGTH
     */
    @Throws(IOException::class)
    fun readNibbleArray(): NibbleArray {
        return NibbleArray(
            readByteArray(NIBBLE_ARRAY_LENGTH)
        )
    }

    /**
     * Writes the next [.NIBBLE_ARRAY_LENGTH] bytes
     * to the specified nibble array.
     *
     * @param nibbleArray the nibble array to write to
     * @return the number of read bytes
     * @throws IOException if the bytes cannot be read
     */
    @Throws(IOException::class)
    fun readNibbleArray(nibbleArray: NibbleArray): Int {
        return read(nibbleArray.a())
    }

    @Throws(IOException::class)
    fun readBitSet(byteCount: Int): BitSet {
        return BitSet.valueOf(
            readByteArray(byteCount)
        )
    }

    /**
     * Reads a block of zstd-compressed data. This method
     * expects the following ints to be the compressed size,
     * and uncompressed size respectively.
     *
     * @return the uncompressed data
     * @throws IOException if the bytes cannot be read
     * @throws IllegalArgumentException if the uncompressed length doesn't match
     */
    @Throws(IOException::class)
    fun readCompressed(): ByteArray {
        val compressedLength = readInt()
        val uncompressedLength = readInt()
        val compressed = readByteArray(compressedLength)
        val data = Zstd.decompress(compressed, uncompressedLength)
        require(data.size == uncompressedLength) { "Uncompressed length doesn't match" }
        return data
    }

    /**
     * Reads and parses a block of zstd-compressed bytes as
     * an NBT named compound tag.
     *
     * @return the parsed named compound tag.
     * @throws IOException if the bytes cannot be read
     * @see .readCompressed
     */
    @Throws(IOException::class)
    fun readCompressedCompound(): NBTTagCompound {
        val data = readCompressed()
        return NBTCompressedStreamTools.a(
            DataInputStream(
                ByteArrayInputStream(data)
            )
        )
    }

    /**
     * Skips a block of zstd-compressed data.
     *
     * @return the number of bytes skipped
     * @throws IOException if the bytes cannot be skipped
     * @see .readCompressed
     */
    @Throws(IOException::class)
    fun skipCompressed(): Long {
        val compressedLength = readInt()

        // Skip uncompressed length + compressed data
        return skip((4 + compressedLength).toLong())
    }

}