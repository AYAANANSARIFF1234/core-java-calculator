package calculator;

public class CalculatorLogic {
    
    public double calculate(double num1, double num2, Operation operation) {
        switch (operation) {
            case ADD: return num1 + num2;
            case SUBTRACT: return num1 - num2;
            case MULTIPLY: return num1 * num2;
            case DIVIDE:
                if (num2 == 0) throw new ArithmeticException("Cannot divide by zero");
                return num1 / num2;
            case PERCENTAGE: return (num1 * num2) / 100;
            case POWER: return Math.pow(num1, num2);
            default: throw new IllegalArgumentException("Unknown operation");
        }
    }

    public double calculatePercentage(double num) {
        return num / 100;
    }

    public double sin(double num) { return Math.sin(Math.toRadians(num)); }
    public double cos(double num) { return Math.cos(Math.toRadians(num)); }
    public double tan(double num) { return Math.tan(Math.toRadians(num)); }
    public double asin(double num) { return Math.toDegrees(Math.asin(num)); }
    public double acos(double num) { return Math.toDegrees(Math.acos(num)); }
    public double atan(double num) { return Math.toDegrees(Math.atan(num)); }
    public double sinh(double num) { return Math.sinh(num); }
    public double cosh(double num) { return Math.cosh(num); }
    public double tanh(double num) { return Math.tanh(num); }
    
    public double log(double num) { return Math.log10(num); }
    public double ln(double num) { return Math.log(num); }
    public double sqrt(double num) { return Math.sqrt(num); }
    public double cbrt(double num) { return Math.cbrt(num); }
    public double square(double num) { return num * num; }
    public double cube(double num) { return num * num * num; }
    public double absVal(double num) { return Math.abs(num); }
    public double reciprocal(double num) { 
        if (num == 0) throw new ArithmeticException("Cannot divide by zero");
        return 1.0 / num; 
    }
    public double factorial(int n) {
        if (n < 0) throw new ArithmeticException("Factorial undefined for negative");
        if (n > 170) throw new ArithmeticException("Factorial too large");
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }
    public double power2(double num) { return Math.pow(2, num); }
    public double tenPower(double num) { return Math.pow(10, num); }
    public double ePower(double num) { return Math.exp(num); }
    
    public double degreesToRadians(double deg) { return Math.toRadians(deg); }
    public double radiansToDegrees(double rad) { return Math.toDegrees(rad); }
}
