package com.gmail.helpfulstranger999.ix21bot;

import org.jibble.pircbot.*;
import static java.util.concurrent.TimeUnit.MICROSECONDS;

public class IX21Bot_V2 extends PircBot {

	String[] commands;
	
	public IX21Bot_V2 () {
		this.setName("anonymoususer_1234");
		this.setAutoNickChange(true);
	}
	
	public void onMessage (String channel, String sender, String login, String hostname, String message) {
		if(sender.equals("helpfulstranger999") && message.equals("!disconnectbot")) {
			sendMessage(channel, "Disconnecting bot....");
			try {
				MICROSECONDS.sleep(1);
			} catch (InterruptedException e) {
				sendMessage(channel, "ERROR Shutting down! See console for more details.");
				e.printStackTrace();
			}
			this.disconnect();
			System.out.println("Disconnected Successfully!");
			System.exit(0);
		}
	}
	
	public void onConnect () {
		sendMessage("#helpfulstranger999", "The bot is connected and running. SeemsGood");
	}
}
