package org.example.project

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import org.example.project.dto.*
import org.example.project.utils.FileUtils
import java.io.File
import java.util.*

object ChatController {
    val server = ServerSocketManager
    val clientParticipants = Collections.synchronizedMap(mutableMapOf<ClientSocket, ParticipantDto>())
    val messages = Collections.synchronizedList(mutableListOf<Message>())

    fun startServer() {
        server.start();

        server.onConnection { client ->
            CoroutineScope(Dispatchers.IO).launch {
                server.join("chat_room", client)
            }
        }

        server.onDisconnection { client ->
            CoroutineScope(Dispatchers.IO).launch {
                val participant = clientParticipants.remove(client)
                if (participant != null) {
                    server.broadcastToRoom("chat_room", "participant_left", participant, ParticipantDto.serializer())
                    val message = Message(
                        id = UUID.randomUUID().toString(),
                        sender = "0",
                        content = "${participant.username} left the chat",
                        type = "system",
                        file = null,
                        timestamp = System.currentTimeMillis()
                    )

                    server.broadcastToRoom("chat_room", "message", message, Message.serializer())
                }
            }
        }

        server.subscribe<RegisterDto>("register") { client, data ->
            CoroutineScope(Dispatchers.IO).launch {
                val participant = ParticipantDto(username = data.username)
                clientParticipants[client] = participant

                server.sendToClient(client, "res_register", participant, ParticipantDto.serializer())

                server.broadcastToRoom("chat_room", "new_participant", participant, ParticipantDto.serializer())

                val message = Message(
                    id = UUID.randomUUID().toString(),
                    sender = "0",
                    content = "${data.username} joined the chat",
                    type = "system",
                    file = null,
                    timestamp = System.currentTimeMillis()
                )

                server.broadcastToRoom("chat_room", "message", message, Message.serializer())
            }
        }

        server.subscribe("get_initial_participant") { client ->
            println("get_initial_participant")
            CoroutineScope(Dispatchers.IO).launch {
                println(clientParticipants.values.toList())
                server.sendToClient(client, "initial_participant", clientParticipants.values.toList(), ListSerializer(ParticipantDto.serializer()))
            }
        }

        server.subscribe<MessageSendDto>("message") { client, messsageSend ->
            var fileMessage: FileMessage? = null

            if (messsageSend.type == "file" || messsageSend.type == "image") {
                if (messsageSend.file != null) {
                    val safeFileName = "${UUID.randomUUID()}_${messsageSend.file.filename}"
                    val outputPath = "uploads/$safeFileName"
                    FileUtils.base64ToFile(messsageSend.file.filedata, outputPath)
                    fileMessage = FileMessage(
                        id = UUID.randomUUID().toString(),
                        fileName = messsageSend.file.filename,
                        fileUrl = outputPath,
                        fileSize = messsageSend.file.filesize
                    )
                }
            }

            val newMessage = Message(
                id = UUID.randomUUID().toString(),
                sender = messsageSend.sender,
                content = messsageSend.content,
                type = messsageSend.type,
                file = fileMessage,
                timestamp = System.currentTimeMillis()
            )

            messages.add(newMessage)

            CoroutineScope(Dispatchers.IO).launch {
                server.broadcastToRoom("chat_room", "message", newMessage, Message.serializer())
            }
        }

        server.subscribe<DownloadFile>("download_file") { client, request ->
            CoroutineScope(Dispatchers.IO).launch {
                val file = File(request.path)
                if (file.exists()) {
                    val fileData = Base64.getEncoder().encodeToString(file.readBytes())
                    val fileResponse = FileResponse(
                        fileName = file.name,
                        fileData = fileData,
                        fileSize = file.length()
                    )
                    server.sendToClient(client, "res_download_file", fileResponse, FileResponse.serializer())
                } else {
                    // server.sendToClient(client, "res_download_file_error", "File not found")
                }
            }
        }
    }
}