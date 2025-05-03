package com.mycompany.spreadsheetreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.mycompany.spreadsheetreader.testutil.SwiftCodeTestFactory.swift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;        // or JUnit's assertNotNull
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class SwiftCodeControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SwiftCodeService service;
    @Autowired SwiftCodeRepository repository;

    private static final String HQ_CODE = "MOCKPLPWXXX";

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        service.save(
          swift(HQ_CODE)
            .bank("Mock Bank")
            .address("Mock Address")
            .countryISO2("pl")
            .countryName("poland")
            .headquarter(true)
            .build()
        );
    }

    @Nested class GetBySwiftCode {

        @Test
        void validPatternButMissing_shouldReturn404() throws Exception {
            // 8‐char code matches our SWIFT pattern but isn't in the DB
            mockMvc.perform(get("/v1/swift-codes/ABCDEFGH"))
                   .andExpect(status().isNotFound())
                   .andExpect(jsonPath("$.error").value("SWIFT code not found"));
        }

        @Test
        void invalidPattern_shouldReturn400() throws Exception {
            // 10‐char code fails our SWIFT regex => BadRequest
            mockMvc.perform(get("/v1/swift-codes/ABCDEFGHXX"))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.error").value("Invalid SWIFT code format"));
        }

        @Test
        void headquarterWithBranches_shouldReturnBranchesArray() throws Exception {
            // add two branches
            service.save(
              swift("MOCKPLPW001")
                .bank("Branch1")
                .address("Addr1")
                .countryISO2("pl")
                .countryName("poland")
                .headquarter(false)
                .build()
            );
            service.save(
              swift("MOCKPLPW002")
                .bank("Branch2")
                .address("Addr2")
                .countryISO2("pl")
                .countryName("poland")
                .headquarter(false)
                .build()
            );

            mockMvc.perform(get("/v1/swift-codes/" + HQ_CODE))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.swiftCode").value(HQ_CODE))
                   .andExpect(jsonPath("$.isHeadquarter").value(true))
                   .andExpect(jsonPath("$.branches").isArray())
                   .andExpect(jsonPath("$.branches.length()").value(2));
        }
    }

    @Nested class Create {

        @Test
        void validPayload_shouldSaveAndReturnMessage() throws Exception {
            // use an 8‐char SWIFT so it passes @Pattern
            var newCode = swift("NEWAPLPL")
               .bank("New Bank")
               .address("New Addr")
               .countryISO2("pl")
               .countryName("poland")
               .headquarter(true)
               .build();

            String payload = objectMapper.writeValueAsString(newCode);

            mockMvc.perform(post("/v1/swift-codes")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(payload))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message")
                       .value("SWIFT code saved successfully"));

            // sanity‐check it really landed in the DB
            var fromDb = repository.findBySwiftCode("NEWAPLPL");
            assertThat(fromDb).isNotNull();
        }

        @Test
        void duplicateSwift_shouldReturn409Conflict() throws Exception {
            // HQ_CODE was added in @BeforeEach
            var dup = swift(HQ_CODE)
               .bank("Dup Bank")
               .address("Dup Addr")
               .countryISO2("pl")
               .countryName("poland")
               .headquarter(true)
               .build();

            String payload = objectMapper.writeValueAsString(dup);

            mockMvc.perform(post("/v1/swift-codes")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(payload))
                   .andExpect(status().isConflict())
                   .andExpect(jsonPath("$.error")
                       .value("SWIFT code already exists, please provide a unique code."));
        }
    }

    @Nested class Delete {

        @Test
        void existing_shouldReturn200AndMessage() throws Exception {
            mockMvc.perform(delete("/v1/swift-codes/" + HQ_CODE))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message")
                       .value("SWIFT code deleted successfully"));
        }

        @Test
        void missing_shouldReturn404() throws Exception {
            mockMvc.perform(delete("/v1/swift-codes/NONEXIST"))
                   .andExpect(status().isNotFound())
                   .andExpect(jsonPath("$.error")
                       .value("SWIFT code not found"));
        }
    }
}
