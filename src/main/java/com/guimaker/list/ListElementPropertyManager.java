package com.guimaker.list;


import javax.swing.text.JTextComponent;

public interface ListElementPropertyManager<PropertyType, PropertyHolder extends ListElement> {

	public String getInvalidPropertyReason();

	public boolean isPropertyFound(PropertyType property,
			PropertyHolder wordToCheck, PropertyHolder propertyHolder);

	public String getPropertyValue(PropertyHolder propertyHolder);

	public default boolean validateInput(
			JTextComponent textInput, PropertyHolder propertyHolder){
		return true;
	}

	public default PropertyType convertToProperty(JTextComponent input){
		return (PropertyType) input.getText();
	}

	public String getPropertyDefinedException(PropertyType property);

	public void setProperty(PropertyHolder propertyHolder,
			PropertyType newValue, PropertyType previousValue);

}
