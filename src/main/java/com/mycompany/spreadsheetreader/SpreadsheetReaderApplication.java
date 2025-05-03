package com.mycompany.spreadsheetreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;

@SpringBootApplication
public class SpreadsheetReaderApplication {

    private static final Logger log = LoggerFactory.getLogger(SpreadsheetReaderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpreadsheetReaderApplication.class, args);
    }

    @Bean
    public CommandLineRunner importSwiftCodes(InputSpreadsheetReader reader,
                                              SwiftCodeRepository repository) {
        return args -> {
            long count = repository.count();
            if (count > 0) {
                log.info("Database already contains {} SWIFT codes — skipping import", count);
                return;
            }

            try {
                log.info("Importing SWIFT codes from spreadsheet…");
                reader.readAndSaveSwiftCodes();
                log.info("SWIFT import completed successfully");
            } catch (DataAccessException dae) {
                log.warn("Failed to import SWIFT codes (DB not ready?): {}", dae.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error during SWIFT import", e);
            }
        };
    }
}
