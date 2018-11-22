package utfpr.loyola.gabriel.viziseg.model

class Group(val name: String,
            val description: String,
            val userIds: MutableList<String>) {
    constructor(): this("", "", mutableListOf())
}