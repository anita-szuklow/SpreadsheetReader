package com.mycompany.spreadsheetreader;

import java.util.List;

public class HeadquarterDto extends SwiftCodeDto {
    private String countryName;
    private List<SwiftCodeDto> branches;    

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }    
    
    public List<SwiftCodeDto> getBranches() { return branches; }
    public void setBranches(List<SwiftCodeDto> branches) { this.branches = branches; }
    
}
