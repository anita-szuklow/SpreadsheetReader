package com.mycompany.spreadsheetreader.dto;

import java.util.List;

public class CountryDto {
    private String countryISO2;
    private String countryName;
    private List<SwiftCodeDto> swiftCodes;
    
    public String getCountryISO2() { return countryISO2; }
    public void setCountryISO2(String countryISO2) { this.countryISO2 = countryISO2; }
    
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }    
    
    public List<SwiftCodeDto> getSwiftCodes() { return swiftCodes; }
    public void setSwiftCodes(List<SwiftCodeDto> swiftCodes) { this.swiftCodes = swiftCodes; }
    
}
