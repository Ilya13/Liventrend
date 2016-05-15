package ru.vertiprakhov.lcl.client.ui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBoxBase;

public class CrazyTextBox extends TextBoxBase {

	private Canvas canvas;
	private Context2d context;

	private int canvasWidth = 168;
	private int canvasHeight = 15;
	
	private String value;
	private int curretPosition;
	private double currentPosition;
	
	public CrazyTextBox() {
		this(Document.get().createDivElement(), "gwt-TextBox lcl-CrazyTextBox");
	}
	
	private CrazyTextBox(Element element, String styleName) {
		super(element);
	    if (styleName != null) {
	    	setStyleName(styleName);
	    }
	    init();
	}

	private void init() {
		canvas = Canvas.createIfSupported();
		if (canvas == null) return;
		
		canvas.setWidth(canvasWidth+"px");
		canvas.setHeight(canvasHeight+"px");
		
		context = canvas.getContext2d();
		context.save();
		
		addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE){
					removeChar();
					event.getNativeEvent().stopPropagation();
					event.getNativeEvent().preventDefault();
				}
			}
		});
		
		addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				addChar(event.getCharCode());
			}
		});
		
		DOM.appendChild(getElement(), canvas.getElement());
	}

	private void addChar(char charCode) {
		// TODO Auto-generated method stub
		
	}

	private void removeChar() {
		// TODO Auto-generated method stub
		
	}

}
