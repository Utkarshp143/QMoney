
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.PortfolioManagerApplication;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl  implements PortfolioManager{
private StockQuotesService service;
private RestTemplate restTemplate;

protected PortfolioManagerImpl(String provider,RestTemplate restTemplate) {
  //this.restTemplate = restTemplate;
}

public PortfolioManagerImpl(StockQuotesService service2, RestTemplate restTemplate2) {
  //  this.restTemplate = restTemplate2;
    this.service = service2; 
  }

  public PortfolioManagerImpl(StockQuotesService service){
  //  this.restTemplate = new RestTemplate();
    this.service = service;
  }

public PortfolioManagerImpl(RestTemplate restTemplate2) {
  // this.restTemplate =  restTemplate2;
}

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF






  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws Exception {
        
    return this.service.getStockQuote(symbol, from, to);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

       String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
            uriTemplate.replace("$SYMBOL", symbol);
            uriTemplate.replace("$STARTDATE", startDate.toString());
            uriTemplate.replace("$ENDDATE", endDate.toString());
            uriTemplate.replace("$APIKEY", getToken());
            
            
            return uriTemplate;
  }

  public  List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws Exception
  {

    List<AnnualizedReturn> annualizedReturnsList = new ArrayList<>();

    for (PortfolioTrade trade : portfolioTrades) {
      
      List<Candle> candles = getStockQuote(trade.getSymbol(), trade.getPurchaseDate(), endDate);

      // defining price of buy and sell Price

      double buyPrice = getOpeningPriceOnStartDate(candles);
      double sellPrice = getClosingPriceOnEndDate(candles);
      double totalReturn = (sellPrice - buyPrice) / buyPrice;
      double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(),endDate)/365.24;
      double annualized_returns = Math.pow((1+totalReturn),(1 / total_num_years)) - 1;
      
      AnnualizedReturn  annualizedReturn = new AnnualizedReturn(trade.getSymbol(),annualized_returns,totalReturn);

      annualizedReturnsList.add(annualizedReturn);
    }

  //   double totalReturn = (sellPrice - buyPrice) / buyPrice;
  //   double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(),endDate)/365.24;
  //   double annualized_returns = Math.pow((1+totalReturn),(1 / total_num_years)) - 1;
  // return new AnnualizedReturn(trade.getSymbol(),annualized_returns,totalReturn);

   Collections.sort(annualizedReturnsList);
    return annualizedReturnsList;

  }
  private  Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
     //return 0.0;
  }


  private Double getClosingPriceOnEndDate(List<Candle> candles) {
     return candles.get(candles.size()-1).getClose();
    //return 0.0;
  }
  private String getToken() {
    return "31538ba9b3b4984d4577c6ae43e001ec8c0e2d21";
    //return "59892a96542d99303fafeedbe3f970bcd2100c5c";
  }


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
