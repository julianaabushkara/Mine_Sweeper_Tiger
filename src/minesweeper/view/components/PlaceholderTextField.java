package minesweeper.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PlaceholderTextField extends JTextField {

    private String placeholder;
    private Color placeholderColor = new Color(130, 130, 130);

    public PlaceholderTextField(String placeholder) {
        super(15);
        this.placeholder = placeholder;
        setText(placeholder);
        setForeground(placeholderColor);
        setFont(new Font("Segoe UI", Font.PLAIN, 16));
        setCaretColor(Color.WHITE);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(placeholderColor);
                }
            }
        });
    }
}
