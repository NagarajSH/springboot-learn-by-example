package com.sivalabs.demo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Siva
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootTestingDemoApplication.class,
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationRestTests
{

	@Value("${local.server.port}") 
	private int port;
	
	@Test
	public void testPing()
	{
		TestRestTemplate restTemplate = new TestRestTemplate("admin","admin123");
		String resp = restTemplate.getForObject("http://localhost:"+port+"/ping", String.class);
		System.err.println(resp);
	}
	
	@Test
	public void testGetUsers()
	{

		TestRestTemplate restTemplate = restTemplate();
		ResponseEntity<PagedResources<User>> responseEntity = 
				restTemplate.exchange(
								"http://localhost:"+port+"/users",
						        HttpMethod.GET, 
						        null, 
						        new ParameterizedTypeReference<PagedResources<User>>()
						        {}, 
						        Collections.emptyMap()
						        );
		if (responseEntity.getStatusCode() == HttpStatus.OK)
		{
			PagedResources<User> userResource = responseEntity.getBody();
			Collection<User> users = userResource.getContent();
			System.err.println(users);
			assertNotNull(users);
			assertEquals(3, users.size());
		}

	}
	
	protected TestRestTemplate restTemplate()
	{
		RestTemplate restTemplate = new RestTemplate();

		  ObjectMapper mapper = new ObjectMapper();
		  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		  mapper.registerModule(new Jackson2HalModule());

		  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		  converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		  converter.setObjectMapper(mapper);
		  
		  restTemplate.setMessageConverters(Arrays.asList(converter));

		  return new TestRestTemplate(restTemplate, "admin","admin123");
	}

}
