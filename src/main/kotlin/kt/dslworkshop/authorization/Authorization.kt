package kt.dslworkshop.authorization

import kt.dslworkshop.authorization.condition.Condition
import kotlin.reflect.KClass

data class Grant<T : Any>(val permission: String, val target: KClass<T>?, val condition: Condition?)
data class Privilege<S : Any>(val subject: KClass<S>, val grants: List<Grant<*>>)
