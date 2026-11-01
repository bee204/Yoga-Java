package com.yoga.vita;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VitaApplicationTests {
    @Autowired MockMvc mvc;

    @Test void publicPingEndpointWorks() throws Exception {
        mvc.perform(get("/api/ping")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("API is working"));
    }

    @Test void publicCatalogEndpointsWork() throws Exception {
        mvc.perform(get("/api/public/classes")).andExpect(status().isOk());
        mvc.perform(get("/api/public/teachers")).andExpect(status().isOk());
    }
}
