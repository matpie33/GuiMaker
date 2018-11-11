package com.guimaker.list.myList.filtering;

import com.guimaker.list.myList.ListWordsController;

import java.util.Timer;
import java.util.TimerTask;

public class ListFilteringScheduler {

	private Timer timer;
	private final int millisecondsAfterWhichFilteringIsTriggered = 500;
	private ListFilteringController listFilteringController;

	public ListFilteringScheduler(ListFilteringController listFilteringController) {
		this.listFilteringController = listFilteringController;
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
				listFilteringController.filterWords();
			}
		}, millisecondsAfterWhichFilteringIsTriggered);
	}
}
