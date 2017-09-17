package com.guimaker.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.utilities.CommonActionsMaker;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.LimitDocumentFilter;

public class GuiMaker {

	private static final Dimension scrollPanesSize = new Dimension(300, 300);
	private static final Border textFieldBorder = BorderFactory
			.createBevelBorder(BevelBorder.LOWERED);

	public static JLabel createLabel(String title, Color color) {
		JLabel l = new JLabel(title);
		l.setForeground(color);
		return l;
	}

	public static JLabel createErrorLabel(String message) {
		JLabel l = new JLabel(message);
		l.setForeground(Color.red);
		return l;
	}

	public static JTextArea createTextArea(boolean editable) {
		return createTextArea(editable, 0, 0); // TODO have to do it better
	}

	public static JTextArea createTextArea(boolean editable, boolean opaque) {
		return createTextArea(editable, opaque, 0, 0);
	}

	public static JTextArea createTextArea(boolean editable, int initialRows, int initialColumns) {
		return createTextArea(editable, true, initialRows, initialColumns);
	}

	public static JTextArea createTextArea(boolean editable, boolean opaque, int initialRows,
			int initialColumns) {
		JTextArea j = new JTextArea(initialRows, initialColumns);
		j.setWrapStyleWord(true);
		j.setLineWrap(true);
		j.setBorder(textFieldBorder);
		j.setEditable(editable);
		j.setOpaque(opaque);
		if (!editable)
			j.setBackground(BasicColors.GREY);
		return j;
	}

	public static JTextArea createTextArea(boolean editable, int maxCharacters) {
		JTextArea j = createTextArea(editable);
		limitCharactersInTextField(j, maxCharacters);
		return j;
	}

	public static JTextArea createTextArea(int maximumNumberOfDigits, String text,
			FocusListener listener) {
		JTextArea j = new JTextArea(1, maximumNumberOfDigits);
		limitCharactersInTextField(j, maximumNumberOfDigits);
		j.setBorder(textFieldBorder);
		j.setLineWrap(true);
		j.setWrapStyleWord(true);
		j.setText(text);
		j.addFocusListener(listener);
		return j;
	}

	public static AbstractButton createButtonlikeComponent(ComponentType type, String message,
			AbstractAction actionOnClick, int hotkey, int keyModifier) {
		AbstractButton component = createButtonlikeComponent(type, message, actionOnClick);
		HotkeyWrapper wrapper = new HotkeyWrapper(keyModifier, hotkey);
		CommonActionsMaker.addHotkey(hotkey, wrapper.getKeyMask(), actionOnClick, component);
		return component;
	}

	public static AbstractButton createButtonlikeComponent(ComponentType type, String message,
			AbstractAction actionOnClick) {
		AbstractButton component;
		switch (type) {
		case BUTTON:
			component = new JButton(message);
			break;
		case RADIOBUTTON:
			component = new JRadioButton(message);
			break;
		default:
			component = null;
			break;
		}
		component.addActionListener(actionOnClick);
		return component;
	}

	public static AbstractButton createButtonlikeComponent(ComponentType type, String message,
			AbstractAction actionOnClick, int hotkey) {
		return createButtonlikeComponent(type, message, actionOnClick, hotkey, 0);
	}

	public static JScrollPane createScrollPane(Color bgColor, Border border, Component component,
			Dimension size) {
		JScrollPane scroll = new JScrollPane(component);
		if (bgColor == null) {
			scroll.setOpaque(false);
		}
		else {
			scroll.getViewport().setBackground(bgColor);
		}
		scroll.setBorder(border);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		if (size != null) {
			scroll.setPreferredSize(size);
		}

		return scroll;
	}

	public static JScrollPane createScrollPane(Color bgColor, Border border, Component component) {
		return createScrollPane(bgColor, border, component, scrollPanesSize);
	}

	public static JTextField createTextField(int textLength) {
		return createTextField(textLength, "", true);
	}

	public static JTextField createTextField(int textLength, String text) {
		return createTextField(textLength, text, true);
	}

	public static JTextField createTextField(int textLength, String text, boolean editable) {
		JTextField textField = new JTextField(text, textLength);
		// limitCharactersInTextField(textField, textLength);
		textField.setEditable(editable);
		return textField;
	}

	private static void limitCharactersInTextField(JTextComponent textField, int maxDigits) {
		((AbstractDocument) textField.getDocument())
				.setDocumentFilter(new LimitDocumentFilter(maxDigits));
	}

	public static JTextPane createTextPane(String text, TextAlignment alignment) {
		JTextPane textPane = new JTextPane();
		textPane.setBackground(BasicColors.VERY_LIGHT_BLUE);
		textPane.setText(text);
		textPane.setEditable(false);
		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, alignment.getStyleConstant());
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		return textPane;
	}

	public static JScrollPane createTextPaneWrappedInScrollPane(String text,
			TextAlignment alignment) {
		JScrollPane pane = new JScrollPane(createTextPane(text, alignment));
		pane.setPreferredSize(new Dimension(250, 70));
		return pane;
	}

	public static JTextArea createTextArea(String text, FocusListener listener) {
		JTextArea elem = new JTextArea(text, 1, 15);
		elem.addFocusListener(listener);
		elem.setOpaque(true);
		elem.setBorder(textFieldBorder);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		return elem;
	}

}
