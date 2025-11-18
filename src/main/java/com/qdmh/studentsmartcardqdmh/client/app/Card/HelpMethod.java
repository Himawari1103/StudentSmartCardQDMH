package com.qdmh.studentsmartcardqdmh.client.app.Card;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

public class HelpMethod {

    // Method to convert a string to a byte array
    public static byte[] ConvertStringToByteArr(String input) {
        // Log the input string
        // System.out.println("Input String: " + input);

        // Convert string to byte array using UTF-8 encoding
        byte[] byteArray = input.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        // System.out.println("Initialized byte array of length: " + byteArray.length);

        // Debug print the resulting byte array
        // System.out.println("Resulting byte array: " + java.util.Arrays.toString(byteArray));

        return byteArray;
    }

    // Method to convert byte array to string array using a delimiter
    /* Mảng Byte sẽ được chuyển sang mảng String, thứ tự các giá trị trong mảng String như sau:
        String[0]: Hoten
        String[1]: NgaySinh
        String[2]: QueQuan(diaChi)
        String[3]: GioiTinh
        String[4]: SDT
        String[5]: MaBenhNhan(SoBHYT)
     */
    public static String[] convertByteToStringArr(byte[] byteArray, char delimiter) {
        // Check if the byte array is null
        if (byteArray == null) {
            return null;
        }

        // Convert byte array to string with UTF-8 decoding
        String str = new String(byteArray, java.nio.charset.StandardCharsets.UTF_8);

        // Manually split the string using the delimiter
        List<String> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == delimiter) {
                parts.add(currentPart.toString());
                currentPart.setLength(0); // Reset the StringBuilder
            } else {
                currentPart.append(c);
            }
        }
        // Add the last part
        parts.add(currentPart.toString());

        // Convert the list to an array and return
        return parts.toArray(new String[0]);
    }

    // Method to convert byte array to image
    public static BufferedImage convertByteArrayToImage(byte[] byteArray) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            return ImageIO.read(bis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to convert image to byte array
    public static byte[] convertImageToByteArray(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public static boolean verifySignature(byte[] publicKeyBytes, byte[] dataToVerify, byte[] signedData) throws Exception {
        short expLen = ByteBuffer.wrap(new byte[]{publicKeyBytes[publicKeyBytes.length - 2], publicKeyBytes[publicKeyBytes.length - 1]}).getShort();
        short modLen = ByteBuffer.wrap(new byte[]{publicKeyBytes[publicKeyBytes.length - 4], publicKeyBytes[publicKeyBytes.length - 3]}).getShort();
        byte[] modulusBytes = Arrays.copyOfRange(publicKeyBytes, 0, modLen);
        byte[] exponentBytes = Arrays.copyOfRange(publicKeyBytes, modLen, modLen + expLen);
        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger exponent = new BigInteger(1, exponentBytes);
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
        Signature rsaSign = Signature.getInstance("MD5withRSA");
        rsaSign.initVerify(publicKey);
        rsaSign.update(dataToVerify);
        return rsaSign.verify(signedData);
    }

    }
