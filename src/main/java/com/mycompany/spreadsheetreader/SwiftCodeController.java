package com.mycompany.spreadsheetreader;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

@RestController
@RequestMapping("v1/swift-codes")
public class SwiftCodeController {

    @Autowired
    private SwiftCodeService swiftCodeService;

    @GetMapping("/{swift-code}")
    public ResponseEntity<? extends SwiftCodeDto> getBySwiftCode(@PathVariable("swift-code") String swiftCode) {
        SwiftCode code = swiftCodeService.getBySwiftCode(swiftCode);

        if (code.getIsHeadquarter()) {
            HeadquarterDto dto = DtoMapper.toDto(code);
            String prefix = code.getSwiftCode().substring(0, 8);
            List<SwiftCode> branches = swiftCodeService.getBranchesByPrefix(prefix);
            dto.setBranches(DtoMapper.toBranchDtoList(branches));
            return ResponseEntity.ok(dto);
        } else {
            BranchDto dto = DtoMapper.toBranchDto(code);
            return ResponseEntity.ok(dto);
        }
    }

    @GetMapping("/country/{countryISO2code}")
    public ResponseEntity<CountryDto> getByCountryISO2(@PathVariable("countryISO2code") String countryISO2) {
        List<SwiftCode> codes = swiftCodeService.getByCountry(countryISO2);
        CountryDto dto = new CountryDto();
        dto.setCountryISO2(countryISO2.toUpperCase());
            if (!codes.isEmpty()) {
                dto.setCountryName(codes.get(0).getCountryName());
            } else {
                dto.setCountryName("Unknown"); 
            }
        dto.setSwiftCodes(DtoMapper.toBranchDtoList(codes));
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> addSwiftCode(@Valid @RequestBody SwiftCode newCode) {
        swiftCodeService.save(newCode);
        return ResponseEntity.ok(Map.of("message", "SWIFT code saved successfully"));
    }

    @DeleteMapping("/{swift-code}")
    public ResponseEntity<?> deleteSwiftCode(@PathVariable("swift-code") String swiftCode) {
        swiftCodeService.delete(swiftCode);
        return ResponseEntity.ok(Map.of("message", "SWIFT code deleted successfully"));
    }
}
