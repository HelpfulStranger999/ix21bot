package com.gmail.helpfulstranger999.ix21bot;

import java.io.IOException;
import java.sql.*;

import org.pircbotx.*;
import org.pircbotx.cap.*;
import org.pircbotx.exception.*;
import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.*;

import com.google.common.collect.ImmutableMap;


public class IX21Bot extends ListenerAdapter implements Listener {
	
	public static PircBotX bot = null;
	public static boolean shutdown = false;
	
	public static Connection commandsconn = null;
	public static Connection usersconn = null;
	public static Statement commandsquery = null;
	public static Statement usersquery = null;
	public static String sql = null;

	public IX21Bot() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Configuration config = new Configuration.Builder().setAutoNickChange(false)
				.setOnJoinWhoEnabled(false).setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("twitch.tv/membership"))
				.addCapHandler(new EnableCapHandler("twitch.tv/tags"))
				.addServer("irc.twitch.tv").setName("anonymoususer_1234")
				.setServerPassword("oauth:bvolwngwvomszb1ubj90ghc9y736xc")
				.addAutoJoinChannel("#helpfulstranger999")
				.addListener(new IX21Bot())
				.addListener(new MessageListener())
				.buildConfiguration();
		
		bot = new PircBotX(config);
		try {
			bot.startBot();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IrcException e) {
			
			e.printStackTrace();
		}
		
		try {
			Class.forName("org.sqlite.JDBC");
			commandsconn = DriverManager.getConnection("jdbc:sqlite:commands.db");
			commandsquery = commandsconn.createStatement();
			
			usersconn = DriverManager.getConnection("jdbc:sqlite:users.db");
			usersquery = usersconn.createStatement();
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			//25 = SQLite Exception
			System.exit(25);
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found while creating SQL Connection");
			e.printStackTrace();
		}
		sql = "CREATE TABLE IF NOT EXISTS COMMANDS " +
				"(ID INTEGER PRIMARY KEY    AUTOINCREMENT," +
				" TRIGGER           TEXT    NOT NULL" +
				" RESPONE            TEXT NOT NULL)"; 
		
	}
	
	@Override
	public void onJoin (JoinEvent event) throws Exception {
		if(event.getUser().equals(bot.getUserBot())) {
			event.getChannel().send().message("IX21Bot is now connected.");
		}
	}
	
	public static boolean isAuth (ImmutableMap<String, String> map) {
		String badge = map.get("badges");
		if (badge.contains("moderator/1")) {
			return true;
		} else if(badge.contains("broadcaster/1")) {
			return true;
		} else if (badge.contains("staff/1")) {
			return true;
		} else if (badge.contains("admin/1")) {
			return true;
		} else if (badge.contains("global_mod/1")) {
			return true;
		} else {
			return false;
		}
	}

}
