package com.yildiz.expenly.service;

import com.yildiz.expenly.model.Company;
import com.yildiz.expenly.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public Optional<Company> getCompanyByName(String name) {
        return companyRepository.findByCompany_Name(name);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public void deleteCompany(Long id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
        } else{
            throw new RuntimeException("Company not found");
        }
    }

    public Company updateCompany(Long id, Company updatedCompany) {
        return companyRepository.findByCompany_Id(id)
                .map(existingCompany -> {
                    existingCompany.setCompanyName(updatedCompany.getCompanyName());
                    existingCompany.setCompanyAddress(updatedCompany.getCompanyAddress());
                    existingCompany.setCompanyLogoUrl(updatedCompany.getCompanyLogoUrl());
                    return companyRepository.save(existingCompany);
                })
                .orElseThrow(() -> {
                    throw new RuntimeException("Company not found");
                });
    }
}
