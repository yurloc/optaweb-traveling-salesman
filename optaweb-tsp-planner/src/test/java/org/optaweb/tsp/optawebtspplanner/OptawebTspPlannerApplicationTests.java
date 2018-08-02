package org.optaweb.tsp.optawebtspplanner;

import java.math.BigDecimal;

import org.assertj.core.api.SoftAssertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OptawebTspPlannerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void all_possible_values_should_be_persisted_without_loss_of_precision() {
        // arrange
        // https://wiki.openstreetmap.org/wiki/Node#Structure
        final BigDecimal maxLatitude = new BigDecimal("90.0000000");
        final BigDecimal maxLongitude = new BigDecimal("214.7483647");
        final BigDecimal minLatitude = maxLatitude.negate();
        final BigDecimal minLongitude = maxLongitude.negate();
        restTemplate.postForEntity("/places", new Place(minLatitude, minLongitude), Place.class);
        restTemplate.postForEntity("/places", new Place(maxLatitude, maxLongitude), Place.class);

        // act
        ResponseEntity<Place> response1 = restTemplate.getForEntity("/places/1", Place.class);
        ResponseEntity<Place> response2 = restTemplate.getForEntity("/places/2", Place.class);

        // assert
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response1.getBody().getLatitude()).isEqualTo(minLatitude);
            softly.assertThat(response1.getBody().getLongitude()).isEqualTo(minLongitude);
            softly.assertThat(response2.getBody().getLatitude()).isEqualTo(maxLatitude);
            softly.assertThat(response2.getBody().getLongitude()).isEqualTo(maxLongitude);
        });
    }

    @Test
    public void should_create_entity() throws Exception {
        mockMvc.perform(post("/places").content(
                "{\"latitude\": \"1\", \"longitude\":\"2\"}")).andExpect(
                status().isCreated()).andExpect(
                header().string("Location", containsString("places/")));
    }

    @Test
    @Ignore
    public void values_close_to_zero_should_not_use_scientific_format() throws Exception {
        // arrange
        final String lat = "0.0000003";
        final String lng = "0.0000005";
        MvcResult mvcResult = mockMvc.perform(post("/places").content(
                "{\"lat\": \"" + lat + "\", \"lng\":\"" + lng + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        // act & assert
        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lat").value(lat))
                .andExpect(jsonPath("$.lng").value(lng));
    }
}
