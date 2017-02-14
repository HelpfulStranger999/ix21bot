package com.gmail.helpfulstranger999.ix21bot;

import org.jibble.pircbot.*;
import static java.util.concurrent.TimeUnit.*;
import static java.lang.System.out;
import java.sql.*;

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
			try {
				IX21BotMain.commandsquery.close();
				IX21BotMain.usersquery.close();
				IX21BotMain.commandsconn.close();
				IX21BotMain.usersconn.close();
			} catch (SQLException e) {
				out.println("Error Closing Database: ");
				e.printStackTrace();
				System.exit(25);
			}
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
				try {
					MINUTES.sleep(5);
				} catch (InterruptedException e1) {
					out.println("ERROR Shutting down!");
					e1.printStackTrace();
				}
				try {
					this.reconnect();
				} catch (NickAlreadyInUseException e1) {
					out.println("Nickname already in use! Please try again.");
					e1.printStackTrace();
					out.println("Exiting program");
					try {
						IX21BotMain.commandsquery.close();
						IX21BotMain.usersquery.close();
						IX21BotMain.commandsconn.close();
						IX21BotMain.usersconn.close();
					} catch (SQLException e2) {
						out.println("Error Closing Database: ");
						e2.printStackTrace();
						System.exit(25);
					}
					System.exit(ERR_NICKNAMEINUSE);
				} catch (Exception e1) {
					out.println("Another error happened while reconnecting: ");
					e1.printStackTrace();
					out.println("Exiting program");
					//23 = Error reconnecting
					try {
						IX21BotMain.commandsquery.close();
						IX21BotMain.usersquery.close();
						IX21BotMain.commandsconn.close();
						IX21BotMain.usersconn.close();
					} catch (SQLException e3) {
						out.println("Error Closing Database: ");
						e3.printStackTrace();
						System.exit(25);
					}
					System.exit(23);
				}
			}
		}
	}
}
