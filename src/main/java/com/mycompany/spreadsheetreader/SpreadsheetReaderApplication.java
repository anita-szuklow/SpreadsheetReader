package com.mycompany.spreadsheetreader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpreadsheetReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpreadsheetReaderApplication.class, args);
    }
//    @Bean
//    public CommandLineRunner run(InputSpreadsheetReader reader) {
//        return args -> {
//            reader.readAndSaveSwiftCodes();
//        };
//    }
}
