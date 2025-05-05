package com.example.demo.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TransactionRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trx_id")
	private Long trxId;
	
	@Column(name = "trx_amount")
	private BigDecimal trxAmount;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "trx_date")
    private LocalDate trxDate;
	
	@Column(name = "trx_time")
    private LocalTime trxTime;
    
	@ManyToOne
    @JoinColumn(name="acount_id")
    private Account account;

	public Long getTrxId() {
		return trxId;
	}

	public void setTrxId(Long trxId) {
		this.trxId = trxId;
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}
