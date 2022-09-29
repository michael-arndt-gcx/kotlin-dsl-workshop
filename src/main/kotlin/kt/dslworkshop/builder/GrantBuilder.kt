package kt.dslworkshop.builder

import kt.dslworkshop.authorization.condition.Condition
import kt.dslworkshop.domain.Floor
import kt.dslworkshop.authorization.Grant
import kotlin.reflect.KClass

class GrantBuilder {
    var target: KClass<Floor>? = null
    lateinit var permission: String
    var condition: Condition? = null

    fun build(): Grant = Grant(permission, target, condition)
}
