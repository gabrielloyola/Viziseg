package utfpr.loyola.gabriel.viziseg.model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}