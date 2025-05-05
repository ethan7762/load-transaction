package com.example.demo.service;

import com.example.demo.model.entity.Account;

public interface AccountService {

	public Account saveAccount(String accountNumber, Long customerId);

}
