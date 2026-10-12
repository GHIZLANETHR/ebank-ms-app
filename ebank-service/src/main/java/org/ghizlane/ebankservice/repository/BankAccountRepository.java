package org.ghizlane.ebankservice.repository;


import org.ghizlane.ebankservice.entities.BankAccount;
import org.ghizlane.ebankservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbankService {
    private BankAccountRepository accountRepository;
    private CustomerRestClient customerRestClient;

    public EbankService(BankAccountRepository accountRepository, CustomerRestClient customerRestClient) {
        this.accountRepository = accountRepository;
        this.customerRestClient = customerRestClient;
    }

    @McpTool(description = "Get all Bank accounts")
    public List<BankAccount> getAllBankAccounts(){
        return accountRepository.findAll();
    }
    @McpTool(description = "Get a bank account by id")
    public BankAccount getBankAccountById(
            @McpToolParam(description = "The bank account id") String id){
        BankAccount bankAccount = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        bankAccount.setCustomer(
                customerRestClient.getCustomerById(bankAccount.getCustomerId()));
        return bankAccount;
    }
    @McpTool(description = "Save a new bank Account")
    public BankAccount save(
            @McpToolParam(description = "The bank account to save (balance, type, customerId)") BankAccount bankAccount){
        try{
            Customer customer = customerRestClient.getCustomerById(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreatedAt(new Date());
            return accountRepository.save(bankAccount);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage()) ;
        }

    }
}