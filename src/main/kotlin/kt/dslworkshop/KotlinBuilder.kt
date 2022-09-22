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

fun kotlinBuilderStyle(): Privilege {
    return privilege {
        // TODO vergessen = exception -> zu Parameter
        subject = User::class

        val grant = grant {
            permission = "JANITOR"
            target = Floor::class
            condition = Conjunction(
                Equals(User::id, Floor::ownerId),
                Equals(User::isAdmin, true)
            )
        }

        // TODO addGrant soll in grant passieren
        addGrant(grant)
    }
}

fun grant(block: GrantBuilder.() -> Unit): Grant {
    val grantBuilder = GrantBuilder()
    block(grantBuilder)
    return grantBuilder.build()
}

fun privilege(block: PrivilegeBuilder.() -> Unit): Privilege {
    val privilegeBuilder = PrivilegeBuilder()
    block.invoke(privilegeBuilder)
    return privilegeBuilder.build()
}
