package de.lecuutex.factions.utils;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class MySQL {
    private Connection conn = null;

    public MySQL() {
        connection();
    }

    private void connection() {
        try { conn = DriverManager.getConnection("jdbc:mysql://localhost/factions?autoReconnect=true", "factions", "DZhec1IMR7BkIb4o");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
