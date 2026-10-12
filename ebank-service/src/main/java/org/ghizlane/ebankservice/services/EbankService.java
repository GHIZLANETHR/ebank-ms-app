package org.ghizlane.ebankservice.services;

import org.ghizlane.ebankservice.entities.BankAccount;
import org.ghizlane.ebankservice.model.Customer;
import org.ghizlane.ebankservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EbankService {
    private BankAccountRepository accountRepository;

    public EbankService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public List<BankAccount> getAllBankAccounts(){
        return accountRepository.findAll();
    }
    public BankAccount getBankAccountById(String id){
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));


    }
    public BankAccount save(BankAccount bankAccount){
        return accountRepository.save(bankAccount);

    }
}