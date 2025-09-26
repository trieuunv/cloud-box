package org.example.project.dto

import kotlinx.serialization.Serializable
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import java.util.UUID

data class ClientSocket(
    val id: String = UUID.randomUUID().toString(),
    val socket: Socket,
    val output: ByteWriteChannel
)

@Serializable
data class SocketMessage(
    val event: String,
    val data: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)