package kt.dslworkshop.jpa

import kt.dslworkshop.authorization.condition.Condition
import kt.dslworkshop.authorization.condition.Conjunction
import kt.dslworkshop.authorization.condition.Equals
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate
import javax.persistence.metamodel.SingularAttribute
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Service
class ConditionToCriteriaTransformer(val entityManager: EntityManager) {
    fun transformCondition(
        condition: Condition,
        cb: CriteriaBuilder,
        criteriaQuery: CriteriaQuery<*>
    ): Predicate {
        // Wir treffen hier sehr viele Annahmen, um initial zu zeigen, dass es grundsätzlich möglich ist.

        val x = when (condition) {
            is Equals -> condition.left.transformExpression(entityManager, cb, criteriaQuery)
            is Conjunction -> TODO()
        }

        // nur Equals
        val equals = condition as Equals

        // links muss eine Property einer Entity stehen
        val left = equals.left as KProperty<*>

        val entityClass = ((left as CallableReference).owner as KClass<*>).java
        val entityModel = entityManager.metamodel.entity(entityClass)

        @Suppress("UNCHECKED_CAST")
        val attribute = entityModel.getAttribute(left.name) as SingularAttribute<Any, *>

        // wir erlauben keine self-joins, jede Tabelle darf nur 1 mal vorkommen.
        val root = criteriaQuery.roots.single { it.model == entityModel }

        return cb.equal(root.get(attribute), cb.literal(equals.right))
    }
}

private fun Any.transformExpression(entityManager: EntityManager, cb: CriteriaBuilder, criteriaQuery: CriteriaQuery<*>): Expression<*> {
    return when (this) {
        is KProperty<*> -> {
            val entityClass = ((this as CallableReference).owner as KClass<*>).java
            val entityModel = entityManager.metamodel.entity(entityClass)
            val attribute = entityModel.getAttribute(this.name) as SingularAttribute<Any, *>

            // wir erlauben keine self-joins, jede Tabelle darf nur 1 mal vorkommen.
            val root = criteriaQuery.roots.single { it.model == entityModel }
            root.get(attribute)
        }
    }
}
