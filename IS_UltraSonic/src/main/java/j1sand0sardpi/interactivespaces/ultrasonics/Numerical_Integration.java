package j1sand0sardpi.interactivespaces.ultrasonics;

import j1sand0sardpi.interactivespaces.ultrasonics.Ultra_virtual.Integral_Rayleigh_Sommerfeld;

/**
 * This class performs the task of computing the Rayleigh Sommerfeld Integral
 * through numerical integration.It acheives this through either the
 * trapezoidal,simpson's 1/3 and simpson's 3/8 depending on the number of
 * terms,accuracy,efficiency and performance required. For certain features of
 * the integrand certain methods converge faster thereby making them the
 * preferred choice
 */
public class Numerical_Integration {

  /*
   * 'n'-> Step Count 'a'-> Lower Limit of Integration 'b'-> Upper Limit of
   * Integration
   */
  static Complex res[];
  static double r, im;

  /*
   * 'res'->The complex object which is used to store the complex numbers
   * pertaining to each iteration 'r'-> Accumulator to sum up the real part of
   * 'res' 'im'->Accumulator to sum up the imaginary part of 'res'
   */
  public static Complex Trapezoidal(int n, double a, double b) {
    /*
     * This method of numerical integration uses trapezoids to approximate the
     * function curve. It is most preferred when the step count is small.
     */
    double del_x = (b - a) / n;
    res = new Complex[n + 1];
    r = 0.0;
    im = 0.0;

    /*
     * 'del_x'-> The width of the trapezoid 'res'-> The accumulator to store the
     * result after each iteration
     */
    for (int i = 0; i <= n; i++) {
      double x = a + i * del_x;
      res[i] =
          i == 0 || i == n ? Integral_Rayleigh_Sommerfeld.func(x, 2, 1)
              : Integral_Rayleigh_Sommerfeld.func(x, 1, 1);
      r += res[i].real;
      im += res[i].imag;
      /*
       * I have execute the condition using ternary operator for compactness and
       * efficiency but the logic is as follows
       * 
       * if(i==0||i==n) { res[i]=Integral_Rayleigh_Sommerfeld.func(x,2,1); }
       * else { res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,1); } Basically if
       * the term corressponding to the iterator is either the first or last
       * term then divide it by two and add or else add it as it is
       */
    }
    r *= del_x;
    im *= del_x;
    /* Multiply the result with del_x and return */
    return new Complex(r, im);

    /*
     * Code Logic: The area of trapezoid is ((sum of parallel sides)*distance
     * between the parallel sides)/2 The result returned by the 'func'
     * corressponds to the parallel sides of the trapezoid The del_x corresponds
     * to the width
     */
  }

  public static Complex Simpsons_one_third(int n, double a, double b) {
    /*
     * This method employs quadratic curves(parabolas) to approximate the
     * function curve. It is the most preferred method when the number of terms
     * is even
     */
    double del_x = (b - a) / n;
    res = new Complex[n + 1];
    r = 0.0;
    im = 0.0;
    for (int i = 0; i <= n; i++) {
      double x = a + i * del_x;
      res[i] =
          i == 0 || i == n ? (Integral_Rayleigh_Sommerfeld.func(x, 1, 1))
              : (i % 2 == 0 ? (Integral_Rayleigh_Sommerfeld.func(x, 1, 2))
                  : (Integral_Rayleigh_Sommerfeld.func(x, 1, 4)));
      r += res[i].real;
      im += res[i].imag;
      /*
       * I have execute the condition using ternary operator for compactness and
       * efficiency but the logic is as follows
       * 
       * if(i==0||i==n) { res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,1); }
       * else if(i%2==0) { res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,2); }
       * else { res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,4); } Basically if
       * the term corressponding to the iterator is either the first or last
       * term then add as it is, if they are even terms excluding the first and
       * last then multiply by '2' and add or else if they are odd terms
       * excluding the first and last multiply by '4' and add
       */
    }
    r *= (del_x / 3);
    im *= (del_x / 3);
    return new Complex(r, im);
    /* Multiply result by del_x and divide by '3' and return */

    /*
     * Code Logic The expression for any parabola is 'y=ax^2+bx+c' and given
     * three points each to determine 'a','b'and 'c' constants we can find the
     * parabola that passes through those points,therefore the code works by
     * finding the regions of many such parabolas computed which best fit the
     * given curve
     */
  }

  public static Complex Simpsons_Three_Eighth(int n, double a, double b) {
    /*
     * This method employs cubic polynomials to approximate the function curve.
     * It is the most preferred method when the number of terms or step count is
     * multiple of 3
     */
    double del_x = (b - a) / n;
    res = new Complex[n + 1];
    r = 0.0;
    im = 0.0;
    for (int i = 0; i <= n; i++) {
      double x = a + i * del_x;
      res[i] =
          i == 0 || i == n ? (Integral_Rayleigh_Sommerfeld.func(x, 1, 1))
              : (i % 3 == 0 ? (Integral_Rayleigh_Sommerfeld.func(x, 1, 2))
                  : (Integral_Rayleigh_Sommerfeld.func(x, 1, 2)));
      r += res[i].real;
      im += res[i].imag;
      /*
       * I have execute the condition using ternary operator for compactness and
       * efficiency but the logic is as follows
       * 
       * if(i==0||i==n) { res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,1); }
       * else if(i%3==0) { res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,2); }
       * else { res[i]=Integral_Rayleigh_Sommerfeld.func(x,1,3); } Basically if
       * the term corressponding to the iterator is either the first or last
       * term then add as it is, if they are multiples of 3 excluding the first
       * and last then multiply by '2' and add or else if they are non-multiples
       * of 3 excluding the first and last multiply by '3' and add
       */
    }
    r *= ((del_x * 3) / 8);
    im *= ((del_x * 3) / 8);
    return new Complex(r, im);
    /*
     * Fairly the same logic as Simpson's 1/3 rule but instead employs cubic
     * polynomials
     */

  }
}
