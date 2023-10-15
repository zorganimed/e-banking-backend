package org.mzo.ebankingbackend.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mzo.ebankingbackend.enums.OperationType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOperationDTO {

    private Long id;
    private Date dateOperation;
    private double amount;
    private OperationType type;
    private String description;
}
