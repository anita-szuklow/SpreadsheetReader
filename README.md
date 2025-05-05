SpreadsheetReader – SWIFT Code Management API
This Spring Boot application imports SWIFT codes from an Excel spreadsheet and exposes a RESTful API to manage them. 
The data is stored in a MySQL database and the entire solution is containerized using Docker.

Requirements:
Docker & Docker Compose
(Optional) Java 21 & Maven 3.9+
MySQL 8 database named "swiftcodesdb" must exist before app starts

Setup instructions:
1. Clone the repository: https://https://github.com/anita-szuklow/SpreadsheetReader.git
2. Create MySQL Database "swiftcodesdb"
3. Place the spreadsheet (Interns_2025_SWIFT_CODES.xlsx) inside the /data directory of the project

Running with Docker
Ensure the swiftcodesdb database is created before running Docker Compose!

bash
docker-compose up --build

The application will:

Connect to MySQL
Parse and import the Interns_2025_SWIFT_CODES.xlsx file

Start REST API at http://localhost:8080
MySQL DB is exposed at: localhost:3306 (internal host: db)

Default credentials:
Username: root
Password: (empty)

API secured with basic auth:
Username: anita
Password: password123

Running Tests
If you're running tests outside Docker:

bash
mvn clean test
📬 API Endpoints
🔹 Get SWIFT Code Details (Headquarters or Branch)
bash
Kopiuj
Edytuj
GET /v1/swift-codes/{swiftCode}
🔹 Get All SWIFT Codes by Country
bash
Kopiuj
Edytuj
GET /v1/swift-codes/country/{countryISO2}
🔹 Add New SWIFT Code
bash
Kopiuj
Edytuj
POST /v1/swift-codes
Body Example:

json
Kopiuj
Edytuj
{
  "swiftCode": "BANKPLPWXXX",
  "bankName": "Bank PL",
  "countryISO2": "PL",
  "countryName": "POLAND",
  "isHeadquarter": true,
  "address": "Warsaw HQ"
}
🔹 Delete a SWIFT Code
bash
Kopiuj
Edytuj
DELETE /v1/swift-codes/{swiftCode}
🛡️ Validation and Error Handling
All inputs are validated for correct format and consistency.

Detailed and human-readable error responses are returned for:

Missing or invalid fields

Duplicated entries

Format mismatches (e.g., ISO2 or SWIFT code)

✅ Status
✔️ Ready for submission.
✔️ Fully functional and tested.
✔️ Dockerized and self-contained.

