package utfpr.loyola.gabriel.viziseg.model

data class ChatChannel(val userIds: MutableList<String>,
                       val group: Boolean) {
    constructor() : this(mutableListOf(), false)
}