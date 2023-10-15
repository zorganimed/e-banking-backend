package org.mzo.ebankingbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mzo.ebankingbackend.enums.AccountStatus;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingBankAccountDTO extends BankAccountDTO{

    private String id;
    private double balance;
    private Date createDate;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double interestRate;

}
