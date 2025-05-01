package com.mycompany.spreadsheetreader;

import com.mycompany.spreadsheetreader.exception.SwiftCodeNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SwiftCodeServiceTest {

    @Autowired
    private SwiftCodeService service;

    @Autowired
    private SwiftCodeRepository repository;

    private static final String EXISTING_CODE = "MOCKPLPWXXX";

    @BeforeEach
    void setUp() {
        // Start from a clean slate
        repository.deleteAll();

        // Insert a known entity for tests
        SwiftCode testCode = new SwiftCode();
        testCode.setSwiftCode(EXISTING_CODE);
        testCode.setBankName("Mock Bank");
        testCode.setAddress("Mock Address");
        testCode.setCountryISO2("PL");
        testCode.setCountryName("POLAND");
        testCode.setIsHeadquarter(true);

        service.save(testCode);
        assertEquals(1, repository.count(), "setup: should have one entry");
    }

    @Test
    void save_validSwiftCode_shouldSucceed() {
        // given
        SwiftCode swift = new SwiftCode();
        swift.setSwiftCode("TESTPLPWXXX");
        swift.setBankName("Test Bank");
        swift.setAddress("Address");
        swift.setCountryISO2("PL");
        swift.setCountryName("POLAND");
        swift.setIsHeadquarter(true);

        // when
        SwiftCode saved = service.save(swift);

        // then
        assertNotNull(saved.getId(), "save: ID should be generated");
        assertEquals("TESTPLPWXXX", saved.getSwiftCode());
        assertEquals(2, repository.count(), "two entries after save");
    }

    @Test
    void getBySwiftCode_existingCode_shouldReturnEntity() {
        SwiftCode found = service.getBySwiftCode(EXISTING_CODE);
        assertNotNull(found);
        assertEquals("Mock Bank", found.getBankName());
    }

    @Test
    void getBySwiftCode_nonExisting_shouldThrowNotFound() {
        assertThrows(SwiftCodeNotFoundException.class,
            () -> service.getBySwiftCode("UNKNOWN1234"));
    }

    @Test
    void delete_existingCode_shouldRemoveEntity() {
        service.delete(EXISTING_CODE);
        assertEquals(0, repository.count(), "repository should be empty after delete");
        assertThrows(SwiftCodeNotFoundException.class,
            () -> service.getBySwiftCode(EXISTING_CODE));
    }
}
