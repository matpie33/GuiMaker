package com.guimaker.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;

import com.guimaker.colors.BasicColors;

public class GuiMaker {

	private static final Dimension scrollPanesSize = new Dimension(300, 300);

	public static JLabel createLabel(String title) {
		JLabel l = new JLabel(title);
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

	public static JButton createButton(String title, ActionListener listener) {
		JButton b = new JButton(title);
		b.addActionListener(listener);
		return b;
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

	public static JRadioButton createRadioButton(String text, ActionListener listener) {
		JRadioButton radioButton = new JRadioButton(text);
		radioButton.addActionListener(listener);
		return radioButton;
	}

	public static JCheckBox createCheckBox(String text, ActionListener listener) {
		JCheckBox checkbox = new JCheckBox(text);
		checkbox.addActionListener(listener);
		return checkbox;
	}

}
