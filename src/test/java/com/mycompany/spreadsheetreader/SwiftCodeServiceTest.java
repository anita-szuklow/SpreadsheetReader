package com.mycompany.spreadsheetreader;

import com.mycompany.spreadsheetreader.exception.HeadquarterFlagMismatchException;
import com.mycompany.spreadsheetreader.exception.InvalidSwiftCodeException;
import com.mycompany.spreadsheetreader.exception.SwiftCodeNotFoundException;

import static com.mycompany.spreadsheetreader.testutil.SwiftCodeTestFactory.swift;
import static com.mycompany.spreadsheetreader.testutil.TestDataSeeder.seedBranches;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
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

    static Stream<Arguments> invalidSaveScenarios() {
        return Stream.of(
            arguments("",             false, InvalidSwiftCodeException.class),
            arguments("BANK",         false, InvalidSwiftCodeException.class),
            arguments("BANKPL1234",   false, InvalidSwiftCodeException.class),
            arguments("BANKPL123456", false, InvalidSwiftCodeException.class),
            arguments("BANK1234567",  false, InvalidSwiftCodeException.class),
            arguments("BANKPLPWXXX",  false, HeadquarterFlagMismatchException.class),
            arguments("BANKPLPW",     false, HeadquarterFlagMismatchException.class),
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
        assertEquals(1, repository.count(), "after a failed save, I still only have my one setup entry");
    }

    @ParameterizedTest(name = "getBySwiftCode(“{0}”) returns entity")
    @MethodSource("caseInsensitiveCodes")
    void getBySwiftCode_caseInsensitive_shouldReturnEntity(String code) {
        SwiftCode found = service.getBySwiftCode(code);
        assertEquals("Mock Bank", found.getBankName());
        assertEquals(EXISTING_CODE, found.getSwiftCode());
    }

    static Stream<String> caseInsensitiveCodes() {
        return Stream.of(
            EXISTING_CODE,
            EXISTING_CODE.toLowerCase()
        );
    }
    
    @ParameterizedTest(name = "[{index}] getBySwiftCode(''{0}'') → {1}")
    @MethodSource("provideBadAndMissingSwiftCodes")
    void getBySwiftCode_errors_throwExpected(
        String code,
        Class<? extends RuntimeException> expectedException
    ) {
        assertThrows(expectedException, () -> service.getBySwiftCode(code));
    }
    
    static Stream<Arguments> provideBadAndMissingSwiftCodes() {
        return Stream.of(
            arguments(null,             InvalidSwiftCodeException.class),
            arguments("",               InvalidSwiftCodeException.class),
            arguments("BANK",           InvalidSwiftCodeException.class),
            arguments("BANKPLPWXX",     InvalidSwiftCodeException.class),
            arguments("BANKPL@W123",    InvalidSwiftCodeException.class),
            arguments("ABCDEFGH",       SwiftCodeNotFoundException.class),
            arguments("ABCDEFGHXXX",    SwiftCodeNotFoundException.class),
            arguments("abcdefghxxx",    SwiftCodeNotFoundException.class)
        );
    }

    @ParameterizedTest(name = "delete_existing(“{0}”) returns entity")
    @MethodSource("caseInsensitiveCodes")
    void delete_existing_caseInsensitive_shouldRemoveEntity(String code) {
        service.delete(code);
        assertEquals(0, repository.count(), "should be empty after delete");
        assertThrows(
            SwiftCodeNotFoundException.class,
            () -> service.getBySwiftCode(code),
            "getBySwiftCode(" + code + ") should now be not found"
        );
    }
    
    @ParameterizedTest(name = "[{index}] delete(''{0}'') → throws {1}")
    @MethodSource("provideBadAndMissingSwiftCodes")
    void delete_errors_throwExpected(
        String code,
        Class<? extends RuntimeException> expectedException
    ) {
        assertThrows(expectedException, () -> service.delete(code));
    }

    static Stream<Arguments> countryLookupScenarios() {
        return Stream.of(
            arguments("PL",   0, 1),
            arguments("pl",   0, 1),
            arguments("PL",   1, 2),
            arguments("PL",   2, 3),
            arguments("ZZ",   0, 0)
        );
    }

    @ParameterizedTest(name = "[{index}] getByCountry(''{0}'') +{1} branches → {2}")
    @MethodSource("countryLookupScenarios")
    void getByCountry_shouldReturnExpectedCount(String iso,
                                                int extras,
                                                int expectedCount) {
        seedBranches(service, EXISTING_CODE.substring(0, 8), extras);

        List<SwiftCode> results = service.getByCountry(iso);
        assertEquals(expectedCount, results.size(),
            () -> "getByCountry(" + iso + ") should return " + expectedCount);
    }
    
    static Stream<Arguments> branchLookupScenarios() {
        return Stream.of(
            arguments("MOCKPLPW",       0, 0),
            arguments("MOCKPLPW",       1, 1),
            arguments("MOCKPLPW",       2, 2)
        );
    }

    @ParameterizedTest(name = "[{index}] getBranchesByPrefix(''{0}'') +{1} → {2}")
    @MethodSource("branchLookupScenarios")
    void getBranchesByPrefix_parametrized(
        String prefix,
        int branchExtras,
        int expectedCount
    ) {
        // drop your for-loop here…
        seedBranches(service, prefix, branchExtras);

        var branches = service.getBranchesByPrefix(prefix);
        assertEquals(expectedCount, branches.size(),
            () -> "prefix=" + prefix + ", extras=" + branchExtras);
        if (expectedCount > 0) {
            assertTrue(branches.stream().noneMatch(SwiftCode::getIsHeadquarter),
                       "all returned must be non-headquarters");
        }
    }
}
