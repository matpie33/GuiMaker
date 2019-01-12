package com.guimaker.webPanel;

import com.guimaker.application.ApplicationWindow;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.model.HotkeyWrapper;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.strings.ButtonsNames;
import com.guimaker.strings.Prompts;
import com.guimaker.utilities.CommonActionsCreator;
import com.guimaker.utilities.ElementCopier;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WebPagePanel {

	private JFXPanel webPage;
	private WebView webView;
	private JPanel switchingPanel;
	private MainPanel messagePanel;
	private ChangeListener connectionChange;
	private final String MESSAGE_PANEL = "MESSAGE PANEL";
	private final String WEB_PAGE_PANEL = "WEB PAGE PANEL";
	private final String CONNECTION_FAIL_PANEL = "CONNECTION FAIL PANEL";
	private JTextComponent messageComponent;
	private JPanel connectionFailPanel;
	private ContextOwner contextOwner;
	private boolean shouldGrabFocusOnReload;
	private AbstractButton reloadButton;
	private String currentlyLoadingPage;
	private ApplicationWindow applicationWindow;
	private final String JAVASCRIPT_ONLOAD = "window.onload=function(){%s};";

	//TODO it's too coupled to kanji context, should be more generic
	public WebPagePanel(ContextOwner contextOwner,
			ConnectionFailPageHandler connectionFailPageHandler,
			ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
		this.contextOwner = contextOwner;
		webPage = new JFXPanel();
		switchingPanel = new JPanel(new CardLayout());
		createButtonReload();
		if (connectionFailPageHandler == null) {
			connectionFailPageHandler = new ConnectionFailMessagePage(
					ElementCopier.copyButton(reloadButton));
		}
		initiateConnectionFailListener(connectionFailPageHandler);
		initiatePanels();
		shouldGrabFocusOnReload = true;
	}

	private void initiateConnectionFailListener(
			ConnectionFailPageHandler connectionFailPageHandler) {
		connectionFailPanel = connectionFailPageHandler.getConnectionFailPage();
		Platform.setImplicitExit(false);
		connectionChange = new ChangeListener<Worker.State>() {
			@Override
			public void changed(
					ObservableValue<? extends Worker.State> observable,
					Worker.State oldValue, final Worker.State newValue) {
				if (newValue == Worker.State.FAILED) {
					connectionFailPageHandler.modifyConnectionFailPage(
							contextOwner.getContext());
					showPanel(CONNECTION_FAIL_PANEL);
					shouldGrabFocusOnReload = true;
				}
				if (newValue == Worker.State.SUCCEEDED) {
					showPanel(WEB_PAGE_PANEL);
					if (shouldGrabFocusOnReload) {
						webPage.requestFocusInWindow();
					}
					shouldGrabFocusOnReload = true;
				}
			}
		};
	}

	public void addJavaObjectReferenceForJavascript(Object o) {
		PlatformImpl.runLater(() -> {
			JSObject jsObject = (JSObject) webView.getEngine()
												  .executeScript("window");
			jsObject.setMember(o.getClass()
								.getSimpleName(), o);
		});

	}

	public void addHotkey(HotkeyWrapper hotkey, AbstractAction action) {
		CommonActionsCreator.addHotkey(hotkey, action, getSwitchingPanel());
	}

	public void executeJavascript(String javascript) {
		PlatformImpl.runLater(() -> webView.getEngine()
										   .executeScript(javascript));
	}

	public void addJavascript(String javascript) {
		PlatformImpl.runLater(() -> webView.getEngine()
										   .executeScript(String.format(
												   JAVASCRIPT_ONLOAD,
												   javascript)));

	}

	private void showPanel(String panel) {
		((CardLayout) switchingPanel.getLayout()).show(switchingPanel, panel);
	}

	private void initiatePanels() {
		messagePanel = new MainPanel(new PanelConfiguration().setColorToUse(
				applicationWindow.getApplicationConfiguration()
								 .getContentPanelColor()));
		messageComponent = GuiElementsCreator.createTextPane(
				new TextPaneOptions().
											 text(Prompts.LOADING_PAGE)
									 .fontSize(20)
									 .textAlignment(TextAlignment.CENTERED)
									 .editable(false));

		messagePanel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				messageComponent));
		messagePanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER,
						reloadButton));

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webView = new WebView();

				StackPane pane = new StackPane(webView);
				webView.getEngine()
					   .getLoadWorker()
					   .stateProperty()
					   .addListener(connectionChange);
				webView.getEngine()
					   .getLoadWorker()
					   .exceptionProperty()
					   .addListener((obs, oldExc, newExc) -> {
						   if (newExc != null) { newExc.printStackTrace();}
					   });

				webPage.setScene(new Scene(pane));

				switchingPanel.add(MESSAGE_PANEL, messagePanel.getPanel());
				switchingPanel.add(WEB_PAGE_PANEL, webPage);
				switchingPanel.add(CONNECTION_FAIL_PANEL, connectionFailPanel);
			}
		});

	}

	private void createButtonReload() {
		reloadButton = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(
						ButtonsNames.RELOAD_PAGE), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showPage(currentlyLoadingPage);
					}
				});

	}

	public void showPage(String url) {
		showPanel(MESSAGE_PANEL);
		currentlyLoadingPage = url;
		Platform.runLater(() -> webView.getEngine()
									   .load(url));
	}

	public JFXPanel getWebPanel() {
		return webPage;
	}

	public JPanel getSwitchingPanel() {
		return switchingPanel;
	}

	public void showPageWithoutGrabbingFocus(String url) {
		shouldGrabFocusOnReload = false;
		showPage(url);
	}

}
