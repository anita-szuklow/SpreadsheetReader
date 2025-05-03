package com.mycompany.spreadsheetreader.testutil;

import com.mycompany.spreadsheetreader.SwiftCode;

public final class SwiftCodeTestFactory {
  private SwiftCodeTestFactory() {} // no instantiation

  public static Builder swift(String code) {
    return new Builder().code(code);
  }

  public static class Builder {
    private final SwiftCode s = new SwiftCode();
    public Builder code(String c)          { s.setSwiftCode(c);          return this; }
    public Builder bank(String b)          { s.setBankName(b);           return this; }
    public Builder address(String a)       { s.setAddress(a);            return this; }
    public Builder countryISO2(String i)   { s.setCountryISO2(i);        return this; }
    public Builder countryName(String n)   { s.setCountryName(n);        return this; }
    public Builder headquarter(boolean hq) { s.setIsHeadquarter(hq);     return this; }
    public SwiftCode build()              { return s; }
  }
}

