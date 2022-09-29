package kt.dslworkshop.builder

import kt.dslworkshop.authorization.Grant
import kt.dslworkshop.authorization.Privilege
import kt.dslworkshop.domain.User
import kotlin.reflect.KClass

class PrivilegeBuilder {
    lateinit var subject: KClass<User>
    private val grants: MutableList<Grant> = mutableListOf()

    fun addGrant(grant: Grant) {
        this.grants.add(grant)
    }

    fun build(): Privilege = Privilege(subject, grants.toList())
}
