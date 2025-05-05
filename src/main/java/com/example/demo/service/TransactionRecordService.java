package com.example.demo.service;

import org.springframework.data.domain.Page;

import com.example.demo.model.common.ObjectResponse;
import com.example.demo.model.dto.SearchTransactionRecordDto;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.dto.UpdateTransactionRecordDto;

public interface TransactionRecordService {

	public ObjectResponse<Long> saveTransactionRecord(TransactionRecordDto dto);
	
	public ObjectResponse<TransactionRecordDto> getTransactionRecord(Long trxId);
	
	public ObjectResponse<TransactionRecordDto> updateTransactionRecord(Long trxId, UpdateTransactionRecordDto updateDto);

	public Page<TransactionRecordDto> searchTransactionRecords(SearchTransactionRecordDto searchDto);
	
}
