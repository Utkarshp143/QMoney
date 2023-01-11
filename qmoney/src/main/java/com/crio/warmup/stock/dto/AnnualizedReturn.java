
package com.crio.warmup.stock.dto;
//import java.util.Comparator;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

public class AnnualizedReturn implements Comparable<AnnualizedReturn>{
//  public static final Comparator closingComparator = null;
  private final String symbol;
  private final Double annualizedReturn;
  private final Double totalReturns;

  public AnnualizedReturn(String symbol, Double annualizedReturn, Double totalReturns) {
    this.symbol = symbol;
    this.annualizedReturn = annualizedReturn;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return annualizedReturn;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }
  public int compareTo(AnnualizedReturn a){  
    if(this.getAnnualizedReturn() == a.getAnnualizedReturn())
      return 0;
    else if(this.getAnnualizedReturn() < a.getAnnualizedReturn())
      return 1;
    else
      return -1;
    }  
  
}
