package com.guimaker.list.myList.panel;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.application.ApplicationWindow;
import com.guimaker.application.DialogWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.list.myList.MyList;
import com.guimaker.panels.InsertWordPanel;
import com.guimaker.strings.Prompts;
import com.guimaker.strings.Titles;
import com.guimaker.utilities.ThreadUtilities;

import javax.swing.*;
import javax.swing.FocusManager;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ListActionsCreator<Word extends ListElement> {

	private InsertWordPanel<Word> insertWordPanel;
	private ListWordsController<Word> listWordsController;
	private ListConfiguration<Word> listConfiguration;
	private ListPanelUpdater listPanelUpdater;

	public ListActionsCreator(InsertWordPanel<Word> insertWordPanel,
			ListWordsController<Word> listWordsController,
			ListConfiguration<Word> listConfiguration,
			ListPanelUpdater listPanelUpdater) {
		this.listWordsController = listWordsController;
		this.listConfiguration = listConfiguration;
		this.listPanelUpdater = listPanelUpdater;
		this.insertWordPanel = insertWordPanel;
	}

	public MyList<Word> getList() {
		return listWordsController.getMyList();
	}

	public AbstractAction createDeleteRowAction(Word word) {
		return new AbstractAction() {
			private static final long serialVersionUID = 5946111397005824819L;

			@Override
			public void actionPerformed(ActionEvent e) {
				ApplicationChangesManager applicationChangesManager = listConfiguration.getApplicationChangesManager();
				if (!applicationChangesManager.getApplicationWindow()
											  .showConfirmDialog(String.format(
													  Prompts.DELETE_ELEMENT,
													  listConfiguration.getWordSpecificDeletePrompt()))) {
					return;
				}
				listWordsController.remove(word);
				applicationChangesManager.save();
			}
		};
	}

	public AbstractAction createActionAddNewWord(InputGoal inputGoal) {
		return new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				listWordsController.add(
						listConfiguration.getListElementInitializer()
										 .initializeElement(), inputGoal, true);
				listPanelUpdater.updateRowsPanel();
				listWordsController.focusFirstTextfieldInPanel();
			}
		};

	}

	public AbstractAction createEditWordAction(Word word) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listWordsController.editTemporarily(word);
			}
		};
	}

	public AbstractAction createFinishEditAction(Word word) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listWordsController.setInEditMode(false);
				Component focusOwner = FocusManager.getCurrentManager()
												   .getFocusOwner();
				if (focusOwner instanceof JTextComponent) {
					listWordsController.setFinishEditActionRequested(true);
					KeyboardFocusManager.getCurrentKeyboardFocusManager()
										.clearGlobalFocusOwner();
				}
				else {
					listWordsController.repaintWordAndHighlightIfNeeded(word);
				}

			}
		};
	}

	public AbstractAction createActionShowInsertWordDialog() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ApplicationWindow applicationWindow = listConfiguration.getApplicationChangesManager()
																	   .getApplicationWindow();
				applicationWindow.createDialog(insertWordPanel,
						Titles.INSERT_WORD_DIALOG, false,
						DialogWindow.Position.CENTER);
			}
		};
	}

	public AbstractAction createActionClearFilter() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ThreadUtilities.callOnOtherThread(
						() -> listWordsController.showWordsStartingFromRow(
								listWordsController.getStartOfRangeOfDisplayedWords()));
			}
		};
	}

	public AbstractAction createActionShowNextOrPreviousWords(
			ListWordsLoadingDirection loadingDirection) {

		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listWordsController.showNextOrPreviousWords(loadingDirection);
			}
		};

	}

}
