package com.example.demo.model.dto;

import java.util.List;

public class SearchTransactionRecordDto {

	private Long trxId;
	private Long customerId;
    private List<String> accountNumbers;
    private String description;

    private int page = 0;
    private int size = 10;
    
	public Long getTrxId() {
		return trxId;
	}
	public void setTrxId(Long trxId) {
		this.trxId = trxId;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}	
	public List<String> getAccountNumbers() {
		return accountNumbers;
	}
	public void setAccountNumbers(List<String> accountNumbers) {
		this.accountNumbers = accountNumbers;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
    
    

}
