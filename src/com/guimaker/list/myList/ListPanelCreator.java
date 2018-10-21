package com.guimaker.list.myList;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.application.ApplicationWindow;
import com.guimaker.application.DialogWindow;
import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListRowData;
import com.guimaker.model.ListRow;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.strings.ButtonsNames;
import com.guimaker.strings.HotkeysDescriptions;
import com.guimaker.utilities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ListPanelCreator<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private ListInputsSelectionManager listInputsSelectionManager;
	private ListWordsController<Word> listWordsController;
	private MainPanel rowsPanel;
	private JScrollPane parentScrollPane;
	private final Dimension scrollPanesSize = new Dimension(550, 100);
	private JLabel titleLabel;
	private ListRowCreator<Word> listRow;
	private ApplicationChangesManager applicationChangesManager;
	private boolean enableWordAdding;
	private AbstractButton buttonLoadNextWords;
	private AbstractButton buttonLoadPreviousWords;
	private LoadNextWordsHandler loadNextWordsHandler;
	private LoadPreviousWordsHandler loadPreviousWordsHandler;
	private List<AbstractButton> navigationButtons;
	private JComponent listElementsPanel;
	private boolean isScrollBarInherited;
	private boolean enableWordSearching;
	private boolean showButtonsNextAndPrevious;
	private boolean isSkipTitle;
	private Color labelsColor = Color.WHITE;
	private boolean scrollBarSizeFittingContent;
	private InputGoal inputGoal;
	private boolean hasParentList;
	private ListSearchPanelCreator<Word> listSearchPanelCreator;
	private final static String UNIQUE_NAME = "list panel creator";
	private MyList myList;
	private boolean isInitialized = false;
	private MainPanel filterPanel;

	public ListPanelCreator(ListConfiguration listConfiguration,
			ApplicationChangesManager applicationChangesManager,
			ListRowCreator<Word> listRow, ListWordsController<Word> controller,
			MyList<Word> myList) {
		mainPanel.setRowsBorder(null);
		filterPanel = new MainPanel();
		filterPanel.setGapsBetweenRowsTo0();
		filterPanel.setRowsBorder(getDefaultBorder());
		this.myList = myList;
		listSearchPanelCreator = new ListSearchPanelCreator<>();
		this.applicationChangesManager = applicationChangesManager;
		listWordsController = controller;
		isSkipTitle = listConfiguration.isSkipTitle();
		hasParentList =
				listConfiguration.getParentListAndWordContainingThisList()
						!= null;

		Color contentColor = BasicColors.PURPLE_DARK_1;
		rowsPanel = new MainPanel(
				new PanelConfiguration().setColorToUse(contentColor)
						.setPanelDisplayMode(listConfiguration.getDisplayMode())
						.putRowsAsHighestAsPossible());
		setParentDialog(applicationChangesManager.getApplicationWindow());
		if (hasParentList) {
			mainPanel.setRowColor(ColorChanger.makeLighter(contentColor));
			mainPanel.setBackgroundColor(null);
			rowsPanel.setWrappingPanelBorder(getDefaultBorder());
		}

		titleLabel = GuiElementsCreator.createLabel(new ComponentOptions());
		loadNextWordsHandler = new LoadNextWordsHandler(listWordsController,
				rowsPanel);
		loadPreviousWordsHandler = new LoadPreviousWordsHandler(
				listWordsController, rowsPanel);
		this.listRow = listRow;

		navigationButtons = new ArrayList<>();
		unwrapConfiguration(listConfiguration);
		listInputsSelectionManager = listConfiguration
				.getAllInputsSelectionManager();
		addElementsForEmptyList();
		createButtonsShowNextAndPrevious();
		initializeNavigationButtons();
	}

	private void initializeNavigationButtons() {
		if (enableWordAdding) {
			navigationButtons.add(createButtonAddWord());
		}
		setNavigationButtons(
				navigationButtons.toArray(new AbstractButton[] {}));
	}

	public void addElementsForEmptyList() {
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				GuiElementsCreator.createLabel(new ComponentOptions()
						.text(com.guimaker.strings.Prompts.EMPTY_LIST)),
				createButtonAddRow(InputGoal.EDIT))
				.setBorder(getDefaultBorder()));
	}

	private void unwrapConfiguration(ListConfiguration listConfiguration) {
		this.enableWordAdding = listConfiguration.isWordAddingEnabled();
		this.isScrollBarInherited = listConfiguration.isScrollBarInherited();
		this.enableWordSearching = listConfiguration.isWordSearchingEnabled();
		showButtonsNextAndPrevious = listConfiguration
				.isShowButtonsLoadNextPreviousWords();
		scrollBarSizeFittingContent = listConfiguration
				.isScrollBarSizeFittingContent();
		addNavigationButtons(
				listConfiguration.getAdditionalNavigationButtons());
		//TODO redundant code - keep reference to list configuration instead of keep all the params in this class
	}

	public void inheritScrollPane() {
		isScrollBarInherited = false;
	}

	private void addNavigationButtons(AbstractButton... buttons) {
		for (AbstractButton button : buttons) {
			navigationButtons.add(button);
		}
	}

	public LoadNextWordsHandler getLoadNextWordsHandler() {
		return loadNextWordsHandler;
	}

	public LoadPreviousWordsHandler getLoadPreviousWordsHandler() {
		return loadPreviousWordsHandler;
	}

	private void createButtonsShowNextAndPrevious() {
		buttonLoadNextWords = createAndAddButtonLoadWords(
				ButtonsNames.SHOW_NEXT_WORDS_ON_LIST);
		buttonLoadPreviousWords = createAndAddButtonLoadWords(
				ButtonsNames.SHOW_PREVIOUS_WORDS_ON_LIST);
		buttonLoadNextWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadNextWordsHandler));
		buttonLoadPreviousWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadPreviousWordsHandler));
		if (!showButtonsNextAndPrevious) {
			buttonLoadPreviousWords.setVisible(false);
			buttonLoadNextWords.setVisible(false);
		}
	}

	private AbstractButton createAndAddButtonLoadWords(String buttonName) {
		AbstractButton button = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(buttonName), null);
		button.setEnabled(false);
		return button;
	}

	public ListRow<Word> addRow(Word word, int rowNumber,
			boolean shouldShowWord, LoadWordsHandler loadWordsHandler,
			InputGoal inputGoal) {
		if (!isInitialized) {
			createElements();
		}
		this.inputGoal = inputGoal;
		CommonListElements commonListElements = createCommonListElements(word,
				inputGoal, rowNumber);
		MainPanel rowPanel = null;
		if (shouldShowWord) {
			ListRowData<Word> listRow = this.listRow
					.createListRow(word, commonListElements, inputGoal);
			rowPanel = listRow.getRowPanel();
			AbstractSimpleRow abstractSimpleRow = SimpleRowBuilder
					.createRow(FillType.HORIZONTAL, Anchor.NORTH,
							rowPanel.getPanel());
			loadWordsHandler.showWord(abstractSimpleRow);
		}
		else if (!buttonLoadNextWords.isEnabled()) {
			buttonLoadNextWords.setEnabled(true);
		}
		if (rowPanel != null && listInputsSelectionManager != null) {
			rowPanel.addManager(listInputsSelectionManager);
		}
		rowsPanel.updateView();
		return new ListRow<>(word, rowPanel,
				commonListElements.getRowNumberLabel(), rowNumber);
	}

	private CommonListElements createCommonListElements(Word word,
			InputGoal inputGoal, int rowNumber) {
		JLabel rowNumberLabel = new JLabel(createTextForRowNumber(rowNumber));
		rowNumberLabel.setForeground(labelsColor);
		AbstractButton remove = createButtonRemoveWord(word);
		AbstractButton addNewWord = createButtonAddRow(inputGoal);
		AbstractButton editWord = createButtonEditWord(word);
		AbstractButton finishEditing = createButtonFinishEditing(word);
		return new CommonListElements(remove, rowNumberLabel, addNewWord,
				labelsColor, editWord, finishEditing, false);

	}

	private AbstractButton createButtonRemoveWord(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.REMOVE_ROW),
				listWordsController.createDeleteRowAction(word));
	}

	private AbstractButton createButtonAddRow(InputGoal inputGoal) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(ButtonsNames.ADD_ROW),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						listWordsController.addNewWord(inputGoal);
					}
				});
	}

	private AbstractAction createButtonShowNextOrPreviousWords(
			LoadWordsHandler loadWordsHandler) {

		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int numberOfAddedWords = listWordsController
						.addNextHalfOfMaximumWords(loadWordsHandler);
				if (numberOfAddedWords > 0) {
					removeWordsFromRangeInclusive(loadWordsHandler
							.getRangeOfWordsToRemove(numberOfAddedWords));
				}
				boolean hasMoreWordsToShow = numberOfAddedWords == Math
						.ceil((double) listWordsController
								.getMaximumWordsToShow() / 2);
				loadWordsHandler
						.enableOrDisableLoadWordsButtons(buttonLoadNextWords,
								buttonLoadPreviousWords, hasMoreWordsToShow);
				rowsPanel.updateView();
			}
		};

	}

	public String createTextForRowNumber(int rowNumber) {
		return "" + rowNumber + ". ";
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	private void createMainPanelElements() {
		if (!isSkipTitle) {
			mainPanel.addRow(SimpleRowBuilder
					.createRow(FillType.NONE, Anchor.CENTER, titleLabel));
		}
		if (filterPanel.getNumberOfRows() > 0) {
			mainPanel.addRow(SimpleRowBuilder
					.createRow(FillType.HORIZONTAL, filterPanel.getPanel()));
		}
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, buttonLoadPreviousWords));
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, listElementsPanel));
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, buttonLoadNextWords));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				navigationButtons.toArray(new JComponent[] {})));

	}

	@Override
	public void setParentDialog(DialogWindow dialog) {
		super.setParentDialog(dialog);
		mainPanel.setBackgroundColor(
				dialog.getParentConfiguration().getContentPanelColor());
		filterPanel.setRowColor(BasicColors.GREEN_BRIGHT_1);
	}

	@Override
	public void createElements() {

		mainPanel.removeRow(0);
		isInitialized = true;
		createRootPanel();

		if (enableWordSearching) {
			ListRowData<Word> listRow = this.listRow.createListRow(
					listWordsController.getWordInitializer()
							.initializeElement(),
					CommonListElements.forSingleRowOnly(Color.WHITE),
					InputGoal.SEARCH);
			if (!listRow.isEmpty()) {

				JPanel panel = listSearchPanelCreator.createPanel(listRow,
						createButtonFilter(listSearchPanelCreator));
				filterPanel.addRow(SimpleRowBuilder
						.createRow(FillType.HORIZONTAL, Anchor.WEST, panel));
				addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_SPACE,
						listSearchPanelCreator
								.createActionSwitchComboboxValue(),
						mainPanel.getPanel(),
						HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
				addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_F,
						new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								listSearchPanelCreator.getFilteringInput()
										.requestFocusInWindow();
							}
						}, mainPanel.getPanel(),
						HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
			}
		}

		if (!enableWordSearching && !enableWordAdding) {
			mainPanel.getPanel().setOpaque(false);
		}

		createMainPanelElements();

	}

	private AbstractButton createButtonFilter(
			ListSearchPanelCreator<Word> listSearchPanelCreator) {
		AbstractButton filterButton = GuiElementsCreator
				.createButtonLikeComponent(new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.FILTER));
		AbstractAction action = listWordsController
				.createFilterAction(listSearchPanelCreator, filterButton);
		addHotkey(KeyEvent.VK_ENTER, action, getPanel(),
				HotkeysDescriptions.FILTER_WORDS);
		filterButton.addActionListener(action);
		return filterButton;
	}

	private void createRootPanel() {
		if (!isScrollBarInherited) {
			parentScrollPane = GuiElementsCreator.createScrollPane(
					new ScrollPaneOptions().opaque(false)
							.componentToWrap(rowsPanel.getPanel())
							.border(getDefaultBorder()));
			if (!scrollBarSizeFittingContent) {
				parentScrollPane.setPreferredSize(scrollPanesSize);
			}
			listElementsPanel = parentScrollPane;
		}
		else {
			listElementsPanel = rowsPanel.getPanel();
		}

	}

	public void removeWordsFromRangeInclusive(Range range) {
		rowsPanel.removeRowsInclusive(range.getRangeStart(),
				range.getRangeEnd());
	}

	private AbstractButton createButtonFinishEditing(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.FINISH_EDITING),
				listWordsController.createFinishEditAction(word),
				new HotkeyWrapper(KeyEvent.VK_ENTER));
	}

	private AbstractButton createButtonEditWord(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(ButtonsNames.EDIT),
				listWordsController.createEditWordAction(word));
	}

	private AbstractButton createButtonAddWord() {
		String name = ButtonsNames.ADD;
		String hotkeyDescription = HotkeysDescriptions.ADD_WORD;
		int keyEvent = KeyEvent.VK_I;
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ApplicationWindow applicationWindow = applicationChangesManager
						.getApplicationWindow();
				applicationWindow.showInsertWordDialog(myList,
						applicationWindow.getApplicationConfiguration().
								getInsertWordPanelPositioner());
			}
		};
		//TODO add in my list a parameter with hotkeys mapping for add/search panels
		return createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent, action,
				name, hotkeyDescription);

	}

	public void clearHighlightedRow(JComponent row) {
		rowsPanel.clearPanelColor(row);
	}

	public void highlightRowAndScroll(JComponent row) {
		int rowNumber = rowsPanel.getIndexOfPanel(row);
		changePanelColor(rowNumber,
				applicationChangesManager.getApplicationWindow()
						.getApplicationConfiguration()
						.getListRowHighlightColor());
		scrollTo(rowsPanel.getRows().get(rowNumber));
		this.rowsPanel.getPanel().repaint();
	}

	private void changePanelColor(int rowNumber, Color color) {
		rowsPanel.setPanelColor(rowNumber, color);
	}

	public void scrollTo(JComponent panel) {
		if (isScrollBarInherited) {
			//TODO keep reference to the inherited scrollbar and use it to scroll
			return;
		}
		SwingUtilities.invokeLater(() -> {
			int r = panel.getY();
			this.parentScrollPane.getViewport()
					.setViewPosition(new Point(0, r));
		});
	}

	public void scrollToBottom() {
		if (isScrollBarInherited) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO swing utilities
				JScrollBar scrollBar = parentScrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});

	}

	public int removeRow(JComponent panel) {
		int rowNumber = rowsPanel.getIndexOfPanel(panel);
		rowsPanel.removeRow(rowNumber);
		return rowNumber;
	}

	public void clear() {
		rowsPanel.clear();
		createButtonsShowNextAndPrevious();
	}

	public void scrollToTop() {
		SwingUtilities.invokeLater(
				() -> parentScrollPane.getVerticalScrollBar().setValue(0));
	}

	public int getNumberOfListRows() {
		return rowsPanel.getNumberOfRows();
	}

	public void enableButtonShowPreviousWords() {
		buttonLoadPreviousWords.setEnabled(true);
	}

	public MainPanel getRowsPanel() {
		return rowsPanel;
	}

	public void toggleEnabledState() {
		rowsPanel.toggleEnabledState();
	}

	public MainPanel repaintWord(Word word, int rowNumber, JComponent oldPanel,
			InputGoal customInputGoal, boolean highlighted) {
		CommonListElements commonListElements = createCommonListElements(word,
				this.inputGoal, rowNumber);
		MainPanel newPanel = listRow.createListRow(word, commonListElements,
				customInputGoal == null ? this.inputGoal : customInputGoal)
				.getRowPanel();
		if (highlighted) {
			newPanel.setBackgroundColor(
					applicationChangesManager.getApplicationWindow()
							.getApplicationConfiguration()
							.getListRowHighlightColor());
		}
		if (customInputGoal != null && customInputGoal
				.equals(InputGoal.EDIT_TEMPORARILY)) {
			newPanel.setBackgroundColor(
					applicationChangesManager.getApplicationWindow()
							.getApplicationConfiguration()
							.getListRowEditTemporarilyColor());
			newPanel.updateView();
		}
		rowsPanel.replacePanel(oldPanel, newPanel.getPanel());
		rowsPanel.updateView();
		return newPanel;
	}

	public boolean isFilterInputFocused() {
		return listSearchPanelCreator.getFilteringInput().hasFocus();
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}
}
