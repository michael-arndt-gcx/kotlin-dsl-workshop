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
        // TODO mit 2 einfachen Parametern eher unverständlich, mit infix Operator natürlicher Sprache annähern
        grant("JANITOR", Floor::class) {
            // dieses liefert direkt die Condition
            Conjunction(
                Equals(User::id, Floor::ownerId),
                Equals(User::isAdmin, true)
            )
        }
    }
}

class PrivilegeBuilderDslFacade(private val privilegeBuilder: PrivilegeBuilder) {
    private fun addGrant(grant: Grant) = privilegeBuilder.addGrant(grant)
    fun grant(permission: String, target: KClass<Floor>? = null, block: GrantBuilderDsl.() -> Condition) {
        val condition = block(GrantBuilderDsl)

        val grantBuilder = GrantBuilder().apply {
            this.permission = permission
            this.target = target
            this.condition = condition
        }

        val grant = grantBuilder.build()
        addGrant(grant)
    }
}

object GrantBuilderDsl

fun privilege(subject: KClass<User>, block: PrivilegeBuilderDslFacade.() -> Unit): Privilege {
    val privilegeBuilder = PrivilegeBuilder().apply {
        this.subject = subject
    }
    val facade = PrivilegeBuilderDslFacade(privilegeBuilder)
    block.invoke(facade)
    return privilegeBuilder.build()
}
