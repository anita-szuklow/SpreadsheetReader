package com.mycompany.spreadsheetreader;

import com.mycompany.spreadsheetreader.dto.BranchDto;
import com.mycompany.spreadsheetreader.dto.CountryDto;
import com.mycompany.spreadsheetreader.dto.HeadquarterDto;
import com.mycompany.spreadsheetreader.dto.SwiftCodeDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoMapperTest {

    private SwiftCode makeCode() {
        SwiftCode c = new SwiftCode();
        c.setSwiftCode("TESTPLPWXXX");
        c.setBankName("My Bank");
        c.setAddress("123 Main St");
        c.setCountryISO2("PL");
        c.setCountryName("POLAND");
        c.setIsHeadquarter(true);
        return c;
    }

    @Test
    void toDto_headquarterDto_fieldsCopied() {
        SwiftCode src = makeCode();
        HeadquarterDto dto = DtoMapper.toDto(src);

        assertEquals(src.getSwiftCode(),     dto.getSwiftCode());
        assertEquals(src.getBankName(),      dto.getBankName());
        assertEquals(src.getAddress(),       dto.getAddress());
        assertEquals(src.getCountryISO2(),   dto.getCountryISO2());
        assertEquals(src.getCountryName(),   dto.getCountryName());
        assertTrue(dto.getIsHeadquarter());
    }

    @Test
    void toBranchListItemDto_swiftCodeDto_noCountryName() {
        SwiftCode src = makeCode();
        SwiftCodeDto dto = DtoMapper.toBranchListItemDto(src);

        assertEquals(src.getSwiftCode(),   dto.getSwiftCode());
        assertEquals(src.getBankName(),    dto.getBankName());
        assertEquals(src.getAddress(),     dto.getAddress());
        assertEquals(src.getCountryISO2(), dto.getCountryISO2());
        assertTrue(dto.getIsHeadquarter());

        // SwiftCodeDto has no countryName property
        // we can only assert that getCountryName() doesn't exist,
        // but at least verify there's no data loss on other fields
    }

    @Test
    void toBranchDto_branchDto_includesCountryName_and_forcesIsHeadquarterFalse() {
        SwiftCode src = makeCode();
        // force src to HQ, branchDto should override to false
        src.setIsHeadquarter(true);

        BranchDto dto = DtoMapper.toBranchDto(src);

        assertEquals(src.getSwiftCode(),   dto.getSwiftCode());
        assertEquals(src.getBankName(),    dto.getBankName());
        assertEquals(src.getAddress(),     dto.getAddress());
        assertEquals(src.getCountryISO2(), dto.getCountryISO2());
        assertEquals(src.getCountryName(), dto.getCountryName());

        // always false for branch DTO
        assertFalse(dto.getIsHeadquarter());
    }

    @Test
    void toBranchDtoList_transformsAll() {
        SwiftCode c1 = makeCode();
        SwiftCode c2 = makeCode();
        c2.setSwiftCode("TESTPLPW001");
        List<SwiftCode> srcList = List.of(c1, c2);

        List<SwiftCodeDto> dtos = DtoMapper.toBranchDtoList(srcList);
        assertEquals(2, dtos.size());

        assertEquals("TESTPLPWXXX", dtos.get(0).getSwiftCode());
        assertEquals("TESTPLPW001", dtos.get(1).getSwiftCode());
    }

    @Test
    void toCountryDto_onlyCountryFields() {
        SwiftCode src = makeCode();
        CountryDto dto = DtoMapper.toCountryDto(src);

        assertEquals(src.getCountryISO2(), dto.getCountryISO2());
        assertEquals(src.getCountryName(), dto.getCountryName());
        // no swiftCodes list populated here
        assertNull(dto.getSwiftCodes());
    }

    @Test
    void toSwiftCode_roundTrip_fromBranchDto() {
        // first create a BranchDto
        BranchDto b = new BranchDto();
        b.setSwiftCode("ROUNDTRIP01");
        b.setBankName("Round Bank");
        b.setAddress("R Street");
        b.setCountryISO2("DE");
        b.setCountryName("GERMANY");
        b.setIsHeadquarter(false);

        SwiftCode back = DtoMapper.toSwiftCode(b);
        assertEquals("ROUNDTRIP01", back.getSwiftCode());
        assertEquals("Round Bank",  back.getBankName());
        assertEquals("R Street",    back.getAddress());
        assertEquals("DE",          back.getCountryISO2());
        assertEquals("GERMANY",     back.getCountryName());
        assertFalse(back.getIsHeadquarter());
    }
}
