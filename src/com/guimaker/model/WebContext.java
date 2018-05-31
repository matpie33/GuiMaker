package com.guimaker.model;

public class WebContext {

	private String content;
	private String noContentMessage;

	public WebContext(String content, String noContentMessage) {
		this.content = content;
		this.noContentMessage = noContentMessage;
	}

	public String getContent() {
		return content;
	}

	public String getNoContentMessage() {
		return noContentMessage;
	}

	public boolean isEmpty (){
		return content.isEmpty();
	}

}
