package org.mzo.ebankingbackend;

import jakarta.transaction.Transactional;
import org.mzo.ebankingbackend.dtos.CurrentBankAccountDTO;
import org.mzo.ebankingbackend.dtos.CustomerDTO;
import org.mzo.ebankingbackend.dtos.SavingBankAccountDTO;
import org.mzo.ebankingbackend.entities.*;
import org.mzo.ebankingbackend.enums.AccountStatus;
import org.mzo.ebankingbackend.enums.OperationType;
import org.mzo.ebankingbackend.exceptions.BalanceNotSufficientException;
import org.mzo.ebankingbackend.exceptions.BankAccountException;
import org.mzo.ebankingbackend.exceptions.CustomerNotFoundException;
import org.mzo.ebankingbackend.repositories.AccountOperationRepository;
import org.mzo.ebankingbackend.repositories.BankAccountRepository;
import org.mzo.ebankingbackend.repositories.CustomerRepository;
import org.mzo.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Mohamed","Ahmed","Ali").forEach(name->{
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setName(name);
                customerDTO.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customerDTO);
            });

            bankAccountService.listCustomers().forEach(customer->{
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*12000,5.5, customer.getId());

                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            bankAccountService.bankAccountList().forEach(account->{
                String accountId;
                for (int i = 0; i < 10; i++) {

                    try {
                        if(account instanceof SavingBankAccountDTO){
                            accountId = ((SavingBankAccountDTO) account).getId();
                        }else{
                            accountId = ((CurrentBankAccountDTO) account).getId();
                        }
                        bankAccountService.credit(accountId, 1000+Math.random()*120000,"Credit");
                        bankAccountService.debit(accountId, 1000+Math.random()*9000,"Debit");
                    } catch (BankAccountException | BalanceNotSufficientException e) {
                        e.printStackTrace();
                    }


                }
            });

        };
    }

    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository) {

        return args -> {
            Stream.of("Hassan", "Mohamed", "Jouri").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                customerRepository.save(customer);

            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 9000);
                currentAccount.setCreateDate(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setOverDraft(9000);
                currentAccount.setCustomer(customer);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 9000);
                savingAccount.setCreateDate(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setInterestRate(5.5);
                savingAccount.setCustomer(customer);
                bankAccountRepository.save(savingAccount);

            });

            bankAccountRepository.findAll().forEach(account -> {
                for (int i = 0; i < 5; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setDateOperation(new Date());
                    accountOperation.setAmount(Math.random() * 12000);
                    accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                    accountOperation.setBankAccount(account);
                    accountOperationRepository.save(accountOperation);

                }
            });


        };
    }
}
