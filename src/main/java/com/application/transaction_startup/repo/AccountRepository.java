package com.application.transaction_startup.repo;

import com.application.transaction_startup.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account,Long> {


    @Query(value = "SELECT balance FROM account WHERE iban = :iban" ,
        nativeQuery = true)
    long getBalance(@Param("iban") String iban);

    @Query(value = "update account set balance = balance + :amount where iban = :iban",
        nativeQuery = true)
    @Modifying
    @Transactional
    int addBalance(@Param("iban") String iban, @Param("amount") long amount);

}
