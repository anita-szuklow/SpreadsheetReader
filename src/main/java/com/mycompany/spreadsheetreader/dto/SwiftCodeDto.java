package com.mycompany.spreadsheetreader.dto;

import java.util.List;

public class SwiftCodeDto {
    private String address;
    private String bankName;
    private String countryISO2;
    private boolean isHeadquarter;
    private String swiftCode;
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public String getCountryISO2() { return countryISO2; }
    public void setCountryISO2(String countryISO2) { this.countryISO2 = countryISO2; }
    
    public boolean getIsHeadquarter() { return isHeadquarter; }
    public void setIsHeadquarter(boolean isHeadquarter) { this.isHeadquarter = isHeadquarter; }
    
    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }
}
