package j1sand0sardpi.interactivespaces.ultrasonics;

/*
 * Class used for creating and operating and executing complex operations
 * Allows easy manipulations of complex numbers and their maintainance
 */
public class Complex {
  /**
   * The real part of the complex number.
   */
  double real;

  /**
   * The imaginary part of the complex number.
   */
  double imag;

  /**
   * Constructor to define the real and imaginary part of the complex number
   */
  public Complex(double r, double i) {
    real = r;
    imag = i;
  }

  void div(double i) {
    real /= i;
    imag /= i;
    /* Method to divide the complex number by certain real number */
  }

  void mul(double m) {
    real *= m;
    imag *= m;
    /* Method to multiply the complex number by real number */
  }

  void mul(String h) {
    double t = real;
    real = -imag;
    imag = t;
    /* Method to multiply the complex number by complex value 'i' */
  }

  double abs() {
    return Math.sqrt(Math.pow(real, 2) + Math.pow(imag, 2));
    /* Method to calculate and return the magnitude of the complex number */
  }
}
