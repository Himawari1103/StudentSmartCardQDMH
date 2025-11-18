package com.qdmh.studentsmartcardqdmh.client.app.Card;

import com.qdmh.studentsmartcardqdmh.client.mysocketcardsimulatorprovider.SocketCardProvider;
import com.qdmh.studentsmartcardqdmh.client.mysocketcardsimulatorprovider.SocketProviderParameter;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author DELL
 */
public class SmartCard {

    public static final byte[] AID_APPLET = {(byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55,
        (byte) 0x00, (byte) 0x00};
    private Card card;
    private TerminalFactory factory;
    private CardChannel channel;
    private CardTerminal terminal;
    private List<CardTerminal> terminals;
    private ResponseAPDU response;
    public static boolean isCardBlocked = false;
    public static byte counter = 0;
    public static byte[] publicKey;
    private static SmartCard instance;
    public static boolean unknownIssue = false;
    public boolean isConnected = false;

    public SmartCard() {
    }

    public static SmartCard getInstance() {
        if (instance == null) {
            instance = new SmartCard();
        }
        return instance;
    }

    public boolean connectCard() throws NoSuchAlgorithmException {
        try {
            if (isConnected) {
                return true;
            }
            if (Security.getProvider("SocketCardSim") == null) {
                SocketCardProvider provider = new SocketCardProvider();
                Security.addProvider(provider);
            }
            factory = TerminalFactory.getInstance("SocketCardSim", new SocketProviderParameter("localhost", 9025));
            terminals = factory.terminals().list();
            terminal = terminals.get(0);
            card = terminal.connect("T=1");
            channel = card.getBasicChannel();
            if (channel == null) {
                return false;
            }

            response = channel.transmit(new CommandAPDU(0x00, (byte) 0xA4, 0x04, 0x00, AID_APPLET));
            String check = Integer.toHexString(response.getSW());
            if (check.equals("9000")) {
                isConnected = true;
                return true;
            } else if (check.equals("6400")) {
                isConnected = true;
                JOptionPane.showMessageDialog(null, "Thẻ đã bị vô hiệu hóa");
                return true;
            } else {
                return false;
            }
        } catch (HeadlessException | CardException ex) {
            System.out.println(ex);
            return false;
        }
    }

    public boolean disconnect() {
        try {
            card.disconnect(false);
            isConnected = false;
            return true;
        } catch (CardException e) {
            System.out.println("Loi: " + e);
        }
        return false;
    }

    // Method to send a command APDU to the applet with debug prints
    public ResponseAPDU sendCommandAPDU(byte[] command) {
        try {
            // Print the size of the data being sent
            System.out.println("Sending APDU command. Data size: " + command.length + " bytes");

            // Print the content of the command in hex format
            System.out.println("APDU Command: " + bytesToHex(command));

            CommandAPDU commandAPDU = new CommandAPDU(command);
            response = channel.transmit(commandAPDU);

            // Print the status word (SW) of the response
            System.out.println("Response SW: " + Integer.toHexString(response.getSW()));
            return response;
        } catch (CardException e) {
            System.out.println("Error sending APDU: " + e);
            return null;
        }
    }

    // Utility method to convert a byte array to a hexadecimal string
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    public byte[] getPatientPublicKey() {
        byte[] command = {(byte) 0x00, (byte) 0x24, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ResponseAPDU response = sendCommandAPDU(command);

        if (response != null && response.getSW() == 0x9000) {
            byte[] data = response.getData();
            System.out.println("Raw response data (bytes): " + Arrays.toString(data));
            System.out.println("Raw response data length (bytes): " + data.length);
            return data;

        } else {
            System.out.println("Failed to get patient public key, SW: " + Integer.toHexString(response.getSW()));
            return null;
        }
    }

    // Method to get patient info from the applet
    public String[] getPatientInfo() {
        byte[] command; // Example command, adjust as needed
//        command = new byte[]{(byte) 0x00, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        command = new byte[]{(byte) 0x00, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ResponseAPDU response = sendCommandAPDU(command);
        if (response != null && response.getSW() == 0x9000) {
            byte[] data = response.getData();
            return HelpMethod.convertByteToStringArr(data, '.');
        } else {
            System.out.println("Failed to get patient info, SW: " + Integer.toHexString(response.getSW()));
            return null;
        }
    }

    // Method to get patient info from the applet
    public String[] getPatientCardId() {
        byte[] command; // Example command, adjust as needed
        command = new byte[]{(byte) 0x00, (byte) 0x27, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ResponseAPDU response = sendCommandAPDU(command);
        if (response != null && response.getSW() == 0x9000) {
            byte[] data = response.getData();
            return HelpMethod.convertByteToStringArr(data, '.');
        } else {
            System.out.println("Failed to get patient info, SW: " + Integer.toHexString(response.getSW()));
            return null;
        }
    }

    // Method to get patient info from the applet
    public String[] getPatientPin() {
        byte[] command; // Example command, adjust as needed
        command = new byte[]{(byte) 0x00, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ResponseAPDU response = sendCommandAPDU(command);
        if (response != null && response.getSW() == 0x9000) {
            byte[] data = response.getData();
            return HelpMethod.convertByteToStringArr(data, '.');
        } else {
            System.out.println("Failed to get patient info, SW: " + Integer.toHexString(response.getSW()));
            return null;
        }
    }

    // Method to get patient Balance from the applet
    public String[] getPatientBalance() {
        byte[] command; // Example command, adjust as needed
        command = new byte[]{(byte) 0x00, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ResponseAPDU response = sendCommandAPDU(command);
        if (response != null && response.getSW() == 0x9000) {
            byte[] data = response.getData();
            return HelpMethod.convertByteToStringArr(data, '.');
        } else {
            System.out.println("Failed to get patient balance, SW: " + Integer.toHexString(response.getSW()));
            return null;
        }
    }

    // Method to get patient picture from the applet
    public BufferedImage GetPatientPicture() {
        byte[] command;
        command = new byte[]{(byte) 0x00, (byte) 0x23, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

        // Send the APDU command to get the picture data
        ResponseAPDU response = sendCommandAPDU(command);

        // Check response status
        if (response != null && response.getSW() == 0x9000) {
            byte[] imageData = response.getData();

            // Debug: Print the number of bytes received
            System.out.println("Received " + imageData.length + " bytes.");

            // Debug: Print the content of the received bytes
            System.out.print("Data content (hex): ");
            for (byte b : imageData) {
                System.out.printf("%02X ", b);
            }
            System.out.println();

            // Convert the byte array into an image
            return HelpMethod.convertByteArrayToImage(imageData);
        } else {
            // Print error message if operation failed
            System.out.println("Failed to get patient picture, SW: " + Integer.toHexString(response.getSW()));
            return null;
        }
    }

    public boolean updatePatientInfo(String hoTen, String ngaySinh, String queQuan, String gioiTinh, String sdt,
            String maBenhNhan) {
        try {
            // Build the patient info string with delimiters in the reverse order (to match
            // init_bn)
            String dataBuilder = hoTen + "."
                    + // hoTen
                    ngaySinh + "."
                    + // ngaySinh
                    queQuan + "."
                    + // queQuan
                    gioiTinh + "."
                    + // gioiTinh
                    sdt + "."
                    + // maBenhNhan
                    maBenhNhan; // sdt (last field)

            // Convert the string to a byte array
            byte[] dataBytes = HelpMethod.ConvertStringToByteArr(dataBuilder);

            // Create the APDU command for Extended Length format
            byte[] command = new byte[7 + dataBytes.length]; // Header (7 bytes: CLA, INS, P1, P2, 3-byte Lc) + data
            command[0] = (byte) 0x00; // CLA
            command[1] = (byte) 0x20; // INS for UPDATE_BN
            command[2] = (byte) 0x00; // P1
            command[3] = (byte) 0x00; // P2
            command[4] = (byte) 0x00; // Extended Length indicator (Lc MSB)
            command[5] = (byte) ((dataBytes.length >> 8) & 0xFF); // Lc (high byte)
            command[6] = (byte) (dataBytes.length & 0xFF); // Lc (low byte)
            System.arraycopy(dataBytes, 0, command, 7, dataBytes.length); // Append data

            // Send the command APDU to the applet
            ResponseAPDU response = sendCommandAPDU(command);

            // Check the response status word (SW)
            if (response != null && response.getSW() == 0x9000) {
                System.out.println("Student info updated successfully.");
                return true;
            } else {
                System.out.println("Failed to update patient info, SW: " + Integer.toHexString(response.getSW()));
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error updating patient info: " + e);
            return false;
        }
    }

    public boolean updatePatientPin(String newPin) {
        try {
            // Convert the new PIN string to a byte array
            byte[] pinBytes = HelpMethod.ConvertStringToByteArr(newPin);

            // Create the APDU command for updating the PIN
            byte[] command = new byte[5 + pinBytes.length]; // Header (5 bytes) + PIN data
            command[0] = (byte) 0x00; // CLA
            command[1] = (byte) 0x21; // INS for UPDATE_PIN
            command[2] = (byte) 0x00; // P1
            command[3] = (byte) 0x00; // P2
            command[4] = (byte) pinBytes.length; // Lc: length of the new PIN
            System.arraycopy(pinBytes, 0, command, 5, pinBytes.length); // Append PIN data

            // Send the command APDU to the applet
            ResponseAPDU response = sendCommandAPDU(command);

            // Check the response status word (SW)
            if (response != null && response.getSW() == 0x9000) {
                System.out.println("Student PIN updated successfully.");
                return true;
            } else {
                System.out.println("Failed to update PIN, SW: " + Integer.toHexString(response.getSW()));
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error updating PIN: " + e);
            return false;
        }
    }

    public boolean updateCardId(String newCardId) {
        try {
            // Convert the new PIN string to a byte array
            byte[] cardBytes = HelpMethod.ConvertStringToByteArr(newCardId);

            // Create the APDU command for updating the PIN
            byte[] command = new byte[5 + cardBytes.length]; // Header (5 bytes) + PIN data
            command[0] = (byte) 0x00; // CLA
            command[1] = (byte) 0x26; // INS for UPDATE_PIN
            command[2] = (byte) 0x00; // P1
            command[3] = (byte) 0x00; // P2
            command[4] = (byte) cardBytes.length; // Lc: length of the new PIN
            System.arraycopy(cardBytes, 0, command, 5, cardBytes.length); // Append PIN data

            // Send the command APDU to the applet
            ResponseAPDU response = sendCommandAPDU(command);

            // Check the response status word (SW)
            if (response != null && response.getSW() == 0x9000) {
                System.out.println("Student CardId updated successfully.");
                return true;
            } else {
                System.out.println("Failed to update CardId, SW: " + Integer.toHexString(response.getSW()));
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error updating PIN: " + e);
            return false;
        }
    }

    public boolean updatePatientBalance(String Balance) {
        try {
            // Convert the new PIN string to a byte array
            byte[] balanceBytes = HelpMethod.ConvertStringToByteArr(Balance);

            // Create the APDU command for updating the PIN
            byte[] command = new byte[5 + balanceBytes.length]; // Header (5 bytes) + PIN data
            command[0] = (byte) 0x00; // CLA
            command[1] = (byte) 0x16; // INS for UPDATE_PIN
            command[2] = (byte) 0x00; // P1
            command[3] = (byte) 0x00; // P2
            command[4] = (byte) balanceBytes.length; // Lc: length of the new PIN
            System.arraycopy(balanceBytes, 0, command, 5, balanceBytes.length); // Append PIN data

            // Send the command APDU to the applet
            ResponseAPDU response = sendCommandAPDU(command);

            // Check the response status word (SW)
            if (response != null && response.getSW() == 0x9000) {
                System.out.println("Student Balance updated successfully.");
                return true;
            } else {
                System.out.println("Failed to update Balance, SW: " + Integer.toHexString(response.getSW()));
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error updating Balance: " + e);
            return false;
        }
    }

    // Example function calling the methods from HelpMethod
    public boolean updatePatientPicture(BufferedImage image) {
        try {
            byte[] pictureBytes = HelpMethod.convertImageToByteArray(image);
            if (pictureBytes == null) {
                System.out.println("Failed to convert image to byte array.");
                return false;
            }

            byte[] command = new byte[7 + pictureBytes.length];
            command[0] = (byte) 0x00; // CLA
            command[1] = (byte) 0x22; // INS for UPDATE_PICTURE
            command[2] = (byte) 0x00; // P1
            command[3] = (byte) 0x00; // P2
            command[4] = (byte) 0x00; // Extended length indicator
            command[5] = (byte) (pictureBytes.length >> 8); // Lc high byte
            command[6] = (byte) (pictureBytes.length & 0xFF); // Lc low byte
            System.arraycopy(pictureBytes, 0, command, 7, pictureBytes.length);

            System.out.print("Command APDU (hex): ");
            for (byte b : command) {
                System.out.printf("%02X ", b);
            }
            System.out.println();

            ResponseAPDU response = sendCommandAPDU(command);

            if (response == null || response.getSW() != 0x9000) {
                System.out.println("Failed to update picture, SW: "
                        + (response != null ? Integer.toHexString(response.getSW()) : "null"));
                return false;
            }

            System.out.println("Student picture updated successfully.");
            return true;
        } catch (Exception e) {
            System.out.println("Error updating picture: " + e);
            return false;
        }
    }

    public boolean CheckPin(String userPin) {
        try {
            byte[] pinBytes = HelpMethod.ConvertStringToByteArr(userPin);

            byte[] command = new byte[5 + pinBytes.length];
            command[0] = (byte) 0x00;
            command[1] = (byte) 0x19;
            command[2] = (byte) 0x00;
            command[3] = (byte) 0x00;
            command[4] = (byte) pinBytes.length;
            System.arraycopy(pinBytes, 0, command, 5, pinBytes.length);

            ResponseAPDU response = sendCommandAPDU(command);

            if (response != null) {
                byte[] responseBytes = response.getBytes();
                int sw = response.getSW();

                if (responseBytes.length >= 3 && responseBytes[0] == (byte) 0x00 && sw == 0x9000) {
                    unknownIssue = false;
                    isCardBlocked = false;
                    System.out.println("PIN verified successfully.");
                    return true;
                } else if (responseBytes.length >= 3 && responseBytes[0] != (byte) 0x00 && sw == 0x9000) {
                    unknownIssue = false;
                    counter = responseBytes[0];
                    System.out.println("Incorrect PIN entered.");
                    return false;
                } else if (sw == 0x6983) {
                    unknownIssue = false;
                    isCardBlocked = true;
                    System.out.println("Card is blocked.");
                    return false;
                } else {
                    unknownIssue = true;
                    System.out.println("Unexpected response. SW: " + Integer.toHexString(sw));
                    return false;
                }
            } else {
                System.out.println("No response from the card.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error verifying PIN: " + e);
            return false;
        }
    }

    public boolean VerifyPin(String userPin) {
        try {
            byte[] pinBytes = HelpMethod.ConvertStringToByteArr(userPin);
            // Construct APDU command
            byte[] command = new byte[5 + pinBytes.length];
            command[0] = (byte) 0x00; // CLA
            command[1] = (byte) 0x30; // INS
            command[2] = (byte) 0x00; // P1
            command[3] = (byte) 0x00; // P2
            command[4] = (byte) pinBytes.length; // Lc (length of data)
            System.arraycopy(pinBytes, 0, command, 5, pinBytes.length); // Add PIN data
            // Send the command and receive the response
            ResponseAPDU response = sendCommandAPDU(command);
            if (response != null) {
                byte[] responseBytes = response.getBytes();
                int sw = response.getSW();

                if (responseBytes.length >= 1 && responseBytes[0] == (byte) 0x00 && sw == 0x9000) {
                    // Correct PIN
                    System.out.println("PIN verified successfully.");
                    return true;
                } else if (responseBytes.length >= 1 && responseBytes[0] == (byte) 0x01 && sw == 0x9000) {
                    // Incorrect PIN
                    System.out.println("Incorrect PIN entered.");
                    return false;
                } else {
                    // Unexpected response
                    System.out.println("Unexpected response. SW: " + Integer.toHexString(sw));
                    return false;
                }
            } else {
                System.out.println("No response from the card.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error verifying PIN: " + e);
            return false;
        }
    }

    public boolean CheckCardCreated() {
        byte[] command; // Example command, adjust as needed
        command = new byte[]{(byte) 0x00, (byte) 0x29, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ResponseAPDU response = sendCommandAPDU(command);
        if (response != null && response.getSW() == 0x9000) {
            System.out.println("The card have been initialized, SW: " + Integer.toHexString(response.getSW()));
            return true;
        } else if (response != null && response.getSW() == 0x6A88) {
            System.out.println("The card haven't been initialized, SW: " + Integer.toHexString(response.getSW()));
            return false;
        } else {
            System.out.println("Failed to called check card created.");
            return false;
        }
    }

    public boolean ClearCard() {
        byte[] command; // Example command, adjust as needed
        command = new byte[]{(byte) 0x00, (byte) 0x18, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ResponseAPDU response = sendCommandAPDU(command);
        if (response != null && response.getSW() == 0x9000) {
            System.out.println("Succeed to called clear card, SW: " + Integer.toHexString(response.getSW()));
            return true;
        } else {
            System.out.println("Failed to called clear card, SW: " + Integer.toHexString(response.getSW()));
            return false;
        }
    }

    public boolean UnLockCard() {
        byte[] command; // Example command, adjust as needed
        command = new byte[]{(byte) 0x00, (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ResponseAPDU response = sendCommandAPDU(command);
        if (response != null && response.getSW() == 0x9000) {
            System.out.println("Succeed to called unblock card, SW: " + Integer.toHexString(response.getSW()));
            return true;
        } else {
            System.out.println("Failed to called unblock card, SW: " + Integer.toHexString(response.getSW()));
            return false;
        }
    }

    public boolean LockCard() {
        byte[] command; // Example command, adjust as needed
        command = new byte[]{(byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ResponseAPDU response = sendCommandAPDU(command);
        if (response != null && response.getSW() == 0x9000) {
            System.out.println("Succeed to called block card, SW: " + Integer.toHexString(response.getSW()));
            return true;
        } else {
            System.out.println("Failed to called block card, SW: " + Integer.toHexString(response.getSW()));
            return false;
        }
    }

    public boolean VerifyCard(byte[] publicKey) {
        try {
            String randomData = HelpMethod.generateRandomString(20);
            byte[] dataToVerify = HelpMethod.ConvertStringToByteArr(randomData);
            byte[] command = new byte[5 + dataToVerify.length];
            command[0] = (byte) 0x00;
            command[1] = (byte) 0x25;
            command[2] = (byte) 0x00;
            command[3] = (byte) 0x00;
            command[4] = (byte) dataToVerify.length;
            System.arraycopy(dataToVerify, 0, command, 5, dataToVerify.length);
            System.out.println("PublicKey in Verify: " + Arrays.toString(publicKey));
            System.out.println("PublicKey Length: " + publicKey.length);
            ResponseAPDU res = sendCommandAPDU(command);
            if (res != null && res.getSW() == 0x9000) {
                byte[] signedData = res.getData();
                return HelpMethod.verifySignature(publicKey, dataToVerify, signedData);
            } else {
                System.out.println("Failed to verified card, SW: " + Integer.toHexString(res.getSW()));
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
