package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoServiceApplication.class)
@WebAppConfiguration
public class DemoServiceApplicationTests {


    private MockMvc mockMvc;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype());

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void getCustomerByPin() throws Exception {
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$firstname", is("Timo")))
                .andExpect(jsonPath("$lastname", is("Tapanainen")));
    }

    @Test
    public void postCustomer() throws Exception {
        String newCustomer = new ObjectMapper().writeValueAsString(new Customer("3", "Test", "User"));
        System.out.println(newCustomer);
        mockMvc.perform(post("/customers").contentType(contentType).content(newCustomer)).andExpect(status().isCreated());
        mockMvc.perform(get("/customers/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$id", is("3")))
                .andExpect(jsonPath("$firstname", is("Test")))
                .andExpect(jsonPath("$lastname", is("User")));
    }
}
