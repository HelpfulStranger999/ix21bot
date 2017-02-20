package com.gmail.helpfulstranger999.ix21bot;

import org.pircbotx.hooks.events.*;

import static java.lang.System.out;
import java.sql.*;
import static com.gmail.helpfulstranger999.ix21bot.IX21Bot.isAuth;

public class Commands {

	private static String noauth = "You do not have the authorization to perform this command. If you have been recently modded, try reloading the page. If you should have such privileges, please inform the streamer";
	
	public static void disconnectBot (PrivateMessageEvent event) {
		try {
			IX21Bot.shutdown = true;
			event.respondWith("Disconnecting Bot now....");
			Thread.sleep(1000);
			IX21Bot.bot.sendRaw().rawLineNow("QUIT :");
		} catch (InterruptedException e) {
			out.println("InterrupedException thrown while shutting down gracefully: ");
			e.printStackTrace();
		}
		try {
			IX21Bot.commandsquery.close();
			IX21Bot.usersquery.close();
			IX21Bot.commandsconn.close();
			IX21Bot.usersconn.close();
		} catch (SQLException e) {
			out.println("Error Closing Database: ");
			e.printStackTrace();
			System.exit(25);
		}
	}

	public static void command(MessageEvent event) {
		if (event.getMessage().trim().equals("!commands")) {
			// TODO Command printing
		} else if(event.getMessage().startsWith("!commands add")) {
			if(isAuth(event.getTags())) {
				// TODO Command adding processing
			} else {
				event.respondPrivateMessage(noauth);
			}
		} else if (event.getMessage().startsWith("!commands delete")) {
			if(isAuth(event.getTags())) {
				// TODO Command Removing processing
			} else {
				event.respondPrivateMessage(noauth);
			}
		} else if (event.getMessage().startsWith("!commands edit")) {
			if(isAuth(event.getTags())) {
				// TODO Command Editing processing
			} else {
				event.respondPrivateMessage(noauth);
			}
		}
	}
}
