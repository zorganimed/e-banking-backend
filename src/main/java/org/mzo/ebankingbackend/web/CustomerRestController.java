package org.mzo.ebankingbackend.web;

import lombok.AllArgsConstructor;
import org.mzo.ebankingbackend.dtos.BankAccountDTO;
import org.mzo.ebankingbackend.dtos.CustomerDTO;
import org.mzo.ebankingbackend.dtos.CustomersPageDTO;
import org.mzo.ebankingbackend.entities.Customer;
import org.mzo.ebankingbackend.exceptions.CustomerNotFoundException;
import org.mzo.ebankingbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class CustomerRestController {

    private BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<CustomerDTO> customers(){
        return bankAccountService.listCustomers();
    }

    @GetMapping("customers/{id}")
    public CustomerDTO getCustomer(@PathVariable("id") Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
       return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerId}")
    public  CustomerDTO updateCustomer(@PathVariable Long customerId,@RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id){
        bankAccountService.deleteCustomer(id);
    }


    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword", defaultValue = "") String keyword){
        return bankAccountService.searchCustomers("%"+keyword+"%");
    }

    @GetMapping("/customers/{customerId}/accounts")
    public List<BankAccountDTO> getCustomerAccounts(@PathVariable Long customerId){

        return this.bankAccountService.getCustomerAccounts(customerId);
    }

    @GetMapping("/customers/searchByName")
    public CustomersPageDTO searchCustomers(@RequestParam(name = "keyword", defaultValue = "") String keyword,
                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                            @RequestParam(name = "size", defaultValue = "1") int size){
        return bankAccountService.searchCustomerPage("%"+keyword+"%",page,size);
    }

}
