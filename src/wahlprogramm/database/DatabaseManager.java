/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.PropsManager;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import tabs.election.SektionDataModel;
import tabs.electionPreparation.CandidatesDataModel;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseManager {
    public static final String DATABASE_LOCATION = "src/resources/wahlprogramm.database";

    public static boolean databaseExists(){
        return new File(DATABASE_LOCATION).exists();
    }

    public static void initializeDatabase(User admin) throws SQLException {
        var dbConfig = new FluentConfiguration();
        dbConfig.dataSource("jdbc:sqlite:"+DATABASE_LOCATION, "", "");
        dbConfig.locations("classpath:main/database/");
        dbConfig.mixed(true);
        Flyway flyway = new Flyway(dbConfig);
        try {
            flyway.migrate();
        }
        catch (Exception ignored){

        }

        insertUser(admin);
    }

    private static Connection Connect() {
        Connection conn;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(PropsManager.Props.getProperty("database"));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            conn = null;
        }

        return conn;
    }

    private static Connection OpenConnection() {
        Connection conn = Connect();

        return conn;
    }

    private static void CloseConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static final void insertUser(User user) throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return;
        }

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (username, password) VALUES(?,?);");
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getDecryptedPassword());
        stmt.executeUpdate();

        CloseConnection(conn);
    }

    public static final void insertCandidateForRoleForSektion(ObservableList<CandidatesDataModel> candidatesDataModels, String role, Integer sektion) throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return;
        }

        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO role_sektion_candidate (sektion_num, role_name, candidate_name) VALUES(?,?,?)");
        for (CandidatesDataModel candidatesDataModel : candidatesDataModels) {
            if(!doesCandidateAlreadyExist(candidatesDataModel)){
                insertNewCandidate(candidatesDataModel);
            }

            stmt.setInt(1, sektion);
            stmt.setString(2, role);
            stmt.setString(3, candidatesDataModel.getName());
            stmt.executeUpdate();
        }

        conn.commit();
        CloseConnection(conn);
    }

    public static final void insertNewCandidate(CandidatesDataModel candidatesDataModel) throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return;
        }

        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO candidate (name, gender) VALUES (?,?);");
        stmt.setString(1, candidatesDataModel.getName());
        stmt.setString(2, candidatesDataModel.getGender());
        stmt.executeUpdate();
        conn.commit();

        CloseConnection(conn);
    }

    public static boolean confirmUser(User user) throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return false;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT (*) AS count FROM user WHERE password=? AND username=?;");
        stmt.setString(1, user.getDecryptedPassword());
        stmt.setString(2, user.getName());

        int toRet = stmt.executeQuery().getInt("count");

        CloseConnection(conn);

        return toRet == 1;
    }

    public static final boolean doesCandidateAlreadyExist(CandidatesDataModel candidatesDataModel) throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return false;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM candidate WHERE name=? AND gender=?;");
        stmt.setString(1, candidatesDataModel.getName());
        stmt.setString(2, candidatesDataModel.getGender());
        ResultSet rs = stmt.executeQuery();

        boolean toRet = rs.getInt("count") > 0;

        CloseConnection(conn);

        return toRet;
    }

    public static final ObservableList<String> getRoles() throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT role.name AS name FROM role;");
        ResultSet rs = stmt.executeQuery();

        List<String> toRet = new ArrayList<>();
        while (rs.next()) {
            toRet.add(rs.getString("name"));
        }

        CloseConnection(conn);

        return FXCollections.observableArrayList(toRet);
    }

    public static final ObservableList<SektionDataModel> getSektionen() throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT num AS num FROM sektion;");
        ResultSet rs = stmt.executeQuery();

        List<SektionDataModel> toRet = new ArrayList<>();
        while (rs.next()) {
            toRet.add(new SektionDataModel(rs.getInt("num")));
        }

        CloseConnection(conn);

        return FXCollections.observableArrayList(toRet);
    }

    public static final ObservableList<CandidatesDataModel> getCandidatesForRole(Integer sektion, String role) throws SQLException {
        Connection conn = OpenConnection();
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

        CloseConnection(conn);

        return FXCollections.observableArrayList(toRet);
    }

    public static final ObservableList<String> getGender() throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT gender.name AS gender FROM gender;");
        ResultSet rs = stmt.executeQuery();

        List<String> toRet = new ArrayList<>();
        while (rs.next()) {
            toRet.add(rs.getString("gender"));
        }

        CloseConnection(conn);

        return FXCollections.observableArrayList(toRet);
    }

    public static final void dumpCandidatesForRoleOfSektion(Integer sektion, String role) throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return;
        }

        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM role_sektion_candidate WHERE sektion_num=? AND role_name=?;");
        stmt.setInt(1, sektion);
        stmt.setString(2, role);
        stmt.executeUpdate();
        conn.commit();

        CloseConnection(conn);
    }

    public static void dumpUnusedCandidates() throws SQLException {
        Connection conn = OpenConnection();
        if (conn == null) {
            return;
        }

        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM candidate WHERE candidate.name NOT IN (SELECT role_sektion_candidate.candidate_name FROM role_sektion_candidate);");
        stmt.executeUpdate();
        conn.commit();

        CloseConnection(conn);
    }

    public static List<String> getBlacklistedGendersForRole(String role) throws SQLException {
        Connection conn = OpenConnection();
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

        CloseConnection(conn);

        return toRet;
    }

    public static String getVotingOptionFromRole(String role) throws SQLException{
        Connection conn = OpenConnection();
        if (conn == null) {
            return null;
        }

        PreparedStatement stmt = conn.prepareStatement("SELECT role.voting_option_type AS type FROM role WHERE role.name=?;");
        stmt.setString(1, role);
        ResultSet rs = stmt.executeQuery();

        String toRet = rs.getString("type");

        CloseConnection(conn);

        return toRet;
    }
}
