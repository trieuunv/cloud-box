package org.example.project.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.example.project.SocketManager
import org.example.project.dto.Participant
import org.example.project.dto.RegisterDto

class RegisterViewModel : ViewModel() {
    val client = SocketManager

    var username by mutableStateOf("")
        private set

    var registeredUser: Participant? by mutableStateOf(null)
        private set

    fun onUsernameChanged(newValue: String) {
        username = newValue
    }

    init {
        client.on<Participant>("res_register") { participant ->
            registeredUser = participant
        }
    }

    fun register() {
        if (username.isNotBlank()) {
            val dto = RegisterDto(username)
            client.emit("register", dto)
        }
    }
}