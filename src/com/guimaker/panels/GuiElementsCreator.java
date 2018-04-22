package com.guimaker.panels;

import com.guimaker.options.*;
import com.guimaker.utilities.CommonActionsCreator;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;
import com.guimaker.utilities.LimitDocumentFilter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class GuiElementsCreator {

	public static JLabel createLabel(ComponentOptions options) {
		JLabel label = new JLabel();
		setGeneralComponentOptions(options, label);
		label.setOpaque(false); //otherwise it will have white background
		label.setText(options.getText());
		return label;
	}

	private static void setGeneralComponentOptions(
			AbstractComponentOptions options, JComponent component) {
		component.setForeground(options.getForegroundColor());
		if (options.getBackgroundColor() != null) {
			component.setBackground(options.getBackgroundColor());
		}
		if (options.hasPreferredSize()) {
			component.setPreferredSize(options.getPreferredSize());
		}
		//TODO assure that the options are not null i.e. give them default values, then remove the assertions from here
		component.setOpaque(options.isOpaque());
		if (options.getBorder() != null) {
			component.setBorder(options.getBorder());
		}

		if (options.getFont() != null) {
			component.setFont(options.getFont());
		}
		if (options.getFontSize() > 0) {
			component.setFont(
					component.getFont().deriveFont(options.getFontSize())
							.deriveFont(Font.PLAIN));
		}

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

	private static AbstractButton createButtonlikeComponent(
			ButtonOptions options, AbstractAction actionOnClick, int hotkey,
			KeyModifiers keyModifier) {
		AbstractButton component = createButtonlikeComponent(options,
				actionOnClick);
		HotkeyWrapper wrapper = new HotkeyWrapper(keyModifier, hotkey);
		CommonActionsCreator
				.addHotkey(hotkey, wrapper.getKeyMask(), actionOnClick,
						component);
		return component;
	}

	public static AbstractButton createButtonlikeComponent(
			ButtonOptions options, AbstractAction actionOnClick) {
		AbstractButton component;
		String text = options.getText();
		switch (options.getButtonType()) {
		case BUTTON:
			component = new JButton(text);
			break;
		case RADIOBUTTON:
			component = new JRadioButton(text);
			break;
		case CHECKBOX:
			component = new JCheckBox(text);
			break;
		default:
			component = null;
			break;
		}
		setGeneralComponentOptions(options, component);
		component.addActionListener(actionOnClick);
		return component;
	}

	public static AbstractButton createButtonlikeComponent(
			ButtonOptions options, AbstractAction actionOnClick, int hotkey) {
		return createButtonlikeComponent(options, actionOnClick, hotkey,
				KeyModifiers.NONE);
	}

	public static JScrollPane createScrollPane(ScrollPaneOptions options) {
		JScrollPane scrollPane = new JScrollPane(options.getComponentToWrap());
		setGeneralComponentOptions(options, scrollPane);
		if (options.getBackgroundColor() != null) {
			scrollPane.getViewport()
					.setBackground(options.getBackgroundColor());
		}
		if (!options.isOpaque()) {
			scrollPane.getViewport().setOpaque(false);
		}
		if (options.getBorder() == null) {
			scrollPane.setBorder(BorderFactory.createEmptyBorder());
		}
		scrollPane.getVerticalScrollBar()
				.setUnitIncrement(options.getUnitIncrement());
		return scrollPane;
	}

	private static void setTextComponentOptions(
			AbstractTextComponentOptions options,
			JTextComponent textComponent) {
		setGeneralComponentOptions(options, textComponent);
		textComponent.setEditable(options.isEditable());
		textComponent.setEnabled(options.isEnabled());
		textComponent.setFocusable(options.isFocusable());

		if (!options.isEditable())
			textComponent.setHighlighter(null);

		if (!options.getPromptWhenEmpty().isEmpty()) {
			String prompt = options.getPromptWhenEmpty();
			textComponent.addFocusListener(
					CommonActionsCreator.addPromptWhenEmpty(prompt));
			if (options.getText().isEmpty()) {
				CommonActionsCreator
						.setTextFieldToPromptValue(textComponent, prompt);
			}
		}
		if (options.getText() != null && !options.getText().isEmpty()) {
			textComponent.setText(options.getText());
			textComponent.setForeground(options.getForegroundColor());
		}
		textComponent.setDisabledTextColor(Color.BLACK);
		if (options.getMaximumCharacters() > 0) {
			limitCharactersInTextComponent(textComponent,
					options.getMaximumCharacters(), options.isDigitsOnly());
		}

	}

	public static JTextField createTextField(TextComponentOptions options) {
		JTextField textField = new JTextField(options.getText(),
				options.getNumberOfColumns());
		setTextComponentOptions(options, textField);
		if (options.isEditable()) {
			textField.setBorder(
					BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
		}
		else {
			textField.setBorder(null);
		}
		textField.setOpaque(false);
		if (options.isSelectable()) {
			changeCursorOnMouseEnter(textField);
			textField.setBackground(Color.GRAY);
		}
		textField.setCaretColor(Color.WHITE);
		return textField;
	}

	private static void changeCursorOnMouseEnter(JTextField textField) {
		textField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				textField.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});
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
