package com.ongres.dvdrentalstore.rest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ongres.dvdrentalstore.dto.GenericRequest;
import com.ongres.dvdrentalstore.dto.RentRequest;
import com.ongres.dvdrentalstore.service.IRentalService;

@RunWith(SpringRunner.class)
@WebMvcTest(DVDRentalController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class DVDRentalControllerTest
{
	public static final MediaType APPLICATION_JSON_UTF8 = 
			new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
	private IRentalService rentalService;

    @Test
    public void rentDVD() throws Exception
    {
    	RentRequest request = new RentRequest();
    	request.setCustomerID(1);
    	request.setStaffName("STORE GUY");
    	request.setTitle("MOVIE TITLE");
    	
    	ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(request);
    	
        this.mockMvc.perform(post("/rental/rentDVD").contentType(APPLICATION_JSON_UTF8).content(requestJson))
        					.andDo(print()).andExpect(status().isOk())
        					.andExpect(content().string(containsString("DVD rented successfully.")))
        					.andDo(document("rental/rentDVD"));
    }
    
    @Test
    public void returnDVD() throws Exception
    {
    	GenericRequest request = new GenericRequest();
    	request.setCustomerID(1);
    	request.setTitle("MOVIE TITLE");
    	
    	ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = writer.writeValueAsString(request);
    	
        this.mockMvc.perform(post("/rental/returnDVD").contentType(APPLICATION_JSON_UTF8).content(requestJson))
        					.andDo(print()).andExpect(status().isOk())
        					.andExpect(content().string(containsString("DVD returned successfully.")))
        					.andDo(document("rental/returnDVD"));
    }
}
