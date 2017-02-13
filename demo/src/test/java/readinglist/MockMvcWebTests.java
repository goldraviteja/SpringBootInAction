package readinglist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class MockMvcWebTests {

	@Autowired
	private WebApplicationContext webContext;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
	}

	@Test
	public void homePage() throws Exception {
		ResultActions action = mockMvc.perform(get("/readingList/ggk"));
		action.andExpect(status().isOk());
		action.andExpect(view().name("readingList"));
		action.andExpect(model().attributeExists("books"));
		action.andExpect(model().attribute("books", not(empty())));
	}

	@Test
	public void postBook() throws Exception {
		mockMvc.perform(
				post("/readingList/ggk").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("title", "BOOK TITLE")
						.param("author", "BOOK AUTHOR").param("isbn", "1234567890").param("description", "DESCRIPTION"))
				.andExpect(status().is3xxRedirection()).andExpect(header().string("Location", "/readingList"));

		Book expectedBook = new Book();
		expectedBook.setId(1L);
		expectedBook.setReader("craig");
		expectedBook.setTitle("BOOK TITLE");
		expectedBook.setAuthor("BOOK AUTHOR");
		expectedBook.setIsbn("1234567890");
		expectedBook.setDescription("DESCRIPTION");
		mockMvc.perform(get("/readingList/ggk")).andExpect(status().isOk()).andExpect(view().name("readingList"))
				.andExpect(model().attributeExists("books")).andExpect(model().attribute("books", hasSize(1)))
				.andExpect(model().attribute("books", contains(samePropertyValuesAs(expectedBook))));
	}

}
