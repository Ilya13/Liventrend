package ru.vertiprakhov.lcl.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import ru.vertiprakhov.lcl.client.ui.CrazyTextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Liventrend implements EntryPoint {

	public void onModuleLoad() {
		final CrazyTextBox nameField = new CrazyTextBox();

		RootPanel.get("textBoxContainer").add(new TextBox());
		RootPanel.get("crazyTextBoxContainer").add(nameField);
		nameField.setFocus(true);
	}
}
