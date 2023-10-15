package org.mzo.ebankingbackend.mappers;

import com.fasterxml.jackson.databind.util.BeanUtil;
import org.mzo.ebankingbackend.dtos.AccountOperationDTO;
import org.mzo.ebankingbackend.dtos.CurrentBankAccountDTO;
import org.mzo.ebankingbackend.dtos.CustomerDTO;
import org.mzo.ebankingbackend.dtos.SavingBankAccountDTO;
import org.mzo.ebankingbackend.entities.AccountOperation;
import org.mzo.ebankingbackend.entities.CurrentAccount;
import org.mzo.ebankingbackend.entities.Customer;
import org.mzo.ebankingbackend.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

//MapStruct: permet le mapper d'un objet vers un autre
//son mécanisme créer juste les signatures des méthodes

@Service
public class BankAccountMapperImpl {

    public CustomerDTO fromCustomer(Customer customer){

        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer,customerDTO);

        /*customerDTO.setId(customer.getId());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setName(customerDTO.getName());*/
        return  customerDTO;
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO){

        Customer customer = new Customer();

        BeanUtils.copyProperties(customerDTO,customer);

        return customer;
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount){

        SavingBankAccountDTO savingBankAccountDTO = new SavingBankAccountDTO();
        BeanUtils.copyProperties(savingAccount,savingBankAccountDTO);
        savingBankAccountDTO.setCustomerDTO(fromCustomer(savingAccount.getCustomer()));
        savingBankAccountDTO.setType(savingAccount.getClass().getSimpleName());
        return savingBankAccountDTO;
    }

    public SavingAccount fromSavingAccountDTO(SavingBankAccountDTO savingBankAccountDTO){

        SavingAccount savingAccount = new SavingAccount();
        BeanUtils.copyProperties(savingBankAccountDTO,savingAccount);
        savingAccount.setCustomer(fromCustomerDTO(savingBankAccountDTO.getCustomerDTO()));
        savingBankAccountDTO.setType(savingAccount.getClass().getSimpleName());
        return savingAccount;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount){

        CurrentBankAccountDTO currentBankAccountDTO = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(currentAccount,currentBankAccountDTO);
        currentBankAccountDTO.setCustomerDTO(fromCustomer(currentAccount.getCustomer()));
        currentBankAccountDTO.setType(currentAccount.getClass().getSimpleName());
        return  currentBankAccountDTO;
    }

    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO){

        CurrentAccount currentAccount = new CurrentAccount();
        BeanUtils.copyProperties(currentBankAccountDTO,currentAccount);
        currentAccount.setCustomer(fromCustomerDTO(currentBankAccountDTO.getCustomerDTO()));
        return currentAccount;
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation){
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation,accountOperationDTO);
        return accountOperationDTO;
    }
}
