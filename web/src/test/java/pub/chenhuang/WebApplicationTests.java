package pub.chenhuang;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WebApplication.class, MockHttpServletRequestBuilder.class})
public class WebApplicationTests {

	@Autowired
	private WebApplicationContext context;
	@Test
	public void contextLoads() {
	}

	@Test
	public void testControllerDemo() throws Exception {
		MockMvc mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
		RequestBuilder e = get("/chen").param("name", "sunshineasbefore");
		String response = mvc.perform(e).andReturn().getResponse().getContentAsString();
		Assert.assertEquals("{\"id\":1,\"name\":\"admin\",\"age\":12}",response);
	}
}
