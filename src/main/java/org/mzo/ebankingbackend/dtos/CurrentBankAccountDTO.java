package org.mzo.ebankingbackend.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.mzo.ebankingbackend.entities.AccountOperation;
import org.mzo.ebankingbackend.entities.Customer;
import org.mzo.ebankingbackend.enums.AccountStatus;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentBankAccountDTO extends BankAccountDTO{

    private String id;
    private double balance;
    private Date createDate;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double overDraft;
}
