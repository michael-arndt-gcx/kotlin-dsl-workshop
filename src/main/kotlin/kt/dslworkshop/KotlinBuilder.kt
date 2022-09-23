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
        // TODO entferne alle Klammern
        (grant permission ("JANITOR" whenAccessing Floor::class)) where {
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

    infix fun GrantKeyword.permission(pair: Pair<String, KClass<Floor>>) : GrantBuilder = GrantBuilder().apply {
        this.permission = pair.first
        this.target = pair.second
    }

    infix fun GrantBuilder.where(block: GrantBuilderDsl.() -> Conjunction) {
        this.condition = block.invoke(GrantBuilderDsl)
        addGrant(this.build())
    }

    //.also { privilegeBuilder.addGrant(it) }

    private fun addGrant(grant: Grant) = privilegeBuilder.addGrant(grant)
    fun grant(pair: Pair<String, KClass<Floor>>, block: GrantBuilderDsl.() -> Condition) {
        val (permission, target) = pair
        return grant(permission, target, block)
    }

    @Suppress("MemberVisibilityCanBePrivate")
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

    infix fun String.whenAccessing(target: KClass<Floor>) = Pair(this, target)
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
