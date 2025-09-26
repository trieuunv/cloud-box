package org.example.project.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    val username: String
)

@Serializable
data class Participant(
    val id: String,
    val username: String
)

@Serializable
data class Message(
    val id: String,
    val sender: String,
    val content: String? = null,
    val type: String,
    val file: FileMessage?,
    val timestamp: Long
)

@Serializable
data class FileMessage(
    val id: String,
    val fileName: String,
    val fileUrl: String,
    val fileSize: Long,
)

@Serializable
data class MessageSend(
    val sender: String,
    val content: String? = null,
    val type: String,
    val file: FileSend? = null
)

@Serializable
data class FileSend(
    val filename: String,
    val filesize: Long,
    val filedata: String
)

@Serializable
data class DownloadFile(
    val path: String
)

@Serializable
data class FileResponse(
    val fileName: String,
    val fileSize: Long,
    val fileData: String
)

