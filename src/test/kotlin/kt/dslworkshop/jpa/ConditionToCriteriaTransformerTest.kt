package kt.dslworkshop.jpa

import assertk.all
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isSuccess
import kt.dslworkshop.EmptyContext
import kt.dslworkshop.authorization.condition.Condition
import kt.dslworkshop.authorization.condition.Equals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.test.context.ContextConfiguration
import javax.persistence.Entity
import javax.persistence.EntityManager
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.criteria.Predicate
import javax.transaction.Transactional


@SpringBootTest(classes = [EmptyContext::class], webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration
@AutoConfigureDataJpa
@EntityScan
@AutoConfigureTestDatabase
@EnableJpaRepositories(
    considerNestedRepositories = true,
    basePackageClasses = [ConditionToCriteriaTransformerTest::class]
)
@Import(ConditionToCriteriaTransformer::class)
@Transactional
class ConditionToCriteriaTransformerTest {

    @Entity
    open class User(
        @Suppress("unused") @Id @GeneratedValue var id: Int? = null,
        var name: String
    ) {
        override fun equals(other: Any?): Boolean = (other as? User)?.id == this.id
        override fun hashCode(): Int = id.hashCode()
        override fun toString(): String {
            return "User(id=$id, name='$name')"
        }
    }

    @Entity
    open class Device(
        @Suppress("unused") @Id @GeneratedValue var id: Int? = null,
        var name: String,

        @ManyToOne
        var owner: User,

        var securityLevel: Int,
    ) {
        override fun equals(other: Any?): Boolean = (other as? Device)?.id == this.id
        override fun hashCode(): Int = id.hashCode()
        override fun toString(): String {
            return "Device(id=$id, name='$name', owner=${owner.id}, securityLevel=$securityLevel)"
        }
    }

    @Repository
    interface UserRepository : CrudRepository<User, Int>, UserRepositoryCustom

    @Repository
    interface DeviceRepository : CrudRepository<Device, Int>

    interface UserRepositoryCustom {
        fun findByCondition(condition: Condition): List<User>
        fun findDevices(user: User, condition: Condition): List<Device>
    }

    @Autowired
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    lateinit var userRepository: UserRepository

    @Autowired
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    lateinit var deviceRepository: DeviceRepository

    @Suppress("unused")
    class UserRepositoryImpl(
        val entityManager: EntityManager,
        val conditionToCriteriaTransformer: ConditionToCriteriaTransformer,
    ) : UserRepositoryCustom {

        override fun findByCondition(condition: Condition): List<User> {
            val cb = entityManager.criteriaBuilder
            val criteriaQuery = cb.createQuery(User::class.java)
            criteriaQuery.from(User::class.java)

            val predicate: Predicate = conditionToCriteriaTransformer.transformCondition(condition, cb, criteriaQuery)
            criteriaQuery.where(predicate)

            return entityManager.createQuery(criteriaQuery).resultList
        }

        override fun findDevices(user: User, condition: Condition): List<Device> {
            val cb = entityManager.criteriaBuilder
            val criteriaQuery = cb.createQuery(Device::class.java)
            val fromDevice = criteriaQuery.from(Device::class.java)

            val predicate: Predicate = conditionToCriteriaTransformer.transformCondition(condition, cb, criteriaQuery)

            criteriaQuery.where(
                cb.equal(
                    fromDevice.get<Any>("owner").get<Any>("id"),
                    cb.literal(user.id)
                ),
                predicate
            )

            criteriaQuery.select(fromDevice)
            return entityManager.createQuery(criteriaQuery).resultList
        }
    }

    @Test
    fun `transforms equality`() {
        userRepository.save(User(name = "a"))
        userRepository.save(User(name = "b"))

        val condition = Equals(User::name, "a")

        assertThat { userRepository.findByCondition(condition) }
            .isSuccess()
            .hasSize(1)
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun `transforms relation`() {
        val userA = userRepository.save(User(name = "a"))
        val userB = userRepository.save(User(name = "b"))

        val device1 = deviceRepository.save(Device(name = "device1", owner = userA, securityLevel = 1))
        val device2 = deviceRepository.save(Device(name = "device2", owner = userA, securityLevel = 1))
        val device3 = deviceRepository.save(Device(name = "device3", owner = userA, securityLevel = 2))
        val device4 = deviceRepository.save(Device(name = "device4", owner = userB, securityLevel = 1))

        val condition = Equals(Device::securityLevel, 1)

        assertThat {
            userRepository.findDevices(userA, condition)
        }
            .isSuccess()
            .all {
                hasSize(2)
                containsExactly(device1, device2)
            }
    }
}
