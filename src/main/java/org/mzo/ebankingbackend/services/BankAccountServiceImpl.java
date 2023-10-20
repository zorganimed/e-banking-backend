package org.mzo.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mzo.ebankingbackend.dtos.*;
import org.mzo.ebankingbackend.entities.*;
import org.mzo.ebankingbackend.enums.AccountStatus;
import org.mzo.ebankingbackend.enums.OperationType;
import org.mzo.ebankingbackend.exceptions.BalanceNotSufficientException;
import org.mzo.ebankingbackend.exceptions.BankAccountException;
import org.mzo.ebankingbackend.exceptions.CustomerNotFoundException;
import org.mzo.ebankingbackend.mappers.BankAccountMapperImpl;
import org.mzo.ebankingbackend.repositories.AccountOperationRepository;
import org.mzo.ebankingbackend.repositories.BankAccountRepository;
import org.mzo.ebankingbackend.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
//@Log4j
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{

    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;
    //Logger logger = LoggerFactory.getLogger(this.getClass().getName())



    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer) ;
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if(customer == null)
            throw new CustomerNotFoundException("Customer Not found");
        CurrentAccount  currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreateDate(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        currentAccount.setStatus(AccountStatus.CREATED);
        CurrentAccount currentBankAccount= bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(currentBankAccount) ;
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if(customer == null)
            throw new CustomerNotFoundException("Customer Not found");
        SavingAccount  savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreateDate(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        savingAccount.setStatus(AccountStatus.CREATED);

        SavingAccount savingBankAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savingBankAccount);
    }

   @Override
    public List<CustomerDTO> listCustomers() {
       List<Customer> customers = customerRepository.findAll();
       List<CustomerDTO> customerDTOS = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());

       /* Méthode impérative
       List<CustomerDTO> customerDTOS = new ArrayList<>();
       for (Customer cutomer:customers) {
           CustomerDTO customerDTO = dtoMapper.fromCustomer(cutomer);
           customerDTOS.add(customerDTO);
       }*/
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountException("Bank account not found"));

        if(bankAccount instanceof  SavingAccount){
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount , String description) throws BankAccountException, BalanceNotSufficientException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountException("Bank account not found"));

        if(bankAccount.getBalance()<amount)
            throw new BalanceNotSufficientException("Banlance not sufficient");

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setDateOperation(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountException("Bank account not found"));

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setDateOperation(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountException, BalanceNotSufficientException {

        debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList(){
        /*return bankAccountRepository.findAll().stream().map(account->{
            if(account instanceof SavingAccount)
                return dtoMapper.fromSavingBankAccount((SavingAccount) account)
            else
                return dtoMapper.fromCurrentBankAccount((CurrentAccount) account);
        }).collect(Collectors.toList());*/
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());

        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer updatedCustomer = customerRepository.save(customer) ;
        return dtoMapper.fromCustomer(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation>  accountOperations = accountOperationRepository.findByBankAccountId(accountId);

        return accountOperations.stream().map(operation->dtoMapper.fromAccountOperation(operation)).collect(Collectors.toList());

    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null)
            throw new BankAccountException("Account not found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByDateOperationDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO>  accountOperationDTOS = accountOperations.getContent().stream().map(operation -> dtoMapper.fromAccountOperation(operation)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.findByNameContains(keyword);
        List<CustomerDTO> customerDTOS = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public List<BankAccountDTO> getCustomerAccounts(Long customerId) {

        List<BankAccount> bankAccounts = bankAccountRepository.findByCustomerId(customerId);
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());

        return bankAccountDTOS;
    }

    @Override
    public void deleteAccount(String accountId){
        bankAccountRepository.deleteById(accountId);
    }


    @Override
    public CustomersPageDTO searchCustomerPage(String customerName, int page, int size){

        Page<Customer> customers = customerRepository.findByNameContainsOrderByNameDesc(customerName, PageRequest.of(page,size));
        CustomersPageDTO customersPageDTO = new CustomersPageDTO();
        List<CustomerDTO> customerDTOS = customers.getContent().stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        customersPageDTO.setCurrentPage(page);
        customersPageDTO.setPageSize(size);
        customersPageDTO.setTotalPages(customers.getTotalPages());
        customersPageDTO.setCustomerDTOS(customerDTOS);
        return customersPageDTO;

    }
}
