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
    return forSubject(User::class) {
        // TODO verhindere `grant permission "JANITOR" whenAccessing Floor::class whenAccessing Floor::class`
        grant permission "JANITOR" whenAccessing Floor::class where {
            Conjunction(
                Equals(User::id, Floor::ownerId),
                Equals(User::isAdmin, true)
            )
        }
    }
}

object GrantKeyword

class PrivilegeBuilderDslFacade(private val privilegeBuilder: PrivilegeBuilder) {
    val grant = GrantKeyword

    infix fun GrantKeyword.permission(permission: String): GrantBuilder = GrantBuilder().apply {
        this.permission = permission
    }

    infix fun GrantBuilder.whenAccessing(target: KClass<Floor>): GrantBuilder {
        this.target = target
        return this
    }

    infix fun GrantBuilder.where(block: GrantBuilderDsl.() -> Conjunction) {
        this.condition = block.invoke(GrantBuilderDsl)
        addGrant(this.build())
    }
    
    private fun addGrant(grant: Grant) = privilegeBuilder.addGrant(grant)
}

object GrantBuilderDsl

fun forSubject(subject: KClass<User>, block: PrivilegeBuilderDslFacade.() -> Unit): Privilege {
    val privilegeBuilder = PrivilegeBuilder().apply {
        this.subject = subject
    }
    val facade = PrivilegeBuilderDslFacade(privilegeBuilder)
    block.invoke(facade)
    return privilegeBuilder.build()
}
