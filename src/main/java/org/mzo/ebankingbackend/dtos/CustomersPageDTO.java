package org.mzo.ebankingbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomersPageDTO {

    private int currentPage;
    private int totalPages;
    private int pageSize;
    List<CustomerDTO> customerDTOS;
}
