package com.guimaker.panels;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.guimaker.enums.ComponentType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.utilities.CommonActionsMaker;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.LimitDocumentFilter;

public class GuiMaker {

	private static final Dimension scrollPanesSize = new Dimension(300, 300);

	public static JLabel createLabel(ComponentOptions options) {
		JLabel l = new JLabel();
		l.setText(options.getText());
		l.setForeground(options.getForegroundColor());
		l.setBackground(options.getBackgroundColor());
		l.setBorder(options.getBorder());
		return l;
	}

	public static JTextArea createTextArea(TextComponentOptions options) {
		JTextArea j = new JTextArea(options.getNumberOfRows(), options.getNumberOfColumns());
		j.setWrapStyleWord(options.isWrapStyleWord());
		j.setLineWrap(options.isLineWrap());
		j.setEditable(options.isEditable());
		j.setBorder(options.getBorder());
		if (!options.isEditable())
			j.setHighlighter(null);
		j.setOpaque(options.isOpaque());
		j.setText(options.getText());
		if (options.getMaximumCharacters() > 0) {
			limitCharactersInTextComponent(j, options.getMaximumCharacters());
		}
		if (options.isMoveToNextComponentWhenTabbed()) {
			j.addKeyListener(createTabListenerThatMovesFocusToNextComponent(j));
		}
		return j;
	}

	private static KeyListener createTabListenerThatMovesFocusToNextComponent(JTextComponent a) {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					a.transferFocus();
					e.consume();
				}
			}
		};
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

	public static JScrollPane createScrollPane(ScrollPaneOptions options) {
		JScrollPane scroll = new JScrollPane(options.getComponentToWrap());
		scroll.setOpaque(options.isOpaque());
		scroll.getViewport().setBackground(options.getBackgroundColor());
		scroll.setBorder(options.getBorder());
		scroll.getVerticalScrollBar().setUnitIncrement(options.getUnitIncrement());
		scroll.setPreferredSize(options.getPreferredSize());
		return scroll;
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

	private static void limitCharactersInTextComponent(JTextComponent textField, int maxDigits) {
		((AbstractDocument) textField.getDocument())
				.setDocumentFilter(new LimitDocumentFilter(maxDigits));
	}

	public static JTextPane createTextPane(TextPaneOptions textPaneOptions) {
		JTextPane textPane = new JTextPane();
		textPane.setBackground(textPaneOptions.getBackgroundColor());
		textPane.setText(textPaneOptions.getText());
		textPane.setEditable(textPaneOptions.isEditable());
		textPane.setPreferredSize(textPaneOptions.getPreferredSize());
		textPane.setEnabled(textPaneOptions.isEnabled());
		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, textPaneOptions.getTextAlignment().getStyleConstant());
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		return textPane;
	}

	public static JScrollPane createTextPaneWrappedInScrollPane(TextPaneOptions textPaneOptions,
			ScrollPaneOptions scrollPaneOptions) {
		JScrollPane pane = createScrollPane(
				scrollPaneOptions.componentToWrap(createTextPane(textPaneOptions)));
		return pane;
	}

	public static JScrollPane createTextPaneWrappedInScrollPane(TextPaneOptions textPaneOptions) {
		JScrollPane pane = createScrollPane(
				new ScrollPaneOptions().componentToWrap(createTextPane(textPaneOptions)));
		return pane;
	}

}
