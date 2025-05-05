package com.mycompany.spreadsheetreader;

import com.mycompany.spreadsheetreader.dto.*;
import com.mycompany.spreadsheetreader.exception.*;
import org.springframework.context.annotation.Import;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import static org.hamcrest.Matchers.containsString;

import static org.mockito.BDDMockito.*;
import static org.hamcrest.Matchers.hasSize;
import org.springframework.dao.DataIntegrityViolationException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(controllers = SwiftCodeController.class,
            useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
@Import({ SwiftCodeController.class,
          GlobalExceptionHandler.class  })

class SwiftCodeControllerUnitTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  SwiftCodeService service;
  
  @MockBean
  InputSpreadsheetReader inputSpreadsheetReader;
  
  @MockBean 
  SwiftCodeRepository swiftCodeRepository;

  @Test  
  void getBranchByCode_shouldReturnBranchDto() throws Exception {
    var code = new SwiftCode();
    code.setSwiftCode("BRANCHPL123");
    code.setBankName("Bank X");
    code.setAddress("Addr");
    code.setCountryISO2("US");
    code.setCountryName("UNITED STATES");
    code.setIsHeadquarter(false);

    given(service.getBySwiftCode("BRANCHPL123")).willReturn(code);

    mockMvc.perform(get("/v1/swift-codes/BRANCHPL123"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.swiftCode").value("BRANCHPL123"))
      .andExpect(jsonPath("$.bankName").value("Bank X"))
      .andExpect(jsonPath("$.countryISO2").value("US"))
      .andExpect(jsonPath("$.branches").doesNotExist());
  }
  
  @Test
  void getHeadquarterByCode_shouldReturnHeadquarterDto() throws Exception {
    // given
    var hq = new SwiftCode();
    hq.setSwiftCode("HEADQUATXXX");
    hq.setBankName("Bank X");
    hq.setAddress("HQ Address");
    hq.setCountryISO2("US");
    hq.setCountryName("UNITED STATES");
    hq.setIsHeadquarter(true);

    var branch = new SwiftCode();
    branch.setSwiftCode("HEADQUAT001");
    branch.setBankName("Bank X - Branch 1");
    branch.setAddress("Branch Address");
    branch.setCountryISO2("US");
    branch.setCountryName("UNITED STATES");
    branch.setIsHeadquarter(false);
    
    given(service.getBySwiftCode("HEADQUATXXX"))
    .willReturn(hq);

    given(service.getBranchesByPrefix("HEADQUAT"))
    .willReturn(List.of(branch));

    mockMvc.perform(get("/v1/swift-codes/HEADQUATXXX"))
    .andExpect(status().isOk())
            
    .andExpect(jsonPath("$.swiftCode").value("HEADQUATXXX"))
    .andExpect(jsonPath("$.bankName").value("Bank X"))
    .andExpect(jsonPath("$.address").value("HQ Address"))
    .andExpect(jsonPath("$.countryISO2").value("US"))
    .andExpect(jsonPath("$.countryName").value("UNITED STATES"))
    .andExpect(jsonPath("$.isHeadquarter").value(true))

    .andExpect(jsonPath("$.branches", hasSize(1)))

    .andExpect(jsonPath("$.branches[0].swiftCode").value("HEADQUAT001"))
    .andExpect(jsonPath("$.branches[0].bankName").value("Bank X - Branch 1"))
    .andExpect(jsonPath("$.branches[0].address").value("Branch Address"))
    .andExpect(jsonPath("$.branches[0].countryISO2").value("US"))
    .andExpect(jsonPath("$.branches[0].isHeadquarter").value(false));
  }

  @Test
  void getBySwiftMissing_shouldReturn404() throws Exception {
    willThrow(new SwiftCodeNotFoundException("not found"))
      .given(service).getBySwiftCode("MISSING1234");

    mockMvc.perform(get("/v1/swift-codes/MISSING1234"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("SWIFT code not found"));
  }
  
  @Test
    void getBySwiftBadFormat_shouldReturn400() throws Exception {
      willThrow(new InvalidSwiftCodeException("SWIFT code must be 8 or 11 characters and properly formatted."))
        .given(service).getBySwiftCode("BADCODE");

      mockMvc.perform(get("/v1/swift-codes/BADCODE"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid SWIFT code format"))
        .andExpect(jsonPath("$.message").value("SWIFT code must be 8 or 11 characters and properly formatted."));
    }

  @Test  
    void getByCountryISO_shouldReturnSwiftList() throws Exception {
    SwiftCode c1 = new SwiftCode();
    c1.setSwiftCode("HQUSAA00XXX");
    c1.setBankName("Big Bank US");
    c1.setAddress("1 Wall St");
    c1.setCountryISO2("US");
    c1.setCountryName("UNITED STATES");
    c1.setIsHeadquarter(true);

    SwiftCode c2 = new SwiftCode();
    c2.setSwiftCode("HQUSAA00001");
    c2.setBankName("Big Bank US Branch");
    c2.setAddress("2 Main St");
    c2.setCountryISO2("US");
    c2.setCountryName("UNITED STATES");
    c2.setIsHeadquarter(false);

    given(service.getByCountry("US")).willReturn(List.of(c1, c2));

    mockMvc.perform(get("/v1/swift-codes/country/US"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))

      .andExpect(jsonPath("$.countryISO2").value("US"))
      .andExpect(jsonPath("$.countryName").value("UNITED STATES"))

      .andExpect(jsonPath("$.swiftCodes", hasSize(2)))

      .andExpect(jsonPath("$.swiftCodes[0].swiftCode").value("HQUSAA00XXX"))
      .andExpect(jsonPath("$.swiftCodes[0].bankName").value("Big Bank US"))
      .andExpect(jsonPath("$.swiftCodes[0].address").value("1 Wall St"))
      .andExpect(jsonPath("$.swiftCodes[0].countryISO2").value("US"))
      .andExpect(jsonPath("$.swiftCodes[0].isHeadquarter").value(true))

      .andExpect(jsonPath("$.swiftCodes[1].swiftCode").value("HQUSAA00001"))
      .andExpect(jsonPath("$.swiftCodes[1].bankName").value("Big Bank US Branch"))
      .andExpect(jsonPath("$.swiftCodes[1].address").value("2 Main St"))
      .andExpect(jsonPath("$.swiftCodes[1].countryISO2").value("US"))
      .andExpect(jsonPath("$.swiftCodes[1].isHeadquarter").value(false));
  }
    
  @Test
  void getByCountryISO_notFound_shouldReturn404() throws Exception {
    given(service.getByCountry("ZZ"))
      .willThrow(new CountryNotFoundException("ZZ"));

    mockMvc.perform(get("/v1/swift-codes/country/ZZ"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("Country not found"));
  }
  
  @Test
  void getByCountryISO_incorrectIso_shouldReturn400() throws Exception {
    given(service.getByCountry("ZZZ"))
      .willThrow(new InvalidIso2Exception(""));

    mockMvc.perform(get("/v1/swift-codes/country/ZZZ"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error").value("Invalid country ISO2 format"));
  }
    
  @Test
  void postValid_shouldReturn200AndMessage() throws Exception {
    var payload = """
      {
        "swiftCode":"ABCDUS33",
        "bankName":"My Bank",
        "countryISO2":"US",
        "countryName":"UNITED STATES",
        "isHeadquarter":true,
        "address":"123 Main St"
      }
      """;

    willReturn(new SwiftCode()).given(service)
      .save(any(SwiftCode.class));

    mockMvc.perform(post("/v1/swift-codes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(payload))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("SWIFT code saved successfully"));
  }

  @Test
  void postDuplicate_shouldReturn409() throws Exception {
    willThrow(new DataIntegrityViolationException("dup"))
      .given(service).save(any());

    mockMvc.perform(post("/v1/swift-codes")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
            "swiftCode":"ABCDUS33",
            "bankName":"X",
            "countryISO2":"US",
            "countryName":"UNITED STATES",
            "isHeadquarter":true
          }
        """))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.error").value("SWIFT code already exists, please provide a unique code."));
  }
  
    @Test
    void postMissingField_shouldReturn400AndValidationErrors() throws Exception {
      String payload = """
        {
          "bankName":"My Bank",
          "countryISO2":"US",
          "countryName":"UNITED STATES",
          "isHeadquarter":true,
          "address":"123 Main St"
        }
        """;

      mockMvc.perform(post("/v1/swift-codes")
              .contentType(MediaType.APPLICATION_JSON)
              .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"))
        .andExpect(jsonPath("$.fields.swiftCode").value("SWIFT code is required"));
    }

    @Test
    void postMalformedJson_shouldReturn400AndParseError() throws Exception {
      String badJson = "{ \"swiftCode\":\"ABCDUS33\", \"bankName\":\"My Bank\"  ";

      mockMvc.perform(post("/v1/swift-codes")
              .contentType(MediaType.APPLICATION_JSON)
              .content(badJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid JSON format"))
        .andExpect(jsonPath("$.details", containsString("Unexpected end-of-input")));
    }

    @Test
    void postHQFlagMismatch_shouldReturn400() throws Exception {
      willThrow(new HeadquarterFlagMismatchException("BANKPLPWXXX", false))
        .given(service).save(any());

      String payload = """
        {
          "swiftCode":"BANKPLPWXXX",
          "bankName":"Bank X",
          "countryISO2":"PL",
          "countryName":"POLAND",
          "isHeadquarter":false
        }
        """;

      mockMvc.perform(post("/v1/swift-codes")
              .contentType(MediaType.APPLICATION_JSON)
              .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("SWIFT code/headquarter mismatch"))
        .andExpect(jsonPath("$.message", containsString("BANKPLPWXXX")));
    }

  @Test
  void deleteExisting_shouldReturn200() throws Exception {
    mockMvc.perform(delete("/v1/swift-codes/FOOBAR12"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("SWIFT code deleted successfully"));
  }

  @Test
  void deleteMissing_shouldReturn404() throws Exception {
    willThrow(new SwiftCodeNotFoundException("nope"))
      .given(service).delete("MISSING");

    mockMvc.perform(delete("/v1/swift-codes/MISSING"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.error").value("SWIFT code not found"));
  }
  
  @Test
    void deleteBadFormat_shouldReturn400() throws Exception {
      willThrow(new InvalidSwiftCodeException(
                  "SWIFT code must be 8 or 11 characters and properly formatted."))
        .given(service).delete("BADCODE");

      mockMvc.perform(delete("/v1/swift-codes/BADCODE"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Invalid SWIFT code format"))
        .andExpect(jsonPath("$.message")
            .value("SWIFT code must be 8 or 11 characters and properly formatted."));
    }

}
