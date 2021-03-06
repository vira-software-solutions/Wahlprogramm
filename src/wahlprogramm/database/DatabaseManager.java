/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.CollectionOfCollections;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import tabs.election.SektionDataModel;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseManager {
    public static final String DATABASE_LOCATION = "src/resources/wahlprogramm.database";

    public static boolean databaseExists() {
        return new File(DATABASE_LOCATION).exists();
    }

    public static void initializeDatabase(User admin) throws SQLException {
        var dbConfig = new FluentConfiguration();
        dbConfig.dataSource("jdbc:sqlite:" + DATABASE_LOCATION, "", "");
        dbConfig.locations("classpath:main/database/");
        dbConfig.mixed(true);
        Flyway flyway = new Flyway(dbConfig);
        try {
            flyway.migrate();
        } catch (Exception ignored) {

        }

        insertUser(admin);
    }

    private static Connection connect() {
        Connection conn;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_LOCATION);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            conn = null;
        }

        return conn;
    }

    private static Connection openConnection() {
        return connect();
    }

    private static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertUser(User user) throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return;
        }

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (username, password) VALUES(?,?);");
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getDecryptedPassword());
        stmt.executeUpdate();

        closeConnection(conn);
    }

    public static void insertCandidateForRoleForSektion(ObservableList<CandidatesDataModel> candidatesDataModels, String role, Integer sektion) throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return;
        }

        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO role_sektion_candidate (sektion_num, role_name, candidate_name) VALUES(?,?,?)");
        for (CandidatesDataModel candidatesDataModel : candidatesDataModels) {
            if (!doesCandidateAlreadyExist(candidatesDataModel)) {
                insertNewCandidate(candidatesDataModel);
            }

            stmt.setInt(1, sektion);
            stmt.setString(2, role);
            stmt.setString(3, candidatesDataModel.getName());
            stmt.executeUpdate();
        }

        conn.commit();
        closeConnection(conn);
    }

    public static void insertNewCandidate(CandidatesDataModel candidatesDataModel) throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return;
        }

        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO candidate (name, gender) VALUES (?,?);");
        stmt.setString(1, candidatesDataModel.getName());
        stmt.setString(2, candidatesDataModel.getGender());
        stmt.executeUpdate();
        conn.commit();

        closeConnection(conn);
    }

    public static boolean confirmUser(User user) throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return false;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT (*) AS count FROM user WHERE password=? AND username=?;");
        stmt.setString(1, user.getDecryptedPassword());
        stmt.setString(2, user.getName());

        int toRet = stmt.executeQuery().getInt("count");

        closeConnection(conn);

        return toRet == 1;
    }

    public static boolean doesCandidateAlreadyExist(CandidatesDataModel candidatesDataModel) throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return false;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM candidate WHERE name=? AND gender=?;");
        stmt.setString(1, candidatesDataModel.getName());
        stmt.setString(2, candidatesDataModel.getGender());
        ResultSet rs = stmt.executeQuery();

        boolean toRet = rs.getInt("count") > 0;

        closeConnection(conn);

        return toRet;
    }

    public static ObservableList<String> getRoles() throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT role.name AS name FROM role;");
        ResultSet rs = stmt.executeQuery();

        List<String> toRet = new ArrayList<>();
        while (rs.next()) {
            toRet.add(rs.getString("name"));
        }

        closeConnection(conn);

        return FXCollections.observableArrayList(toRet);
    }

    public static ObservableList<SektionDataModel> getSektionen() throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT sektion.num AS num, sektion.bezR AS bezR, sektion.bezK AS bezK FROM sektion;");
        ResultSet rs = stmt.executeQuery();

        List<SektionDataModel> toRet = new ArrayList<>();
        while (rs.next()) {
            toRet.add(new SektionDataModel(rs.getInt("num"), rs.getInt("bezR"), rs.getInt("bezK")));
        }

        closeConnection(conn);

        return FXCollections.observableArrayList(toRet);
    }

    public static ObservableList<CandidatesDataModel> getCandidatesForRole(Integer sektion, String role) throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement(
                "SELECT role_sektion_candidate.candidate_name AS name, " +
                        "candidate.gender AS gender " +
                        "FROM role_sektion_candidate " +
                        "INNER JOIN candidate " +
                        "ON role_sektion_candidate.candidate_name = candidate.name " +
                        "WHERE role_sektion_candidate.sektion_num=? " +
                        "AND role_sektion_candidate.role_name=?;");

        stmt.setInt(1, sektion);
        stmt.setString(2, role);
        ResultSet rs = stmt.executeQuery();

        List<CandidatesDataModel> toRet = new ArrayList<>();
        while (rs.next()) {
            toRet.add(
                    new CandidatesDataModel(
                            rs.getString("name"),
                            rs.getString("gender")
                    )
            );
        }

        closeConnection(conn);

        return FXCollections.observableArrayList(toRet);
    }

    public static ObservableList<String> getGender() throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT gender.name AS gender FROM gender;");
        ResultSet rs = stmt.executeQuery();

        List<String> toRet = new ArrayList<>();
        while (rs.next()) {
            toRet.add(rs.getString("gender"));
        }

        closeConnection(conn);

        return FXCollections.observableArrayList(toRet);
    }

    public static void dumpCandidatesForRoleOfSektion(Integer sektion, String role) throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return;
        }

        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM role_sektion_candidate WHERE sektion_num=? AND role_name=?;");
        stmt.setInt(1, sektion);
        stmt.setString(2, role);
        stmt.executeUpdate();
        conn.commit();

        closeConnection(conn);
        CollectionOfCollections.removeCandidates(sektion, role);
    }

    public static void dumpUnusedCandidates() throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return;
        }

        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM candidate WHERE candidate.name NOT IN (SELECT role_sektion_candidate.candidate_name FROM role_sektion_candidate);");
        stmt.executeUpdate();
        conn.commit();

        closeConnection(conn);
        CollectionOfCollections.updateCandidates();
    }

    public static List<String> getBlacklistedGendersForRole(String role) throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT role_gender_blacklist.gender AS gender FROM role_gender_blacklist WHERE role_gender_blacklist.role=?;");
        stmt.setString(1, role);
        ResultSet rs = stmt.executeQuery();

        List<String> toRet = new ArrayList<>();
        while (rs.next()) {
            toRet.add(rs.getString("gender"));
        }

        closeConnection(conn);

        return toRet;
    }

    public static String getVotingOptionFromRole(String role) throws SQLException {
        Connection conn = openConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT role.voting_option_type AS type FROM role WHERE role.name=?;");
        stmt.setString(1, role);
        ResultSet rs = stmt.executeQuery();

        String toRet = rs.getString("type");

        closeConnection(conn);

        return toRet;
    }

    public static void updateSeatInformation(SektionDataModel sektionDataModel) throws SQLException {
        Connection conn = openConnection();
        if (sektionDataModel == null || conn == null) {
            return;
        }

        PreparedStatement stmt = conn.prepareStatement("UPDATE sektion SET bezR=?, bezK=? WHERE num=?;");

        stmt.setInt(1, sektionDataModel.getBezR());
        stmt.setInt(2, sektionDataModel.getBezK());
        stmt.setInt(3, sektionDataModel.getNum());

        stmt.execute();

        closeConnection(conn);
    }
}
