package com.mycompany.spreadsheetreader.exception;

public class HeadquarterFlagMismatchException extends RuntimeException {
    public HeadquarterFlagMismatchException(String swiftCode, boolean shouldBeHeadquarter) {
        super(buildMessage(swiftCode, shouldBeHeadquarter));
    }

    private static String buildMessage(String swiftCode, boolean shouldBeHeadquarter) {
        if (shouldBeHeadquarter) {
            return "SWIFT code '" + swiftCode + "' ends with 'XXX' and must be marked as a headquarter.";
        } else {
            return "SWIFT code '" + swiftCode + "' does not end with 'XXX' and must not be marked as a headquarter.";
        }
    }
}

