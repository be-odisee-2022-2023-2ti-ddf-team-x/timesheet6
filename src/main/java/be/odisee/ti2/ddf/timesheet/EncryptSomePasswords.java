package be.odisee.ti2.ddf.timesheet;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class EncryptSomePasswords {

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static void main(String[] args) {

        String[] passwords = {"courtois", "wullaert", "evrard","geheim"};

        for (String pwd : passwords ) {
            System.out.printf("%s is {bcrypt}%s%n", pwd, hash(pwd));
        }
    }
}
