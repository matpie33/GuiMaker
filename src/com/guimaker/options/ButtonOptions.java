package com.guimaker.options;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.ButtonType;

import java.awt.*;

public class ButtonOptions extends AbstractComponentOptions<ButtonOptions> {

	private ButtonType buttonType;
	private final Color backgroundColor = BasicColors.SUPER_BLUE;

	public ButtonOptions (ButtonType buttonType){
		opaque(false);
		if (buttonType.equals(ButtonType.BUTTON)){
			backgroundColor(backgroundColor);
		}
		this.buttonType = buttonType;
	}

	public ButtonType getButtonType() {
		return buttonType;
	}

	@Override
	public ButtonOptions getThis() {
		return this;
	}
}
