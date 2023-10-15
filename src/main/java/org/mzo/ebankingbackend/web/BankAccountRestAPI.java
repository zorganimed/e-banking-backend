package org.mzo.ebankingbackend.web;

import lombok.AllArgsConstructor;
import org.mzo.ebankingbackend.dtos.AccountHistoryDTO;
import org.mzo.ebankingbackend.dtos.AccountOperationDTO;
import org.mzo.ebankingbackend.dtos.BankAccountDTO;
import org.mzo.ebankingbackend.entities.AccountOperation;
import org.mzo.ebankingbackend.entities.BankAccount;
import org.mzo.ebankingbackend.exceptions.BankAccountException;
import org.mzo.ebankingbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
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
}
