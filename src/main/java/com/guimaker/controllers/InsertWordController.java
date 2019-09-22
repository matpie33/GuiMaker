package com.guimaker.controllers;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.MyList;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.model.CommonListElements;
import com.guimaker.model.PropertyPostValidationData;
import com.guimaker.panels.InsertWordPanel;
import com.guimaker.panels.mainPanel.MainPanel;
import com.guimaker.strings.ExceptionsMessages;
import com.guimaker.utilities.ThreadUtilities;

import javax.swing.*;
import javax.swing.FocusManager;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class InsertWordController<Word extends ListElement>
		implements InputValidationListener<Word> {

	private MyList<Word> list;
	private ApplicationChangesManager applicationChangesManager;
	private InsertWordPanel<Word> panel;
	private boolean addingWordWasRequested = false;
	private Word word;
	private ListRowData<Word> listRow;

	public InsertWordController(MyList<Word> list,
			ApplicationChangesManager applicationChangesManager,
			InsertWordPanel panel) {
		this.panel = panel;
		this.list = list;
		this.applicationChangesManager = applicationChangesManager;
	}

	private void checkIfWordDoesntExistAndIsNotEmptyThenAddIt(Word word) {
		if (word.isEmpty()) {
			panel.getDialog()
				 .showMessageDialog(ExceptionsMessages.REQUIRED_FIELD_IS_EMPTY);
			return;
		}
		ThreadUtilities.callOnOtherThread(() -> {
			boolean addedWord = list.addWord(word);
			if (addedWord) {
				list.scrollToBottom();
				applicationChangesManager.save();
				panel.reinitializePanel();
			}
			else {
				list.highlightRow(list.get1BasedRowNumberOfWord(word) - 1,
						true);
				panel.getDialog()
					 .showMessageDialog(String.format(
							 ExceptionsMessages.WORD_ALREADY_EXISTS,
							 list.get1BasedRowNumberOfWord(word)), false);
			}
		});
	}

	public AbstractAction createActionValidateAndAddFocusedElement() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component focusOwner = FocusManager.getCurrentManager()
												   .getFocusOwner();
				if (focusOwner instanceof JTextComponent) {
					addingWordWasRequested = true;
					validateFocusedTextInput();
				}
				else {
					checkIfWordDoesntExistAndIsNotEmptyThenAddIt(word);
				}
			}
		};

	}

	private void validateFocusedTextInput() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
							.clearGlobalFocusOwner();
	}

	@Override
	public <WordProperty> void inputValidated(
			PropertyPostValidationData<WordProperty, Word> postValidationData) {
		if (addingWordWasRequested && postValidationData.isValid()) {
			checkIfWordDoesntExistAndIsNotEmptyThenAddIt(word);
		}
		addingWordWasRequested = false;
	}

	public MainPanel createListRowPanel() {
		word = list.getWordInitializer()
				   .initializeElement();
		listRow = list.getListRowCreator()
					  .createListRow(word, CommonListElements.forSingleRowOnly(
							  panel.getLabelsColor(), list), InputGoal.ADD);
		return listRow.getRowPanel();
	}

	public void focusFirstInput() {
		SwingUtilities.invokeLater(() -> {
			listRow.getRowPropertiesData()
				   .values()
				   .iterator()
				   .next()
				   .getFilteringTextComponent()
				   .requestFocusInWindow();
		});
	}

	public WindowListener createActionReinitializeOnWindowClose() {
		return new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				panel.reinitializePanel();
			}
		};
	}
}
