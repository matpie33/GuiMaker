package com.guimaker.panels;

import com.guimaker.enums.ConditionForHotkey;
import com.guimaker.options.*;
import com.guimaker.utilities.CommonActionsCreator;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;
import com.guimaker.utilities.LimitDocumentFilter;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

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
		component.setEnabled(options.isEnabled());
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
				.addHotkey(hotkey, wrapper.getKeyModifier(), actionOnClick,
						component);
		return component;
	}

	public static AbstractButton createButtonLikeComponent(
			ButtonOptions options) {
		return createButtonlikeComponent(options, null);
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
		removeEnterKeyBehaviourOfInput(textComponent);
		addUndoRedoActions(textComponent);

		textComponent.setEditable(options.isEditable());
		if (options.isEditable()) {
			textComponent.setBorder(
					BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
		}
		else {
			textComponent.setBorder(null);
		}
		textComponent.setOpaque(false);
		if (options.isSelectable()) {
			changeCursorOnMouseEnter(textComponent);
			textComponent.setBackground(Color.GRAY);
		}
		textComponent.setCaretColor(Color.WHITE);
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
		textComponent.setDisabledTextColor(Color.WHITE);
		if (options.getMaximumCharacters() > 0) {
			limitCharactersInTextComponent(textComponent,
					options.getMaximumCharacters(), options.isDigitsOnly());
		}

	}

	private static void addUndoRedoActions(JTextComponent textComponent) {
		UndoManager undoManager = new UndoManager();
		textComponent.getDocument().addUndoableEditListener(undoManager);
		AbstractAction undo = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (undoManager.canUndo()) {
					undoManager.undo();
				}
			}
		};

		CommonActionsCreator.addHotkey(
				new HotkeyWrapper(KeyModifiers.CONTROL, KeyEvent.VK_Z,
						ConditionForHotkey.COMPONENT_FOCUSED), undo,
				textComponent);

		AbstractAction redo = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (undoManager.canRedo()) {
					undoManager.redo();
				}
			}
		};

		CommonActionsCreator.addHotkey(
				new HotkeyWrapper(KeyModifiers.CONTROL, KeyEvent.VK_Y,
						ConditionForHotkey.COMPONENT_FOCUSED), redo,
				textComponent);
	}

	public static JComboBox createCombobox(ComboboxOptions options) {
		JComboBox comboBox = new JComboBox();
		setGeneralComponentOptions(options, comboBox);
		options.getComboboxValues().forEach(value -> comboBox.addItem(value));
		ListCellRenderer defaultRenderer = comboBox.getRenderer();
		setBackgroundColorOnSelection(options, comboBox, defaultRenderer);
		return comboBox;
	}

	private static String longestOfStrings(List<String> strings) {
		int maxLength = 0;
		int indexOfLongestString = 0;
		for (int i = 0; i < strings.size(); i++) {
			String s = strings.get(i);
			if (s.length() > maxLength) {
				maxLength = s.length();
				indexOfLongestString = i;
			}
		}
		return strings.get(indexOfLongestString);
	}

	private static void setBackgroundColorOnSelection(ComboboxOptions options,
			JComboBox comboBox, ListCellRenderer defaultRenderer) {
		comboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				Component c = defaultRenderer
						.getListCellRendererComponent(list, value, index,
								isSelected, cellHasFocus);
				Color backgroundColor = options.getBackgroundColor();
				if (c instanceof JLabel) {
					if (isSelected) {
						c.setBackground(Color.WHITE);
					}
					else {
						c.setBackground(backgroundColor);
					}
				}
				else {
					c.setBackground(backgroundColor);
					c = super.getListCellRendererComponent(list, value, index,
							isSelected, cellHasFocus);
				}
				return c;
			}
		});
	}

	public static JTextField createTextField(TextComponentOptions options) {
		JTextField textField = new JTextField(options.getText(),
				options.getNumberOfColumns());
		setTextComponentOptions(options, textField);
		return textField;
	}

	private static void changeCursorOnMouseEnter(JTextComponent textField) {
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

	private static void removeEnterKeyBehaviourOfInput(JTextComponent input) {
		InputMap inputMap = input.getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "");
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
