package com.guimaker.options;

import java.awt.Component;

public class ScrollPaneOptions extends AbstractComponentOptions<ScrollPaneOptions> {

	private Component componentToWrap;
	private int unitIncrement = 10;

	public Component getComponentToWrap() {
		return componentToWrap;
	}

	public int getUnitIncrement() {
		return unitIncrement;
	}

	public ScrollPaneOptions componentToWrap(Component component) {
		this.componentToWrap = component;
		return getThis();
	}

	public ScrollPaneOptions unitIncrement(int unitIncrement) {
		this.unitIncrement = unitIncrement;
		return getThis();
	}

	@Override
	public ScrollPaneOptions getThis() {
		return this;
	}

}
