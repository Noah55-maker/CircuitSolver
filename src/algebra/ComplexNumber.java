package algebra;

public class ComplexNumber {
    private double real;
    private double imaginary;

    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public ComplexNumber(ComplexNumber other) {
        this.real = other.real;
        this.imaginary = other.imaginary;
    }

    public static ComplexNumber add(ComplexNumber n1, ComplexNumber n2) {
        return new ComplexNumber(n1.real + n2.real, n1.imaginary + n2.imaginary);
    }

    public static ComplexNumber subtract(ComplexNumber n1, ComplexNumber n2) {
        return new ComplexNumber(n1.real - n2.real, n1.imaginary - n2.imaginary);
    }

    public static ComplexNumber multiply(ComplexNumber n1, ComplexNumber n2) {
        return new ComplexNumber(
                (n1.real * n2.real) - (n1.imaginary * n2.imaginary),
                (n1.real * n2.imaginary) + (n1.imaginary * n2.real)
        );
    }

    public static ComplexNumber divide(ComplexNumber n1, ComplexNumber n2) {
        double dist = n2.real * n2.real + n2.imaginary * n2.imaginary;
        ComplexNumber inverse = ComplexNumber.multiply(n2.conjugate(), new ComplexNumber(1/dist, 0));
        return multiply(n1, inverse);
    }

    public ComplexNumber conjugate() {
        return new ComplexNumber(this.real, -this.imaginary);
    }

    public double magnitude() {
        return Math.sqrt(this.real * this.real + this.imaginary * this.imaginary);
    }

    public double angle() {
        return Math.atan2(this.imaginary, this.real);
    }

    public double angleDegrees() {
        return angle() * 180 / Math.PI;
    }

    public void add(ComplexNumber other) {
        this.real += other.real;
        this.imaginary += other.imaginary;
    }

    public void subtract(ComplexNumber other) {
        this.real -= other.real;
        this.imaginary -= other.imaginary;
    }

    public void multiply(ComplexNumber other) {
        double re = (this.real * other.real) - (this.imaginary * other.imaginary);
        double im = (this.real * other.imaginary) + (this.imaginary * other.real);

        this.real = re;
        this.imaginary = im;
    }

    public void divide(ComplexNumber other) {
        double dist = other.real * other.real + other.imaginary * other.imaginary;
        ComplexNumber inverse = ComplexNumber.multiply(other.conjugate(), new ComplexNumber(1/dist, 0));
        this.multiply(inverse);
    }

    @Override
    public String toString() {
        return String.format("%f + %fi", this.real, this.imaginary);
    }
}
