package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.UIObject;

public class HelloWorld extends UIObject { // Could extend Widget instead
  interface MyUiBinder extends UiBinder<DivElement, HelloWorld> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  @UiField SpanElement nameSpan;
  @UiField SpanElement dateSpan;

  public HelloWorld() {
    // createAndBindUi initializes this.nameSpan
    setElement(uiBinder.createAndBindUi(this));
  }

  public void setName(String name) { nameSpan.setInnerText(name); }

  public void setDate(String date) { dateSpan.setInnerText(date); }
}
