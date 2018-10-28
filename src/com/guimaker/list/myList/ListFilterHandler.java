package com.guimaker.list.myList;

import java.util.Timer;
import java.util.TimerTask;

public class ListFilterHandler {

	private Timer timer;
	private final int millisecondsAfterWhichFilteringIsTriggered = 500;
	private ListWordsController listWordsController;

	public ListFilterHandler(ListWordsController listWordsController) {
		this.listWordsController = listWordsController;
	}

	public void scheduleFiltering() {
		if (timer != null){
			timer.cancel();
			timer.purge();
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				listWordsController.filterWords();
			}
		}, millisecondsAfterWhichFilteringIsTriggered);
	}
}
