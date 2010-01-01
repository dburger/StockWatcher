package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DelistedException extends Exception implements IsSerializable {

  private String symbol;

  public DelistedException() {
  }

  public DelistedException(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return this.symbol;
  }
}
