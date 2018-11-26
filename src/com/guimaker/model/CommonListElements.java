package com.guimaker.model;

import com.guimaker.list.ListElement;
import com.guimaker.list.myList.MyList;

import javax.swing.*;
import java.awt.*;

public class CommonListElements<Word extends ListElement> {

	private AbstractButton finishEditing;
	private AbstractButton buttonDelete;
	private AbstractButton buttonEdit;
	private JLabel rowNumberLabel;
	private AbstractButton buttonAddRow;
	private Color labelsColor;
	private boolean forSingleRowOnly;
	private MyList<Word> list;

	public CommonListElements(AbstractButton buttonDelete,
			JLabel rowNumberLabel, AbstractButton buttonAddRow,
			Color labelsColor, AbstractButton buttonEdit,
			AbstractButton finishEditing, boolean forSingleRowOnly,
			MyList<Word> myList) {
		this.buttonDelete = buttonDelete;
		this.rowNumberLabel = rowNumberLabel;
		this.buttonAddRow = buttonAddRow;
		this.labelsColor = labelsColor;
		this.forSingleRowOnly = forSingleRowOnly;
		this.buttonEdit = buttonEdit;
		this.finishEditing = finishEditing;
		this.list = myList;
	}

	public AbstractButton getButtonEdit() {
		return buttonEdit;
	}

	public boolean isForSingleRowOnly() {
		return forSingleRowOnly;
	}

	public Color getLabelsColor() {
		return labelsColor;
	}

	public AbstractButton getButtonDelete() {
		return buttonDelete;
	}

	public JLabel getRowNumberLabel() {
		return rowNumberLabel;
	}

	public AbstractButton getButtonAddRow() {
		return buttonAddRow;
	}

	public AbstractButton getFinishEditing() {
		return finishEditing;
	}

	public MyList<Word> getList() {
		return list;
	}

	public static <Word extends ListElement> CommonListElements forSingleRowOnly(
			Color labelsColor, MyList<Word> myList) {
		return new CommonListElements(null, null, null, labelsColor, null, null,
				true, myList);
	}

}
