package com.mycompany.spreadsheetreader;

import java.util.regex.Pattern;

public class SwiftValidator {

    private static final Pattern SWIFT_PATTERN = Pattern.compile("^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}([A-Z0-9]{3})?$");
    private static final Pattern HEADQUARTER_SWIFT_PATTERN = Pattern.compile("^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}([XXX]{3})?$");

    public static boolean swiftValidator(String swiftToCheck) {
        if (swiftToCheck == null) {
            return false;
        }
        
        swiftToCheck = swiftToCheck.trim().toUpperCase();
        
        if (swiftToCheck.length() != 8 && swiftToCheck.length() != 11) {
            return false;
        }
        
        return SWIFT_PATTERN.matcher(swiftToCheck).matches();
    }
        public static boolean swiftIsHeadquarterValidator(String swiftToCheck) {
        if (swiftToCheck == null) {
            return false;
        }
        
        swiftToCheck = swiftToCheck.trim().toUpperCase();
        
        if (swiftToCheck.length() != 8 && swiftToCheck.length() != 11) {
            return false;
        }
        
        return HEADQUARTER_SWIFT_PATTERN.matcher(swiftToCheck).matches();
    }
}
