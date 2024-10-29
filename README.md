# Expense Management OCR Service

This project is an OCR-based expense management system built using Spring Boot. It allows users to upload images of receipts, extracts expense details using OCR (Optical Character Recognition), and saves the information to the database. The extracted details include store name, date, time, total amount, tax amount, and taxless amount.

## Project Structure

- **OCRService**: Main service class responsible for extracting text from images and parsing expense details.
- **Expense**: Model class representing the expense data, including fields like store name, date, time, total amount, tax amount, and taxless expense.
- **Controller**: Handles HTTP requests for uploading receipt images and retrieving expenses.

## Requirements

- **Java 17** or higher
- **Spring Boot 2.5.x**
- **Tesseract OCR** (language data should be set in `tessdata` folder)
- **Lombok** (for getter and setter annotations)

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/ecemoz/expenly.git
   cd expenly
   ```

2. Configure `tessdata` path in `OCRService` if needed:
   ```java
   tesseract.setDatapath("tessdata");
   tesseract.setLanguage("tur");
   ```

3. Build and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. Test the endpoints using **Postman** or **cURL**.

## API Documentation

### Base URL

The base URL for all endpoints is:
```
http://localhost:3001*/api
```

### Endpoints

#### 1. Upload Expense Receipt

- **URL**: `/upload`
- **Method**: `POST`
- **Description**: This endpoint allows you to upload a receipt image. The server extracts relevant details from the receipt, including store name, date, time, tax amount, and total amount.
- **Request Parameters**:
  - `file`: Multipart file (the image of the receipt).
- **Response**:
  - **200 OK**: Returns the parsed expense details.
  - **400 Bad Request**: If the parsing fails or no relevant information is extracted.

**Request Example (using cURL)**:
```bash
curl -X POST http://localhost:3001*/upload \
  -F 'file=@/path/to/receipt.jpg'
```

**Response Example**:
```json
{
  "id": 1,
  "storeName": "Sample Store",
  "expenseDate": "2024-05-26",
  "expenseTime": "14:30:00",
  "totalExpense": 100.00,
  "taxAmount": 10.00,
  "taxlessExpense": 90.00
}
```

#### 2. Get Expense by ID

- **URL**: `/expenses/{id}`
- **Method**: `GET`
- **Description**: Retrieve an expense record by its ID.
- **Path Parameters**:
  - `id` (required): The ID of the expense.
- **Response**:
  - **200 OK**: Returns the expense data if found.
  - **404 Not Found**: If no expense is found with the given ID.

**Request Example**:
```bash
curl -X GET http://localhost:3001*/expenses/1
```

**Response Example**:
```json
{
  "id": 1,
  "storeName": "Sample Store",
  "expenseDate": "2024-05-26",
  "expenseTime": "14:30:00",
  "totalExpense": 100.00,
  "taxAmount": 10.00,
  "taxlessExpense": 90.00
}
```

#### 3. Get All Expenses

- **URL**: `/expenses`
- **Method**: `GET`
- **Description**: Retrieve all expense records stored in the database.
- **Response**:
  - **200 OK**: Returns a list of all expenses.

**Request Example**:
```bash
curl -X GET http://localhost:3001*/expenses
```

**Response Example**:
```json
[
  {
    "id": 1,
    "storeName": "Sample Store",
    "expenseDate": "2024-05-26",
    "expenseTime": "14:30:00",
    "totalExpense": 100.00,
    "taxAmount": 10.00,
    "taxlessExpense": 90.00
  },
  {
    "id": 2,
    "storeName": "Another Store",
    "expenseDate": "2024-05-27",
    "expenseTime": "12:15:00",
    "totalExpense": 200.00,
    "taxAmount": 20.00,
    "taxlessExpense": 180.00
  }
]
```

#### 4. Delete Expense by ID

- **URL**: `/expenses/{id}`
- **Method**: `DELETE`
- **Description**: Deletes an expense record by its ID.
- **Path Parameters**:
  - `id` (required): The ID of the expense.
- **Response**:
  - **200 OK**: If the expense is successfully deleted.
  - **404 Not Found**: If no expense is found with the given ID.

**Request Example**:
```bash
curl -X DELETE http://localhost:3001*/expenses/1
```

**Response Example**:
```json
{
  "message": "Expense with ID 1 deleted successfully."
}
```

## Error Codes and Responses

- **400 Bad Request**: Invalid input or failed to parse the image.
- **404 Not Found**: Expense not found.
- **500 Internal Server Error**: An unexpected error occurred on the server side.

## Data Model

### Expense Model

| Field           | Type         | Description                     |
|-----------------|--------------|---------------------------------|
| id              | Long         | Unique identifier of the expense |
| storeName       | String       | Name of the store               |
| expenseDate     | LocalDate    | Date of the expense             |
| expenseTime     | LocalTime    | Time of the expense             |
| totalExpense    | BigDecimal   | Total amount including tax      |
| taxAmount       | BigDecimal   | Tax amount                      |
| taxlessExpense  | BigDecimal   | Amount excluding tax            |

## Example Use Case

1. User uploads a receipt image using `/upload`.
2. The OCR service extracts text data and parses store name, date, time, tax, and total amounts.
3. The parsed data is stored in the database.
4. User can view all expenses or retrieve individual expenses by ID.
5. User can delete expenses if needed.

## Testing

### Postman

You can import these endpoints into Postman for testing. Use the `POST /upload` endpoint to upload receipt images and test the OCR functionality.

### cURL

You can also test the endpoints using `cURL` commands as shown in the examples above.


