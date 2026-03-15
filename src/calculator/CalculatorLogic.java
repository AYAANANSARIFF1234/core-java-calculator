package calculator;

public class CalculatorLogic {
    
    public double calculate(double num1, double num2, Operation operation) {
        switch (operation) {
            case ADD:
                return num1 + num2;
            case SUBTRACT:
                return num1 - num2;
            case MULTIPLY:
                return num1 * num2;
            case DIVIDE:
                if (num2 == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                return num1 / num2;
            case PERCENTAGE:
                return (num1 * num2) / 100;
            default:
                throw new IllegalArgumentException("Unknown operation");
        }
    }

    public double calculatePercentage(double num) {
        return num / 100;
    }
}
