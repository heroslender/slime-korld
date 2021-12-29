package io.github.luizotavio.slimekorld.exception

data class SlimeStorageException(override val message: String) : Exception(message)

data class SlimeNotFoundException(override val message: String) : Exception(message)

data class SlimeAlreadyExistsException(override val message: String) : Exception(message)

data class SlimeFactoryException(override val message: String) : Exception(message)