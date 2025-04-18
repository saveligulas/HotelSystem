package fhv.hotel.event.utility;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ClassHasher {
    public static byte[] hashClass(Class<?> clazz) {
        String resourceName = "/" + clazz.getName().replace('.', '/') + ".class";
        try (InputStream is = clazz.getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IOException("Could not locate class file for " + clazz.getName());
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            return digest.digest();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash class: " + clazz.getName(), e);
        }
    }
}
