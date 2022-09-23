package kt.dslworkshop

import kt.dslworkshop.authorization.Privilege
import kt.dslworkshop.authorization.condition.Conjunction
import kt.dslworkshop.authorization.condition.Equals
import kt.dslworkshop.builder.GrantBuilder
import kt.dslworkshop.builder.PrivilegeBuilder
import kt.dslworkshop.domain.*

@Suppress("UnnecessaryVariable")
fun javaBuilderStyle(): Privilege<*> {
    val privilegeBuilder = PrivilegeBuilder<User>()

    val grantBuilder = GrantBuilder<Floor>()
    grantBuilder.permission = "JANITOR"
    grantBuilder.target = Floor::class
    grantBuilder.condition = Conjunction(
        Equals(User::id, Floor::ownerId),
        Equals(User::isAdmin, true)
    )
    privilegeBuilder.subject = User::class

    val grant = grantBuilder.build()
    privilegeBuilder.addGrant(grant)

    val globalGrantBuilder = GrantBuilder<Floor>()
    globalGrantBuilder.permission = "JANITOR"
    globalGrantBuilder.target = Floor::class
    globalGrantBuilder.condition = null

    val globalGrant = globalGrantBuilder.build()
    privilegeBuilder.addGrant(globalGrant)

    val privilege = privilegeBuilder.build()

    return privilege
}