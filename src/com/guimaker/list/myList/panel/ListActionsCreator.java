package com.guimaker.list.myList.panel;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.application.ApplicationWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.ListWordsLoadingDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.loadAdditionalWordsHandling.LoadWordsHandler;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.ListWordsController;
import com.guimaker.list.myList.MyList;
import com.guimaker.strings.Prompts;
import com.guimaker.utilities.Pair;
import com.guimaker.utilities.ThreadUtilities;

import javax.swing.*;
import javax.swing.FocusManager;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ListActionsCreator<Word extends ListElement> {

	private ListWordsController<Word> listWordsController;
	private ListConfiguration<Word> listConfiguration;
	private ListPanelUpdater listPanelUpdater;

	public ListActionsCreator(ListWordsController<Word> listWordsController,
			ListConfiguration<Word> listConfiguration,
			ListPanelUpdater listPanelUpdater) {
		this.listWordsController = listWordsController;
		this.listConfiguration = listConfiguration;
		this.listPanelUpdater = listPanelUpdater;
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
				Pair<MyList, ListElement> parentListAndWord = listConfiguration.getParentListAndWordContainingThisList();
				listWordsController.add(
						listConfiguration.getListElementInitializer()
										 .initializeElement(), inputGoal, true);
				if (parentListAndWord != null) {
					parentListAndWord.getLeft()
									 .updateObservers(
											 parentListAndWord.getRight(),
											 ListElementModificationType.EDIT);
				}
				listPanelUpdater.updateRowsPanel();
				listWordsController.focusFirstTextfieldInPanel();
			}
		};

	}

	public AbstractAction createEditWordAction(Word word) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listWordsController.setInEditMode(true);
				listWordsController.repaint(word, InputGoal.EDIT_TEMPORARILY);
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
					KeyboardFocusManager.getCurrentKeyboardFocusManager()
										.clearGlobalFocusOwner();
					listWordsController.setFinishEditActionRequested(true);
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
				applicationWindow.showInsertWordDialog(
						listWordsController.getMyList(),
						applicationWindow.getApplicationConfiguration()
										 .getInsertWordPanelPositioner());
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
				listWordsController.showNextOrPreviousWords(
						loadingDirection);
			}
		};

	}

}
