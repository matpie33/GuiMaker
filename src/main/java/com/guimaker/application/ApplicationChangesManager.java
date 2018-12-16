package com.guimaker.application;

public interface ApplicationChangesManager {

	public boolean isClosingSafe();

	public void save();

	public ApplicationWindow getApplicationWindow();

}
