package com.yildiz.expenly.controller;


import com.yildiz.expenly.model.Company;
import com.yildiz.expenly.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping
    public ResponseEntity<Company> creteCompany(@RequestBody Company company) {
        Company createdCompany = companyService.createCompany(company);
        return ResponseEntity.ok(createdCompany);
    }

    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {
        List <Company> company = companyService.getAllCompanies();
        return ResponseEntity.ok(company);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Company> getCompanyByName(@PathVariable String name) {
        Optional<Company> company = companyService.getCompanyByName(name);
        return company.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public  ResponseEntity<Company> updateCompany(@PathVariable  Long id , @RequestBody Company updatedCompany) {
        try {
            Company company = companyService.updateCompany(id, updatedCompany);
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return  ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable  Long id) {
        try {
            companyService.deleteCompany(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return  ResponseEntity.notFound().build();
        }
    }
}
