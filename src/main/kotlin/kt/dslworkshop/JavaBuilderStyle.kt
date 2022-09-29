package kt.dslworkshop

import kt.dslworkshop.authorization.Privilege
import kt.dslworkshop.authorization.condition.Conjunction
import kt.dslworkshop.authorization.condition.Equals
import kt.dslworkshop.builder.GrantBuilder
import kt.dslworkshop.builder.PrivilegeBuilder
import kt.dslworkshop.domain.*

@Suppress("UnnecessaryVariable")
fun javaBuilderStyle(): Privilege {
    val privilegeBuilder = PrivilegeBuilder()

    val grantBuilder = GrantBuilder()
    grantBuilder.permission = "JANITOR"
    grantBuilder.target = Floor::class
    grantBuilder.condition = Conjunction(
        Equals(User::id, Floor::ownerId),
        Equals(User::isAdmin, true)
    )
    privilegeBuilder.subject = User::class

    val grant = grantBuilder.build()
    privilegeBuilder.addGrant(grant)

    val privilege = privilegeBuilder.build()

    return privilege
}