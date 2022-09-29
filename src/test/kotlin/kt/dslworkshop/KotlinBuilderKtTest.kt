package kt.dslworkshop

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class KotlinBuilderKtTest {
    @Test
    fun `kotlin-style builder creates same output as java-style`() {
        // given
        val java = javaBuilderStyle()

        assertThat(kotlinBuilderStyle()).isEqualTo(java)
    }
}