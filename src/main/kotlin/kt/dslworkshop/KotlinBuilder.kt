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
import kotlin.reflect.KProperty1

fun kotlinBuilderStyle(): Privilege<*> {
    return forSubject<User> {
        grant permission "JANITOR" whenAccessing Floor::class where {
            //TODO: hier kann
            // grant permission "USER"
            // aufgerufen werden

            // TODO: ConditionBuilderDsl ist ein object, eq kann deshalb statisch importiert werden
            User::id eq Floor::ownerId and User::isAdmin eq true
        }
        grant permission "JANITOR" whenAccessing Floor::class
    }
}

object GrantKeyword

sealed interface GlobalGrantNode<T : Any>
sealed interface GrantNodeWithTarget<T : Any>
sealed interface GrantNodeWithCondition
class GrantBuilderFacade<T : Any>(val grantBuilder: GrantBuilder<T>) : GlobalGrantNode<T>,
    GrantNodeWithTarget<T>,
    GrantNodeWithCondition {
    fun build(): Grant<*> = grantBuilder.build()

    var condition: Condition?
        get() = grantBuilder.condition
        set(value) {
            grantBuilder.condition = value
        }
    var target: KClass<T>?
        get() = grantBuilder.target
        set(value) {
            grantBuilder.target = value
        }
}


class PrivilegeBuilderDsl {
    val grant = GrantKeyword
    val grantBuilderFacades: MutableList<GrantBuilderFacade<*>> = mutableListOf()

    infix fun GrantKeyword.permission(permission: String): GlobalGrantNode<Nothing> =
        GrantBuilderFacade(GrantBuilder<Nothing>().apply {
            this.permission = permission
        }).also(grantBuilderFacades::add)

    infix fun <T : Any> GlobalGrantNode<Nothing>.whenAccessing(target: KClass<T>): GrantNodeWithTarget<T> {
        @Suppress("UNCHECKED_CAST")
        (this as GrantBuilderFacade<T>).target = target
        return this
    }

    infix fun <T : Any> GrantNodeWithTarget<T>.where(block: ConditionBuilderDsl.() -> Condition) {
        (this as GrantBuilderFacade<T>).condition = block.invoke(ConditionBuilderDsl)
    }
}

object ConditionBuilderDsl {
    // TODO der code is sehr unübersichtlich geworden. Vereinfache durch Abstraktionsschicht
    infix fun Condition.and(other: Condition) = Conjunction(this, other)
    infix fun <V> KProperty1<*, V>.eq(other: V): Equals = Equals(this, other)
    infix fun <V> KProperty1<*, V>.eq(other: KProperty1<*, V>): Equals = Equals(this, other)
    infix fun <V> Condition.and(right: KProperty1<*, V>) = IncompleteConjunctionWithProperty(this, right)
    infix fun <V> Condition.and(right: V) = IncompleteConjunctionWithValue(this, right)
    infix fun <V> IncompleteConjunctionWithProperty<V>.eq(other: V): Condition =
        left and (this.right eq other)
    infix fun <V> IncompleteConjunctionWithProperty<V>.eq(other: KProperty1<*, V>): Condition =
        left and (this.right eq other)
    infix fun <V> IncompleteConjunctionWithValue<V>.eq(other: KProperty1<*, V>): Condition =
        left and (this.right eq other)
    infix fun <V> V.eq(other: KProperty1<*, V>): Equals = Equals(this, other)
}

// Beachte: IncompleteConjunctionWithInstanceNode ist keine Condition!
data class IncompleteConjunctionWithProperty<V>(val left: Condition, val right: KProperty1<*, V>)
data class IncompleteConjunctionWithValue<V>(val left: Condition, val right: V)

inline fun <reified T : Any> forSubject(block: PrivilegeBuilderDsl.() -> Unit): Privilege<T> {
    val privilegeBuilder = PrivilegeBuilder<T>().apply {
        this.subject = T::class
    }
    val facade = PrivilegeBuilderDsl()
    block.invoke(facade)
    facade.grantBuilderFacades.map { it.build() }.forEach(privilegeBuilder::addGrant)
    return privilegeBuilder.build()
}
