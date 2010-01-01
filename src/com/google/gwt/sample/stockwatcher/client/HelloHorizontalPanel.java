package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HelloHorizontalPanel extends Composite {

  interface MyUiBinder extends UiBinder<Widget, HelloHorizontalPanel> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  @UiField Label firstLabel;
  @UiField Label secondLabel;

  public HelloHorizontalPanel(String firstLabelText, String secondLabelText) {
    initWidget(uiBinder.createAndBindUi(this));
    firstLabel.setText(firstLabelText);
    secondLabel.setText(secondLabelText);
  }

}
