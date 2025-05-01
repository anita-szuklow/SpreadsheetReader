package com.mycompany.spreadsheetreader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SwiftValidatorTest {

    @Test
    void validSwiftCode_8Chars_shouldPass() {
        assertTrue(SwiftValidator.swiftValidator("BANKPLPW"));
    }

    @Test
    void validSwiftCode_11Chars_shouldPass() {
        assertTrue(SwiftValidator.swiftValidator("BANKPLPWXXX"));
    }

    @Test
    void invalidSwiftCode_tooShort_shouldFail() {
        assertFalse(SwiftValidator.swiftValidator("BANK"));
    }
    
    @Test
    void invalidSwiftCode_blank_shouldFail() {
        assertFalse(SwiftValidator.swiftValidator(""));
    }

    @Test
    void invalidSwiftCode_null_shouldFail() {
        assertFalse(SwiftValidator.swiftValidator(null));
    }

    @Test
    void invalidSwiftCode_tooLong_shouldFail() {
        assertFalse(SwiftValidator.swiftValidator("BANKPLPWXXX1"));
    }
    
    @Test
    void invalidSwiftCode_9Or10Chars_shouldFail() {
        assertFalse(SwiftValidator.swiftValidator("BANKPLPWXX"));
    }

    @Test
    void invalidSwiftCode_lowercase_shouldPassDueToTrimAndUppercase() {
        assertTrue(SwiftValidator.swiftValidator("bankplpwxxx"));
    }

    @Test
    void invalidSwiftCode_withSymbols_shouldFail() {
        assertFalse(SwiftValidator.swiftValidator("BANKPL@W"));
    }
    
    @Test
    void invalidSwiftCode_withDigitsOnLetterOnlyPositions_shouldFail() {
        assertFalse(SwiftValidator.swiftValidator("1ANKPLPWXXX"));
    }

    @Test
    void isHeadquarterValidator_withXXX_shouldBeTrue() {
        assertTrue(SwiftValidator.swiftIsHeadquarterValidator("BANKPLPWXXX"));
    }
    
    @Test
    void isHeadquarterValidator_8Chars_shouldBeTrue() {
        assertTrue(SwiftValidator.swiftIsHeadquarterValidator("BANKPLPW"));
    }

    @Test
    void isHeadquarterValidator_withBranchCode_shouldBeFalse() {
        assertFalse(SwiftValidator.swiftIsHeadquarterValidator("BANKPLPW123"));
    }
    
    @Test
    void isHeadquarterValidator_lowercaseXXX_shouldBeTrue() {
        assertTrue(SwiftValidator.swiftIsHeadquarterValidator("bankplpwxxx"));
    }
    
    @Test
    void isHeadquarterValidator_with1XX_shouldBeFalse() {
        assertFalse(SwiftValidator.swiftIsHeadquarterValidator("BANKPLPW1XX"));
    }
}
