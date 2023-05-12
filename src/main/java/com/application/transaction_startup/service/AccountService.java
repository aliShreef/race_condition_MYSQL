package com.application.transaction_startup.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class AccountService {

    @Autowired
    DataSource dataSource;


    public void transfer(String fromIban, String toIban, long amount){
        long fromBalanceAmount = getBalance(fromIban);
        if(fromBalanceAmount >= amount){
            addBalance(fromIban, (-1) * amount);
            addBalance(toIban,  amount);

        }
    }

    public void addBalance(String iban, long amount) {
        doInJdbc(connection -> {
            try(PreparedStatement statement = connection.prepareStatement("update account set balance = balance+? where iban = ?")){
                statement.setLong(1,amount);
                statement.setString(2,iban);
                statement.executeUpdate();
            }
        });
    }

    public void addBalance(Connection connection,String iban, long amount) {
            try(PreparedStatement statement = connection.prepareStatement("update account set balance = balance+? where iban = ?")){
                statement.setLong(1,amount);
                statement.setString(2,iban);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }

    public long getBalance(Connection connection,String iban) {
            try(PreparedStatement statement = connection.prepareStatement("select balance from account where iban = ? ")) {
                statement.setString(1,iban);
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()){
                    return resultSet.getLong(1);
                }
                throw new IllegalArgumentException(
                        "Can't find account with IBAN: " + iban
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


    }


    public long getBalance(String iban) {
        return doInJdbc(connection -> {
            try(PreparedStatement statement = connection.prepareStatement("select balance from account where iban = ? ")) {
                statement.setString(1,iban);
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()){
                    return resultSet.getLong(1);
                }
                throw new IllegalArgumentException(
                        "Can't find account with IBAN: " + iban
                );
            }
        });
    }

    public void doInJdbc(ConnectionVoidCallable callable){
        try{
            Connection connection = null;
            try{
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);
                //connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                callable.execute(connection);
                connection.commit();
            }catch (SQLException e){
                if(connection != null){
                    connection.rollback();
                }
                throw e;
            }finally {
                if(connection != null){
                    connection.close();
                }
            }
        }catch (SQLException e){
            throw new IllegalStateException(e);
        }
    }

    public <T> T doInJdbc(ConnectionCallable<T> callable){
        try {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);
                //connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                T result = callable.execute(connection);
                connection.commit();
                return result;
            } catch (SQLException e) {
                if(connection != null) {
                    connection.rollback();
                }
                throw e;
            } finally {
                if(connection !=  null) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}

