package com.guimaker.panels;

import com.guimaker.enums.ComponentType;
import com.guimaker.options.*;
import com.guimaker.utilities.CommonActionsMaker;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;
import com.guimaker.utilities.LimitDocumentFilter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GuiMaker {

	public static JLabel createLabel(ComponentOptions options) {
		JLabel label = new JLabel();
		label.setText(options.getText());
		label.setForeground(options.getForegroundColor());
		label.setBackground(options.getBackgroundColor());
		label.setBorder(options.getBorder());
		if (options.getFontSize() > 0) {
			label.setFont(label.getFont().deriveFont(options.getFontSize())
					.deriveFont(Font.PLAIN));
		}

		return label;
	}

	public static JTextArea createTextArea(TextAreaOptions options) {
		JTextArea textArea = new JTextArea(options.getNumberOfRows(),
				options.getNumberOfColumns());
		setTextComponentOptions(options, textArea);
		textArea.setWrapStyleWord(options.isWrapStyleWord());
		textArea.setLineWrap(options.isLineWrap());
		if (options.isMoveToNextComponentWhenTabbed()) {
			textArea.addKeyListener(
					createTabListenerThatMovesFocusToNextComponent(textArea));
		}
		return textArea;
	}

	private static KeyListener createTabListenerThatMovesFocusToNextComponent(
			JTextComponent a) {
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

	private static AbstractButton createButtonlikeComponent(ComponentType type,
			String message, AbstractAction actionOnClick, int hotkey,
			KeyModifiers keyModifier) {
		AbstractButton component = createButtonlikeComponent(type, message,
				actionOnClick);
		HotkeyWrapper wrapper = new HotkeyWrapper(keyModifier, hotkey);
		CommonActionsMaker
				.addHotkey(hotkey, wrapper.getKeyMask(), actionOnClick,
						component);
		return component;
	}

	public static AbstractButton createButtonlikeComponent(ComponentType type,
			String message, AbstractAction actionOnClick) {
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

	public static AbstractButton createButtonlikeComponent(ComponentType type,
			String message, AbstractAction actionOnClick, int hotkey) {
		return createButtonlikeComponent(type, message, actionOnClick, hotkey,
				KeyModifiers.NONE);
	}

	public static JScrollPane createScrollPane(ScrollPaneOptions options) {
		JScrollPane scrollPane = new JScrollPane(options.getComponentToWrap());
		scrollPane.setOpaque(options.isOpaque());
		if (options.getBackgroundColor() != null) {
			scrollPane.getViewport()
					.setBackground(options.getBackgroundColor());
		}

		scrollPane.setBorder(options.getBorder());
		scrollPane.getVerticalScrollBar()
				.setUnitIncrement(options.getUnitIncrement());
		scrollPane.setPreferredSize(options.getPreferredSize());
		return scrollPane;
	}

	private static void setTextComponentOptions(
			AbstractTextComponentOptions options,
			JTextComponent textComponent) {
		textComponent.setEditable(options.isEditable());
		textComponent.setEnabled(options.isEnabled());
		textComponent.setBorder(options.getBorder());
		textComponent.setFocusable(options.isFocusable());
		if (!options.isEditable())
			textComponent.setHighlighter(null);
		if (options.hasPreferredSize()) {
			textComponent.setPreferredSize(options.getPreferredSize());
		}
		if (options.getFontSize() > 0) {
			textComponent.setFont(
					textComponent.getFont().deriveFont(options.getFontSize()));
		}
		if (!options.getPromptWhenEmpty().isEmpty()) {
			String prompt = options.getPromptWhenEmpty();
			textComponent.addFocusListener(
					CommonActionsMaker.addPromptWhenEmpty(prompt));
			if (options.getText().isEmpty()) {
				CommonActionsMaker
						.setTextFieldToPromptValue(textComponent, prompt);
			}
		}
		if (options.getText() != null && !options.getText().isEmpty()){
			textComponent.setText(options.getText());
			textComponent.setForeground(options.getForegroundColor());
		}
		textComponent.setOpaque(options.isOpaque());
		textComponent.setDisabledTextColor(Color.BLACK);
		if (options.getMaximumCharacters() > 0) {
			limitCharactersInTextComponent(textComponent,
					options.getMaximumCharacters(), options.isDigitsOnly());
		}
		if (options.getBackgroundColor() != null) {
			textComponent.setBackground(options.getBackgroundColor());
		}


	}

	public static JTextField createTextField(TextComponentOptions options) {
		JTextField textField = new JTextField(options.getText(),
				options.getNumberOfColumns());
		setTextComponentOptions(options, textField);
		return textField;
	}

	private static void limitCharactersInTextComponent(JTextComponent textField,
			int maxDigits, boolean digitsOnly) {
		((AbstractDocument) textField.getDocument()).setDocumentFilter(
				new LimitDocumentFilter(maxDigits, digitsOnly));
	}

	public static JTextPane createTextPane(TextPaneOptions textPaneOptions) {
		JTextPane textPane = new JTextPane();
		setTextComponentOptions(textPaneOptions, textPane);
		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center,
				textPaneOptions.getTextAlignment().getStyleConstant());
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		return textPane;
	}

	public static JScrollPane createTextPaneWrappedInScrollPane(
			TextPaneOptions textPaneOptions,
			ScrollPaneOptions scrollPaneOptions) {
		JScrollPane pane = createScrollPane(scrollPaneOptions
				.componentToWrap(createTextPane(textPaneOptions)));
		return pane;
	}

	public static JScrollPane createTextPaneWrappedInScrollPane(
			TextPaneOptions textPaneOptions) {
		JScrollPane pane = createScrollPane(new ScrollPaneOptions()
				.componentToWrap(createTextPane(textPaneOptions))
				.opaque(textPaneOptions.isOpaque()));
		pane.getViewport().setOpaque(textPaneOptions.isOpaque());
		return pane;
	}

}
