package com.gmail.helpfulstranger999.ix21bot;

import org.jibble.pircbot.*;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.lang.System.out;

public class IX21Bot_V2 extends PircBot {

	String[] commands;
	boolean safeshutdown = false;
	
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
			safeshutdown = true;
			this.disconnect();
			safeshutdown = false;
			System.out.println("Disconnected Successfully!");
			System.exit(0);
		}
	}
	
	public void onConnect () {
		sendMessage("#helpfulstranger999", "The bot is connected and running. SeemsGood");
	}
	
	public void onDisconnect () {
		if (!safeshutdown)  {
			try {
				this.reconnect();
			} catch (NickAlreadyInUseException e) {
				out.println("Nickname already in use! Please try again.");
				e.printStackTrace();
			} catch (Exception e) {
				out.println("An error happened while reconnecting: ");
				e.printStackTrace();
			}
		}
	}
}
