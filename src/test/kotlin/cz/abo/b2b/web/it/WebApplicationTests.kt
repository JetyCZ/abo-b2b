package cz.abo.b2b.web.it

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class WebApplicationTests(val applicationContext: ApplicationContext) {

	@Test
	fun contextLoads() {
		assertNotNull(applicationContext)
	}

}
