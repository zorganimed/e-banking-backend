package org.mzo.ebankingbackend;

import jakarta.transaction.Transactional;
import org.mzo.ebankingbackend.entities.*;
import org.mzo.ebankingbackend.enums.AccountStatus;
import org.mzo.ebankingbackend.enums.OperationType;
import org.mzo.ebankingbackend.repositories.AccountOperationRepository;
import org.mzo.ebankingbackend.repositories.BankAccountRepository;
import org.mzo.ebankingbackend.repositories.CustomerRepository;
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
    CommandLineRunner commandLineRunner(BankAccountRepository bankAccountRepository){
        return  args -> {
            BankAccount  bankAccount = bankAccountRepository.findById("1c94e9b5-1d50-42f1-9165-375bf8f0f333").orElseThrow(()->new IllegalArgumentException("Account Not existing!"));

            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreateDate());
            System.out.println(bankAccount.getCustomer().getName());

            if(bankAccount instanceof CurrentAccount){
                System.out.println(((CurrentAccount) bankAccount).getOverDraft());
            }else if(bankAccount instanceof SavingAccount){
                System.out.println(((SavingAccount) bankAccount).getInterestRate());
            }

            bankAccount.getAccountOperations().forEach(operation->{
                System.out.println(" "+operation.getAmount()+" "+operation.getType()+" "+operation.getDateOperation());
            });
        };
    }

   // @Bean
    CommandLineRunner start(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){

        return args -> {
            Stream.of("Hassan","Mohamed","Jouri").forEach(name->{
                Customer customer=new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);

            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*9000);
                currentAccount.setCreateDate(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setOverDraft(9000);
                currentAccount.setCustomer(customer);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*9000);
                savingAccount.setCreateDate(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setInterestRate(5.5);
                savingAccount.setCustomer(customer);
                bankAccountRepository.save(savingAccount);

            });

            bankAccountRepository.findAll().forEach(account->{
                for (int i = 0; i < 5; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setDateOperation(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5 ? OperationType.DEBIT:OperationType.CREDIT);
                    accountOperation.setBankAccount(account);
                    accountOperationRepository.save(accountOperation);

                }
            });


        };
    }
}
