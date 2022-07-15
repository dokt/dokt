package fi.papinkivi.hash

/** A message digest algorithm */
enum class Hash {
    MD5,
    SHA_1,
    SHA_256;

    val id = name.replace('_', '-')

    private val computer = HashComputer(id)

    fun hash(data: ByteArray) = computer.hash(data)

    fun hash(data: String) = hash(data.encodeToByteArray())

    override fun toString() = id
}

expect class HashComputer(algorithm: String) {
    /**
     * Creates one-way hash. This must be thread-safe.
     *
     * @param data arbitrary-sized data
     * @return Base64 encoded hash
     */
    fun hash(data: ByteArray): String
}