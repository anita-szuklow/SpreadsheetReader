package com.mycompany.spreadsheetreader;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SwiftValidatorTest {

    @ParameterizedTest(name = "[{index}] ''{0}'' is valid")
    @ValueSource(strings = {
        "BANKPLPW",       // 8 chars
        "BANKPLPWXXX",    // 11 chars
        "bankplpwxxx",    // lowercase
        "bankplpwxxx "    // trailing space
    })
    void swiftValidator_validCodes_shouldReturnTrue(String code) {
        assertTrue(SwiftValidator.swiftValidator(code));
    }

    @ParameterizedTest(name = "[{index}] ''{0}'' is invalid")
    @NullAndEmptySource
    @ValueSource(strings = {
        "BANK",
        "BANKPLPWXX",
        "BANKPLPWXXX1",
        "BANKPL@W",
        "1ANKPLPWXXX",
        "BANK2LPWXXX"
    })
    void swiftValidator_invalidCodes_shouldReturnFalse(String code) {
        assertFalse(SwiftValidator.swiftValidator(code));
    }

    @ParameterizedTest(name = "[{index}] ''{0}'' is HQ")
    @ValueSource(strings = {
        "BANKPLPWXXX",
        "BANKPLPWXXX ",
        "BANKPLPW",
        "bankplpwxxx"
    })
    void swiftIsHeadquarterValidator_valid_shouldReturnTrue(String code) {
        assertTrue(SwiftValidator.swiftIsHeadquarterValidator(code));
    }

    @ParameterizedTest(name = "[{index}] ''{0}'' is not HQ")
    @NullAndEmptySource 
    @ValueSource(strings = {
        "BANKPLPW123",
        "BANKPLPW1XX",
        "BANKPLPXXX"
    })
    void swiftIsHeadquarterValidator_invalid_shouldReturnFalse(String code) {
        assertFalse(SwiftValidator.swiftIsHeadquarterValidator(code));
    }
}
