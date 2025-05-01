package com.mycompany.spreadsheetreader;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SwiftCodeRepository extends JpaRepository<SwiftCode, Long> {
    SwiftCode findBySwiftCode(String swiftCode);
    List<SwiftCode> findBySwiftCodeStartingWithAndIsHeadquarterFalse(String prefix);
    List<SwiftCode> findByCountryISO2(String countryISO2);
}

