package com.gmail.helpfulstranger999.ix21bot.pircbot;

import static java.lang.System.out;
import org.jibble.pircbot.*;
import java.sql.*;

@Deprecated
public class IX21BotMain {

	public static int port = 6667;
	public static Connection commandsconn = null;
	public static Connection usersconn = null;
	public static Statement commandsquery = null;
	public static Statement usersquery = null;
	
	public static void main(String[] args) {
		
		
		IX21Bot bot = new IX21Bot();
		bot.setVerbose(true);
		try {
			bot.connect("irc.twitch.tv", port, "oauth:7lfe5byck0k49g80lc4auryriywhdb");
		} catch (NickAlreadyInUseException e) {
			out.println("Nickname already in use! Please try again.");
			e.printStackTrace();
		} catch (Exception e) {
			out.println("An error happened: ");
			e.printStackTrace();
		}
		bot.joinChannel("#helpfulstranger999");
		
		try {
			Class.forName("org.sqlite.JDBC");
			commandsconn = DriverManager.getConnection("jdbc:sqlite:commands.db");
			commandsquery = commandsconn.createStatement();
			
			usersconn = DriverManager.getConnection("jdbc:sqlite:users.db");
			usersquery = usersconn.createStatement();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			//25 = SQLite Exception
			System.exit(25);
		}
	}

}
