package org.mzo.ebankingbackend.services;

import org.mzo.ebankingbackend.dtos.*;
import org.mzo.ebankingbackend.entities.BankAccount;
import org.mzo.ebankingbackend.entities.CurrentAccount;
import org.mzo.ebankingbackend.entities.Customer;
import org.mzo.ebankingbackend.entities.SavingAccount;
import org.mzo.ebankingbackend.exceptions.BalanceNotSufficientException;
import org.mzo.ebankingbackend.exceptions.BankAccountException;
import org.mzo.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {


    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountException;
    void debit(String accountId,double amount, String description) throws BankAccountException, BalanceNotSufficientException;
    void credit(String accountId,double amount,String description) throws BankAccountException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountException, BalanceNotSufficientException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountException;

    List<CustomerDTO> searchCustomers(String keyword);

    List<BankAccountDTO> getCustomerAccounts(Long customerId);
}
