package com.mycompany.spreadsheetreader;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Test
    void shouldConnectToDatabase() {
        // Try to count entries (even if 0)
        long count = swiftCodeRepository.count();
        
        System.out.println("Database connection OK. SwiftCode entries found: " + count);
        
        // Just to make sure we don't throw anything
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
}
