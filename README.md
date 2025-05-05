## SpreadsheetReader – SWIFT Code Management API

## Overview

This Spring Boot application imports SWIFT codes from an Excel spreadsheet and exposes a RESTful API to manage them. 
The data is stored in a MySQL database and the entire solution is containerized using Docker.

## Prerequisites

- Docker & Docker Compose
- (Optional) Java 21 & Maven 3.9+
- (Only if running without Docker) MySQL 8 database named "swiftcodesdb" must exist before app starts

## Project Structure

```plaintext
SpreadsheetReader
├── data
│   └── Interns_2025_SWIFT_CODES.xlsx
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── mycompany
│   │   │           └── spreadsheetreader
│   │   │               ├── dto
│   │   │               │   ├── BranchDto.java
│   │   │               │   ├── CountryDto.java
│   │   │               │   ├── HeadquarterDto.java
│   │   │               │   └── SwiftCodeDto.java
│   │   │               ├── exception
│   │   │               │   ├── CountryNotFoundException.java
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── HeadquarterFlagMismatchException.java
│   │   │               │   ├── InvalidIso2Exception.java
│   │   │               │   ├── InvalidSwiftCodeException.java
│   │   │               │   └── SwiftCodeNotFoundException.java
│   │   │               ├── DtoMapper.java
│   │   │               ├── InputSpreadsheetReader.java
│   │   │               ├── SecurityConfig.java
│   │   │               ├── SpreadsheetReaderApplication.java
│   │   │               ├── SwiftCode.java
│   │   │               ├── SwiftCodeController.java
│   │   │               ├── SwiftCodeRepository.java
│   │   │               ├── SwiftCodeService.java
│   │   │               └── SwiftValidator.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── com
│               └── mycompany
│                   └── spreadsheetreader
│                       ├── testutil
│                       │   ├── SwiftCodeTestFactory.java
│                       │   └── TestDataSeeder.java
│                       ├── DatabaseConnectionTest.java
│                       ├── DtoMapperTest.java
│                       ├── SwiftCodeControllerIntegrationTest.java
│                       ├── SwiftCodeControllerUnitTest.java
│                       ├── SwiftCodeServiceTest.java
│                       └── SwiftValidatorTest.java
├── target
├── .dockerignore
├── .gitignore
├── Dockerfile
├── README.md
├── docker-compose.yml
├── nbactions.xml
└── pom.xml
```

## Setup instructions:

1. Clone the repository:  
```bash
git clone https://github.com/anita-szuklow/SpreadsheetReader.git
cd SpreadsheetReader
```
2. (If you want to run locally, no Docker)
```bash
mvn clean package
```
3. Place the spreadsheet (Interns_2025_SWIFT_CODES.xlsx) inside the /data directory of the project

## Running 

With Docker:
```bash
docker-compose up --build
```

Locally (no Docker):
Start a local MySQL instance and ensure it's reachable at jdbc:mysql://localhost:3306/swiftcodesdb
```bash
mvn spring-boot:run
```

The application will:

Connect to MySQL
1. Parse and import the Interns_2025_SWIFT_CODES.xlsx file
2. Start REST API at http://localhost:8080
3. MySQL DB is exposed at: localhost:3306 (internal host: db)

## Configuration

Default credentials:
Username: root
Password: (empty)

API secured with basic auth:
Username: anita
Password: password123

If you need to override, set the usual Spring Boot properties via environment variables or application.properties

## Testing

With Docker:
```bash
docker compose up -d db
docker compose run --rm tester
```

Locally: 
```bash
mvn clean test
```

## API Endpoints

1. Get SWIFT Code Details (Headquarters or Branch)
GET /v1/swift-codes/{swiftCode}

2. Get All SWIFT Codes by Country
GET /v1/swift-codes/country/{countryISO2}

3. Add New SWIFT Code
POST /v1/swift-codes
Body Example:
json
{
  "swiftCode": "BANKPLPWXXX",
  "bankName": "Bank PL",
  "countryISO2": "PL",
  "countryName": "POLAND",
  "isHeadquarter": true,
  "address": "Warsaw HQ"
}

4. Delete a SWIFT Code
DELETE /v1/swift-codes/{swiftCode}

All endpoints require basic auth

## Validation and Error Handling

All inputs are validated for correct format and consistency.
Detailed and human-readable error responses are returned for:
Missing or invalid fields
Duplicated entries
Format mismatches (e.g., ISO2 or SWIFT code)
