package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class StockWatcher implements EntryPoint {

  private static final String JSON_URL = GWT.getModuleBaseURL() + "jsonStockPrices?q=";
  private static final int REFRESH_INTERVAL = 5000; // ms
  private VerticalPanel mainPanel = new VerticalPanel();
  private FlexTable stocksFlexTable = new FlexTable();
  private HorizontalPanel addPanel = new HorizontalPanel();
  private TextBox newSymbolTextBox = new TextBox();
  private Button addStockButton = new Button("Add");
  private Label lastUpdatedLabel = new Label();
  private ArrayList<String> stocks = new ArrayList<String>();
  private StockPriceServiceAsync stockPriceSvc = GWT.create(StockPriceService.class);
  private Label errorMsgLabel = new Label();
  private VerticalPanel fetchJsonPanel = new VerticalPanel();
  private Button fetchJsonButton = new Button("Fetch JSON");
  private Label fetchJsonResultsLabel = new Label("JSON results");

  /**
   * Entry point method.
   */
  public void onModuleLoad() {
    // Create table for stock data.
    stocksFlexTable.setText(0, 0, "Symbol");
    stocksFlexTable.setText(0, 1, "Price");
    stocksFlexTable.setText(0, 2, "Change");
    stocksFlexTable.setText(0, 3, "Remove");

    // Add styles to elements in the stock list table.
    stocksFlexTable.setCellPadding(6);
    stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
    stocksFlexTable.addStyleName("watchList");
    stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
    stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
    stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");

    // Assemble Add Stock panel.
    addPanel.add(newSymbolTextBox);
    addPanel.add(addStockButton);
    addPanel.addStyleName("addPanel");

    // Assemble Main panel.
    errorMsgLabel.setStyleName("errorMessage");
    errorMsgLabel.setVisible(false);

    mainPanel.add(errorMsgLabel);
    mainPanel.add(stocksFlexTable);
    mainPanel.add(addPanel);
    mainPanel.add(lastUpdatedLabel);

    // Associate the Main panel with the HTML host page.
    RootPanel.get("stockList").add(mainPanel);


    // Move cursor focus to the input box.
    newSymbolTextBox.setFocus(true);

    // Setup timer to refresh list automatically.
    Timer refreshTimer = new Timer() {
	@Override
	  public void run() {
	  refreshWatchList();
	}
      };
    refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

    // Listen for mouse events on the Add button.
    addStockButton.addClickHandler(new ClickHandler() {
	public void onClick(ClickEvent event) {
	  addStock();
	}
      });

    // Listen for keyboard events in the input box.
    newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
	public void onKeyPress(KeyPressEvent event) {
	  if (event.getCharCode() == KeyCodes.KEY_ENTER) {
	    addStock();
	  }
	}
      });


    HelloWorld helloWorld = new HelloWorld();
    String name = "Bob Dylan";
    helloWorld.setName(name);
    String date = "I called this dateSpan, so I shoulda put a date here";
    helloWorld.setDate(date);
    // helloWorld is not a widget and so cannot be added directly like this:
    // RootPanel.get("uiBinder1").add(helloWorld);
    Document.get().getElementById("uiBinder1")
      .appendChild(helloWorld.getElement());

    HelloWidgetWorld helloWidgetWorld = new HelloWidgetWorld("able", "baker",
							     "charlie");
    // Document.get().getBody().appendChild(helloWidgetWorld.getElement());
    RootPanel.get("uiBinder2").add(helloWidgetWorld);

    HelloHorizontalPanel helloHorizontalPanel
      = new HelloHorizontalPanel("first", "second");
    RootPanel.get("uiBinder3").add(helloHorizontalPanel);

    HelloButton helloButton = new HelloButton();
    RootPanel.get("uiBinder4").add(helloButton);

    HelloConstructorArgs helloConstructorArgs
      = new HelloConstructorArgs("one", "two", "three", "four", "five", "six");
    RootPanel.get("uiBinder5").add(helloConstructorArgs);

    fetchJsonButton.addClickHandler(new ClickHandler() {
	public void onClick(ClickEvent event) {
	  fetchJsonStockData();
	}
      });
    fetchJsonPanel.add(fetchJsonButton);
    fetchJsonPanel.add(fetchJsonResultsLabel);
    RootPanel.get("json_demo").add(fetchJsonPanel);
  }

  /**
   * Add stock to FlexTable. Executed when the user clicks the addStockButton or
   * presses enter in the newSymbolTextBox.
   */
  private void addStock() {
    // TODO Auto-generated method stub
    final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
    newSymbolTextBox.setFocus(true);

    // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
    if (!symbol.matches("^[0-9A-Z\\.]{1,10}$")) {
      Window.alert("'" + symbol + "' is not a valid symbol.");
      newSymbolTextBox.selectAll();
      return;
    }

    newSymbolTextBox.setText("");

    // Don't add the stock if it's already in the table.
    if (stocks.contains(symbol))
      return;

    // Add the stock to the table.
    int row = stocksFlexTable.getRowCount();
    stocks.add(symbol);
    stocksFlexTable.setText(row, 0, symbol);
    stocksFlexTable.setWidget(row, 2, new Label());
    stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
    stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
    stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");

    // Add a button to remove this stock from the table.
    Button removeStockButton = new Button("x");
    removeStockButton.addStyleDependentName("remove");
    removeStockButton.addClickHandler(new ClickHandler() {
	public void onClick(ClickEvent event) {
	  int removedIndex = stocks.indexOf(symbol);
	  stocks.remove(removedIndex);
	  stocksFlexTable.removeRow(removedIndex + 1);
	}
      });
    stocksFlexTable.setWidget(row, 3, removeStockButton);


    // Get the stock price.
    refreshWatchList();
  }

  /**
   * Generate random stock prices.
   */
  private void refreshWatchList() {
    // Initialize the service proxy.
    if (stockPriceSvc == null) {
      stockPriceSvc = GWT.create(StockPriceService.class);
    }

    // Set up the callback object.
    AsyncCallback<StockPrice[]> callback = new AsyncCallback<StockPrice[]>() {
      public void onFailure(Throwable caught) {
        // If the stock code is in the list of delisted codes, display an error message.
        String details = caught.getMessage();
        if (caught instanceof DelistedException) {
          details = "Company '" + ((DelistedException)caught).getSymbol() + "' was delisted";
        }

        errorMsgLabel.setText("Error: " + details);
        errorMsgLabel.setVisible(true);
      }

      public void onSuccess(StockPrice[] result) {
        updateTable(result);
      }
    };

    // Make the call to the stock price service.
    stockPriceSvc.getPrices(stocks.toArray(new String[0]), callback);
  }

  /**
   * Update the Price and Change fields all the rows in the stock table.
   *
   * @param prices Stock data for all rows.
   */
  private void updateTable(StockPrice[] prices) {
    for (int i = 0; i < prices.length; i++) {
      updateTable(prices[i]);
    }

    // Display timestamp showing last refresh.
    lastUpdatedLabel.setText("Last update : "
			     + DateTimeFormat.getMediumDateTimeFormat().format(new Date()));

    // Clear any errors.
    errorMsgLabel.setVisible(false);
  }

  /**
   * Update a single row in the stock table.
   *
   * @param price Stock data for a single row.
   */
  private void updateTable(StockPrice price) {
    // Make sure the stock is still in the stock table.
    if (!stocks.contains(price.getSymbol())) {
      return;
    }

    int row = stocks.indexOf(price.getSymbol()) + 1;

    // Format the data in the Price and Change fields.
    String priceText = NumberFormat.getFormat("#,##0.00").format(
								 price.getPrice());
    NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
    String changeText = changeFormat.format(price.getChange());
    String changePercentText = changeFormat.format(price.getChangePercent());

    // Populate the Price and Change fields with new data.
    stocksFlexTable.setText(row, 1, priceText);
    Label changeWidget = (Label)stocksFlexTable.getWidget(row, 2);
    changeWidget.setText(changeText + " (" + changePercentText + "%)");

    // Change the color of text in the Change field based on its value.
    String changeStyleName = "noChange";
    if (price.getChangePercent() < -0.1f) {
      changeStyleName = "negativeChange";
    }
    else if (price.getChangePercent() > 0.1f) {
      changeStyleName = "positiveChange";
    }

    changeWidget.setStyleName(changeStyleName);
  }

  /**
   * Convert the string of JSON into JavaScript object.
   */
  private final native JsArray<StockData> asArrayOfStockData(String json) /*-{
									    return eval(json);
									    }-*/;

  private void fetchJsonStockData() {
    if (stocks.size() == 0) {
      displayJsonError("Need to add some stocks above before trying this");
      return;
    }

    String url = JSON_URL;

    for (Iterator i = stocks.iterator(); i.hasNext();) {
      url += i.next();
      if (i.hasNext()) url += "+";
    }

    url = URL.encode(url);

    // Send request to server and catch any errors.
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

    try {
      Request request = builder.sendRequest(null, new RequestCallback() {
	  public void onError(Request request, Throwable exception) {
	    displayJsonError("Couldn't retrieve JSON");
	  }

	  public void onResponseReceived(Request request, Response response) {
	    if (200 == response.getStatusCode()) {
              JsArray<StockData> stockData = asArrayOfStockData(response.getText());
	      String msg = "";
	      for (int i = 0; i < stockData.length(); i++) {
		StockData data = stockData.get(i);
		msg += data.summary() + "|";
	      }
	      fetchJsonResultsLabel.setText(msg);
	    } else {
	      displayJsonError("Couldn't retrieve JSON (" + response.getStatusText()
			   + ")");
	    }
	  }
	});
    } catch (RequestException e) {
      displayJsonError("Couldn't retrieve JSON");
    }

  }

  private void displayJsonError(String error) {
    fetchJsonResultsLabel.setText("ERROR: " + error);
  }

}
