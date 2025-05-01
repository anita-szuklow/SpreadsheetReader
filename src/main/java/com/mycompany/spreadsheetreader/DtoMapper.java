package com.mycompany.spreadsheetreader;

import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    public static HeadquarterDto toDto(SwiftCode code) {
        HeadquarterDto dto = new HeadquarterDto();
        dto.setAddress(code.getAddress());
        dto.setBankName(code.getBankName());
        dto.setCountryISO2(code.getCountryISO2());
        dto.setCountryName(code.getCountryName());
        dto.setIsHeadquarter(code.getIsHeadquarter());
        dto.setSwiftCode(code.getSwiftCode());
        return dto;
    }

    public static SwiftCodeDto toBranchListItemDto(SwiftCode code) {
        SwiftCodeDto dto = new SwiftCodeDto();
        dto.setAddress(code.getAddress());
        dto.setBankName(code.getBankName());
        dto.setCountryISO2(code.getCountryISO2());
        dto.setIsHeadquarter(code.getIsHeadquarter());
        dto.setSwiftCode(code.getSwiftCode());
        return dto;
    }
    
    public static BranchDto toBranchDto(SwiftCode code) {
        BranchDto dto = new BranchDto();
        dto.setAddress(code.getAddress());
        dto.setBankName(code.getBankName());
        dto.setCountryISO2(code.getCountryISO2());
        dto.setCountryName(code.getCountryName());
        dto.setIsHeadquarter(false);
        dto.setSwiftCode(code.getSwiftCode());
        return dto;
}

    public static List<SwiftCodeDto> toBranchDtoList(List<SwiftCode> codes) {
        return codes.stream()
                .map(DtoMapper::toBranchListItemDto)
                .collect(Collectors.toList());
    }

    static CountryDto toCountryDto(SwiftCode code) {
        CountryDto dto = new CountryDto();
        dto.setCountryISO2(code.getCountryISO2());
        dto.setCountryName(code.getCountryName());
        return dto;
    }
    
    public static SwiftCode toSwiftCode(BranchDto dto) {
        SwiftCode code = new SwiftCode();
        code.setAddress(dto.getAddress());
        code.setBankName(dto.getBankName());
        code.setCountryISO2(dto.getCountryISO2());
        code.setCountryName(dto.getCountryName());
        code.setIsHeadquarter(dto.getIsHeadquarter());
        code.setSwiftCode(dto.getSwiftCode());
        return code;
    }
}
