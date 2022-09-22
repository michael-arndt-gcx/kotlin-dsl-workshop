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
            permission = "JANITOR"
            target = Floor::class
            condition = Conjunction(
                Equals(User::id, Floor::ownerId),
                Equals(User::isAdmin, true)
            )
        }
        // TODO sollte nicht mehr verfügbar sein
        subject = User::class
    }
}

fun PrivilegeBuilder.grant(block: GrantBuilder.() -> Unit) {
    val grantBuilder = GrantBuilder()
    block(grantBuilder)
    val grant = grantBuilder.build()
    addGrant(grant)
}

fun privilege(subject: KClass<User>, block: PrivilegeBuilder.() -> Unit): Privilege {
    val privilegeBuilder = PrivilegeBuilder()
    privilegeBuilder.subject = subject
    block.invoke(privilegeBuilder)
    return privilegeBuilder.build()
}
