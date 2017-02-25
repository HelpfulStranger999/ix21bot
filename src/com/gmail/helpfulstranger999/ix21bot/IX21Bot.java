package com.gmail.helpfulstranger999.ix21bot;

import java.sql.*;
import java.util.*;

import org.pircbotx.*;
import org.pircbotx.cap.*;
import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.*;
import org.slf4j.*;

import com.gmail.helpfulstranger.ix21bot.timers.PointsTimer;
import com.gmail.helpfulstranger.ix21bot.timers.PollingTimer;
import com.google.common.collect.ImmutableMap;


public class IX21Bot extends ListenerAdapter implements Listener {
	
	public static PircBotX bot = null;
	//public static PircBotX whisperBot = null;
	public static MultiBotManager manager = null;
	
	public static Connection conn = null;
	public static Statement query = null;

	public static String sql = null;
	//public static ArrayList<String> emotes = new ArrayList<String>();
	
	public static Timer poller = new Timer(true);
	public static Timer points = new Timer();
	
	public static Map<String, PointsTimer> timers = new HashMap<String, PointsTimer>();
	public static HashMap<String, String> commands = new HashMap<String, String>();
	
	public final static Logger logger = LoggerFactory.getLogger(IX21Bot.class);

	public static void main(String[] args) {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:bot.db");
			query = conn.createStatement();
			conn.setAutoCommit(true);
			logger.info("Successfully connected to database.");
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			//25 = SQLite Exception
			System.exit(25);
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found while creating SQL Connection");
			e.printStackTrace();
		}
		//sql = "CREATE TABLE IF NOT EXISTS COMMANDS " +
		//		"(ID INTEGER PRIMARY KEY    AUTOINCREMENT," +
		//		" TRIGGER           TEXT    NOT NULL" +
		//		" RESPONE            TEXT NOT NULL)";
		try {
			sql = "CREATE TABLE IF NOT EXISTS USERS " +
					"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"USER                 TEXT NOT NULL, " +
					"POINTS BIGINT NOT NULL);";
		
			query.executeUpdate(sql);
			logger.debug("Successfully created table USERS");
		} catch (SQLException e) {
			logger.error("SQLException occurred adding users table: ");
			e.printStackTrace();
		}
		try {
			sql = "CREATE TABLE IF NOT EXISTS COMMANDS " +
					"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"COMMAND TEXT NOT NULL, " +
					"RESPONSE TEXT NOT NULL)";
			query.executeUpdate(sql);
			logger.debug("Successfully created table COMMANDS");
		} catch (SQLException e) {
			logger.error("A SQLException occurred creating COMMANDS table: ");
			e.printStackTrace();
		}
		try {
			commands.clear();
			sql = "SELECT * FROM COMMANDS";
			ResultSet set = query.executeQuery(sql);
			while(set.next()) {
				String trigger = set.getString("COMMAND");
				String response = set.getString("RESPONSE");
				commands.put(trigger, response);
			}
		} catch (SQLException e) {
			
		}
		Configuration.Builder templateConfig = new Configuration.Builder().setAutoNickChange(false)
				.setOnJoinWhoEnabled(false).setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("twitch.tv/membership"))
				.addCapHandler(new EnableCapHandler("twitch.tv/tags"))
				.setName("wipdevbot")
				.setServerPassword("oauth:7lfe5byck0k49g80lc4auryriywhdb")
				.addAutoJoinChannel("#helpfulstranger999")
				.addListener(new IX21Bot())
				.addListener(new MessageListener())
				.setAutoReconnect(true).setAutoReconnectDelay(15000).setAutoReconnectAttempts(30);
		
		manager = new MultiBotManager();
		bot = new PircBotX(templateConfig.buildForServer("irc.twitch.tv"));
		//whisperBot = new PircBotX(templateConfig.buildForServer("199.9.253.119"));
		manager.addBot(bot);
		//manager.addBot(whisperBot);
		manager.start();
	}
	
	@Override
	public void onConnect (ConnectEvent event) throws Exception {
		poller.schedule(new PollingTimer(), 0, 1800000);
	}
	
	@Override
	public void onJoin (JoinEvent event) throws Exception {
		if(event.getUser().equals(bot.getUserBot())) {
			event.getChannel().send().message("IX21Bot is now connected.");
		}
		try {
			boolean userfound = false;
			sql = "SELECT USER FROM USERS";
			ResultSet set = query.executeQuery(sql);
			while (set.next()) {
				String name = set.getString("USER");
				if(name.equalsIgnoreCase(event.getUser().getNick())) {
					userfound = true;
					PointsTimer timeder = new PointsTimer(event.getUser().getNick());
					points.schedule(timeder, 120000);
					timers.put(event.getUser().getNick(), timeder);
					return;
				}
			}
			if(!userfound) {
				sql = "INSERT INTO USERS (USER,POINTS) " +
						"VALUES (\'" + event.getUser().getNick() + "\', 0);";
				query.executeUpdate(sql);
				PointsTimer timeder = new PointsTimer(event.getUser().getNick());
				points.schedule(timeder, 120000);
				timers.put(event.getUser().getNick(), timeder);
			}
		} catch (SQLException e) {
			System.err.println("SQLException occurred: ");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPart (PartEvent event) throws Exception {
		timers.get(event.getUser().getNick()).cancel();
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
	
	public static boolean isAuthBroad (ImmutableMap<String, String> map) {
		String badge = map.get("badges");
		if(badge.contains("broadcaster/1")) {
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
	
	public static void errorHandler (Exception e, String cause, GenericMessageEvent event) {
		event.respondWith("An exception occurred; see console for more details.");
		logger.error(e.getClass().getSimpleName() + " occurred while " + cause + ":");
		e.printStackTrace();
	}

}
