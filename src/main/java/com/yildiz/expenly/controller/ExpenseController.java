package com.yildiz.expenly.controller;


import com.yildiz.expenly.model.Expense;
import com.yildiz.expenly.service.ExpenseService;
import com.yildiz.expenly.service.OCRService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private OCRService ocrService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        Expense createdExpense = expenseService.createExpense(expense);
        return ResponseEntity.ok(createdExpense);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Optional<Expense> expense = expenseService.getExpenseById(id);
        return expense.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Expense>> getExpensesByCompanyId(@PathVariable Long companyId) {
        List<Expense> expenses = expenseService.getExpensesByCompanyId(companyId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    @PostMapping("/upload")
    public ResponseEntity<Expense> uploadExpenseFile(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("expense_", ".jpg");
        file.transferTo(tempFile);

        String ocrText;
        try {
            ocrText = ocrService.extractTextFromImage(tempFile);
        } catch (TesseractException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
        Optional<Expense> parsedExpense = ocrService.parseExpenseFromText(ocrText);
        System.out.println(ocrText);

        return parsedExpense.map(expenseService::createExpense)
                            .map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.badRequest().build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable  Long id) {
        try {
            expenseService.deleteExpenseById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return  ResponseEntity.notFound().build();
        }
    }
}