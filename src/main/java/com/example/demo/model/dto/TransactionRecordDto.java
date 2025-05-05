package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.Customer;
import com.example.demo.model.entity.TransactionRecord;

public class TransactionRecordDto {

	private Long trxId;
	private String accountNumber;
	private BigDecimal trxAmount;
	private String description;
	private LocalDate trxDate;
	private LocalTime trxTime;
	private Long customerId;
	
	public Long getTrxId() {
		return trxId;
	}
	public void setTrxId(Long trxId) {
		this.trxId = trxId;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public BigDecimal getTrxAmount() {
		return trxAmount;
	}
	public void setTrxAmount(BigDecimal trxAmount) {
		this.trxAmount = trxAmount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDate getTrxDate() {
		return trxDate;
	}
	public void setTrxDate(LocalDate trxDate) {
		this.trxDate = trxDate;
	}
	public LocalTime getTrxTime() {
		return trxTime;
	}
	public void setTrxTime(LocalTime trxTime) {
		this.trxTime = trxTime;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	
	public static TransactionRecordDto fromEntity(TransactionRecord transactionRecord) {
		TransactionRecordDto dto = new TransactionRecordDto();
		
		Account account = transactionRecord.getAccount();
		Customer customer = account.getCustomer();
		
		dto.setAccountNumber(account.getAccountNumber());
		dto.setCustomerId(customer.getCustomerId());
		
		dto.setTrxId(transactionRecord.getTrxId());
		dto.setTrxAmount(transactionRecord.getTrxAmount());
		dto.setDescription(transactionRecord.getDescription());
		dto.setTrxDate(transactionRecord.getTrxDate());
		dto.setTrxTime(transactionRecord.getTrxTime());
		
		return dto;
	}
}
