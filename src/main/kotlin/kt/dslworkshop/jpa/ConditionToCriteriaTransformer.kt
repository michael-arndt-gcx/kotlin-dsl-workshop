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
        return condition.toPredicate(cb, criteriaQuery)
    }

    private fun Condition.toPredicate(cb: CriteriaBuilder, criteriaQuery: CriteriaQuery<*>): Predicate {
        return when(this) {
            is Equals -> cb.equal(this.left.toPredicate(cb, criteriaQuery), this.right.toPredicate(cb, criteriaQuery))
            is Conjunction -> cb.and(this.left.toPredicate(cb, criteriaQuery), this.right.toPredicate(cb, criteriaQuery))
        }
    }


    private fun Any.toPredicate(cb: CriteriaBuilder, criteriaQuery: CriteriaQuery<*>): Expression<*> {
        return when(this) {
            is Int, is String -> cb.literal(this)
            is KProperty<*> -> {
                val entityClass = ((this as CallableReference).owner as KClass<*>).java
                val entityModel = entityManager.metamodel.entity(entityClass)
                val root = criteriaQuery.roots.single { it.model == entityModel }

                @Suppress("UNCHECKED_CAST")
                val attribute = entityModel.getAttribute(this.name) as SingularAttribute<Any, *>

                root.get(attribute)
            }
            else -> error("Unsupported type ${this::class}")
        }
    }
}


