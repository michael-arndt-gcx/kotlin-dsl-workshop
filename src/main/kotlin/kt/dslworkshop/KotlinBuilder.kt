@file:Suppress("UnusedImport")

package kt.dslworkshop

import kt.dslworkshop.authorization.Grant
import kt.dslworkshop.authorization.Privilege
import kt.dslworkshop.authorization.condition.Conjunction
import kt.dslworkshop.authorization.condition.Equals
import kt.dslworkshop.builder.GrantBuilder
import kt.dslworkshop.builder.PrivilegeBuilder
import kt.dslworkshop.domain.Floor
import kt.dslworkshop.domain.User
import kotlin.reflect.KClass

fun kotlinBuilderStyle(): Privilege {
    return privilege(User::class) {
        grant {
            // TODO permission und target als Parameter für grant und properties hier nicht sichtbar
            permission = "JANITOR"
            target = Floor::class
            condition = Conjunction(
                Equals(User::id, Floor::ownerId),
                Equals(User::isAdmin, true)
            )
        }
        // nicht mehr sichtbar
        //subject = User::class
        //addGrant(grant)
    }
}

class PrivilegeBuilderDslFacade(private val privilegeBuilder: PrivilegeBuilder) {
    private fun addGrant(grant: Grant) = privilegeBuilder.addGrant(grant)
    fun grant(block: GrantBuilder.() -> Unit) {
        // Member statt Extension: privilegeBuilder kann private sein
        val grantBuilder = GrantBuilder()
        block(grantBuilder)
        val grant = grantBuilder.build()
        addGrant(grant)
    }
}

fun privilege(subject: KClass<User>, block: PrivilegeBuilderDslFacade.() -> Unit): Privilege {
    val privilegeBuilder = PrivilegeBuilder().apply {
        this.subject = subject
    }
    val facade = PrivilegeBuilderDslFacade(privilegeBuilder)
    block.invoke(facade)
    return privilegeBuilder.build()
}
