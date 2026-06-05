package com.codenames.frontend.data.model

import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.ui.roles.PlayerRoles

data class GameState(
    val currentHint: String = "",
    val cards: List<GameCard> = emptyList(),
    val currentTurn: PlayerRoles = PlayerRoles.NONE,
    val winner: Team? = null,
    val remainingGuesses: Int = 0,
    val currentRedFound: Int = 0,
    val currentBlueFound: Int = 0,
    val chatLists: ChatLists = ChatLists(),
    val availableChatTabs: List<ChatTab> = listOf(ChatTab.GLOBAL),
)
