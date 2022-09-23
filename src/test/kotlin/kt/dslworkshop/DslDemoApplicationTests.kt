package kt.dslworkshop

import kt.dslworkshop.jpa.ConditionToCriteriaTransformer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.test.context.ContextConfiguration
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

class EmptyContext

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(classes = [EmptyContext::class], webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration
@AutoConfigureDataJpa
@EntityScan
@AutoConfigureTestDatabase
@EnableJpaRepositories(considerNestedRepositories = true, basePackageClasses = [DslDemoApplicationTests::class])
@Import(ConditionToCriteriaTransformer::class)
class DslDemoApplicationTests {
    @Entity class Group(@Id var id: UUID = UUID.randomUUID())
    @Entity class User(@Id var id: UUID = UUID.randomUUID())
    @Entity class Device(
        @Id var id: UUID = UUID.randomUUID(),

        @ManyToOne var group: Group? = null,
    )

    @Entity
    class Role(
        @Id var id: UUID = UUID.randomUUID(),
        @ManyToOne val user: User,
        @ManyToOne val group: Group,
        val type: RoleType = RoleType.NORMAL,
    )

    enum class RoleType {
        ADMIN,
        NORMAL
    }

    @Repository
    interface GroupRepository : CrudRepository<Group, UUID>

    @Repository
    interface UserRepository : CrudRepository<User, UUID>

    @Repository
    interface DeviceRepository : CrudRepository<Device, UUID>

    @Repository
    interface RoleRepository : CrudRepository<Role, UUID>

    @Autowired
    lateinit var groupRepository: GroupRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var deviceRepository: DeviceRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Test
    fun contextLoads() {
        userRepository.save(User())
    }

}
