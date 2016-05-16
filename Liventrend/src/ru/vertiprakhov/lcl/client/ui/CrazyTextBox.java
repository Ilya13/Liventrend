package ru.vertiprakhov.lcl.client.ui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
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
	private int fontSize = 13;
	private int paddingLeft;
	private int paddingRight;
	private int paddingTop;
	private int paddingBottom;
	
	private String value = "";
	private int curretPosition = 0;
	private double currentPosition = 0;
	
	public CrazyTextBox() {
		this(Document.get().createDivElement(), "gwt-TextBox lcl-CrazyTextBox");
	}
	
	private CrazyTextBox(Element element, String styleName) {
		super(element);
	    if (styleName != null) {
	    	setStyleName(styleName);
	    }
	}

	@Override
	protected void onLoad(){
		super.onLoad();
	    init();
	}
	
	private void init() {
		canvas = Canvas.createIfSupported();
		if (canvas == null) return;
		
		context = canvas.getContext2d();

		String fontFamily = getComputedStyleProperty(getElement(), "font-family");
		String fontSize = getComputedStyleProperty(getElement(), "font-size");
		setPaddingLeft(getComputedStyleProperty(getElement(), "padding-left"));
		setPaddingRight(getComputedStyleProperty(getElement(), "padding-right"));
		setPaddingTop(getComputedStyleProperty(getElement(), "padding-top"));
		setPaddingBottom(getComputedStyleProperty(getElement(), "padding-bottom"));
		setFontSize(fontSize);
		
		canvas.setCoordinateSpaceWidth(canvasWidth);
		canvas.setCoordinateSpaceHeight(canvasHeight);
		
		context.setFont(fontSize + " " + fontFamily);
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
		String redrawStr = charCode + value.substring(curretPosition);
		value = value.substring(0, curretPosition) + redrawStr;
		context.clearRect(currentPosition, 0, canvasWidth, canvasHeight);
		drawString(redrawStr);
		currentPosition += context.measureText(String.valueOf(charCode)).getWidth();
		curretPosition++;
	}

	private void drawString(String str) {
		double position = currentPosition;
		for (int i = 0; i < str.length(); i++) {
			char chr = str.charAt(i);
			String ch = String.valueOf(chr);
			double width = context.measureText(ch).getWidth();
			if ((curretPosition + i & 1) != 0){
				double y = -paddingTop;
				if (!Character.isLowerCase(chr)){
					context.setTextBaseline(TextBaseline.BOTTOM);
				} else {
					y -= fontSize/2;
				}
				context.scale(1, -1);
				context.fillText(ch, position, y);
				context.scale(1, -1);
				context.setTextBaseline(TextBaseline.ALPHABETIC);
			} else {
				context.fillText(ch, position, fontSize + paddingTop);
			}
			position += width;
		}
	}
 
	private void removeChar() {
		// TODO Auto-generated method stub
		
	}
	
	private void setFontSize(String fontSize){
		this.fontSize = sizeToInt(fontSize);
		updatHeight();
	}

	public void setPaddingLeft(String paddingLeft) {
		this.paddingLeft = sizeToInt(paddingLeft);
	}

	public void setPaddingRight(String paddingRight) {
		this.paddingRight = sizeToInt(paddingRight);
	}

	public void setPaddingTop(String paddingTop) {
		this.paddingTop = sizeToInt(paddingTop);
		updatHeight();
	}

	public void setPaddingBottom(String paddingBottom) {
		this.paddingBottom = sizeToInt(paddingBottom);
		updatHeight();
	}
	
	private void updatHeight(){
		canvasHeight = this.fontSize + this.paddingTop + this.paddingBottom;
	}
	
	private static int sizeToInt(String size){
		try {
			return Integer.parseInt(size.replaceAll("[^\\d.]", ""));
		} catch (Exception e){}
		return 0;
	}
	
	private static native String getComputedStyleProperty(Element element, String property)  /*-{
		var value = window.getComputedStyle(element,null).getPropertyValue(property)
		console.log(property+":"+value);
		return value;
	}-*/;
	
	private native void consoleLog(String message) /*-{
	    console.log(message);
	}-*/;

}
