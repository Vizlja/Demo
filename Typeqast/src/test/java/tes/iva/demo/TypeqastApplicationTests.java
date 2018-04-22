package tes.iva.demo;

import static org.junit.Assert.assertNotNull;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import tes.iva.demo.exchange.ExchangeRatio;
import tes.iva.demo.repository.RatioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TypeqastApplicationTests {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private MockMvc mockMvc;
	@SuppressWarnings("rawtypes")
	private HttpMessageConverter mappingJackson2HttpMessageConverter;
	@Autowired
	private RatioRepository ratioRepository;
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);
		assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {

		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		this.ratioRepository.deleteAll();
		TestUtilities.init();
	}

	@Test
	public void insertRatiosFailOne() throws Exception {

		ExchangeRatio exRatio = new ExchangeRatio();
		exRatio.setMonth("JAN");
		exRatio.setProfile("A");
		exRatio.setRatio(0.23d);
		List<ExchangeRatio> exRatiosList = new ArrayList<>();
		exRatiosList.add(exRatio);
		mockMvc.perform(post("/ratios/insert").content(this.json(exRatiosList)).contentType(contentType))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void insertRatiosFailBind() throws Exception {

		mockMvc.perform(post("/ratios/insert").content("[{\"month\":\"JAN\",\"profile\":\"A\",\"ratio\":\"A\"}]")
				.contentType(contentType)).andExpect(status().is4xxClientError());
	}

	@Test
	public void insertRatiosFailEmpty() throws Exception {

		mockMvc.perform(post("/ratios/insert").content("[]").contentType(contentType))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void allInOneSuccess() throws Exception {

		mockMvc.perform(
				post("/ratios/insert").content(this.json(TestUtilities.generateValidRatios())).contentType(contentType))
				.andExpect(status().is2xxSuccessful());
		mockMvc.perform(
				post("/ratios/insert").content(this.json(TestUtilities.generateValidRatios())).contentType(contentType))
				.andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/ratios/getall")).andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$", hasSize(12)));
		mockMvc.perform(get("/ratios/getbyprofile/A")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(12)));
		mockMvc.perform(get("/ratios/getbyprofile/B")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(0)));

		mockMvc.perform(post("/readings/insert/2012").content(this.json(TestUtilities.generateValidReadings()))
				.contentType(contentType)).andExpect(status().is2xxSuccessful());
		mockMvc.perform(get("/readings/getall")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(12)));
		mockMvc.perform(get("/readings/getbyconn/0001")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(12)));
		mockMvc.perform(get("/readings/getbyconn/0002")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(0)));
		mockMvc.perform(get("/readings/getone/0001/2012/DEC")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$.month", is("DEC")));

		mockMvc.perform(delete("/ratios/deletebyprofile/B")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", is(0)));
		mockMvc.perform(delete("/ratios/deletebyprofile/A")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", is(12)));
		mockMvc.perform(delete("/ratios/deleteall")).andExpect(status().isOk());

		mockMvc.perform(delete("/readings/deletebyconn/0002")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", is(0)));
		mockMvc.perform(delete("/readings/deletebyconn/0001")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", is(12)));
		mockMvc.perform(delete("/readings/deleteall")).andExpect(status().isOk());
	}

	@SuppressWarnings("unchecked")
	protected String json(Object o) throws IOException {

		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
}
