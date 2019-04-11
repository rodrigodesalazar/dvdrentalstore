package com.ongres.dvdrentalstore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ongres.dvdrentalstore.rest.DVDRentalController;
import com.ongres.dvdrentalstore.rest.DVDReportingController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DvdrentalstoreApplicationTests
{
	@Autowired
	private DVDRentalController dvdRentalController;

	@Autowired
	private DVDReportingController dvdReportingController;

	@Test
	public void smokeTest()
	{
		assertThat(dvdRentalController).isNotNull();
		assertThat(dvdReportingController).isNotNull();
	}
}
