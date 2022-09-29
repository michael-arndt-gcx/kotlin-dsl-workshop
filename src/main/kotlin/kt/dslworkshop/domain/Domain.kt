package kt.dslworkshop.domain

data class User(val id: Int, val isAdmin: Boolean)
class Floor(val ownerId: Int)
