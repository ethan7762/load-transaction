package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.Customer;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.util.JsonUtil;

@Service
public class AccountServiceImpl implements AccountService {
	
	private final static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
	
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public Account saveAccount(String accountNumber, Long customerId) {
		
	 	Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
	 	
	 	if(!ObjectUtils.isEmpty(account)) {
	 		return account;
	 	}

		// account not exist, create new account with current account number
		account = new Account();
		account.setAccountNumber(accountNumber);

		if (customerId == null) {
			throw new RuntimeException("Customer id not found");
		}

		Customer customer = customerRepository.findByCustomerId(customerId).orElse(null);

		if (ObjectUtils.isEmpty(customer)) {
			// customer not exist, create new customer with current customer id
			customer = new Customer();
			customer.setCustomerId(customerId);
			customer = customerRepository.save(customer);

			logger.info("new customer: {}", JsonUtil.toJson(customer));
		}

		account.setCustomer(customer);
		account = accountRepository.save(account);

		logger.info("new account: {}", JsonUtil.toJson(account));
		
		return account;
	}
}
