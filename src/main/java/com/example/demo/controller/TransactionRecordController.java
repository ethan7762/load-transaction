package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.common.ErrorResponse;
import com.example.demo.model.common.ObjectResponse;
import com.example.demo.model.dto.SearchTransactionRecordDto;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.dto.UpdateTransactionRecordDto;
import com.example.demo.service.TransactionRecordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRecordController {

	@Autowired
	private TransactionRecordService transactionRecordService;
	
	@GetMapping
	public ResponseEntity<Object> searchTransactionRecords(SearchTransactionRecordDto searchDto) {

		try {
			
			Page<TransactionRecordDto> pagedRecords = transactionRecordService.searchTransactionRecords(searchDto);

			Map<String, Object> response = new HashMap<>();
			response.put("content", pagedRecords.getContent());
			response.put("page", pagedRecords.getNumber());
			response.put("size", pagedRecords.getSize());
			response.put("totalElements", pagedRecords.getTotalElements());
			response.put("totalPages", pagedRecords.getTotalPages());

			return new ResponseEntity<Object>(response, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<Object>(new ErrorResponse("Failed to search transaction records"), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping("/{trxId}")
	public ResponseEntity<Object> getTransactionRecord(@PathVariable String trxId) {
		
		ObjectResponse<TransactionRecordDto> objResponse = transactionRecordService.getTransactionRecord(Long.parseLong(trxId));
		
		if(!objResponse.isSuccess()) {
			return new ResponseEntity<Object>(new ErrorResponse("Transaction record not found"), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<Object>(objResponse.getData(), HttpStatus.OK);
	}
	
	@PatchMapping("/{trxId}")
	public ResponseEntity<Object> updateTransactionRecord(@PathVariable String trxId, @RequestBody @Valid UpdateTransactionRecordDto updateDto) {
		
		ObjectResponse<TransactionRecordDto> objResponse = transactionRecordService.updateTransactionRecord(Long.parseLong(trxId), updateDto);
		
		if(!objResponse.isSuccess()) {
			return new ResponseEntity<Object>(new ErrorResponse(objResponse.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Object>(objResponse.getData(), HttpStatus.OK);
	}

}
