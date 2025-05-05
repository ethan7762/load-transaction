package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.example.demo.model.common.ObjectResponse;
import com.example.demo.model.dto.SearchTransactionRecordDto;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.dto.UpdateTransactionRecordDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.Customer;
import com.example.demo.model.entity.TransactionRecord;
import com.example.demo.repository.TransactionRecordRepository;
import com.example.demo.util.JsonUtil;
import com.example.demo.util.ReentrantLockByName;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class TransactionRecordServiceImpl implements TransactionRecordService  {
	
	private final static Logger logger = LoggerFactory.getLogger(TransactionRecordServiceImpl.class);
	
	@PersistenceContext
    private EntityManager entityManager;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionRecordRepository transactionRecordRepository;
	
	
	private ReentrantLockByName updateTrxLock = new ReentrantLockByName();

	@Override
	public ObjectResponse<Long> saveTransactionRecord(TransactionRecordDto dto) {
		
		ObjectResponse<Long> objResponse = new ObjectResponse<>();
		
	 	try {
			String accountNumber = dto.getAccountNumber();
			
			if(ObjectUtils.isEmpty(accountNumber)) {
				objResponse.setMessage("Account number not found");
				return objResponse;
			}
			
			Account account = accountService.saveAccount(accountNumber, dto.getCustomerId());
			
			TransactionRecord transactionRecord = new TransactionRecord();
			transactionRecord.setTrxAmount(dto.getTrxAmount());
			transactionRecord.setDescription(dto.getDescription());
			transactionRecord.setTrxDate(dto.getTrxDate());
			transactionRecord.setTrxTime(dto.getTrxTime());
			
			transactionRecord.setAccount(account);
			transactionRecord = transactionRecordRepository.save(transactionRecord);
			
			logger.info("saveTransactionRecord - saved: {}", JsonUtil.toJson(transactionRecord));
			
			objResponse.setSuccess(true);
			objResponse.setData(transactionRecord.getTrxId());
			
		} catch (Exception e) {
			logger.error("saveTransactionRecord error", e);
			objResponse.setMessage("Save transaction record failed");
		}
		
		return objResponse;
	}

	@Override
	public ObjectResponse<TransactionRecordDto> getTransactionRecord(Long trxId) {

		ObjectResponse<TransactionRecordDto> objResponse = new ObjectResponse<>();

		try {
			TransactionRecord transactionRecord = transactionRecordRepository.findById(trxId).orElse(null);

			if(transactionRecord != null) {
				objResponse.setSuccess(true);
				objResponse.setData(TransactionRecordDto.fromEntity(transactionRecord));
			}
			
		} catch (Exception e) {
			logger.error("getTransactionRecord error", e);
		}

		return objResponse;
	}

	@Override
	public ObjectResponse<TransactionRecordDto> updateTransactionRecord(Long trxId, UpdateTransactionRecordDto updateDto) {
		
		ObjectResponse<TransactionRecordDto> objResponse = new  ObjectResponse<>();

		TransactionRecord transactionRecord = transactionRecordRepository.findById(trxId).orElse(null);

		if (ObjectUtils.isEmpty(transactionRecord)) {
			objResponse.setMessage("Transaction record not found");
			return objResponse;
		}
		
		ReentrantLock lock = updateTrxLock.getLock(transactionRecord.getTrxId().toString());
		boolean lockAcquired = false;

		try {
			
			if (lock != null) {
				try {
					lockAcquired = lock.tryLock(2, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					logger.error("updateTransactionRecord - acquiring lock has timed out (2s)",
							transactionRecord.getTrxId().toString());
				}

				if (!lockAcquired) {
					objResponse.setMessage("Update transaction record failed with timeout");
					return objResponse;
				}
			}
			
			transactionRecord.setDescription(updateDto.getDescription());
			transactionRecord = transactionRecordRepository.save(transactionRecord);
			
			objResponse.setSuccess(true);
			objResponse.setData(TransactionRecordDto.fromEntity(transactionRecord));

		} catch (Exception e) {
			logger.error("updateTransactionRecord error", e);
			objResponse.setMessage("Update transaction record failed");
		} finally {
			if (lockAcquired) {
				lock.unlock();
			}
		}
		
		return objResponse;
	}

	@Override
	public Page<TransactionRecordDto> searchTransactionRecords(SearchTransactionRecordDto searchDto) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		
	    CriteriaQuery<TransactionRecord> query = cb.createQuery(TransactionRecord.class);
	    Root<TransactionRecord> root = query.from(TransactionRecord.class);
	    Join<TransactionRecord, Account> accountJoin = root.join("account");
	    Join<Account, Customer> customerJoin = accountJoin.join("customer");

	    List<Predicate> predicates = new ArrayList<>();

	    if (!ObjectUtils.isEmpty(searchDto.getDescription())) {
	        predicates.add(cb.like(cb.lower(root.get("description")), "%" + searchDto.getDescription().toLowerCase() + "%"));
	    }

	    if (!ObjectUtils.isEmpty(searchDto.getAccountNumbers())) {
	        predicates.add(accountJoin.get("accountNumber").in(searchDto.getAccountNumbers()));
	    }

	    if (searchDto.getCustomerId() != null) {
	        predicates.add(cb.equal(customerJoin.get("customerId"), searchDto.getCustomerId()));
	    }
	    
	    if (searchDto.getTrxId() != null) {
	        predicates.add(cb.equal(root.get("trxId"), searchDto.getTrxId()));
	    }
	    
	    query.where(cb.and(predicates.toArray(new Predicate[0])));

	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<TransactionRecord> countRoot = countQuery.from(TransactionRecord.class);
	    Join<TransactionRecord, Account> countAccountJoin = countRoot.join("account");
	    Join<Account, Customer> countCustomerJoin = countAccountJoin.join("customer");

	    List<Predicate> countPredicates = new ArrayList<>();

	    if (!ObjectUtils.isEmpty(searchDto.getDescription())) {
	        countPredicates.add(cb.like(cb.lower(countRoot.get("description")), "%" + searchDto.getDescription().toLowerCase() + "%"));
	    }

	    if (!ObjectUtils.isEmpty(searchDto.getAccountNumbers())) {
	    	countPredicates.add(countAccountJoin.get("accountNumber").in(searchDto.getAccountNumbers()));
	    }

	    if (!ObjectUtils.isEmpty(searchDto.getCustomerId())) {
	        countPredicates.add(cb.equal(countCustomerJoin.get("customerId"), searchDto.getCustomerId()));
	    }
	    
	    if (searchDto.getTrxId() != null) {
	    	countPredicates.add(cb.equal(countRoot.get("trxId"), searchDto.getTrxId()));
	    }

	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

	    int page = searchDto.getPage();
	    int size = searchDto.getSize();

	    List<TransactionRecord> resultList = entityManager.createQuery(query)
	            .setFirstResult(page * size)
	            .setMaxResults(size)
	            .getResultList();

	    List<TransactionRecordDto> dtoList = resultList.stream()
	            .map(TransactionRecordDto::fromEntity)
	            .collect(Collectors.toList());

	    return new PageImpl<>(dtoList, PageRequest.of(page, size), totalCount);
	}
}
