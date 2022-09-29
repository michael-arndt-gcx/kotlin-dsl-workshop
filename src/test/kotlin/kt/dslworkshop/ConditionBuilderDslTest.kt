package kt.dslworkshop

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isSuccess
import kt.dslworkshop.authorization.condition.Condition
import kt.dslworkshop.authorization.condition.Conjunction
import kt.dslworkshop.authorization.condition.Equals
import org.junit.jupiter.api.Test


class ConditionBuilderDslTest {
    data class Foo(val int: Int, val bool: Boolean)
    data class Bar(val int: Int, val bool: Boolean)

    @Test
    fun `1 eq (int property)`() {
        assertThat {
            condition {
                1 eq Foo::int
            }
        }.isSuccess().isEqualTo(Equals(1, Foo::int))
    }

    @Test
    fun `(int property) eq 1`() {
        assertThat {
            condition {
                Foo::int eq 1
            }
        }.isSuccess().isEqualTo(Equals(Foo::int, 1))
    }

    @Test
    fun `(int property) eq (int property)`() {
        assertThat {
            condition {
                Bar::int eq Foo::int
            }
        }.isSuccess().isEqualTo(Equals(Bar::int, Foo::int))
    }

    @Test
    fun `true eq (bool property)`() {
        assertThat {
            condition {
                true eq Foo::bool
            }
        }.isSuccess().isEqualTo(Equals(true, Foo::bool))
    }

    @Test
    fun `(bool property) eq true`() {
        assertThat {
            condition {
                Foo::bool eq true
            }
        }.isSuccess().isEqualTo(Equals(Foo::bool, true))
    }


    @Test
    fun `(bool property) eq (bool property)`() {
        assertThat {
            condition {
                Bar::bool eq Foo::bool
            }
        }.isSuccess().isEqualTo(Equals(Bar::bool, Foo::bool))
    }

    @Test
    fun `(bool property) eq (bool property) and (bool property) eq (bool property)`() {
        assertThat {
            condition {
                Bar::bool eq Foo::bool and Bar::bool eq Foo::bool
            }
        }.isSuccess().isEqualTo(Conjunction(Equals(Bar::bool, Foo::bool), Equals(Bar::bool, Foo::bool)))
    }

    @Test
    fun `(bool property) eq (bool property) and bool eq (bool property)`() {
        assertThat {
            condition {
                Bar::bool eq Foo::bool and true eq Foo::bool
            }
        }.isSuccess().isEqualTo(Conjunction(Equals(Bar::bool, Foo::bool), Equals(true, Foo::bool)))
    }

    private fun condition(block: ConditionBuilderDsl.() -> Condition): Condition {
        return block.invoke(ConditionBuilderDsl)
    }
}