package calculator;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class Calculator extends JFrame {
    private JTextField display;
    private CalculatorLogic logic;
    private double firstNumber = 0;
    private Operation currentOperation = null;
    private boolean startNewNumber = true;
    private boolean operationPerformed = false;
    
    // Glassmorphism colors
    private static final Color DARK_BG = new Color(32, 32, 38);
    private static final Color BUTTON_NUMBER = new Color(85, 85, 85);
    private static final Color BUTTON_OPERATOR = new Color(255, 159, 10);
    private static final Color BUTTON_SPECIAL = new Color(165, 165, 165);
    
    public Calculator() {
        logic = new CalculatorLogic();
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        setupGlassmorphismUI();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupGlassmorphismUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(45, 45, 52),
                    0, getHeight(), new Color(25, 25, 30)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        display = createDisplay();
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(display, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JTextField createDisplay() {
        JTextField field = new JTextField("0");
        field.setEditable(false);
        field.setFont(new Font("Arial", Font.BOLD, 48));
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setPreferredSize(new Dimension(300, 80));
        field.setBackground(new Color(32, 32, 38));
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return field;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 4, 12, 12));
        panel.setBackground(new Color(32, 32, 38));
        
        String[][] buttons = {
            {"AC", "C", "±", "%"},
            {"7", "8", "9", "/"},
            {"4", "5", "6", "×"},
            {"1", "2", "3", "-"},
            {"0", ".", "=", "+"}
        };
        
        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                String text = buttons[row][col];
                JButton btn = createStyledButton(text);
                panel.add(btn);
            }
        }
        
        return panel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bg = getBackgroundColor(text);
                Color fg = Color.WHITE;
                
                if (getModel().isRollover()) bg = bg.brighter();
                if (getModel().isPressed()) bg = bg.darker();
                
                int arc = 45;
                g2d.setColor(bg);
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, arc, arc);
                
                g2d.setColor(fg);
                g2d.setFont(new Font("Arial", Font.BOLD, 26));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(text, x, y);
            }
        };
        
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(70, 70));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.repaint(); }
            public void mouseExited(MouseEvent e) { button.repaint(); }
        });
        
        button.addActionListener(e -> handleButtonClick(text));
        
        return button;
    }
    
    private Color getBackgroundColor(String text) {
        if (text.matches("[0-9]|\\.")) return BUTTON_NUMBER;
        if (text.equals("=")) return BUTTON_OPERATOR;
        if (text.matches("AC|C|±|%")) return BUTTON_SPECIAL;
        return BUTTON_OPERATOR;
    }
    
    private void handleButtonClick(String text) {
        switch (text) {
            case "AC": clearAll(); break;
            case "C": clear(); break;
            case "±": toggleSign(); break;
            case "%": setOperation("%"); break;
            case "/": setOperation("/"); break;
            case "×": setOperation("*"); break;
            case "-": setOperation("-"); break;
            case "+": setOperation("+"); break;
            case "=": calculateResult(); break;
            case ".": addDecimal(); break;
            default: addNumber(text);
        }
    }
    
    private void addNumber(String number) {
        if (startNewNumber || operationPerformed) {
            display.setText(number);
            startNewNumber = false;
            operationPerformed = false;
        } else {
            String current = display.getText();
            if (current.equals("0") && !number.equals("0")) {
                display.setText(number);
            } else if (!current.equals("0") || !number.equals("0")) {
                display.setText(current + number);
            }
        }
    }
    
    private void addDecimal() {
        if (startNewNumber) {
            display.setText("0.");
            startNewNumber = false;
            return;
        }
        String current = display.getText();
        if (!current.contains(".")) {
            display.setText(current + ".");
        }
    }
    
    private void setOperation(String op) {
        try {
            firstNumber = Double.parseDouble(display.getText());
            switch (op) {
                case "+": currentOperation = Operation.ADD; break;
                case "-": currentOperation = Operation.SUBTRACT; break;
                case "*": currentOperation = Operation.MULTIPLY; break;
                case "/": currentOperation = Operation.DIVIDE; break;
                case "%": 
                    double result = logic.calculatePercentage(firstNumber);
                    display.setText(formatResult(result));
                    operationPerformed = true;
                    startNewNumber = true;
                    return;
            }
            startNewNumber = true;
        } catch (NumberFormatException e) {
            display.setText("Error");
            startNewNumber = true;
        }
    }
    
    private void toggleSign() {
        try {
            double value = Double.parseDouble(display.getText());
            value = -value;
            display.setText(formatResult(value));
        } catch (NumberFormatException e) {
            display.setText("Error");
        }
    }
    
    private void calculateResult() {
        if (currentOperation == null) return;
        try {
            double secondNumber = Double.parseDouble(display.getText());
            double result = logic.calculate(firstNumber, secondNumber, currentOperation);
            display.setText(formatResult(result));
            currentOperation = null;
            operationPerformed = true;
            startNewNumber = true;
        } catch (ArithmeticException e) {
            display.setText("Error");
            startNewNumber = true;
            currentOperation = null;
        } catch (NumberFormatException e) {
            display.setText("Error");
            startNewNumber = true;
            currentOperation = null;
        }
    }
    
    private String formatResult(double result) {
        if (result == (long) result) {
            return String.format("%d", (long) result);
        }
        return String.format("%s", result);
    }
    
    private void clear() {
        display.setText("0");
        startNewNumber = true;
    }
    
    private void clearAll() {
        display.setText("0");
        firstNumber = 0;
        currentOperation = null;
        startNewNumber = true;
        operationPerformed = false;
    }
}
