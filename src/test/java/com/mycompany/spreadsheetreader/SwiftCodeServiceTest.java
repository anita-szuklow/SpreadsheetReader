package com.mycompany.spreadsheetreader;

import static com.mycompany.spreadsheetreader.testutil.SwiftCodeTestFactory.swift;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.junit.jupiter.api.Assertions.*;

import com.mycompany.spreadsheetreader.exception.HeadquarterFlagMismatchException;
import com.mycompany.spreadsheetreader.exception.InvalidSwiftCodeException;
import com.mycompany.spreadsheetreader.exception.SwiftCodeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
@Transactional
class SwiftCodeServiceTest {

    private static final String EXISTING_CODE = "MOCKPLPWXXX";

    @Autowired
    private SwiftCodeService service;

    @Autowired
    private SwiftCodeRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        service.save(
            swift(EXISTING_CODE)
                .bank("Mock Bank")
                .address("Mock Address")
                .countryISO2("pl")
                .countryName("poland")
                .headquarter(true)
                .build()
        );

        assertEquals(1, repository.count(), "setup: should have one entry");
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> validSaveScenarios() {
        return Stream.of(
            arguments("BANKPLPW",      true),
            arguments("BANKPLPWXXX",   true),
            arguments("bankplpwxxx",   true),
            arguments("BANKPLPW123",   false)
        );
    }

    @ParameterizedTest(name = "[{index}] save valid code={0}, HQ={1}")
    @MethodSource("validSaveScenarios")
    void save_validScenarios_shouldPersist(String code, boolean isHQ) {
        SwiftCode input = swift(code)
            .bank("Test Bank")
            .address("Test Addr")
            .countryISO2("pl")
            .countryName("poland")
            .headquarter(isHQ)
            .build();

        SwiftCode saved = service.save(input);

        assertNotNull(saved.getId(),      "ID should be generated");
        assertEquals(code.toUpperCase(),  saved.getSwiftCode());
        assertEquals("PL",                saved.getCountryISO2());
        assertEquals("POLAND",            saved.getCountryName());
        assertEquals(2, repository.count(), "two rows after successful save");
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> invalidSaveScenarios() {
        return Stream.of(
            arguments("BANK",         false, InvalidSwiftCodeException.class),
            arguments("BANK1234567",  false, InvalidSwiftCodeException.class),
            arguments("BANKPLPWXXX",  false, HeadquarterFlagMismatchException.class),
            arguments("BANKPLPW123",  true,  HeadquarterFlagMismatchException.class),
            arguments(EXISTING_CODE,  true,  DataIntegrityViolationException.class)
        );
    }

    @ParameterizedTest(name = "[{index}] save invalid code={0}, HQ={1} → throws {2}")
    @MethodSource("invalidSaveScenarios")
    void save_invalidScenarios_throwExpected(
        String code,
        boolean isHQ,
        Class<? extends RuntimeException> exceptionClass
    ) {
        SwiftCode input = swift(code)
            .bank("Test Bank")
            .address("Test Addr")
            .countryISO2("PL")
            .countryName("POLAND")
            .headquarter(isHQ)
            .build();

        assertThrows(exceptionClass, () -> service.save(input));
    }

    @Test
    void getBySwiftCode_existing_shouldReturnEntity() {
        SwiftCode found = service.getBySwiftCode(EXISTING_CODE);
        assertEquals("Mock Bank", found.getBankName());
    }

    @Test
    void getBySwiftCode_missing_shouldThrowNotFound() {
        assertThrows(
            SwiftCodeNotFoundException.class,
            () -> service.getBySwiftCode("UNKNOWN1234")
        );
    }

    @Test
    void delete_existing_shouldRemoveEntity() {
        service.delete(EXISTING_CODE);
        assertEquals(0, repository.count(), "should be empty after delete");
        assertThrows(
            SwiftCodeNotFoundException.class,
            () -> service.getBySwiftCode(EXISTING_CODE)
        );
    }

    @Test
    void getByCountry_multipleMatches_shouldReturnList() {
        // insert a second PL record
        service.save(
            swift("OTHERPLPW12")
              .bank("Other Bank")
              .address("Other Addr")
              .countryISO2("pl")
              .countryName("poland")
              .headquarter(false)
              .build()
        );

        List<SwiftCode> results = service.getByCountry("PL");
        assertEquals(2, results.size());
    }

    @Test
    void getBranchesByPrefix_onlyBranchesReturned() {
        // HQ already exists as MOCKPLPWXXX
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

        List<SwiftCode> branches = service.getBranchesByPrefix("MOCKPLPW");
        assertEquals(2, branches.size());
        assertTrue(branches.stream().allMatch(c -> !c.getIsHeadquarter()));
    }
}
