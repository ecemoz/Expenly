package com.yildiz.expenly.controller;

import com.yildiz.expenly.utils.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/export")
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping("/expenses/{companyId}")
    public ResponseEntity<byte[]> exportExpenses(@PathVariable Long companyId) throws IOException {
        String filePath = System.getProperty("java.io.tmpdir") + "expenses.xlsx";

        excelExportService.exportExpensesToExcel(companyId, filePath);

        File file = new File(filePath);
        byte[] fileContent;
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            fileContent = bos.toByteArray();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expenses.xlsx")
                .body(fileContent);
    }

    @GetMapping("/allExpenses")
    public ResponseEntity<byte[]> exportAllExpenses() throws IOException {
        String filePath = System.getProperty("java.io.tmpdir") + "all_expenses.xlsx" ;

        excelExportService.exportAllExpensesToExcel(filePath);

        File file = new File(filePath);
        byte[] fileContent;
        try(FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()){
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            fileContent = bos.toByteArray();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expenses.xlsx")
                .body(fileContent);

    }
}
