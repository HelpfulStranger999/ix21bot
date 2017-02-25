package com.gmail.helpfulstranger999.ix21bot;

import org.pircbotx.*;
import org.pircbotx.hooks.events.*;

import com.gmail.helpfulstranger.ix21bot.timers.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.*;
import java.util.regex.*;
import java.util.*;
import java.util.Map.Entry;

import org.json.*;

import static com.gmail.helpfulstranger999.ix21bot.IX21Bot.*;

public class Commands {

	private static String noauth = "You do not have the authorization to perform this command. If you have been recently modded, try reloading the page. If you should have such privileges, please inform the streamer";
	public static HashMap<String, Integer> searchMap = new HashMap<String, Integer>();
	public static ArrayList<String> searchUsers = new ArrayList<String>();
	public static ArrayList<Integer> searchPoints = new ArrayList<Integer>();
	public static Timer searchTimer = new Timer();
	public static Timer cooldownSearch = new Timer();
	public static boolean cooldown = false;
	
	public static String noAuth (String user) {
		return "/w " + user + " " + noauth;
	}
	
	public static void disconnectBot (MessageEvent event) {
		
		IX21Bot.bot.stopBotReconnect();
		event.respondWith("Disconnecting Bot now....");
		IX21Bot.manager.stop();
		logger.info("Disconnected bot from server successfully");
		try {
			IX21Bot.query.close();
			IX21Bot.conn.close();
		} catch (SQLException e) {
			logger.error("Error Closing Database: ");
			e.printStackTrace();
			System.exit(25);
		}
		System.exit(0);
	}
	
	public static void command(MessageEvent event) {
		User senderUser = event.getUser();
		String message = event.getMessage().trim();
		String senderNick = senderUser.getNick();
		
		if (message.equals("!commands")) {
			String list = " Commands - ";
			Iterator<Entry<String, String>> iter = commands.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> triggers = (Map.Entry<String, String>) iter.next();
				String trigger = triggers.getKey();
				String response = triggers.getValue();
				list += trigger;
				list += " : ";
				list += response;
				if(iter.hasNext()) list += " | ";
			}
			event.respondWith("/w " + senderNick + list);
		} else if(message.startsWith("!commands add")) {
			if(isAuth(event.getTags())) {
				String regex = "(!commands add )(\\w+)( )(.+)";
				String trigger = "";
				String response = "";
				Matcher matcher = Pattern.compile(regex).matcher(message);
				if(matcher.matches()) {
					matcher.reset();
					if(matcher.find()) {
						trigger = matcher.group(2);
						response = matcher.group(4);
					}
				} else {
					event.respondWith("/w " + senderNick + " Incorrect syntax: !commands add [trigger] [response]");
					return;
				}
				try {
					sql = "INSERT INTO COMMANDS (COMMAND, RESPONSE) " +
							"VALUES (\'" + trigger + "\', \'" + response + "\')";
					query.executeUpdate(sql);
				} catch (SQLException e) {
					errorHandler(e, "adding command", event);
				}
				commands.put(trigger, response);
				event.respondWith("Successfully added command " + trigger + " with response of \"" + response + "\"");
			} else {
				event.respondWith(noAuth(senderNick));
			}
		} else if (message.startsWith("!commands delete")) {
			if(isAuth(event.getTags())) {
				String regex = "(!commands delete )(\\w+)";
				String trigger = "";
				Matcher matcher = Pattern.compile(regex).matcher(message);
				if(matcher.matches()) {
					matcher.reset();
					if(matcher.find()) {
						trigger = matcher.group(2);
					}
				} else {
					event.respondWith("/w " + senderNick + " Incorrect syntax: !commands delete [command]");
					return;
				}
				if(commands.containsKey(trigger)) {
					try {
						sql = "DELETE FROM COMMANDS WHERE COMMAND = \'" + trigger + "\'";
						query.executeUpdate(sql);
					} catch (SQLException e) {
						errorHandler(e, "deleting command", event);
					}
					commands.remove(trigger);
					event.respondWith("Successfully removed command " + trigger);
				} else {
					event.respondWith("Command not found");
				}
			} else {
				event.respondWith(noAuth(senderNick));
			}
		} else if (message.startsWith("!commands edit")) {
			if(isAuth(event.getTags())) {
				String regex = "(!commands edit )(\\w+)( )(.+)";
				String trigger = "";
				String response = "";
				Matcher matcher = Pattern.compile(regex).matcher(message);
				if(matcher.matches()) {
					matcher.reset();
					if(matcher.find()) {
						trigger = matcher.group(2);
						response = matcher.group(4);
					}
				} else {
					event.respondWith("/w " + senderNick + " Incorrect syntax: !commands edit [command] [response]");
					return;
				}
				if(commands.containsKey(trigger)) {
					try {
						sql = "UPDATE COMMANDS SET RESPONSE = \'" + response + "\' WHERE COMMAND = \'" + trigger + "\'";
						query.executeUpdate(sql);
					} catch (SQLException e) {
						errorHandler(e, "editing command", event);
					}
					commands.remove(trigger);
					commands.put(trigger, response);
					event.respondWith("Successfully updated command " + trigger + " to respond with \"" + response + "\"");
				} else {
					event.respondWith("Command not found");
				}
			} else {
				event.respondWith(noAuth(senderNick));
			}
		}
	}

	public static void points(MessageEvent event) {
		User senderUser = event.getUser();
		String message = event.getMessage().trim();
		String senderNick = senderUser.getNick();
		
		if(message.startsWith("!me")) {
			event.respondWith("/w " + senderNick + " The proper command is !scarabs");
			String saveMessage = message.substring(3, message.length());
			message = "!scarabs" + saveMessage;
		}
		if(message.startsWith("!points")) {
			event.respondWith("/w " + senderNick + " The proper command is !scarabs");
			String saveMessage = message.substring(7, message.length());
			message = "!scarabs" + saveMessage;
		}
		if(message.startsWith("!scarabs")) {
			if(message.equals("!scarabs")) {
				try {
					sql = "SELECT POINTS FROM USERS WHERE USER = \'" + senderNick + "\'";
					ResultSet set = query.executeQuery(sql);
					int points = 0;
					while(set.next()) {
						points = set.getInt("POINTS");
					}
					event.respondWith("@" + senderNick + " has " + points + " scarabs.");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}  else if (message.startsWith("!scarabs give")) {
				if (isAuth(event.getTags())) {
					String regex = "(!scarabs give ){1}\\w+[ ]\\d+";
					if(Pattern.compile(regex).matcher(message).matches()) {
						Matcher parserM = Pattern.compile("((!scarabs give ){1})(\\w+)([ ])(\\d+)").matcher(message);
						int amount = 0;
						String target = null;
						if (parserM.find()) {
							amount = Integer.parseInt(parserM.group(5));
							target = parserM.group(3);
						}
						try {
							sql = "SELECT * FROM USERS WHERE USER = \'" + target + "\'";
							ResultSet set = query.executeQuery(sql);
							while(set.next()) {
								int current = set.getInt("POINTS");
								int total = current + amount;
								sql = "UPDATE USERS SET POINTS = " + total + " WHERE USER = \'" + target + "\'";
								query.executeUpdate(sql);
								event.respondWith("Successfully given " + target + " " + amount + " scarabs.");
							}
						} catch (SQLException e) {
							System.err.println("SQLException occurred while giving scarabs");
							e.printStackTrace();
						}
					} else {
						event.respondWith("Invalid syntax: !scarabs give [target user] [amount points]");
					}
				} else {
					event.respondWith(noAuth(senderNick));
				}
			} else if (message.startsWith("!scarabs take")) {
				if (isAuth(event.getTags())) {
					String regex = "(!scarabs take ){1}\\w+[ ]\\d+";
					if(Pattern.compile(regex).matcher(message).matches()) {
						Matcher parserM = Pattern.compile("((!scarabs take ){1})(\\w+)([ ])(\\d+)").matcher(message);
						int amount = 0;
						String target = null;
						if (parserM.find()) {
							amount = Integer.parseInt(parserM.group(5));
							target = parserM.group(3);
						}
						try {
							sql = "SELECT * FROM USERS WHERE USER = \'" + target + "\'";
							ResultSet set = query.executeQuery(sql);
							while(set.next()) {
								int current = set.getInt("POINTS");
								int total = current - amount;
								sql = "UPDATE USERS SET POINTS = " + total + " WHERE USER = \'" + target + "\'";
								query.executeUpdate(sql);
								event.respondWith("Successfully taken " + amount + " scarabs from " + target + ".");
							}
						} catch (SQLException e) {
							System.err.println("SQLException occurred while giving scarabs");
							e.printStackTrace();
						}
					} else {
						event.respondWith("Invalid syntax. !scarabs take [target user] [amount points]");
					}
				} else {
					event.respondWith(noAuth(senderNick));
				}
			} else if (message.startsWith("!scarabs clear")) {
				if (isAuthBroad(event.getTags())) {
					if (message.equals("!scarabs clear")) {
						try {
							sql = "UPDATE USERS SET POINTS = 0";
							query.executeUpdate(sql);
						} catch (SQLException e) {
							System.err.println("SQLException occurred while clearing point board");
							e.printStackTrace();
						}
					} else {
						event.respondWith("Invalid syntax. !scarabs clear");
					}
				} else {
					event.respondWith(noAuth(senderNick));
				}
			} else {
				event.respond("Command not recognized.");
			}
		}
	}
	
	public static void search (MessageEvent event) {
		if(cooldown) {
			event.respondWith("You just returned from your latest expedition! Wait at least three minutes before setting out on another.");
			return;
		}
		
		User senderUser = event.getUser();
		String message = event.getMessage().trim();
		String senderNick = senderUser.getNick();
		
		if (message.startsWith("!gamble")) {
			event.respondWith("/w " + senderNick + " The proper command is !search");
			String saveMessage = message.substring(7, message.length());
			message = "!search" + saveMessage;
		}
		if (message.startsWith("!search")) {
			String regex = "(!search ){1}(\\d+)";
			int search = 0;
			Matcher regexMatcher = Pattern.compile(regex).matcher(message);
			if(regexMatcher.matches()) {
				logger.debug("regexMatcher.matches = true");
				regexMatcher.reset();
				if (regexMatcher.find()) {
					search = Integer.parseInt(regexMatcher.group(2));
					logger.debug("Search Points: " + search);
				}
			} else {
				event.respondWith("Invalid Syntax: !search [x]");
			}
			try {
				//logger.debug("Selecting points...");
				sql = "SELECT POINTS FROM USERS WHERE USER = \'" + senderNick + "\'";
				ResultSet set = query.executeQuery(sql);
				//logger.debug("Selected points...");
				while(set.next()) {
					int points = set.getInt("POINTS");
					logger.debug(senderNick + " has " + points + " points.");
					int remainder = points - search;
					logger.debug("Remainder: " + remainder);
					if (remainder < 0) {
						event.respondWith("/w " + senderNick + " You lack " + (search - points) + " scarabs to search for " + search + ".");
					} else {
						sql = "UPDATE USERS SET POINTS = " + remainder + " WHERE USER = \'" + senderNick + "\'";
						query.executeUpdate(sql);
						//logger.debug("Updated points...");
					}
				}
			} catch (SQLException e) {
				logger.error("An SQLException occurred while performing !search command: ");
				e.printStackTrace();
			}
			logger.debug("SQL Performed.");
			if(searchMap.size() == 0) {
				logger.debug("First one!");
				event.respondWith(senderNick + " is assembling a team to find more scarabs! Type !search [x] to join.");
				searchTimer.schedule(new SearchTimer(), 120000);
				searchMap.put(senderNick, Integer.valueOf(search));
				searchUsers.add(senderNick);
				searchPoints.add(Integer.valueOf(search));
			} else {
				logger.debug("Not the first one.");
				searchMap.put(senderNick, Integer.valueOf(search));
				searchUsers.add(senderNick);
				searchPoints.add(Integer.valueOf(search));
			}
		} else {
			event.respondWith("/w " + senderNick + " Command not recognized.");
		}
	}

	public static void title(MessageEvent event) {
		if (isAuth(event.getTags())) {
			String message = event.getMessage();
			String regex = "(!title ){1}(.+)+";
			String title = "";
			Matcher matcher = Pattern.compile(regex).matcher(message);
			if(matcher.matches()) {
				matcher.reset();
				if(matcher.find()) {
					title = matcher.group(2);
				}
			} else {
				event.respondWith("/w " + event.getUser().getNick() + " Incorrect syntax: !title [title]");
			}
			try {
				logger.info("Beginning to construct PUT request to update title");
				HttpURLConnection titleConnection = null;
				URL pushTitle = new URL("https://api.twitch.tv/kraken/channels/helpfulstranger999");
				titleConnection = (HttpURLConnection) pushTitle.openConnection();
				titleConnection.setRequestMethod("PUT");
				titleConnection.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
				titleConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				titleConnection.setRequestProperty("Authorization", "OAuth oauth:7lfe5byck0k49g80lc4auryriywhdb");
				titleConnection.setRequestProperty("Client-ID", "zyq023641tuy4yk6hx89mz2fqboyvv");
				titleConnection.setDoInput(true);
				titleConnection.setDoOutput(true);
				OutputStreamWriter out = new OutputStreamWriter(titleConnection.getOutputStream());
				out.write("helpfulstranger999[status]=" + URLEncoder.encode(title, "UTF-8") + "&channel[game]=Minecraft");
				out.close();
				InputStream in = titleConnection.getInputStream();
				logger.debug(Integer.toString(in.read()));
				in.close();
			} catch (MalformedURLException e) {
				errorHandler(e, "updating title", event);
			} catch (ProtocolException e) {
				errorHandler(e, "updating title", event);
			} catch (IOException e) {
				errorHandler(e, "updating title", event);
			}
			event.respondWith("@" + event.getUser().getNick() + " Now updating title to \"" + title + "\".");
		} else {
			event.respondWith(noAuth(event.getUser().getNick()));
		}
		
	}

	public static void uptime(MessageEvent event) {
		String user = event.getUser().getNick();
		String channelRaw = event.getChannel().getName().toString();
		String channel = channelRaw.substring(1, channelRaw.length());
		try {
			logger.info("Fetching uptime for " + user);
			URL uptimeURL = new URL("https://api.twitch.tv/kraken/streams/bloorooster1");
			HttpURLConnection uptimeConn = (HttpURLConnection) uptimeURL.openConnection();
			uptimeConn.setRequestMethod("GET");
			uptimeConn.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
			uptimeConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			uptimeConn.setRequestProperty("Client-ID", "zyq023641tuy4yk6hx89mz2fqboyvv");
			logger.debug("Past the uptimeConn stuff");
			BufferedReader uptimeBR = new BufferedReader(new InputStreamReader(uptimeConn.getInputStream()));
			String inputLine = uptimeBR.readLine();
			logger.debug("Beginning org.json stuff");
			JSONObject uptimeJSON = new JSONObject(inputLine);
			JSONObject streamTime;
			if(uptimeJSON.isNull("stream")) {
				event.respondWith(channel + " is not streaming.");
				return;
			} else {
				streamTime = uptimeJSON.getJSONObject("stream");
			}
			logger.debug("Past org.json stuff");
			logger.debug("Stuff: " + uptimeJSON.toString());
			String uptimeStr = streamTime.getString("created_at");
			Instant uptimeAgo = Instant.parse(uptimeStr);
			Instant now = Instant.now();
			Duration uptimeB = Duration.between(uptimeAgo, now);
			long hours = uptimeB.toHours();
			Duration uptimeH = uptimeB.minusHours(hours);
			long minutes = uptimeH.toMinutes();
			Duration uptimeM = uptimeH.minusMinutes(minutes);
			long seconds = uptimeM.getSeconds();
			String duration = "";
			if (hours > 1) {
				duration += hours;
				duration += " hours, ";
			} else if (hours == 1)  {
				duration += "1 hour, ";
			}
			if (minutes > 1) {
				duration += minutes;
				duration += " minutes, ";
			} else if (minutes == 1) {
				duration += "1 minute, ";
			}
			if (seconds > 1) {
				duration += "and ";
				duration += seconds;
				duration += " seconds.";
			} else if (seconds == 1) {
				duration += "and 1 second.";
			}
			event.respondWith(channel + " has been streaming for " + duration);
		} catch (MalformedURLException e) {
			errorHandler(e, "fetching uptime", event);
		} catch (ProtocolException e) {
			errorHandler(e, "fetching uptime", event);
		} catch (IOException e) {
			errorHandler(e, "fetching uptime", event);
		}
	}
	
	public static void caster (MessageEvent event) {
		String user = event.getUser().getNick();
		String message = event.getMessage().trim();
		
		if(isAuth(event.getTags())) {
			String regex = "(!caster ){1}(\\w+)";
			String streamer = "";
			Matcher matcher = Pattern.compile(regex).matcher(message);
			if(matcher.matches()) {
				matcher.reset();
				if(matcher.find()) {
					streamer = matcher.group(2);
					event.respondWith(streamer + " is another awesome streamer! Drop him a follow at twitch.tv/" + streamer);
				}
			} else {
				event.respondWith("/w " + user + "Incorrect syntax: !caster [streamer]");
			}
		} else {
			event.respondWith(noAuth(user));
		}
	}
	
	public static void shoutout (MessageEvent event) {
		String user = event.getUser().getNick();
		String message = event.getMessage().trim();
		
		if(isAuth(event.getTags())) {
			String regex = "(!shoutout ){1}(\\w+)";
			String streamer = "";
			Matcher matcher = Pattern.compile(regex).matcher(message);
			if(matcher.matches()) {
				matcher.reset();
				if(matcher.find()) {
					streamer = matcher.group(2);
					event.respondWith(streamer + " is another awesome streamer! Drop him a follow at twitch.tv/" + streamer);
				}
			} else {
				event.respondWith("/w " + user + "Incorrect syntax: !shoutout [streamer]");
			}
		} else {
			event.respondWith(noAuth(user));
		}
	}

	public static void followage(MessageEvent event) {
		String user = event.getUser().getNick();
		String message = event.getMessage().trim();
		String channelRaw = event.getChannel().getName().toString();
		String channel = channelRaw.substring(1, channelRaw.length());
		
		if (message.equals("!followage")) {
			try {
				URL followURL = new URL("https://mcgamesdot.net/followage.php?channel=" + channel + "&user=" + user);
				HttpURLConnection followConn = (HttpURLConnection) followURL.openConnection();
				followConn.setRequestMethod("GET");
				followConn.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
				followConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				followConn.setRequestProperty("Client-ID", "zyq023641tuy4yk6hx89mz2fqboyvv");
				BufferedReader uptimeBR = new BufferedReader(new InputStreamReader(followConn.getInputStream()));
				String inputLine = uptimeBR.readLine();
				if (!(inputLine.equals(user + " is not following " + channel))) {
					event.respondWith(user + " has been following " + channel + " for " + inputLine);
				} else {
					event.respondWith(inputLine);
				}
			} catch (MalformedURLException e) {
				errorHandler(e, "fetching followage", event);
			} catch (ProtocolException e) {
				errorHandler(e, "fetching followage", event);
			} catch (IOException e) {
				errorHandler(e, "fetching followage", event);
			}
		} else {
			String regex = "(!followage ){1}(\\w+)";
			String follower = "";
			Matcher matcher = Pattern.compile(regex).matcher(message);
			if(matcher.matches()) {
				matcher.reset();
				if (matcher.find()) {
					follower = matcher.group(2);
				}
			} else {
				follower = user;
			}
			try {
				URL followURL = new URL("https://mcgamesdot.net/followage.php?channel=" + channel + "&user=" + follower);
				HttpURLConnection followConn = (HttpURLConnection) followURL.openConnection();
				followConn.setRequestMethod("GET");
				followConn.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
				followConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				followConn.setRequestProperty("Client-ID", "zyq023641tuy4yk6hx89mz2fqboyvv");
				BufferedReader uptimeBR = new BufferedReader(new InputStreamReader(followConn.getInputStream()));
				String inputLine = uptimeBR.readLine();
				if (!(inputLine.equals(follower + " is not following " + channel))) {
					event.respondWith(follower + " has been following " + channel + " for " + inputLine);
				} else {
					event.respondWith(inputLine);
				}
			} catch (MalformedURLException e) {
				errorHandler(e, "fetching followage", event);
			} catch (ProtocolException e) {
				errorHandler(e, "fetching followage", event);
			} catch (IOException e) {
				errorHandler(e, "fetching followage", event);
			}
		}
		
	}
	
}
