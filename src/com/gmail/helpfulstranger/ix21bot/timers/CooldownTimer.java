package com.gmail.helpfulstranger.ix21bot.timers;

import java.util.TimerTask;

import com.gmail.helpfulstranger999.ix21bot.Commands;

import static com.gmail.helpfulstranger999.ix21bot.IX21Bot.bot;

public class CooldownTimer extends TimerTask {
	
	public CooldownTimer() {
		super();
	}

	@Override
	public void run() {
		Commands.cooldown = false;
		bot.send().message("#helpfulstranger999", "You are now thoroughly refreshed and ready to set forth on another expedition for scarabs");
	}

}
