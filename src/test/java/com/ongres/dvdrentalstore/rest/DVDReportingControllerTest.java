package com.ongres.dvdrentalstore.rest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.ongres.dvdrentalstore.dto.Actor;
import com.ongres.dvdrentalstore.dto.ClientCount;
import com.ongres.dvdrentalstore.dto.Film;
import com.ongres.dvdrentalstore.dto.OverdueRental;
import com.ongres.dvdrentalstore.service.IReportingService;

@RunWith(SpringRunner.class)
@WebMvcTest(DVDReportingController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class DVDReportingControllerTest
{
	public static final MediaType APPLICATION_JSON_UTF8 = 
			new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
	private IReportingService reportingService;

    @Test
    public void clientsByCountry() throws Exception
    {	
    	Mockito.when(reportingService.clientsByCountry("COUNTRY", "CITY")).thenReturn(new ClientCount(42));
    	
        this.mockMvc.perform(get("/reporting/clientsByCountry?country=COUNTRY&city=CITY"))
        					.andDo(print()).andExpect(status().isOk())
        					.andExpect(content().string(containsString("Ok")))
        					.andDo(document("reporting/clientsByCountry"));
    }
    
    @Test
    public void filmsByActor() throws Exception
    {	
    	Actor actor1 = new Actor();
		actor1.setFirstName("FIRSTNAME");
		actor1.setLastName("ACTOR");
		Actor actor2 = new Actor();
		actor2.setFirstName("ACTOR");
		actor2.setLastName("LASTNAME");
		String category1 = "CATEGORY";
		String category2 = "CATEGORY";
		Film film1 = new Film();
		film1.setActor(actor1);
		film1.setCategoryName(category1);
		Film film2 = new Film();
		film2.setActor(actor2);
		film2.setCategoryName(category2);
		List<Film> films = Stream.of(film1, film2).collect(Collectors.toList());
		
    	Mockito.when(reportingService.filmsByActor("ACTOR", "CATEGORY")).thenReturn(films);
    	
        this.mockMvc.perform(get("/reporting/filmsByActor?actor=ACTOR&category=CATEGORY"))
        					.andDo(print()).andExpect(status().isOk())
        					.andExpect(content().string(containsString("Ok")))
        					.andDo(document("reporting/filmsByActor"));
    }
    
    @Test
    public void overdueRentals() throws Exception
    {	
    	OverdueRental overdueRental1 = new OverdueRental();
		overdueRental1.setCustomer("CUSTOMER1");
		overdueRental1.setPhone("PHONE1");
		overdueRental1.setTitle("TITLE1");
		OverdueRental overdueRental2 = new OverdueRental();
		overdueRental2.setCustomer("CUSTOMER2");
		overdueRental2.setPhone("PHONE2");
		overdueRental2.setTitle("TITLE2");
		
		List<OverdueRental> overdueRentals = Stream.of(overdueRental1, overdueRental2).collect(Collectors.toList());
    	
    	Mockito.when(reportingService.overdueRentals()).thenReturn(overdueRentals);
    	
        this.mockMvc.perform(get("/reporting/overdueRentals"))
        					.andDo(print()).andExpect(status().isOk())
        					.andExpect(content().string(containsString("Ok")))
        					.andDo(document("reporting/overdueRentals"));
    }
}
