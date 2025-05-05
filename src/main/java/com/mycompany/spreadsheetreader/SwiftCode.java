package com.mycompany.spreadsheetreader;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class SwiftCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String address;
    
    @NotNull(message = "Bank name is required")
    @NotBlank(message = "Bank name is required")
    private String bankName;
    
    @Column(length = 2, nullable = false)
    @NotNull(message = "Country ISO2 code is required")
    @NotBlank(message = "Country ISO2 code is required")
    @Size(min = 2, max = 2, message = "Country ISO2 code must be exactly 2 characters")
    private String countryISO2;
    
    @NotNull(message = "Country name is required")
    @NotBlank(message = "Country name is required")
    private String countryName;
    
    private boolean isHeadquarter;
    
    @Column(length = 11, unique = true, nullable = false)
    @NotNull(message = "SWIFT code is required")
    @NotBlank(message = "SWIFT code is required")
    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "SWIFT code must be 8 or 11 uppercase alphanumeric characters")
    private String swiftCode;    
    
    public SwiftCode(){}
    
    public SwiftCode(Long id, String address, String bankName, String countryISO2, String countryName, boolean isHeadquarter, String swiftCode){
    this.id = id;
    this.address = address;
    this.bankName = bankName;
    this.countryISO2 = countryISO2;
    this.countryName = countryName;
    this.isHeadquarter = isHeadquarter;
    this.swiftCode = swiftCode;    
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public String getCountryISO2() { return countryISO2; }
    public void setCountryISO2(String countryISO2) { this.countryISO2 = countryISO2; }
    
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    
    public boolean getIsHeadquarter() { return isHeadquarter; }
    public void setIsHeadquarter(boolean isHeadquarter) { this.isHeadquarter = isHeadquarter; }
    
    public String getSwiftCode() { return swiftCode; }
    public void setSwiftCode(String swiftCode) { this.swiftCode = swiftCode; }


}
