package org.mzo.ebankingbackend.web;

import lombok.AllArgsConstructor;
import org.mzo.ebankingbackend.dtos.*;
import org.mzo.ebankingbackend.exceptions.BalanceNotSufficientException;
import org.mzo.ebankingbackend.exceptions.BankAccountException;
import org.mzo.ebankingbackend.exceptions.CustomerNotFoundException;
import org.mzo.ebankingbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRestAPI {

    private BankAccountService bankAccountService;

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts(){
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId){
       return  bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(@PathVariable String accountId,
                                               @RequestParam(name = "page", defaultValue = "0") int page,
                                               @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountException {
        return  bankAccountService.getAccountHistory(accountId,page,size);
    }

    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountException, BalanceNotSufficientException {

        this.bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;

    }

    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountException {

        this.bankAccountService.credit(creditDTO.getAccountId(), creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO;

    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) throws BankAccountException, BalanceNotSufficientException {

        this.bankAccountService.transfer(transferRequestDTO.getAccountSource(),
                                         transferRequestDTO.getAccountDestination() ,
                                         transferRequestDTO.getAmount());

    }

    @DeleteMapping("/accounts/{id}")
    public void deleteAccount(@PathVariable String id){
        bankAccountService.deleteAccount(id);
    }

    @PutMapping("/accounts/update/{accountId}")
    public BankAccountDTO updateAccount(@PathVariable String accountId) throws BankAccountException, CustomerNotFoundException {

        return bankAccountService.updateAccount(accountId);
    }

    @PostMapping("accounts/addSavingAccount/{customerId}")
    public SavingBankAccountDTO saveSavingAccount( @RequestBody SavingBankAccountDTO savingBankAccountDTO,
                                                   @PathVariable Long customerId) throws CustomerNotFoundException {
        return bankAccountService.saveSavingBankAccount(savingBankAccountDTO.getBalance(),savingBankAccountDTO.getInterestRate(),customerId);
    }

    @PostMapping("/accounts/addCurrentAccount/{customerId}")
    public CurrentBankAccountDTO saveCurrentAccount(@RequestBody CurrentBankAccountDTO currentBankAccountDTO,
                                                    @PathVariable Long customerId) throws CustomerNotFoundException {
        return bankAccountService.saveCurrentBankAccount(currentBankAccountDTO.getBalance(),currentBankAccountDTO.getOverDraft(), customerId);
    }
}
