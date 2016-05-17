package ru.vertiprakhov.lcl.client.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

import ru.vertiprakhov.lcl.client.model.TextPoint;
import ru.vertiprakhov.lcl.client.model.TextRange;

public class CrazyTextBox extends TextBoxBase {

	private static final int MAX_CHAR_LENGHT = 310;
	
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
	private TextRange range = new TextRange();
	
	private List<TextPoint> points = new ArrayList<>();
	
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
				if (value.length() <= MAX_CHAR_LENGHT){
					addChar(event.getCharCode());	
				}
			}
		});
		
		DOM.appendChild(getElement(), canvas.getElement());
	}

	private void addChar(char charCode) {
		TextPoint point = new TextPoint(charCode, context.measureText(String.valueOf(charCode)).getWidth());
		range.width += point.getWidth();
		int drawLeft = 0;
		boolean clearAll = false;
		if(range.width > canvasWidth){
			if (range.curret == points.size()){
				do {
					range.width -= points.get(range.left).getWidth();
					range.left++;
				} while (range.width > canvasWidth);
				drawLeft = range.left;
				clearAll = true;
			} else {
			}
		} else {
			drawLeft = range.right;
		}
		points.add(range.curret, point);
		range.right++;
		range.curret++;

		double position = 0;
		if (clearAll){
			context.clearRect(0, 0, canvasWidth, canvasHeight);
		} else {
			position = clearRange(drawLeft, range.right);
		}
		
		drawRange(position, drawLeft, range.right);
	}

	private double clearRange(int left, int right) {
		double position = 0;
		for (int i = 0; i < left; i++) {
			position += points.get(i).getWidth();
		}
		context.clearRect(position, 0, canvasWidth, canvasHeight);
		return position;
	}
	
	private void drawRange(double position, int left, int right) {
		for (int i = left; i < right; i++) {
			TextPoint point = points.get(i);
			if ((i & 1) != 0){
				double y = -paddingTop;
				if (!Character.isLowerCase(point.getChr())){
					context.setTextBaseline(TextBaseline.BOTTOM);
				} else {
					y -= fontSize/2;
				}
				context.scale(1, -1);
				context.fillText(String.valueOf(point.getChr()), position, y);
				context.scale(1, -1);
				context.setTextBaseline(TextBaseline.ALPHABETIC);
			} else {
				context.fillText(String.valueOf(point.getChr()), position, fontSize + paddingTop);
			}
			position += point.getWidth();
		}
	}
 
	private void removeChar() {
		
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
