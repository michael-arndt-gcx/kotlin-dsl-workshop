package kt.dslworkshop.builder

import kt.dslworkshop.authorization.condition.Condition
import kt.dslworkshop.authorization.Grant
import kotlin.reflect.KClass

class GrantBuilder<T : Any> {
    var target: KClass<T>? = null
    lateinit var permission: String
    var condition: Condition? = null

    fun build(): Grant<T> = Grant(permission, target, condition)
}
