package kt.dslworkshop.builder

import kt.dslworkshop.authorization.Grant
import kt.dslworkshop.authorization.Privilege
import kotlin.reflect.KClass

class PrivilegeBuilder<T : Any> {
    lateinit var subject: KClass<T>
    private val grants: MutableList<Grant<*>> = mutableListOf()

    fun addGrant(grant: Grant<*>) {
        this.grants.add(grant)
    }

    fun build(): Privilege<T> = Privilege(subject, grants.toList())
}
