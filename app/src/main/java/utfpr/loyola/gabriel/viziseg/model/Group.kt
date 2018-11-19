package utfpr.loyola.gabriel.viziseg.model

class Group(val name: String,
            val description: String,
            val groupPicturePath: String?,
            val userIds: MutableList<String>) {
    constructor(): this("", "", null, mutableListOf())
}