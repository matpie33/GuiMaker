package com.guimaker.list.myList.filtering;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.KeyModifiers;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.panel.ListPanel;
import com.guimaker.model.HotkeyWrapper;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComboboxOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.strings.ButtonsNames;
import com.guimaker.strings.HotkeysDescriptions;
import com.guimaker.strings.Prompts;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

public class ListFilteringPanel<Word extends ListElement> {

	private ListRowData<Word> listRowData;
	private JComboBox<String> comboBox;
	private JLabel filteringProperty;
	private JTextComponent filteringInput;
	private ListElementPropertyManager<?, Word> listElementPropertyManager;
	private MainPanel searchPanel;
	public static final String COLON = ":";
	private AbstractButton buttonClearFilter;
	private ListFilteringController listFilteringController;

	public ListFilteringPanel(ListFilteringController listFilteringController) {
		this.listFilteringController = listFilteringController;
	}

	public JPanel createPanel(ListPanel<Word> listPanel,
			ListRowData<Word> listRowData, AbstractButton buttonClearFilter,
			ListConfiguration listConfiguration) {
		this.buttonClearFilter = buttonClearFilter;
		this.listRowData = listRowData;
		addActionFilterImmediatelyToTextFields(listRowData);

		searchPanel = new MainPanel();

		JLabel searchOptionPrompt = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Prompts.SEARCH_OPTION_PROMPT));

		filteringProperty = GuiElementsCreator.createLabel(
				new ComponentOptions());
		filteringInput = GuiElementsCreator.createTextField(
				new TextComponentOptions().rowsAndColumns(1, 15));

		comboBox = createComboboxForSearchedProperty();
		searchPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.WEST,
						searchOptionPrompt, comboBox));
		if (listConfiguration.getWordDictionaryData() != null) {
			searchPanel.addRow(
					SimpleRowBuilder.createRow(FillType.NONE, Anchor.WEST,
							createComboboxForSearchingInDictionary(
									listConfiguration, listPanel)));
		}
		addFilteringInputAndButton();

		return searchPanel.getPanel();
	}

	private JComponent createComboboxForSearchingInDictionary(
			ListConfiguration listConfiguration, ListPanel<Word> listPanel) {

		HotkeyWrapper hotkeyWrapper = new HotkeyWrapper(KeyModifiers.CONTROL,
				KeyEvent.VK_E);
		ItemListener action = listFilteringController.createActionEnableSearchInDictionary(
				listConfiguration);
		listPanel.addHotkeyInformationOnly(hotkeyWrapper,
				HotkeysDescriptions.OPEN_SEARCH_IN_DICTIONARY_PANEL, action);
		return GuiElementsCreator.createCheckbox(
				new ButtonOptions(ButtonType.CHECKBOX).text(
						ButtonsNames.ENABLE_SEARCH_IN_DICTIONARY), action,
				hotkeyWrapper);

	}

	private void addActionFilterImmediatelyToTextFields(
			ListRowData<Word> listRowData) {
		listRowData.getRowPropertiesData()
				   .values()
				   .forEach(listProperty -> {
					   listProperty.getFilteringTextComponent()
								   .getDocument()
								   .addDocumentListener(
										   listFilteringController.createActionFilterImmediately());
				   });
	}

	private void addFilteringInputAndButton() {
		searchPanel.addRow(
				SimpleRowBuilder.createRow(FillType.HORIZONTAL, Anchor.WEST,
						filteringProperty, filteringInput, buttonClearFilter)
								.fillHorizontallySomeElements(filteringInput));
	}

	private JComboBox<String> createComboboxForSearchedProperty() {
		JComboBox<String> comboBox = GuiElementsCreator.createCombobox(
				new ComboboxOptions());
		if (listRowData.getRowPropertiesData()
					   .isEmpty()) {
			return comboBox;
		}
		listRowData.getRowPropertiesData()
				   .keySet()
				   .forEach(comboBox::addItem);
		comboBox.addActionListener(createActionSwitchSearchingByOption());
		comboBox.setSelectedIndex(0);
		return comboBox;
	}

	private AbstractAction createActionSwitchSearchingByOption() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox comboBox = (JComboBox) e.getSource();
				String optionLabel = (String) comboBox.getSelectedItem();
				switchToListProperty(optionLabel);
			}
		};
	}

	private void switchToListProperty(String property) {
		filteringProperty.setText(property + COLON);
		listElementPropertyManager = listRowData.getRowPropertiesData()
												.get(property)
												.getFilteringHandler();
		filteringInput = listRowData.getRowPropertiesData()
									.get(property)
									.getFilteringTextComponent();
		searchPanel.removeLastRow();
		addFilteringInputAndButton();
		SwingUtilities.invokeLater(this::focusFirstTextfieldForCurrentProperty);
	}

	private void focusFirstTextfieldForCurrentProperty() {
		filteringInput.requestFocusInWindow();
	}

	public AbstractAction createActionSwitchComboboxValue() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboBox.getSelectedIndex() < comboBox.getItemCount() - 1) {
					comboBox.setSelectedIndex(comboBox.getSelectedIndex() + 1);
				}
				else {
					comboBox.setSelectedIndex(0);
				}
				getFilteringInput().requestFocusInWindow();
				getFilteringInput().selectAll();

			}
		};

	}

	public JTextComponent getFilteringInput() {
		return filteringInput;
	}

	public ListElementPropertyManager<?, Word> getPropertyManagerForInput() {
		return listElementPropertyManager;
	}

	public AbstractAction createActionFocusAndSelectAllInFilterTextField() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getFilteringInput().requestFocusInWindow();
				getFilteringInput().selectAll();
			}
		};
	}
}
