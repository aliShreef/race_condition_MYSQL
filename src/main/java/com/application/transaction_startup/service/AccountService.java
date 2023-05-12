package com.application.transaction_startup.service;


import com.application.transaction_startup.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transfer(String fromIban, String toIban, long amount){
        long fromBalanceAmount = accountRepository.getBalance(fromIban);

        if(fromBalanceAmount >= amount){
            accountRepository.addBalance(fromIban, (-1)*amount);
            accountRepository.addBalance(toIban, amount);
        }
    }


}

