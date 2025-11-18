package com.qdmh.studentsmartcardqdmh.applet.studentapplet;

import javacard.framework.*;
import javacard.security.AESKey;
import javacard.security.KeyBuilder;
import javacard.security.KeyPair;
import javacard.security.RSAPrivateKey;
import javacard.security.RSAPublicKey;
import javacard.security.RandomData;
import javacard.security.Signature;
import javacardx.apdu.ExtendedLength;
import javacardx.crypto.Cipher;

public class StudentApplet extends Applet implements ExtendedLength {

    // Student instance
    private static Student student;

    private static short MAX_SIZE = 32767;
    private static short dataLen;
    private static byte counter = 0;
    private static boolean cardCreated = false;
    private static boolean block_card = false;
    
    // AES key for encrypt and decrypt data
    private AESKey aesKey;
    private Cipher cipher;
    private short aesKeyLen;

    // Signature by RSA algorythm for verify
    private RSAPrivateKey rsaPrivKey;
    private RSAPublicKey rsaPubKey;
    private Signature rsaSig;

    // Random data to create AES key from PIN code
    private RandomData randomData;

    // APDU instruction codes
    private static final byte UNBLOCK_CARD = (byte) 0x11;
    private static final byte INS_RQPIN = (byte) 0x12;
    private static final byte INS_GETINFO = (byte) 0x13;
    private static final byte INS_GETBALANCE = (byte) 0x14;
    private static final byte INS_UPDATEBALANCE = (byte) 0x16;
    private static final byte CLEAR_CARD = (byte) 0x18;
    private static final byte CHECK_PIN = (byte) 0x19;
    private static final byte UPDATE_BN = (byte) 0x20;
    private static final byte UPDATE_PIN = (byte) 0x21;
    private static final byte INS_UPDATE_PIC = (byte) 0x22;
    private static final byte INS_GET_PIC = (byte) 0x23;
    private static final byte INS_GET_PUBLIC_KEY = (byte) 0x24;
    private static final byte INS_GET_SIGN = (byte) 0x25;
    private static final byte INS_UPDATE_CARDID = (byte) 0x26;
    private static final byte INS_GET_CARDID = (byte) 0x27;
    private static final byte LOCK_CARD = (byte) 0x28;
    private static final byte CHECK_CARD_CREATED = (byte) 0x29;
    private static final byte VERIFY_PIN = (byte) 0x30;

    private static final byte INS_TEST_CONNECT = (byte) 0x01;

    // Static buffers
    private static byte[] abc = {(byte) 0x3A, (byte) 0x00, (byte) 0x01};
//    private static byte[] tempBuffer = new byte[256];
//    private static byte[] temp = new byte[256];

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new StudentApplet();
    }

    public StudentApplet() {
        // init for AES
        aesKeyLen = (short) (KeyBuilder.LENGTH_AES_128 / 8);
        aesKey = (AESKey) (KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false));
        cipher = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);

        // init for random data
        randomData = RandomData.getInstance(RandomData.ALG_PSEUDO_RANDOM);

        // init for RSA
        rsaSig = Signature.getInstance(Signature.ALG_RSA_MD5_PKCS1, false);
        KeyPair keyPair = new KeyPair(KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1024);
        keyPair.genKeyPair();
        rsaPrivKey = (RSAPrivateKey) keyPair.getPrivate();
        rsaPubKey = (RSAPublicKey) keyPair.getPublic();

        randomData.setSeed(new byte[]{'H', 'e', 'l', 'l', 'o', 'W', 'o', 'r', 'l', 'd'}, (short) 0, (short) 10);
        byte[] keyData = new byte[aesKeyLen];
        randomData.generateData(keyData, (short) 0, aesKeyLen);
        aesKey.setKey(keyData, (short) 0);

        student = new Student();  // Student instance
        register();
    }

    public void process(APDU apdu) {
        if (selectingApplet()) {
            return;
        }

        byte[] buf = apdu.getBuffer();
        short len = apdu.setIncomingAndReceive();

        switch (buf[ISO7816.OFFSET_INS]) {
            case UPDATE_BN:
                receiveInfo(apdu, buf, len);
                break;

            case UPDATE_PIN:
                update_pin(apdu, len);

            case INS_RQPIN:
                get_pin(apdu);
                break;

            case INS_GETINFO:
                sendInfo(apdu);
                break;

            case INS_GETBALANCE:
                get_balance(apdu);
                break;

            case INS_UPDATEBALANCE:
                update_balance(apdu, len);
                break;

            case CLEAR_CARD:
                clear_card(apdu);
                break;

            case CHECK_PIN:
                processCard(apdu, len);
                break;

            case UNBLOCK_CARD:
                unblockcard(apdu);
                break;

            case LOCK_CARD:
                lockcard(apdu);
                break;

            case INS_UPDATE_PIC:
                receivePicture(apdu, buf, len);
                break;

            case INS_GET_PIC:
                sendPicture(apdu);
                break;

            case INS_GET_PUBLIC_KEY:
                getPublicKey(apdu, buf);
                break;

            case INS_GET_SIGN:
                signData(apdu, buf, len);
                break;

            case INS_UPDATE_CARDID:
                updateCardId(apdu, len);
                break;

            case INS_GET_CARDID:
                getCardId(apdu);
                break;

            case CHECK_CARD_CREATED:
                checkCardCreated(apdu);
                break;

            case VERIFY_PIN:
                verifyPin(apdu, len);
                break;

            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                throw new ISOException(ISO7816.SW_INS_NOT_SUPPORTED);
//                break;
        }
    }
    
    private void getHelloWorld(APDU apdu) {
        byte[] hello = {'h', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd'};
        byte[] buffer = apdu.getBuffer();
        short length = (short) hello.length;
        Util.arrayCopy(hello, (short) 0, buffer, (short) 0, length);

        apdu.setOutgoingAndSend((short) 0, length);
    }
    
    private void receiveInfo(APDU apdu, byte[] buf, short recvLen) {
        dataLen = apdu.getIncomingLength();
        if (dataLen > MAX_SIZE) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        short dataOffset = apdu.getOffsetCdata();
        short pointer = 0;
        byte[] rawInfo = new byte[dataLen];

        while (recvLen > 0) {
            Util.arrayCopy(buf, dataOffset, rawInfo, pointer, recvLen);
            pointer += recvLen;
            recvLen = apdu.receiveBytes(dataOffset);
        }
        byte[] encryptedInfo = encryptAes(rawInfo);
        JCSystem.beginTransaction();
        try {
            student.setInfo(encryptedInfo);
            student.setLenInfo((short) encryptedInfo.length);
            JCSystem.commitTransaction();
        } catch (Exception e) {
            JCSystem.abortTransaction();
            ISOException.throwIt(ISO7816.SW_UNKNOWN);
        }
        cardCreated = true;
    }

    private void update_pin(APDU apdu, short len) {
        byte[] buffer = apdu.getBuffer();
        byte[] rawPin = new byte[len];
        Util.arrayCopy(buffer, ISO7816.OFFSET_CDATA, rawPin, (short) 0, len);
        byte[] encryptedPin = encryptAes(rawPin);
        JCSystem.beginTransaction();
        try {
            student.setPin(encryptedPin);
            student.setLenPin((short) encryptedPin.length);
            JCSystem.commitTransaction();
        } catch (Exception e) {
            JCSystem.abortTransaction();
            ISOException.throwIt(ISO7816.SW_UNKNOWN);
        }
    }

    private void get_pin(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte[] decryptedPin = decryptAes(student.getPin());
        short pinLength = (short) decryptedPin.length;
        Util.arrayCopy(decryptedPin, (short) 0, buffer, (short) 0, pinLength);
        apdu.setOutgoingAndSend((short) 0, pinLength);
    }

    private void sendInfo(APDU apdu) {
        byte[] encryptedInfo = student.getInfo();
        byte[] rawInfo = decryptAes(encryptedInfo);
        short toSend = (short) rawInfo.length;
        short maxLenCanSend = apdu.setOutgoing();
        apdu.setOutgoingLength(toSend);
        short sendLen;
        short pointer = 0;
        while (toSend > 0) {
            sendLen = (toSend > maxLenCanSend) ? maxLenCanSend : toSend;
            apdu.sendBytesLong(rawInfo, pointer, sendLen);
            toSend -= sendLen;
            pointer += sendLen;
        }
    }

    private void get_balance(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte[] decryptedBalance = decryptAes(student.getBalance());
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) decryptedBalance.length);
        Util.arrayCopy(decryptedBalance, (short) 0, buffer, (short) 0, (short) decryptedBalance.length);
        apdu.sendBytes((short) 0, (short) decryptedBalance.length);
    }

    private void update_balance(APDU apdu, short len) {
        byte[] buffer = apdu.getBuffer();
        // Retrieve the new balance from the APDU buffer
        byte[] rawBalance = new byte[len];
        Util.arrayCopy(buffer, ISO7816.OFFSET_CDATA, rawBalance, (short) 0, (short) len);
        byte[] encryptedBalance = encryptAes(rawBalance);
        JCSystem.beginTransaction();
        try {
            student.setBalance(encryptedBalance);
            student.setLenBalance((short) encryptedBalance.length);
            JCSystem.commitTransaction();
        } catch (Exception e) {
            JCSystem.abortTransaction();
            ISOException.throwIt(ISO7816.SW_UNKNOWN);
        }
    }
  
    private void clear_card(APDU apdu) {
        // Reset the lengths of each field
        student.setLenInfo((short) 0);
        student.setLenPin((short) 0);
        student.setLenBalance((short) 0);
        student.setLenCardId((short) 0);
        student.setLenPicture((short) 0);

        // Clear each byte array, if not null
        byte[] info = student.getInfo();
        if (info != null) {
            Util.arrayFillNonAtomic(info, (short) 0, (short) info.length, (byte) 0);
        }

        byte[] pin = student.getPin();
        if (pin != null) {
            Util.arrayFillNonAtomic(pin, (short) 0, (short) pin.length, (byte) 0);
        }

        byte[] balance = student.getBalance();
        if (balance != null) {
            Util.arrayFillNonAtomic(balance, (short) 0, (short) balance.length, (byte) 0);
        }

        byte[] cardId = student.getCardId();
        if (cardId != null) {
            Util.arrayFillNonAtomic(cardId, (short) 0, (short) cardId.length, (byte) 0);
        }

        byte[] picture = student.getPicture();
        if (picture != null) {
            Util.arrayFillNonAtomic(picture, (short) 0, (short) picture.length, (byte) 0);
        }
        cardCreated = false;
    }
    
    private void processCard(APDU apdu, short len) {
        byte[] buffer = apdu.getBuffer();
        byte[] decryptedPin = decryptAes(student.getPin());
        apdu.setOutgoing();

        // If the card is already blocked
        if (block_card) {
            // Set status word 6983 to indicate "authentication method blocked"
            ISOException.throwIt((short) 0x6983);
            return;
        }

        // Check if the provided PIN length matches the stored PIN length
        if (len != (short) decryptedPin.length) {
            counter++; // Decrease counter for incorrect PIN
            if (counter == 4) {
                block_card = true; // Block the card
                ISOException.throwIt((short) 0x6983); // Send "authentication method blocked" status
            } else {
                apdu.setOutgoingLength((short) 1);
                // Convert the counter value to a byte and send it as the response
                byte[] response = new byte[1];
                response[0] = (byte) counter; // Set the response to the current counter value
                apdu.sendBytesLong(response, (short) 0, (short) 1); // Send failure response
            }
            return;
        }

        // Check the PIN
        if (Util.arrayCompare(buffer, ISO7816.OFFSET_CDATA, decryptedPin, (short) 0, len) == 0) {
            // Correct PIN
            counter = 0; // Reset counter
            apdu.setOutgoingLength((short) 1);
            apdu.sendBytesLong(abc, (short) 1, (short) 1); // Send success response
        } else {
            // Incorrect PIN
            counter++; // Decrease counter
            if (counter == 4) {
                block_card = true; // Block the card
                ISOException.throwIt((short) 0x6983); // Send "authentication method blocked" status
            } else {
                apdu.setOutgoingLength((short) 1);
                // Convert the counter value to a byte and send it as the response
                byte[] response = new byte[1];
                response[0] = (byte) counter; // Set the response to the current counter value
                apdu.sendBytesLong(response, (short) 0, (short) 1); // Send failure response
            }
        }
    }

    private void unblockcard(APDU apdu) {
        counter = 0;
        block_card = false;
    }
    
    private void lockcard(APDU apdu) {
        counter = 4;
        block_card = true;
    }
    
    private void receivePicture(APDU apdu, byte[] buf, short recvLen) {
        dataLen = apdu.getIncomingLength();
        if (dataLen > MAX_SIZE) {
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        }
        short dataOffset = apdu.getOffsetCdata();
        short pointer = 0;
        byte[] rawImage = new byte[dataLen];
        while (recvLen > 0) {
            Util.arrayCopy(buf, dataOffset, rawImage, pointer, recvLen);
            pointer += recvLen;
            recvLen = apdu.receiveBytes(dataOffset);
        }
        byte[] encryptedImage = encryptAes(rawImage);
        // Start a transaction
        JCSystem.beginTransaction();
        try {
            // Update persistent student data
            student.setPicture(encryptedImage);
            student.setLenPicture((short) encryptedImage.length);

            // Commit the transaction
            JCSystem.commitTransaction();
        } catch (Exception e) {
            // Abort the transaction if an exception occurs
            JCSystem.abortTransaction();
            ISOException.throwIt(ISO7816.SW_UNKNOWN); // Throw a specific error if needed
        }
    }
    
    private void sendPicture(APDU apdu) {
        byte[] encryptedImage = student.getPicture();
        byte[] rawImage = decryptAes(encryptedImage);
        short toSend = (short) rawImage.length;
        short maxLenCanSend = apdu.setOutgoing();
        apdu.setOutgoingLength(toSend);
        short sendLen;
        short pointer = 0;
        while (toSend > 0) {
            sendLen = (toSend > maxLenCanSend) ? maxLenCanSend : toSend;
            apdu.sendBytesLong(rawImage, pointer, sendLen);
            toSend -= sendLen;
            pointer += sendLen;
        }
    }
    
    private void getPublicKey(APDU apdu, byte[] buf) {
        short modLength = rsaPubKey.getModulus(buf, (short) 0);
        short expLength = rsaPubKey.getExponent(buf, modLength);
        byte[] modLengthBytes = new byte[2];
        Util.setShort(modLengthBytes, (short) 0, modLength);
        byte[] expLengthBytes = new byte[2];
        Util.setShort(expLengthBytes, (short) 0, expLength);
        buf[(short) (modLength + expLength)] = modLengthBytes[0];
        buf[(short) (modLength + expLength + 1)] = modLengthBytes[1];
        buf[(short) (modLength + expLength + 2)] = expLengthBytes[0];
        buf[(short) (modLength + expLength + 3)] = expLengthBytes[1];
        apdu.setOutgoingAndSend((short) 0, (short) (modLength + expLength + 4));
    }
    
    private void signData(APDU apdu, byte[] buf, short dataLength) {
        byte[] dataToSign = new byte[dataLength];
        Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, dataToSign, (short) 0, dataLength);
        byte[] signedData = signRsa(dataToSign);
        Util.arrayCopy(signedData, (short) 0, buf, (short) 0, (short) signedData.length);
        apdu.setOutgoingAndSend((short) 0, (short) signedData.length);
    }
    
    private byte[] signRsa(byte[] dataToSign) {
        rsaSig.init(rsaPrivKey, Signature.MODE_SIGN);
        byte[] signedBuffer = new byte[(short) (KeyBuilder.LENGTH_RSA_1024 / 8)];
        rsaSig.sign(dataToSign, (short) 0, (short) dataToSign.length, signedBuffer, (short) 0);
        return signedBuffer;
    }
    
    private void updateCardId(APDU apdu, short len) {
        byte[] buffer = apdu.getBuffer();
        // Retrieve the new balance from the APDU buffer
        byte[] rawCardId = new byte[len];
        Util.arrayCopy(buffer, ISO7816.OFFSET_CDATA, rawCardId, (short) 0, (short) len);
        byte[] encryptedCardId = encryptAes(rawCardId);
        JCSystem.beginTransaction();
        try {
            student.setCardId(encryptedCardId);
            student.setLenCardId((short) encryptedCardId.length);
            JCSystem.commitTransaction();
        } catch (Exception e) {
            JCSystem.abortTransaction();
            ISOException.throwIt(ISO7816.SW_UNKNOWN);
        }
        cardCreated = true;
    }
    
    private void getCardId(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte[] decryptedCardId = decryptAes(student.getCardId());
        short cardIdLength = (short) decryptedCardId.length; // Get the actual PIN length
        Util.arrayCopy(decryptedCardId, (short) 0, buffer, (short) 0, cardIdLength);
        apdu.setOutgoingAndSend((short) 0, cardIdLength);
    }
    
    private void checkCardCreated(APDU apdu) {
        if (!cardCreated) {
            ISOException.throwIt((short) 0x6A88); // Card not initialized
        }
        ISOException.throwIt(ISO7816.SW_NO_ERROR); // Status word for success
    }
    
    private void verifyPin(APDU apdu, short len) {
        byte[] buffer = apdu.getBuffer();
        byte[] decryptedPin = decryptAes(student.getPin());
        apdu.setOutgoing();

        // Check if the provided PIN length matches the stored PIN length
        if (len != (short) decryptedPin.length) {
            // Incorrect PIN length
            apdu.setOutgoingLength((short) 1);
            byte[] response = new byte[1];
            response[0] = (byte) 0x01; // 0x01 indicates incorrect PIN
            apdu.sendBytesLong(response, (short) 0, (short) 1); // Send failure response
            return;
        }

        // Verify the PIN
        if (Util.arrayCompare(buffer, ISO7816.OFFSET_CDATA, decryptedPin, (short) 0, len) == 0) {
            // Correct PIN
            apdu.setOutgoingLength((short) 1);
            byte[] response = new byte[1];
            response[0] = (byte) 0x00; // 0x00 indicates correct PIN
            apdu.sendBytesLong(response, (short) 0, (short) 1); // Send success response
        } else {
            // Incorrect PIN
            apdu.setOutgoingLength((short) 1);
            byte[] response = new byte[1];
            response[0] = (byte) 0x01; // 0x01 indicates incorrect PIN
            apdu.sendBytesLong(response, (short) 0, (short) 1); // Send failure response
        }
    }

    private byte[] encryptAes(byte[] dataToEncrypt) {
        short paddingLength = (short) (16 - (dataToEncrypt.length % 16));
        byte[] paddedData = new byte[(short) (dataToEncrypt.length + paddingLength)];
        for (short u = 0; u < (short) dataToEncrypt.length; u++) {
            paddedData[u] = dataToEncrypt[u];
        }
        for (byte i = 0; i < (byte) (paddingLength - 1); i++) {
            paddedData[(short) (dataToEncrypt.length + 1)] = (byte) 0xFF;
        }
        paddedData[(short) (paddedData.length - 1)] = (byte) paddingLength;
        cipher.init(aesKey, Cipher.MODE_ENCRYPT);
        byte[] encryptedData = new byte[(short) paddedData.length];
        cipher.doFinal(paddedData, (short) 0, (short) paddedData.length, encryptedData, (short) 0);
        return encryptedData;
    }

    private byte[] decryptAes(byte[] dataToDecrypt) {
        cipher.init(aesKey, Cipher.MODE_DECRYPT);
        byte[] decryptedData = new byte[(short) dataToDecrypt.length];
        cipher.doFinal(dataToDecrypt, (short) 0, (short) dataToDecrypt.length, decryptedData, (short) 0);
        short paddingLength = (short) decryptedData[(short) (decryptedData.length - 1)];
        byte[] unpaddedData = new byte[(short) (decryptedData.length - paddingLength)];
        for (short u = 0; u < (short) unpaddedData.length; u++) {
            unpaddedData[u] = decryptedData[u];
        }
        return unpaddedData;
    }
    
    private void setAesKeyFromPinCode() {
        JCSystem.beginTransaction();
        try {
            randomData.setSeed(student.getPin(), (short) 0, (short) student.getPin().length);
            byte[] keyData = new byte[aesKeyLen];
            randomData.generateData(keyData, (short) 0, aesKeyLen);
            aesKey.setKey(keyData, (short) 0);
            JCSystem.commitTransaction();
        } catch (Exception e) {
            JCSystem.abortTransaction();
            ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        }
    }
}
