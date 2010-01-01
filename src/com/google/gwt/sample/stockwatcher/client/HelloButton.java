package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class HelloButton extends Composite {

  interface MyUiBinder extends UiBinder<Widget, HelloButton> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  @UiField Button button;

  public HelloButton(String... names) {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("button")
  void handleClick(ClickEvent e) {
    Window.alert("Hello from Button!");
  }

}
