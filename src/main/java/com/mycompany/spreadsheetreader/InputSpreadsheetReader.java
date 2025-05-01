package com.mycompany.spreadsheetreader;

import static com.mycompany.spreadsheetreader.SwiftValidator.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InputSpreadsheetReader {
    private final SwiftCodeRepository swiftCodeRepository;
    
    public InputSpreadsheetReader(SwiftCodeRepository swiftCodeRepository){
    this.swiftCodeRepository = swiftCodeRepository;
    }
    
    
    
    public void readAndSaveSwiftCodes(){
    String filePath = "data/Interns_2025_SWIFT_CODES.xlsx";
    List<SwiftCode> swiftCodeList = new ArrayList<>();
    
    try(FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis)){
        Sheet sheet = workbook.getSheetAt(0);
         
        int rowStart = Math.min(1, sheet.getFirstRowNum());
        int rowEnd = Math.max(1062, sheet.getLastRowNum());
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
           Row r = sheet.getRow(rowNum);
           SwiftCode swiftCode = new SwiftCode();
           if (r == null) continue;
           swiftCode.setCountryISO2(r.getCell(0).getStringCellValue().trim().toUpperCase());
           swiftCode.setSwiftCode(r.getCell(1).getStringCellValue().trim());
           swiftCode.setBankName(r.getCell(3).getStringCellValue().trim());
           if (r.getCell(4).getStringCellValue().isBlank()) {
                swiftCode.setAddress(r.getCell(5).getStringCellValue().trim()); //if the address cell is blank, I use the town name from next column to provide any data
              } else {
                swiftCode.setAddress(r.getCell(4).getStringCellValue().trim());
              }
           swiftCode.setCountryName(r.getCell(6).getStringCellValue().trim().toUpperCase());
           swiftCode.setIsHeadquarter(swiftIsHeadquarterValidator(swiftCode.getSwiftCode()));
           
           if(swiftValidator(swiftCode.getSwiftCode())){
                    swiftCodeList.add(swiftCode);           
           }
        }

        }
        catch(IOException e){
            e.printStackTrace();
        }

    swiftCodeRepository.saveAll(swiftCodeList);
    }    
}
