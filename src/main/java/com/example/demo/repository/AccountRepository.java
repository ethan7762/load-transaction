package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	public Optional<Account> findByAccountNumber(String accountNumber);
}
