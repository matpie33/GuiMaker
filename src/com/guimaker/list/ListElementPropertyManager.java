package com.guimaker.list;

import javax.swing.text.JTextComponent;

public interface ListElementPropertyManager<PropertyType, PropertyHolder extends ListElement> {

	public String getInvalidPropertyReason();

	public boolean isPropertyFound(PropertyType property,
			PropertyHolder propertyHolder);

	public String getPropertyValue(PropertyHolder propertyHolder);

	public PropertyType validateInputAndConvertToProperty(
			JTextComponent textInput, PropertyHolder propertyHolder);

	public void setProperty(PropertyHolder propertyHolder,
			PropertyType newValue, PropertyType previousValue);

	public String getPropertyDefinedException(PropertyType property);

}
