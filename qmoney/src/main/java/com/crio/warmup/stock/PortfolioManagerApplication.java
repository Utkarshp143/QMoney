
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedAndMetadata;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.crio.warmup.stock.portfolio.PortfolioManagerImpl;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.springframework.cglib.core.Local;
import org.springframework.objenesis.instantiator.android.Android10Instantiator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

 // method to return list of symbol which we get from deserializing json
  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException 
  {
    // getting file from calling method resolveFileFromResources method by passing arg at 1st position
    
    File file = resolveFileFromResources(args[0]);
    // deserializing the file to portfolioTrade POJO
    PortfolioTrade[] trades = getObjectMapper().readValue(file, PortfolioTrade[].class);

    //creating a list of type String to store Symbol
    List<String> result = new ArrayList<>();
    
    for(PortfolioTrade trade : trades)
    {
      // adding symbol to result list
      System.out.println(result.add(trade.getSymbol()));
    }

    //returning result
    return result;

    // we can do the same thing using readTradeFromJson method
  }

  // print json object
  private static void printJsonObject(Object object) throws IOException 
  {
    // logging
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  
  private static File resolveFileFromResources(String filename) throws URISyntaxException 
  {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  // return object of objectMapper
  private static ObjectMapper getObjectMapper() 
  {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  public static List<String> debugOutputs() 
  {

     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "trades.json";
     String toStringOfObjectMapper = "ObjectMapper";
     String functionNameFromTestFileInStackTrace = "mainReadFile";
     String lineNumberFromTestFileInStackTrace = "";

    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }

  public static List<TotalReturnsDto> mainReadQuotesHelper(String[] args,List<PortfolioTrade> trades) throws IOException, URISyntaxException
  {
    // endDate from args[1];
    LocalDate endDate = getEndFormatted(args[1]);

    // Creating list of TotalReturnsDto type
    List<TotalReturnsDto> tests = new ArrayList<TotalReturnsDto>();
    
    // traverse on the list of portFolioTrade
    for(PortfolioTrade trade : trades){
      
      // get a list of tiingocandles by calling fetchCandles method
      List<Candle> candles = fetchCandles(trade, endDate, getToken());

      // if result is not null then add that into list
      if(candles != null){
        tests.add(new TotalReturnsDto(trade.getSymbol(), candles.get(candles.size()-1).getClose()));
      }
    }
    return tests;
    
  }
  

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    
    // create list of totalReturnsDto
    List <TotalReturnsDto> totalReturnsDtosList = new ArrayList<>();
    
    // create a list of String type
    List<String> result = new ArrayList<>();

    // get end formatted in "YYYY-MM-DD" format
    LocalDate endDate = getEndFormatted(args[1]);
    
    // create list of PortFolioTrade using readTradesFromJson function
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);
    
    // traverse the portFolioTrade list
    for(PortfolioTrade trade : trades)
    {

      // get a list of tiingoCandles by calling fetchCandles method
      List<Candle> candles = fetchCandles(trade, endDate, getToken());

      // creating object of totalReturnsDto and then adding it into the list of TotalReturnsDtos list.
    TotalReturnsDto totalReturnsDto = new TotalReturnsDto(trade.getSymbol(),candles.get(candles.size()-1).getClose());
    
    // adding the object into the list of TotalReturnsDto.
    totalReturnsDtosList.add(totalReturnsDto);
    }

    // sort that list
    Collections.sort(totalReturnsDtosList,closingComparator);

    // adding the symbol from totalReturnsDtoList to result
    totalReturnsDtosList.forEach(t -> result.add(t.getSymbol()));
    
    return result;
  }

  // return object of RestTemplate
  protected static RestTemplate getRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }


  // method to format any date in "YYYY-MM-DD" format
  private static LocalDate getEndFormatted(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate endDate = LocalDate.parse(date, formatter);
    return endDate;
  }

  // comparing totalReturnsDto
  public static final Comparator<TotalReturnsDto> closingComparator = new Comparator<TotalReturnsDto>() {
    public int compare(TotalReturnsDto t1, TotalReturnsDto t2){
      return (int)(t1.getClosingPrice().compareTo(t2.getClosingPrice()));
    }
  };

  // method to deserialize the json into POJO
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    
    return Arrays.asList(getObjectMapper().readValue(resolveFileFromResources(filename),PortfolioTrade[].class));
    
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
     String url = "https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices?startDate="+trade.getPurchaseDate().toString()+"&endDate="+endDate.toString()+"&token="+token;
    return url;
  }


  public static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();

  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     return candles.get(candles.size()-1).getClose();
  }


  // return a list of tiingoCandles
  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    
    // create url using prepareurl function 
    String url = prepareUrl(trade, endDate, token);

    // get the json output from calling tiingocandle api using RestTemplate
    TiingoCandle[] tiingoCandles2 = getRestTemplate().getForObject(url, TiingoCandle[].class);
    return Arrays.asList(tiingoCandles2);
  }
  



  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
        
    // get parsed endDate ("yyyy-mm-dd")

    LocalDate endDate = getEndFormatted(args[1]);

    List<AnnualizedReturn> annualizedReturnsList = new ArrayList<>();
    
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);
    if(trades.isEmpty())
    {
      return Collections.emptyList();
    }

    // traverse through the trades which we get from json

    for(PortfolioTrade trade : trades)
    {
      List<Candle> candles = fetchCandles(trade, endDate, getToken());

      // defining price of buy and sell Price

      double buyPrice = getOpeningPriceOnStartDate(candles);
      double sellPrice = getClosingPriceOnEndDate(candles);

      AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(endDate, trade, buyPrice, sellPrice);

      annualizedReturnsList.add(annualizedReturn);
    }

    // Sort in descending
    
    Collections.sort(annualizedReturnsList);

    return annualizedReturnsList;

    

      
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        double totalReturn = (sellPrice - buyPrice) / buyPrice;
        double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(),endDate)/365.24;
        double annualized_returns = Math.pow((1+totalReturn),(1 / total_num_years)) - 1;
      return new AnnualizedReturn(trade.getSymbol(),annualized_returns,totalReturn);
  }


  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
      //  String contents = readFileAsString(file);
      //  ObjectMapper objectMapper = getObjectMapper();
       List <PortfolioTrade> portfolioTrades = readTradesFromJson(file);

       PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(getRestTemplate());
      return portfolioManager.calculateAnnualizedReturn(portfolioTrades, endDate);
  }


  private static String readFileAsString(String file) {
    return file.toString();
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainCalculateReturnsAfterRefactor(args));


  }





  public static String getToken() {
    return "31538ba9b3b4984d4577c6ae43e001ec8c0e2d21";
    //return "59892a96542d99303fafeedbe3f970bcd2100c5c";
  }
}

    // return trades.stream().map(trade -> trades).map(t -> {List<Candle> candles = fetchCandles(trade, endDate, getToken());
      
    //   // defining price of buy and sell Price

    //   double buyPrice = getOpeningPriceOnStartDate(candles);
    //   double sellPrice = getClosingPriceOnEndDate(candles);
    // AnnualizedReturn rAnnualizedReturn = calculateAnnualizedReturns(endDate, t, openPrice, closePrice);
    // return rAnnualizedReturn;}).sorted(Comparator.comparing(AnnualizedReturn :: getAnnualizedReturn))
    // .collect(Collectors.toList());

    // return Collections.emptyList();

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues



 // old token
  
  //static String TOKEN = "31538ba9b3b4984d4577c6ae43e001ec8c0e2d21";
  //static String URL = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Task:
  //       - Read the json file provided in the argument[0], The file is available in the classpath.
  //       - Go through all of the trades in the given file,
  //       - Prepare the list of all symbols a portfolio has.
  //       - if "trades.json" has trades like
  //         [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  //         Then you should return ["MSFT", "AAPL", "GOOGL"]
  //  Hints:
  //    1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  //       Check if they are of any help to you.
  //    2. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.


  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.









  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>




  



    //objectMapper.readValue(contents,PortfolioTrade[].class)

  // LocalDate endDate = getEndFormatted(args[1]);

  //   PortfolioTrade[] portfolioTrades = getPortfolioTradesFromFile(args[0]);
  //   return Arrays.stream(portfolioTrades).map(trade ->
  //  {
  //   //String uri = URL;
  //   //String token = TOKEN;
    
  //   TiingoCandle[] tiingoCandles = getTiingoCandles(endDate, trade, getToken());
  // return new TotalReturnsDto(trade.getSymbol(), Stream.of(tiingoCandles)
  // .filter(candle -> candle.getDate().equals(endDate))
  // .findFirst().get().getClose());
  // }).sorted(Comparator.comparing(TotalReturnsDto :: getClosingPrice))
  // .map(TotalReturnsDto::getSymbol)
  // .collect(Collectors.toList());





    //List<TotalReturnsDto> sortedByValue = mainReadQuotesHelper(args,trades);
    // // Collections.sort(sortedByValue,closingComparator);
    // // List<String> stocks = new ArrayList<String>();
    // // for(TotalReturnsDto trd : sortedByValue){
    // //   stocks.add(trd.getSymbol());
    // // } 
    //  return stocks;


    // LocalDate endDate = LocalDate.parse(args[1]);

    // String file = args[0];
    // String contents = readFileAsString(file);
    // ObjectMapper objectMapper = getObjectMapper();
    // PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents,PortfolioTrade[].class);
    




//private static PortfolioTrade[] getPortfolioTradesFromFile(String file) throws URISyntaxException,IOException{
  // String contents = file.toString();
  //readFileAsString



  //////////

   // LocalDate endDate = getEndFormatted(args[1]);
    
    // PortfolioTrade[] portfolioTrades = getPortfolioTradesFromFile(args[0]);
    


  //  return Arrays.stream(portfolioTrades).map(trade ->{
  //   TiingoCandle[] tiingoCandles = getTiingoCandles(endDate, trade,getToken());

  //   // Prepare the data
  //   double openPrice = tiingoCandles[0].getOpen();
  //   double closePrice = tiingoCandles[tiingoCandles.length-1].getClose();

  //   AnnualizedReturn returnObj = calculateAnnualizedReturns(endDate, trade, openPrice, closePrice);

  // return returnObj;
  // }).sorted(Comparator.comparing(AnnualizedReturn :: getAnnualizedReturn).reversed())
  //   .collect(Collectors.toList());
        
        // LocalDate endDate = LocalDate.parse(args[1]);
        // PortfolioTrade[] portfolioTrades = getPortfolioTradesFromFile(args[0]);


        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // LocalDate endDate = LocalDate.parse(args[1], formatter);
        
        // RestTemplate restTemplate = new RestTemplate();
        // List <TotalReturnsDto> totalReturnsDtosList = new ArrayList<>();
        // List<String> result = new ArrayList<>();
    
        // List<PortfolioTrade> trades = readTradesFromJson(args[0]);
        // for(PortfolioTrade trade : trades)
        // {
        //  String url = prepareUrl(trade, endDate, getToken());
        //  TiingoCandle[] tingo =  restTemplate.getForObject(url, TiingoCandle[].class);

        //  double openPrice = getOpeningPriceOnStartDate(tingo);
        //  double closePrice = getClosingPriceOnEndDate(candles)
        //  AnnualizedReturn returnObj = calculateAnnualizedReturns(endDate, trade, openPrice, closePrice);
        //  return returnObj;




        // }
        // return Arrays.stream(portfolioTrades)
        // .map(trade -> {TiingoCandle[] tiingoCandles = fetchCandles(trade,endDate,);
        // //prepare the data
        // double openPrice = getOpeningPriceOnStartDate(candles);
        // double closePrice = getClosingPriceOnEndDate(candles)
        // AnnualizedReturn returnObj = calculateAnnualizedReturns(endDate, trade, openPrice, closePrice);
        // return returnObj;
        // })
        // .sorted(Comparator.comparing(AnnualizedReturn :: getAnnualizedReturn).reversed())
        // .collect((Collectors.toList()));
     //return Collections.emptyList();



     
//   File contents = resolveFileFromResources(file);
//   ObjectMapper objectMapper = getObjectMapper();
//   return objectMapper.readValue(contents, PortfolioTrade[].class);
// }
