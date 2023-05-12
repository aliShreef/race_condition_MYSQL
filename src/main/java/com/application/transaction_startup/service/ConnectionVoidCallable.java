package com.application.transaction_startup.service;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionVoidCallable {
    void execute(Connection connection) throws SQLException;
}
