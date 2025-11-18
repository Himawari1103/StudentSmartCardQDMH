package com.qdmh.studentsmartcardqdmh.client.app.util;

import javax.swing.*;

public class Utils {
    public static String generateAppointmentCode(int id) {
        // Tiền tố là "DK", định dạng ID thành chuỗi 3 chữ số (zero-padded)
        return String.format("DK%03d", id);
    }

    public static <T> T getValueByRow(JTable table, int row, int column, Class<T> type) {
        Object value = table.getValueAt(row, column);
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
}
