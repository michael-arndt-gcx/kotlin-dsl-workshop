package kt.dslworkshop.authorization

import kt.dslworkshop.authorization.condition.Condition
import kt.dslworkshop.domain.Floor
import kt.dslworkshop.domain.User
import kotlin.reflect.KClass

data class Grant(val permission: String, val target: KClass<Floor>?, val condition: Condition?)
data class Privilege(val subject: KClass<User>, val grants: List<Grant>)
