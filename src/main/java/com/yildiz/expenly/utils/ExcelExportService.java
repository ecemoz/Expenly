package com.yildiz.expenly.utils;

import com.yildiz.expenly.model.Expense;
import com.yildiz.expenly.service.ExpenseService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private ExpenseService expenseService;

    public void exportAllExpensesToExcel(String filePath) {
        List<Expense> expenses = expenseService.getAllExpenses();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expenses");

            createHeaderRow(sheet);

            int rowCount = 1;
            for (Expense expense : expenses) {
                Row row = sheet.createRow(rowCount++);
                writeExpenseToRow(expense, row);
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
                System.out.println("Excel file created successfully at: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportExpensesToExcel(Long companyId, String filePath) {
        List<Expense> expenses = expenseService.getExpensesByCompanyId(companyId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expenses");

            createHeaderRow(sheet);

            int rowCount = 1;
            for (Expense expense : expenses) {
                Row row = sheet.createRow(rowCount++);
                writeExpenseToRow(expense, row);
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
                System.out.println("Excel file created successfully at: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("Store Name");
        headerRow.createCell(1).setCellValue("Date");
        headerRow.createCell(2).setCellValue("Time");
        headerRow.createCell(3).setCellValue("Total Expense");
        headerRow.createCell(4).setCellValue("Tax Amount");
        headerRow.createCell(5).setCellValue("Taxless Expense");

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (Cell cell : headerRow) {
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeExpenseToRow(Expense expense, Row row) {
        row.createCell(0).setCellValue(expense.getStoreName() != null ? expense.getStoreName() : "Unknown");
        row.createCell(1).setCellValue(
                expense.getExpenseDate() != null ? expense.getExpenseDate().format(DATE_FORMATTER) : ""
        );
        row.createCell(2).setCellValue(
                expense.getExpenseTime() != null ? expense.getExpenseTime().format(TIME_FORMATTER) : ""
        );
        row.createCell(3).setCellValue(
                expense.getTotalExpense() != null ? expense.getTotalExpense().doubleValue() : 0.0
        );
        row.createCell(4).setCellValue(
                expense.getTaxAmount() != null ? expense.getTaxAmount().doubleValue() : 0.0
        );
        row.createCell(5).setCellValue(
                expense.getTaxlessExpense() != null ? expense.getTaxlessExpense().doubleValue() : 0.0
        );
    }
}
