package calculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Calculator extends JFrame {
    private JTextField display;
    private CalculatorLogic logic;
    private double memory = 0;
    private boolean startNewNumber = true;
    private String expression = "";
    private Deque<String> memoryStack = new ArrayDeque<>();
    
    private static final Color DARK_BG = new Color(30, 30, 35);
    private static final Color BUTTON_NUMBER = new Color(75, 75, 80);
    private static final Color BUTTON_OPERATOR = new Color(255, 149, 0);
    private static final Color BUTTON_SPECIAL = new Color(100, 100, 105);
    private static final Color BUTTON_SCIENTIFIC = new Color(50, 50, 55);
    
    public Calculator() {
        logic = new CalculatorLogic();
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        setupUI();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(40, 40, 45), 0, getHeight(), new Color(25, 25, 30));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        display = createDisplay();
        
        JPanel buttonContainer = new JPanel(new BorderLayout(5, 5));
        buttonContainer.setBackground(DARK_BG);
        
        JPanel scientificPanel = createScientificPanel();
        JPanel standardPanel = createStandardPanel();
        
        buttonContainer.add(scientificPanel, BorderLayout.WEST);
        buttonContainer.add(standardPanel, BorderLayout.CENTER);
        
        mainPanel.add(display, BorderLayout.NORTH);
        mainPanel.add(buttonContainer, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JTextField createDisplay() {
        JTextField field = new JTextField("0");
        field.setEditable(false);
        field.setFont(new Font("Arial", Font.BOLD, 32));
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setPreferredSize(new Dimension(450, 60));
        field.setBackground(DARK_BG);
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return field;
    }
    
    private JPanel createScientificPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 4, 4, 4));
        panel.setBackground(DARK_BG);
        panel.setPreferredSize(new Dimension(240, 320));
        
        String[] sciButtons = {
            "sin", "cos", "tan", "log",
            "asin", "acos", "atan", "ln",
            "sinh", "cosh", "tanh", "√",
            "x²", "x³", "xʸ", "n!",
            "π", "e", "10ˣ", "eˣ",
            "(", ")", "¹/x", "|x|"
        };
        
        for (String btn : sciButtons) {
            panel.add(createButton(btn, BUTTON_SCIENTIFIC, 14));
        }
        return panel;
    }
    
    private JPanel createStandardPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 4, 4, 4));
        panel.setBackground(DARK_BG);
        
        String[][] buttons = {
            {"MC", "MR", "M+", "M-"},
            {"AC", "C", "±", "÷"},
            {"7", "8", "9", "×"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"},
            {"%", "0", ".", "="}
        };
        
        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons[r].length; c++) {
                String text = buttons[r][c];
                Color bg = getButtonColor(text);
                panel.add(createButton(text, bg, 18));
            }
        }
        return panel;
    }
    
    private JButton createButton(String text, Color bgColor, int fontSize) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bg = bgColor;
                if (getModel().isRollover()) bg = bg.brighter();
                if (getModel().isPressed()) bg = bg.darker();
                
                g2d.setColor(bg);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
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
        button.setPreferredSize(new Dimension(50, 45));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.repaint(); }
            public void mouseExited(MouseEvent e) { button.repaint(); }
        });
        
        button.addActionListener(e -> handleButtonClick(text));
        return button;
    }
    
    private Color getButtonColor(String text) {
        if (text.matches("[0-9]|\\.")) return BUTTON_NUMBER;
        if (text.equals("=")) return BUTTON_OPERATOR;
        if (text.matches("AC|C|±")) return BUTTON_SPECIAL;
        if (text.matches("MC|MR|M\\+|M-")) return new Color(85, 85, 90);
        return BUTTON_OPERATOR;
    }
    
    private void handleButtonClick(String text) {
        switch (text) {
            case "AC": clearAll(); break;
            case "C": clear(); break;
            case "±": toggleSign(); break;
            case "MC": memory = 0; break;
            case "MR": display.setText(formatResult(memory)); startNewNumber = true; break;
            case "M+": memory += getCurrentValue(); break;
            case "M-": memory -= getCurrentValue(); break;
            case "÷": case "×": case "-": case "+": case "%": setOperation(text); break;
            case "=": calculateResult(); break;
            case ".": addDecimal(); break;
            case "sin": applyFunction("sin"); break;
            case "cos": applyFunction("cos"); break;
            case "tan": applyFunction("tan"); break;
            case "asin": applyFunction("asin"); break;
            case "acos": applyFunction("acos"); break;
            case "atan": applyFunction("atan"); break;
            case "sinh": applyFunction("sinh"); break;
            case "cosh": applyFunction("cosh"); break;
            case "tanh": applyFunction("tanh"); break;
            case "log": applyFunction("log"); break;
            case "ln": applyFunction("ln"); break;
            case "√": applyFunction("sqrt"); break;
            case "x²": applyFunction("square"); break;
            case "x³": applyFunction("cube"); break;
            case "xʸ": setOperation("^"); break;
            case "n!": applyFunction("factorial"); break;
            case "π": display.setText(String.valueOf(Math.PI)); startNewNumber = true; break;
            case "e": display.setText(String.valueOf(Math.E)); startNewNumber = true; break;
            case "10ˣ": applyFunction("10power"); break;
            case "eˣ": applyFunction("epower"); break;
            case "(": expression += "("; display.setText(display.getText() + "("); break;
            case ")": expression += ")"; display.setText(display.getText() + ")"); break;
            case "¹/x": applyFunction("reciprocal"); break;
            case "|x|": applyFunction("abs"); break;
            default: addNumber(text);
        }
    }
    
    private void addNumber(String number) {
        if (startNewNumber) {
            display.setText(number);
            startNewNumber = false;
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
        if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }
    
    private void setOperation(String op) {
        if (!display.getText().equals("0")) {
            expression += display.getText();
        }
        switch (op) {
            case "+": expression += "+"; break;
            case "-": expression += "-"; break;
            case "×": expression += "*"; break;
            case "÷": expression += "/"; break;
            case "%": expression += "/100"; break;
            case "^": expression += "^"; break;
        }
        display.setText(display.getText() + op);
        startNewNumber = true;
    }
    
    private void applyFunction(String func) {
        try {
            double val = getCurrentValue();
            double result;
            switch (func) {
                case "sin": result = logic.sin(val); break;
                case "cos": result = logic.cos(val); break;
                case "tan": result = logic.tan(val); break;
                case "asin": result = logic.asin(val); break;
                case "acos": result = logic.acos(val); break;
                case "atan": result = logic.atan(val); break;
                case "sinh": result = logic.sinh(val); break;
                case "cosh": result = logic.cosh(val); break;
                case "tanh": result = logic.tanh(val); break;
                case "log": result = logic.log(val); break;
                case "ln": result = logic.ln(val); break;
                case "sqrt": result = logic.sqrt(val); break;
                case "square": result = logic.square(val); break;
                case "cube": result = logic.cube(val); break;
                case "factorial": result = logic.factorial((int)val); break;
                case "10power": result = logic.power2(val); break;
                case "epower": result = logic.ePower(val); break;
                case "reciprocal": result = logic.reciprocal(val); break;
                case "abs": result = logic.absVal(val); break;
                default: return;
            }
            display.setText(formatResult(result));
            startNewNumber = true;
        } catch (Exception e) {
            display.setText("Error");
            startNewNumber = true;
        }
    }
    
    private void calculateResult() {
        try {
            expression += display.getText();
            String expr = expression.replace("×", "*").replace("÷", "/").replace("^", "**");
            expr = expr.replace("**", "Math.pow(").replace("sin", "Math.sin(Math.toRadians(")
                .replace("cos", "Math.cos(Math.toRadians(").replace("tan", "Math.tan(Math.toRadians(");
            
            double result = evaluateExpression(expr);
            display.setText(formatResult(result));
            expression = "";
            startNewNumber = true;
        } catch (Exception e) {
            display.setText("Error");
            expression = "";
            startNewNumber = true;
        }
    }
    
    private double evaluateExpression(String expr) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < expr.length()) ? expr.charAt(pos) : -1; }
            double parse() { nextChar(); double x = parseExpression(); if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char)ch); return x; }
            double parseExpression() { double x = parseTerm(); while (ch == '+' || ch == '-') { nextChar(); if (ch == '+') x += parseTerm(); else x -= parseTerm(); } return x; }
            double parseTerm() { double x = parsePower(); while (ch == '*' || ch == '/') { nextChar(); if (ch == '*') x *= parsePower(); else x /= parsePower(); } return x; }
            double parsePower() { double x = parseFactor(); if (ch == '^' || (pos+2 < expr.length() && expr.substring(pos, pos+2).equals("**"))) { nextChar(); if (ch == '^') nextChar(); x = Math.pow(x, parseFactor()); } return x; }
            double parseFactor() { double x; int startPos = pos; if (ch == '(') { nextChar(); x = parseExpression(); if (ch == ')') nextChar(); } else if (ch >= '0' && ch <= '9' || ch == '.') { while (ch >= '0' && ch <= '9' || ch == '.') nextChar(); x = Double.parseDouble(expr.substring(startPos, pos)); } else if (ch == 'M' && pos+3 < expr.length() && expr.substring(pos, pos+4).equals("Math")) { 
                int end = expr.indexOf('(', pos); int close = expr.indexOf(')', end); 
                if (expr.substring(end+1, close).contains("toRadians")) x = Math.sin(Math.toRadians(parseNumberInRange(expr, end+11, close-1))); 
                else x = Double.parseDouble(expr.substring(pos, close+1)); pos = close; nextChar(); 
            } else { throw new RuntimeException("Unexpected: " + (char)ch); } return x; }
            double parseNumberInRange(String s, int start, int end) { return Double.parseDouble(s.substring(start, end)); }
        }.parse();
    }
    
    private double getCurrentValue() {
        try {
            return Double.parseDouble(display.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private void toggleSign() {
        try {
            double val = Double.parseDouble(display.getText());
            display.setText(formatResult(-val));
        } catch (NumberFormatException e) {
            display.setText("Error");
        }
    }
    
    private void clear() {
        display.setText("0");
        startNewNumber = true;
    }
    
    private void clearAll() {
        display.setText("0");
        expression = "";
        startNewNumber = true;
    }
    
    private String formatResult(double result) {
        if (result == (long) result && Math.abs(result) < 1e15) {
            return String.format("%d", (long) result);
        }
        return String.format("%.10g", result).replaceFirst("\\.?0+$", "");
    }
}
