package org.example.project.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.dto.Message
import org.example.project.dto.ParticipantDto
import org.example.project.ui.chat.components.MessageInput
import org.example.project.ui.chat.components.MessageItem
import org.example.project.ui.chat.components.Sidebar
import org.example.project.ui.register.RegisterViewModel

class ChatScreen(val participant: ParticipantDto) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChatViewModel = viewModel()

        LaunchedEffect(participant) {
            viewModel.updateUser(participant)
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Sidebar(viewModel.participants)

            Column (modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(
                        count = viewModel.messages.size,
                        key = { index -> viewModel.messages[index].id }
                    ) { index ->
                        MessageItem(viewModel.user?.username, viewModel.messages[index], { message -> viewModel.downloadFile(message.file?.fileUrl, message.file?.fileName) })
                    }
                }

                MessageInput(viewModel)
            }
        }
    }
}