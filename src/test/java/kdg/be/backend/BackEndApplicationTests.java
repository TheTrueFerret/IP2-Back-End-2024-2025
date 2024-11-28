package kdg.be.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainerIPConfiguration.class)
class BackEndApplicationTests {

	@Test
	void contextLoads() {
	}

}
