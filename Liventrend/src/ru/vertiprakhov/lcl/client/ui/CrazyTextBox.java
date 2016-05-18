package ru.vertiprakhov.lcl.client.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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

	private int inputWidth = 168;
	private int inputHeight = 15;
	private int fontSize = 13;
	
	private String value = "";
	private TextRange range = new TextRange();
	
	private LinkedList<TextPoint> points = new LinkedList<>();
	
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
		setFontSize(fontSize);
		
		canvas.setCoordinateSpaceWidth(inputWidth);
		canvas.setCoordinateSpaceHeight(inputHeight);
		
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
		if (range.curret == points.size()){
			points.addLast(point);
			range.right++;
			if(range.width + point.getWidth() <= inputWidth){
				drawWithoutOffset(point);
			} else {
				drawWithLeftOffset(point);
			}
		} else {
			points.add(range.curret, point);
			drawWithRightOffset();
		}
		range.curret++;
		range.width += point.getWidth();
	}
	
	/**
	 * Draw character right of the text without offset other characters.
	 */
	private void drawWithoutOffset(TextPoint point) {
		if ((points.size() & 1) == 0){
			drawCrazyChar(point, range.width);
		} else {
			context.fillText(String.valueOf(point.getChr()), range.width, fontSize-1);
		}
	}
	
	/**
	 * Draw character when curret inside the text.
	 */
	private void drawWithRightOffset() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Draw character when text does not fit input.
	 */
	private void drawWithLeftOffset(TextPoint point) {
		ListIterator<TextPoint> iter = points.listIterator(range.left);
		TextPoint leftPoint = iter.next();
		while (range.width + point.getWidth() - inputWidth > leftPoint.getWidth()){
			range.width -= leftPoint.getWidth();
			leftPoint = iter.next();
			range.left++;
		}
		context.clearRect(0, 0, inputWidth, inputHeight);
		drawRange(inputWidth - range.width - point.getWidth(), range.left, range.right);
	}

	/**
	 * Draw flip character.
	 * @param point the character for drawing
	 * @param x the x coordinate of the character position
	 */
	private void drawCrazyChar(TextPoint point, double x){
		double y = 0;
		if (!Character.isLowerCase(point.getChr())){
			context.setTextBaseline(TextBaseline.BOTTOM);
		} else {
			y -= fontSize/2;
		}
		context.scale(1, -1);
		context.fillText(String.valueOf(point.getChr()), x, y+1);
		context.scale(1, -1);
		context.setTextBaseline(TextBaseline.ALPHABETIC);
	}
	
	private void drawRange(double position, int left, int right) {
		int i = left;
		ListIterator<TextPoint> iter = points.listIterator(i);
		while (i < right){
			TextPoint point = iter.next();
			if ((i & 1) != 0){
				drawCrazyChar(point, position);
			} else {
				context.fillText(String.valueOf(point.getChr()), position, fontSize-1);
			}
			position += point.getWidth();
			i++;
		}
	}
	
	private void removeChar() {
		if(range.width > inputWidth){
			if (range.curret == points.size()){
				TextPoint point = points.getLast();
				range.width -= point.getWidth();
				range.curret--;
				range.right--;
				points.removeLast();
				removeWithLeftOffset();
			}
		} else {
			if (range.curret == points.size()){
				TextPoint point = points.getLast();
				range.width -= point.getWidth();
				range.curret--;
				range.right--;
				points.removeLast();
				removeWithoutOffset();
			}
		}
	}
	
	private void removeWithLeftOffset(TextPoint point) {
		ListIterator<TextPoint> iter = points.listIterator(range.left);
		TextPoint leftPoint = iter.next();
		while (range.width + point.getWidth() - inputWidth < leftPoint.getWidth()){
			range.width -= leftPoint.getWidth();
			leftPoint = iter.next();
			range.left++;
		}
		context.clearRect(0, 0, inputWidth, inputHeight);
		drawRange(inputWidth - range.width - point.getWidth(), range.left, range.right);
	}

	private void removeWithoutOffset() {
		ListIterator<TextPoint> iter = points.listIterator();
		double position = 0;
		while(iter.hasNext()){
			position += iter.next().getWidth();
		}
		
		context.clearRect(position, 0, inputWidth, inputHeight);
	}

	private void setFontSize(String fontSize){
		this.fontSize = sizeToInt(fontSize);
		this.inputHeight = this.fontSize + 2;
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
