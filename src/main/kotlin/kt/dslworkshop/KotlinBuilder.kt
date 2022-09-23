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
        // TODO mache die Condition optional
        grant permission "JANITOR" whenAccessing Floor::class
        grant permission "JANITOR" whenAccessing Floor::class where {
            Conjunction(
                Equals(User::id, Floor::ownerId),
                Equals(User::isAdmin, true)
            )
        }
    }
}

object GrantKeyword

sealed interface GlobalGrantNode
sealed interface GrantNodeWithTarget
sealed interface GrantNodeWithCondition
class GrantBuilderFacade(val grantBuilder: GrantBuilder) : GlobalGrantNode,
    GrantNodeWithTarget,
    GrantNodeWithCondition {
    fun build(): Grant = grantBuilder.build()

    var condition: Condition?
        get() = grantBuilder.condition
        set(value) {
            grantBuilder.condition = value
        }
    var target: KClass<Floor>?
        get() = grantBuilder.target
        set(value) {
            grantBuilder.target = value
        }
}


class PrivilegeBuilderDslFacade(private val privilegeBuilder: PrivilegeBuilder) {
    val grant = GrantKeyword

    infix fun GrantKeyword.permission(permission: String): GlobalGrantNode =
        GrantBuilderFacade(GrantBuilder().apply {
            this.permission = permission
        })

    infix fun GlobalGrantNode.whenAccessing(target: KClass<Floor>): GrantNodeWithTarget {
        (this as GrantBuilderFacade).target = target
        return this
    }

    infix fun GrantNodeWithTarget.where(block: GrantBuilderDsl.() -> Conjunction) {
        (this as GrantBuilderFacade).condition = block.invoke(GrantBuilderDsl)
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
