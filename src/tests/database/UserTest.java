package database;

import helper.Helper;
import main.PropsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @BeforeEach
    void setUp() throws IOException, SQLException {
        PropsManager.init();
    }

    @Test
    void getName() {
        final String name = Helper.randStringGen(12);
        assertEquals(name, new User(name, Helper.randStringGen(22)).getName());
    }

    @Test
    void getDecryptedPassword() {
        assertEquals(User.sha3("Passw0rd", "admin"),
                new User("admin", "Passw0rd").getDecryptedPassword());
    }
}