package com.mycompany.spreadsheetreader.exception;

public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String isoCode) {
        super("No SWIFT codes found for country: " + isoCode);
    }
}
