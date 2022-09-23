@file:Suppress("UnusedImport")

package kt.dslworkshop

import kt.dslworkshop.authorization.Grant
import kt.dslworkshop.authorization.Privilege
import kt.dslworkshop.authorization.condition.Condition
import kt.dslworkshop.authorization.condition.Conjunction
import kt.dslworkshop.authorization.condition.Equals
import kt.dslworkshop.builder.GrantBuilder
import kt.dslworkshop.builder.PrivilegeBuilder
import kt.dslworkshop.domain.Floor
import kt.dslworkshop.domain.User
import kotlin.reflect.KClass

fun kotlinBuilderStyle(): Privilege {
    return privilege(User::class) {
        grant("JANITOR", Floor::class) {
            // permission und target hier nicht sichtbar

            // TODO dieses Lambda soll direkt die Condition liefern, die Property soll nicht mehr sichtbar sein
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
    fun grant(permission: String, target: KClass<Floor>? = null, block: GrantBuilderDslFacade.() -> Unit) {
        // Member statt Extension: privilegeBuilder kann private sein
        val grantBuilder = GrantBuilder().apply {
            this.permission = permission
            this.target = target
        }
        val facade = GrantBuilderDslFacade(grantBuilder)
        block(facade)
        val grant = grantBuilder.build()
        addGrant(grant)
    }
}

class GrantBuilderDslFacade(private val grantBuilder: GrantBuilder) {
    var condition: Condition?
        get() = grantBuilder.condition
        set(value) {
            grantBuilder.condition = value
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
