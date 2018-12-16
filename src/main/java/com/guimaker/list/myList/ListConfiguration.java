package com.guimaker.list.myList;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.application.DialogWindow;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListElementInitializer;
import com.guimaker.model.ParentListData;

import javax.swing.*;
import java.util.Collection;

public class ListConfiguration<Word extends ListElement> {

	private boolean enableWordAdding = true;
	private boolean inheritScrollbar = false;
	private boolean showButtonsLoadNextPreviousWords = true;
	private boolean enableWordSearching = true;
	private boolean skipTitle = false;
	private boolean scrollBarSizeFittingContent = false;
	private PanelDisplayMode displayMode = PanelDisplayMode.EDIT;
	private AbstractButton[] additionalNavigationButtons = new AbstractButton[] {};
	private ListInputsSelectionManager allInputsSelectionManager;
	private ParentListData<?, Word> parentListAndWordContainingThisList;
	private String wordSpecificDeletePrompt;
	private ListRowCreator<Word> listRowCreator;
	private ListElementInitializer<Word> listElementInitializer;
	private String title;
	private DialogWindow dialogWindow;
	private ApplicationChangesManager applicationChangesManager;

	public ListConfiguration(String wordSpecificDeletePrompt,
			ListRowCreator<Word> listRowCreator,
			ListElementInitializer<Word> listElementInitializer, String title,
			DialogWindow dialogWindow,
			ApplicationChangesManager applicationChangesManager) {
		//TODO provide method in listElement: deletePrompt() that returns a
		// string displayed while trying to delete the specific word
		this.wordSpecificDeletePrompt = wordSpecificDeletePrompt;
		this.listRowCreator = listRowCreator;
		this.listElementInitializer = listElementInitializer;
		this.title = title;
		this.dialogWindow = dialogWindow;
		this.applicationChangesManager = applicationChangesManager;
	}

	public ListRowCreator<Word> getListRowCreator() {
		return listRowCreator;
	}

	public ListElementInitializer<Word> getListElementInitializer() {
		return listElementInitializer;
	}

	public String getTitle() {
		return title;
	}

	public DialogWindow getDialogWindow() {
		return dialogWindow;
	}

	public ApplicationChangesManager getApplicationChangesManager() {
		return applicationChangesManager;
	}

	public String getWordSpecificDeletePrompt() {
		return wordSpecificDeletePrompt;
	}

	public PanelDisplayMode getDisplayMode() {
		return displayMode;
	}

	public ListConfiguration<Word> displayMode(PanelDisplayMode displayMode) {
		this.displayMode = displayMode;
		return this;
	}

	public ParentListData<?, Word> getParentListAndWordContainingThisList() {
		return parentListAndWordContainingThisList;
	}

	public <ParentWordType extends ListElement> ListConfiguration<Word> parentListAndWordContainingThisList(
			MyList<ParentWordType> parentList, ParentWordType parentWord,
			Collection<Word> childListRepresentationAsCollection) {
		parentListAndWordContainingThisList = new ParentListData<>(parentList,
				parentWord, childListRepresentationAsCollection);
		return this;
	}

	public boolean isSkipTitle() {
		return skipTitle;
	}

	public ListConfiguration<Word> skipTitle(boolean skipTitle) {
		this.skipTitle = skipTitle;
		return this;
	}

	public ListInputsSelectionManager getAllInputsSelectionManager() {
		return allInputsSelectionManager;
	}

	public ListConfiguration<Word> allInputsSelectionManager(
			ListInputsSelectionManager allInputsSelectionManager) {
		this.allInputsSelectionManager = allInputsSelectionManager;
		return this;
	}

	public boolean isWordSearchingEnabled() {
		return enableWordSearching;
	}

	public ListConfiguration<Word> enableWordSearching(
			boolean enableWordSearching) {
		this.enableWordSearching = enableWordSearching;
		return this;
	}

	public boolean isWordAddingEnabled() {
		return enableWordAdding;
	}

	public ListConfiguration<Word> enableWordAdding(boolean enableWordAdding) {
		this.enableWordAdding = enableWordAdding;
		return this;
	}

	public boolean isScrollBarInherited() {
		return inheritScrollbar;
	}

	public ListConfiguration<Word> inheritScrollbar(boolean inheritScrollbar) {
		this.inheritScrollbar = inheritScrollbar;
		return this;
	}

	public ListConfiguration<Word> scrollBarFitsContent(boolean fitsContent) {
		this.scrollBarSizeFittingContent = fitsContent;
		return this;
	}

	public boolean isShowButtonsLoadNextPreviousWords() {
		return showButtonsLoadNextPreviousWords;
	}

	public ListConfiguration<Word> showButtonsLoadNextPreviousWords(
			boolean showButtonsLoadNextPreviousWords) {
		this.showButtonsLoadNextPreviousWords = showButtonsLoadNextPreviousWords;
		return this;
	}

	public ListConfiguration<Word> withAdditionalNavigationButtons(
			AbstractButton... buttons) {
		this.additionalNavigationButtons = buttons;
		return this;
	}

	public AbstractButton[] getAdditionalNavigationButtons() {
		return additionalNavigationButtons;
	}

	public boolean isScrollBarSizeFittingContent() {
		return scrollBarSizeFittingContent;
	}
}
