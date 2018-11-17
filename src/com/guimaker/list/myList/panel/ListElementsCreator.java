package com.guimaker.list.myList.panel;

import com.guimaker.enums.ButtonType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.KeyModifiers;
import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.loadAdditionalWordsHandling.LoadWordsHandler;
import com.guimaker.model.CommonListElements;
import com.guimaker.model.HotkeyWrapper;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.strings.ButtonsNames;
import com.guimaker.strings.HotkeysDescriptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ListElementsCreator<Word extends ListElement> {

	private ListPanel<Word> listPanel;
	private ListActionsCreator<Word> listActionsCreator;

	public ListElementsCreator(ListPanel<Word> listPanel,
			ListActionsCreator<Word> listActionsCreator) {
		this.listPanel = listPanel;
		this.listActionsCreator = listActionsCreator;
	}

	public AbstractButton createButtonLoadWords(String buttonName,
			ListWordsLoadingDirection loadingDirection) {
		AbstractButton button = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(buttonName),
				listActionsCreator.createActionShowNextOrPreviousWords(
						loadingDirection));
		button.setEnabled(false);
		return button;
	}

	private AbstractButton createButtonRemoveWord(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(
						ButtonsNames.REMOVE_ROW),
				listActionsCreator.createDeleteRowAction(word));
	}

	public AbstractButton createButtonAddRow(InputGoal inputGoal) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(ButtonsNames.ADD_ROW),
				listActionsCreator.createActionAddNewWord(inputGoal));
	}

	private AbstractButton createButtonEditWord(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(ButtonsNames.EDIT),
				listActionsCreator.createEditWordAction(word));
	}

	private AbstractButton createButtonFinishEditing(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(
						ButtonsNames.FINISH_EDITING),
				listActionsCreator.createFinishEditAction(word),
				new HotkeyWrapper(KeyEvent.VK_ENTER));
	}

	public CommonListElements createCommonListElements(Word word,
			InputGoal inputGoal, int rowNumber, Color labelsColor) {
		JLabel rowNumberLabel = new JLabel(createTextForRowNumber(rowNumber));
		rowNumberLabel.setForeground(labelsColor);
		AbstractButton remove = createButtonRemoveWord(word);
		AbstractButton addNewWord = createButtonAddRow(inputGoal);
		AbstractButton editWord = createButtonEditWord(word);
		AbstractButton finishEditing = createButtonFinishEditing(word);
		return new CommonListElements(remove, rowNumberLabel, addNewWord,
				labelsColor, editWord, finishEditing, false);

	}

	public String createTextForRowNumber(int rowNumber) {
		return "" + rowNumber + ". ";
	}

	public AbstractButton createButtonAddWord() {
		String name = ButtonsNames.ADD;
		String hotkeyDescription = HotkeysDescriptions.ADD_WORD;
		int keyEvent = KeyEvent.VK_I;
		//TODO add in my list a parameter with hotkeys mapping for add/search panels
		return listPanel.createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent,
				listActionsCreator.createActionShowInsertWordDialog(), name,
				hotkeyDescription);

	}

	public AbstractButton createButtonClearFilter() {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(
						ButtonsNames.CLEAR_FILTER),
				listActionsCreator.createActionClearFilter());
	}

	public JScrollPane createWrappingScrollPane(MainPanel rowsPanel) {
		return GuiElementsCreator.createScrollPane(
				new ScrollPaneOptions().opaque(false)
									   .componentToWrap(rowsPanel.getPanel())
									   .border(listPanel.getDefaultBorder()));
	}

	public JLabel createTitleLabel(String title) {
		return GuiElementsCreator.createLabel(
				new ComponentOptions().text(title));
	}
}
