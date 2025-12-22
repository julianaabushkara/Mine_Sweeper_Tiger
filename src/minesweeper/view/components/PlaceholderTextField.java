/*package minesweeper.view.components;

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
}*/

package minesweeper.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PlaceholderTextField extends JTextField {

    private final String placeholder;
    private final Color placeholderColor = new Color(130, 130, 130);
    private boolean showingPlaceholder = true;

    public PlaceholderTextField(String placeholder, int maxLength) {
        super();
        this.placeholder = placeholder;

        setFont(new Font("Segoe UI", Font.PLAIN, 16));
        setCaretColor(Color.WHITE);

        setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null) return;
                if (getLength() + str.length() <= maxLength) {
                    super.insertString(offs, str, a);
                }
            }
        });

        showPlaceholder();

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (showingPlaceholder) {
                    setText("");
                    setForeground(Color.WHITE);
                    showingPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().trim().isEmpty()) {
                    showPlaceholder();
                }
            }
        });
    }

    private void showPlaceholder() {
        setText(placeholder);
        setForeground(placeholderColor);
        showingPlaceholder = true;
    }

    public boolean isPlaceholderVisible() {
        return showingPlaceholder;
    }
}


