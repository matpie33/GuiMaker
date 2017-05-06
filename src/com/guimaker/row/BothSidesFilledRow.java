package com.guimaker.row;

import javax.swing.JComponent;

public class BothSidesFilledRow extends HorizontallyFilledRow {

	public BothSidesFilledRow(JComponent[] components) {
		super(true, components);
	}
	
	public SimpleRow fillAllElementsBothSides(){
		return super.fillHorizontallyEqually().fillAllVertically();
	}
	

}
