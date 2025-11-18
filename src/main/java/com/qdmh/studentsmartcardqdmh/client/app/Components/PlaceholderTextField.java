package com.qdmh.studentsmartcardqdmh.client.app.Components;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {
    private final String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY); // Placeholder text color
            g2.drawString(placeholder, 5, getHeight() / 2 + g.getFontMetrics().getAscent() / 2 - 2);
            g2.dispose();
        }
    }
}

