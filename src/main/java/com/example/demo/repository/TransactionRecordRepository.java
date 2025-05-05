package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.TransactionRecord;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
	
}
