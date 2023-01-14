
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
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

public class PortfolioManagerImpl extends  PortfolioManagerApplication implements PortfolioManager{

private RestTemplate restTemplate;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


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
      throws JsonProcessingException {
        
    String url = buildUri(symbol, from, to);

    TiingoCandle[] tiingoCandles2 = getRestTemplate().getForObject(url, TiingoCandle[].class);
    return Arrays.asList(tiingoCandles2);
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
      LocalDate endDate)
  {

    List<AnnualizedReturn> annualizedReturnsList = new ArrayList<>();

    for (PortfolioTrade trade : portfolioTrades) {
      
      List<Candle> candles = fetchCandles(trade, endDate, getToken());

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


}
