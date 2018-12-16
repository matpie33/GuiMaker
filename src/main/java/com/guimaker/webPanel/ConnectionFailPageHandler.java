package com.guimaker.webPanel;

import com.guimaker.model.WebContext;

import javax.swing.*;

public interface ConnectionFailPageHandler {

	public JPanel getConnectionFailPage();

	public void modifyConnectionFailPage(WebContext context);
}
