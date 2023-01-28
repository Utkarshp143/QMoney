
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {


  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) 
      throws JsonProcessingException,Exception {
    try {
      String result = restTemplate.getForObject(buildUri(symbol, from, to), String.class);
      if (result == null || result.isEmpty()) {
        throw new Exception("No response");
      }
      List<TiingoCandle> collection = getObjectMapper()
          .readValue(result, new TypeReference<ArrayList<TiingoCandle>>() {
          });
      return new ArrayList<Candle>(collection);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    return "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?" + "startDate="
        + startDate.toString() + "&endDate=" + endDate.toString() + "&token="
        + getToken();
    
  }

public static String getToken() {
  return "31538ba9b3b4984d4577c6ae43e001ec8c0e2d21";
  //return "59892a96542d99303fafeedbe3f970bcd2100c5c";
}


}
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Now we will be separating communication with Tiingo from PortfolioManager.
  //  Generate the functions as per the declarations in the interface and then
  //  Move the code from PortfolioManagerImpl#getSTockQuotes inside newly created method.
  //  Run the tests using command below -
  //  ./gradlew test --tests TiingoServiceTest and make sure it passes.



  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call tiingo service.






  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  Update the method signature to match the signature change in the interface.
  //  Start throwing new StockQuoteServiceException when you get some invalid response from
  //  Tiingo, or if Tiingo returns empty results for whatever reason,
  //  or you encounter a runtime exception during Json parsing.
  //  Make sure that the exception propagates all the way from
  //  PortfolioManager#calculateAnnualisedReturns,
  //  so that the external user's of our API are able to explicitly handle this exception upfront.
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF

// package com.crio.warmup.stock.quotes;

// import com.crio.warmup.stock.dto.Candle;
// import com.crio.warmup.stock.dto.TiingoCandle;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import org.springframework.web.client.RestTemplate;

// public class TiingoService implements StockQuotesService {


//   private RestTemplate restTemplate;

//   protected TiingoService(RestTemplate restTemplate) {
//     this.restTemplate = restTemplate;
//   }



//   // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
//   //  Implement getStockQuote method below that was also declared in the interface.

//   // Note:
//   // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
//   // 2. Run the tests using command below and make sure it passes.
//   //    ./gradlew test --tests TiingoServiceTest


//   //CHECKSTYLE:OFF

//   // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
//   //  Write a method to create appropriate url to call the Tiingo API.
//   private static ObjectMapper getObjectMapper() {
//     ObjectMapper objectMapper = new ObjectMapper();
//     objectMapper.registerModule(new JavaTimeModule());
//     return objectMapper;
//   }
//   @Override
//   public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
//       throws JsonProcessingException {
//     // TODO Auto-generated method stub
    
//     String url = buildUri(symbol, from, to);

//     String result = this.restTemplate.getForObject(url,String.class);
//     if (result == null || result.isEmpty()) {
//       throw new Exception("No response");
//     }
//     List<TiingoCandle> collection = getObjectMapper()
//         .readValue(result, new TypeReference<ArrayList<TiingoCandle>>() {
//         });
//     return new ArrayList<Candle>(collection);
//   }
   
//   return Collections.emptyList();
  
// }

//   protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {

//     return "https:api.tiingo.com/tiingo/daily/"+"symbol"+"/prices?"
//          + "startDate="+"startDate.toString()"+"&endDate=endDate.toString()"+"&token="+getToken();
//   }


// }
