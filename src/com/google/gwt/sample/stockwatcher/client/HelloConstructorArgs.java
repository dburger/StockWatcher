package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HelloConstructorArgs extends Composite {

  interface MyUiBinder extends UiBinder<Widget, HelloConstructorArgs> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private final String[] names;

  public HelloConstructorArgs(String... names ) {
    this.names = names;
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiFactory HelloWidgetWorld makeHelloWidgetWorld() {
    return new HelloWidgetWorld(names);
  }

}
