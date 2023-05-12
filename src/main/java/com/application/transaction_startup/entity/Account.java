package com.application.transaction_startup.entity;


import javax.persistence.*;

@Entity
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "PK_ID")
    private long id;

    @Column(name = "IBAN")
    private String iban;

    @Column(name = "BALANCE")
    private Double balance;

    @Column(name = "OWNER")
    private String owner;

}
