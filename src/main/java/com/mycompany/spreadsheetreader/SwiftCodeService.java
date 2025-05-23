package com.mycompany.spreadsheetreader;

import com.mycompany.spreadsheetreader.exception.HeadquarterFlagMismatchException;
import com.mycompany.spreadsheetreader.exception.InvalidIso2Exception;
import com.mycompany.spreadsheetreader.exception.SwiftCodeNotFoundException;
import com.mycompany.spreadsheetreader.exception.InvalidSwiftCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class SwiftCodeService {
    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    public SwiftCode getBySwiftCode(String swiftCode) {
        if (!SwiftValidator.swiftValidator(swiftCode)) {
            throw new InvalidSwiftCodeException("SWIFT code must be 8 or 11 characters and properly formatted.");
        }

        SwiftCode code = swiftCodeRepository.findBySwiftCode(swiftCode.toUpperCase());
        if (code == null) {
            throw new SwiftCodeNotFoundException("SWIFT code '" + swiftCode + "' not found.");
        }

        return code;
    }

    public List<SwiftCode> getByCountry(String iso2) {
        if (!SwiftValidator.iso2Validator(iso2)) {
            throw new InvalidIso2Exception("Country ISO2 code must be exactly 2 letters long");
        }
        return swiftCodeRepository.findByCountryISO2(iso2.toUpperCase());
    }

    public SwiftCode save(SwiftCode newCode) {
        newCode.setCountryISO2(newCode.getCountryISO2().toUpperCase());
        newCode.setCountryName(newCode.getCountryName().toUpperCase());
        newCode.setSwiftCode(newCode.getSwiftCode().toUpperCase());
        if (swiftCodeRepository.findBySwiftCode(newCode.getSwiftCode()) != null) {
            throw new DataIntegrityViolationException("SWIFT code already exists");
        }
        if (!SwiftValidator.swiftValidator(newCode.getSwiftCode())) {
            throw new InvalidSwiftCodeException("Invalid SWIFT code format.");
        }
        boolean isActuallyHeadquarter = SwiftValidator.swiftIsHeadquarterValidator(newCode.getSwiftCode());

        if (isActuallyHeadquarter && !newCode.getIsHeadquarter()) {
            throw new HeadquarterFlagMismatchException(newCode.getSwiftCode(), true);
        }

        if (!isActuallyHeadquarter && newCode.getIsHeadquarter()) {
            throw new HeadquarterFlagMismatchException(newCode.getSwiftCode(), false);
        }
        return swiftCodeRepository.save(newCode);
    }

    public void delete(String swiftCode) {
        SwiftCode code = getBySwiftCode(swiftCode);
        swiftCodeRepository.delete(code);
    }
    
    public List<SwiftCode> getBranchesByPrefix(String prefix) {
    return swiftCodeRepository.findBySwiftCodeStartingWithAndIsHeadquarterFalse(prefix.toUpperCase());
    }
    
}
