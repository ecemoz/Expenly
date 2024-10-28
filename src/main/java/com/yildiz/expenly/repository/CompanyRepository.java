package com.yildiz.expenly.repository;

import com.yildiz.expenly.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCompany_Name(String companyName);
    Optional<Company> findByCompany_Id(Long companyId);
}
