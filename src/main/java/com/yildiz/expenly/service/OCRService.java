package com.yildiz.expenly.service;

import com.yildiz.expenly.model.Expense;
import lombok.Getter;
import lombok.Setter;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Getter
@Setter
public class OCRService {

    private final Tesseract tesseract;

    @Autowired
    private ImageProcessService imageProcessService;

    public OCRService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        tesseract.setLanguage("eng");
        tesseract.setLanguage("tur");
    }

    public String extractTextFromImage(File imageFile) throws TesseractException, IOException {
        BufferedImage img = ImageIO.read(imageFile);
        BufferedImage processedImage = imageProcessService.preprocessImage(img);
        String ocrText = tesseract.doOCR(processedImage);
        System.out.println("OCR Output:\n" + ocrText);
        return ocrText;
    }

    public Optional<Expense> parseExpenseFromText(String ocrText) {
        try {
            Expense expense = new Expense();
            String[] lines = ocrText.split("\\r?\\n");

            String storeName = extractStoreName(lines);
            expense.setStoreName(storeName);
            System.out.println("Store Name: " + storeName);

            LocalDate date = extractDate(ocrText);
            expense.setExpenseDate(date);
            System.out.println("Date: " + date);

            LocalTime time = extractTime(ocrText);
            expense.setExpenseTime(time);
            System.out.println("Time: " + time);

            BigDecimal taxAmount = BigDecimal.ZERO;
            BigDecimal totalAmount = BigDecimal.ZERO;
            boolean foundDivider = false;

            for (String line : lines) {
                line = line.trim();
                System.out.println("Processing Line: " + line);
                    if (containsKeyword(line, "TOPKDV") && taxAmount.equals(BigDecimal.ZERO)) {
                        taxAmount = extractAmount(line);
                        expense.setTaxAmount(taxAmount);
                        System.out.println("Extracted Tax Amount: " + taxAmount);
                    } else if (containsKeyword(line, "TOPLAM") && totalAmount.equals(BigDecimal.ZERO)) {
                        totalAmount = extractAmount(line);
                        expense.setTotalExpense(totalAmount);
                        System.out.println("Extracted Total Amount: " + totalAmount);
                        break;
                    }

            }

            if (!totalAmount.equals(BigDecimal.ZERO) && !taxAmount.equals(BigDecimal.ZERO)) {
                BigDecimal taxlessExpense = totalAmount.subtract(taxAmount);
                expense.setTaxlessExpense(taxlessExpense);
                System.out.println("Taxless Expense: " + taxlessExpense);
            } else {
                System.out.println("Total or Tax amount is zero, cannot calculate taxless expense.");
            }

            return Optional.of(expense);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String extractStoreName(String[] lines) {
        for (String line : lines) {
            if (line.length() < 30) {
                System.out.println("Found Store Name: " + line);
                return line;
            }
        }
        System.out.println("Store Name Not Found");
        return "Unknown company";
    }

    private LocalDate extractDate(String text) {
        // Adjust regex to match dates with commas or periods as separators
        Pattern datePattern = Pattern.compile("\\b(\\d{2}[.,]\\d{2}[.,]\\d{4})\\b");
        Matcher matcher = datePattern.matcher(text);
        if (matcher.find()) {
            // Normalize the date format by replacing commas with periods
            String dateStr = matcher.group(1).replace(",", ".");
            System.out.println("Found Date: " + dateStr);
            try {
                // Parse the normalized date string
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (Exception e) {
                System.err.println("Date Parse Error: " + e.getMessage());
            }
        }
        // If no date is found, default to the current date
        System.out.println("Date Not Found, using current date");
        return LocalDate.now();
    }


    private LocalTime extractTime(String text) {
        Pattern timePattern = Pattern.compile("\\b(\\d{2}:\\d{2})\\b");
        Matcher matcher = timePattern.matcher(text);
        if (matcher.find()) {
            String timeStr = matcher.group(1);
            System.out.println("Found Time: " + timeStr);
            return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        }
        System.out.println("Time Not Found, using current time");
        return LocalTime.now();
    }

    private BigDecimal extractAmount(String line) {
        // Allow symbols like ¥, spaces, and commas/periods for decimals
        Pattern amountPattern = Pattern.compile("[¥€$£]?\\s*(\\d{1,3}(?:[.,]\\d{2})?)");
        Matcher matcher = amountPattern.matcher(line);
        if (matcher.find()) {
            String amountString = matcher.group(1).replace(",", ".").trim();
            System.out.println("Parsed Amount String: " + amountString);
            try {
                return new BigDecimal(amountString);
            } catch (NumberFormatException e) {
                System.err.println("BigDecimal Parse Error: " + e.getMessage());
            }
        } else {
            System.out.println("No amount found in line: " + line);
        }
        return BigDecimal.ZERO;
    }





    private boolean containsKeyword(String line, String keyword) {
        String regex;
        switch (keyword) {
            case "TOPKDV":
                regex = "TOP[KQ]?[DVÜY]";
                break;
            case "TOPLAM":
                regex = "TOP[L1I][A4][MNxX]?";
                break;
            default:
                return false;
        }
        Pattern pattern = Pattern.compile("(?i)" + regex);
        Matcher matcher = pattern.matcher(line);
        boolean found = matcher.find();
        System.out.println("Keyword '" + keyword + "' found in line '" + line + "': " + found);
        return found;
    }
}