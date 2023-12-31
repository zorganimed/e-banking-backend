package org.mzo.ebankingbackend.repositories;

import org.mzo.ebankingbackend.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(" select c from Customer c where c.name like :kw ")
    List<Customer> findByNameContains(@Param(value = "kw") String keyword);

    @Query(" select c from Customer c where c.name like :kw ")
    Page<Customer> findByNameContainsOrderByNameDesc(@Param(value = "kw") String keyword, Pageable pageable);


}
