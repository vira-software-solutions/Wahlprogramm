/*
 * Copyright (c) 2019. Johanna Rührig aka Vira aka TheRealVira
 * All rights reserved.
 */

package database;

import main.PropsManager;
import org.bouncycastle.crypto.generators.SCrypt;

public class User {
    private String Name;
    private String Password;

    public User(String name, String password) {
        Name = name;
        Password = sha3(password, name);
    }

    public static String sha3(final String input, final String salt) {
        byte[] scrypt = SCrypt.generate(
                input.getBytes(),
                salt.getBytes(),
                Integer.parseInt((String) PropsManager.Props.get("scrypt.N")),
                Integer.parseInt((String) PropsManager.Props.get("scrypt.r")),
                Integer.parseInt((String) PropsManager.Props.get("scrypt.p")),
                Integer.parseInt((String) PropsManager.Props.get("scrypt.dkLen")));

        return User.hashToString(scrypt);
    }

    private static String hashToString(byte[] hash) {
        StringBuffer buff = new StringBuffer();

        for (byte b : hash) {
            buff.append(String.format("%02x", b & 0xFF));
        }

        return buff.toString();
    }

    public String getName() {
        return Name;
    }

    public String getDecryptedPassword() {
        return Password;
    }
}
