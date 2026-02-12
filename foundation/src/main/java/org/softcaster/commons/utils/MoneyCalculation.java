/*
 * MoneyCalculation.java
 *
 * Created on May 21, 2007, 10:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.softcaster.commons.utils;

/**
 *
 * @author Emy
 */
import java.math.BigDecimal;

public final class MoneyCalculation {

  public MoneyCalculation(BigDecimal aAmountOne, BigDecimal aAmountTwo){
    fAmountOne = rounded(aAmountOne);
    fAmountTwo = rounded(aAmountTwo);
  }

  public MoneyCalculation(double aAmountOne, double aAmountTwo){
    fAmountOne = rounded(BigDecimal.valueOf(aAmountOne));
    fAmountTwo = rounded(BigDecimal.valueOf(aAmountTwo));
  }

  public MoneyCalculation(Double aAmountOne, Double aAmountTwo){
    fAmountOne = rounded(BigDecimal.valueOf(aAmountOne));
    fAmountTwo = rounded(BigDecimal.valueOf(aAmountTwo));
  }

  // PRIVATE //

  private final BigDecimal fAmountOne;
  private final BigDecimal fAmountTwo;

  /**
  * Defined centrally, to allow for easy changes to the rounding mode.
  */
  private static int ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;

  /**
  * Number of decimals to retain. Also referred to as "scale".
  */
  private static int DECIMALS = 2;
  //An alternate style for this value :
  //private static int DECIMAL_PLACES =
  //  Currency.getInstance("USD").getDefaultFractionDigits()
  //;

  private static int EXTRA_DECIMALS = 4;
  private static final BigDecimal TWO = new BigDecimal("2");
  private static BigDecimal HUNDRED = new BigDecimal("100");
  private static BigDecimal PERCENTAGE = new BigDecimal("5.25");

  public BigDecimal getSum(){
    return fAmountOne.add(fAmountTwo);
  }

  public BigDecimal getDifference(){
    return fAmountTwo.subtract(fAmountOne);
  }

  public BigDecimal getAverage(){
    return getSum().divide(TWO, ROUNDING_MODE);
  }

  public BigDecimal getPercentage(){
    BigDecimal result = fAmountOne.multiply(PERCENTAGE);
    result = result.divide(HUNDRED, ROUNDING_MODE);
    return rounded(result);
  }

  public BigDecimal getPercentageChange(){
    BigDecimal fractionalChange = getDifference().divide(
      fAmountOne, EXTRA_DECIMALS, ROUNDING_MODE
    );
    return rounded( fractionalChange.multiply(HUNDRED) );
  }

  // Torna valore attotondato
  public static BigDecimal rounded(double aNumber){
    return rounded(BigDecimal.valueOf(aNumber));
  }
  public static BigDecimal rounded(Double aNumber){
    return rounded(BigDecimal.valueOf(aNumber));
  }
  public static BigDecimal rounded(BigDecimal aNumber){
    return aNumber.setScale(DECIMALS, ROUNDING_MODE);
  }
} 

