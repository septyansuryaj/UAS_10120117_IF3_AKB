/**
 * NIM : 10120117
 * NAMA : Septyan Surya Jatnika
 * KELAS : IF-3
 */

package com.uas.notes.helper;

public class StringHelper {
    // Generate Username From Email
    public static String usernameFromEmail(java.lang.String email) {
        if (!email.contains("@")) return email;

        return email.split("@")[0];
    }
}
