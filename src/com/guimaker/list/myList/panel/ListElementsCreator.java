package com.guimaker.list.myList.panel;

import com.guimaker.application.ApplicationWindow;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListElement;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.list.myList.MyList;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.strings.ButtonsNames;
import com.guimaker.strings.HotkeysDescriptions;
import com.guimaker.utilities.CommonListElements;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ListElementsCreator<Word extends ListElement> {

	private ListWordsController<Word> listWordsController;
	private ListViewManager<Word> listViewManager;
	private MyList<Word> myList;
	private ApplicationWindow applicationWindow;

	public ListElementsCreator(ListWordsController<Word> listWordsController,
			ListViewManager<Word> listViewManager, MyList<Word> myList,
			ApplicationWindow applicationWindow) {
		this.listWordsController = listWordsController;
		this.listViewManager = listViewManager;
		this.myList = myList;
		this.applicationWindow = applicationWindow;
	}

	public AbstractButton createAndAddButtonLoadWords(String buttonName) {
		AbstractButton button = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(buttonName), null);
		button.setEnabled(false);
		return button;
	}

	private AbstractButton createButtonRemoveWord(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(
						ButtonsNames.REMOVE_ROW),
				listWordsController.createDeleteRowAction(word));
	}

	public AbstractButton createButtonAddRow(InputGoal inputGoal) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(ButtonsNames.ADD_ROW),
				listWordsController.addNewWord(inputGoal));
	}

	private AbstractButton createButtonEditWord(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(ButtonsNames.EDIT),
				listWordsController.createEditWordAction(word));
	}

	private AbstractButton createButtonFinishEditing(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(
						ButtonsNames.FINISH_EDITING),
				listWordsController.createFinishEditAction(word),
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
		return listViewManager.createButtonWithHotkey(KeyModifiers.CONTROL,
				keyEvent,
				listWordsController.createActionShowInsertWordDialog(), name,
				hotkeyDescription);

	}

	public AbstractButton createButtonClearFilter() {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(
						ButtonsNames.CLEAR_FILTER),
				listWordsController.createActionClearFilter());
	}

	public JScrollPane createWrappingScrollPane(MainPanel rowsPanel) {
		return GuiElementsCreator.createScrollPane(
				new ScrollPaneOptions().opaque(false)
									   .componentToWrap(rowsPanel.getPanel())
									   .border(listViewManager.getDefaultBorder()));
	}

}
