package com.yildiz.expenly.service;

import com.yildiz.expenly.model.Expense;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OCRService {

    private final Tesseract tesseract;

    public OCRService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");
        tesseract.setLanguage("tur");
    }

    public String extractTextfromImage(File imageFile) throws TesseractException{
        return tesseract.doOCR(imageFile);
    }

    public Optional<Expense> parseExpenseFromText(String ocrText){
        try {
            Expense expense = new Expense();
            String [] lines = ocrText.split("\\r?\\n");

            String storeName = extractStoreName(lines);
            expense.setStoreName(storeName);

            expense.setExpenseDate(extractDate(ocrText));
            expense.setExpenseTime(extractTime(ocrText));

            expense.setTotalExpense(extractAmount(ocrText,"TOPLAM"));
            expense.setTaxAmount(extractAmount(ocrText,"TOPKDV"));
            expense.setTaxlessExpense(expense.getTotalExpense().subtract(expense.getTaxAmount()));

            return Optional.of(expense);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String extractStoreName(String[] lines){
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        for (String line : lines){
            Span [] tokens = tokenizer.tokenizePos(line);
            if (tokens.length > 1 && line.length () <30) {
                return line;
            }
        }
        return "Unknown company";
    }

    private LocalDate extractDate(String text){
        Pattern datePattern = Pattern.compile("\\b(\\d{2}[./]\\d{2}[./]\\d{4})\\b");
        Matcher matcher = datePattern.matcher(text);
        if (matcher.find()) {
            String date = matcher.group(1);
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return LocalDate.now();
    }

    private LocalTime extractTime(String text){
        Pattern timePattern = Pattern.compile("\\b(\\d{2}[.:]\\d{2})\\b");
        Matcher matcher = timePattern.matcher(text);
        if (matcher.find()) {
            String time = matcher.group(1);
            return LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm"));
        }
        return LocalTime.now();
    }

    private BigDecimal extractAmount(String text, String keyword){
       Pattern amountPattern = Pattern.compile (keyword + "\\s*([0-9.,]+)");
       Matcher matcher = amountPattern.matcher(text);
       if (matcher.find()) {
           String amount = matcher.group(1).replace(",", ".");
           return new BigDecimal(amount);
       }
       return BigDecimal.ZERO;
    }
}
