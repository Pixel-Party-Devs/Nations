package de.terranova.nations.database;

import de.terranova.nations.NationsPlugin;
import de.terranova.nations.settlements.AccessLevelEnum;
import de.terranova.nations.settlements.Settlement;
import de.terranova.nations.settlements.level.Objective;
import de.terranova.nations.worldguard.math.Vectore2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class SettleDBstuff {

    public static void getInitialSettlementData() throws SQLException {
        try (Connection con = NationsPlugin.hikari.dataSource.getConnection();
             Statement statement = con.createStatement()) {
            statement.execute("SELECT * FROM `settlements_table`");
            ResultSet rs = statement.getResultSet();
            HashMap<UUID, Settlement> settlements = new HashMap<>();
            while (rs.next()) {
                UUID SUUID = UUID.fromString(rs.getString("SUUID"));
                String name = rs.getString("name");
                String location = rs.getString("location");
                int level = rs.getInt("level");
                int obj_a = rs.getInt("obj_a");
                int obj_b = rs.getInt("obj_b");
                int obj_c = rs.getInt("obj_c");
                int obj_d = rs.getInt("obj_d");
                Objective objective = new Objective(0, obj_a, obj_b, obj_c, obj_d, null, null, null, null);
                settlements.put(SUUID, new Settlement(SUUID, getMembersAccess(SUUID.toString()), new Vectore2(location), name, level, objective));
            }
            NationsPlugin.settlementManager.setSettlements(settlements);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " + "Please check the supplied database credentials in the config file", e);
        }
    }

    public static HashMap<UUID, AccessLevelEnum> getMembersAccess(String SUUID) throws SQLException {
        HashMap<UUID, AccessLevelEnum> access = new HashMap<>();
        try (Connection con = NationsPlugin.hikari.dataSource.getConnection();
             Statement statement = con.createStatement()) {
            statement.execute(String.format("SELECT * FROM `access_table` WHERE SUUID LIKE '%s'", SUUID));
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                access.put(UUID.fromString(rs.getString("PUUID")), AccessLevelEnum.valueOf(rs.getString("access")));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " + "Please check the supplied database credentials in the config file", e);
        }


        return access;
    }

    public static void addSettlement(UUID SUUID, String name, Vectore2 location, UUID owner) throws SQLException {

        try (Connection con = NationsPlugin.hikari.dataSource.getConnection();
             Statement statement = con.createStatement()) {
            statement.execute("INSERT INTO `settlements_table`" +
                    "(`SUUID`, `Name`, `Location`)" +
                    String.format("VALUES ('%s','%s','%s')", SUUID.toString(), name, location.asString()));
            statement.execute("INSERT INTO `access_table`" +
                    "(`SUUID`, `PUUID`, `ACCESS`) " +
                    String.format("VALUES ('%s','%s','MAJOR')", SUUID, owner.toString()));
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " + "Please check the supplied database credentials in the config file", e);
        }
    }

    public static void changeMemberAccess(UUID SUUID, UUID PUUID, AccessLevelEnum access) {
        if (access.equals(AccessLevelEnum.REMOVE)) {
            try (Connection con = NationsPlugin.hikari.dataSource.getConnection();
                 Statement statement = con.createStatement()) {
                statement.execute(String.format("DELETE FROM access_table WHERE access_table.SUUID = '%s' AND access_table.PUUID = '%s'", SUUID, PUUID));
            } catch (SQLException e) {
                throw new IllegalStateException("Failed to establish a connection to the MySQL database. " + "Please check the supplied database credentials in the config file", e);
            }
        } else {
            try (Connection con = NationsPlugin.hikari.dataSource.getConnection();
                 Statement statement = con.createStatement()) {
                statement.execute("INSERT INTO access_table (SUUID, PUUID, access)" +
                        String.format("VALUES ('%s', '%s', '%s')", SUUID, PUUID, access) +
                        "ON DUPLICATE KEY UPDATE access = VALUES(access);");
            } catch (SQLException e) {
                throw new IllegalStateException("Failed to establish a connection to the MySQL database. " + "Please check the supplied database credentials in the config file", e);
            }
        }
    }

    public static void rename(UUID SUUID, String name) {

        try (Connection con = NationsPlugin.hikari.dataSource.getConnection();
             Statement statement = con.createStatement()) {
            statement.execute(String.format("UPDATE settlements_table SET name = '%s' WHERE SUUID = '%s'", name, SUUID));
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " + "Please check the supplied database credentials in the config file", e);
        }

    }

    public static void setLevel(UUID SUUID, int level) {

        try (Connection con = NationsPlugin.hikari.dataSource.getConnection();
             Statement statement = con.createStatement()) {
            statement.execute(String.format("UPDATE settlements_table SET level = '%s' WHERE SUUID = '%s'", level, SUUID));
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " + "Please check the supplied database credentials in the config file", e);
        }
        try (Connection con = NationsPlugin.hikari.dataSource.getConnection();
             Statement statement = con.createStatement()) {
            statement.execute(String.format("UPDATE settlements_table SET obj_a = '0',obj_b = '0',obj_c = '0',obj_d = '0' WHERE SUUID = '%s'", SUUID));
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " + "Please check the supplied database credentials in the config file", e);
        }

    }

    //NOT STARTED WORKING AT YET
    public static void syncObjectives(UUID SUUID, int obj_a, int obj_b, int obj_c, int obj_d) {

        try (Connection con = NationsPlugin.hikari.dataSource.getConnection();
             Statement statement = con.createStatement()) {
            statement.execute(String.format("UPDATE settlements_table SET obj_a = '%s',obj_b = '%s',obj_c = '%s',obj_d = '%s' WHERE SUUID = '%s'", obj_a, obj_b, obj_c, obj_d, SUUID));
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to establish a connection to the MySQL database. " + "Please check the supplied database credentials in the config file", e);
        }

    }


}
