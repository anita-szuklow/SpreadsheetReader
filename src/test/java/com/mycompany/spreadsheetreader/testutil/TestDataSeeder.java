package com.mycompany.spreadsheetreader.testutil;

import com.mycompany.spreadsheetreader.SwiftCode;
import com.mycompany.spreadsheetreader.SwiftCodeService;

public class TestDataSeeder {

    public static void seedBranches(
      SwiftCodeService service,
      String prefix,
      int count
    ) {
        for (int i = 1; i <= count; i++) {
            String code = prefix + String.format("%03d", i);
            service.save(
                SwiftCodeTestFactory
                  .swift(code)
                  .bank("Branch " + i)
                  .address("Addr " + i)
                  .countryISO2("pl")
                  .countryName("poland")
                  .headquarter(false)
                  .build()
            );
        }
    }
}
