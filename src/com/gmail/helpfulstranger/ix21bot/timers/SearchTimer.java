package com.gmail.helpfulstranger.ix21bot.timers;

import java.util.*;
import java.util.Map.Entry;

import com.gmail.helpfulstranger999.ix21bot.Commands;

import java.sql.*;

import static com.gmail.helpfulstranger999.ix21bot.IX21Bot.*;

public class SearchTimer extends TimerTask {
	
	public static HashMap<String, Boolean> successMap = new HashMap<String, Boolean>();
	public static HashMap<String, Integer> rewardMap = new HashMap<String, Integer>();

	public SearchTimer() {
		super();
	}

	@Override
	public void run() {
		bot.send().message("#helpfulstranger999", "The team of explorers are setting out to find more scarabs...");
		for (String user : Commands.searchUsers) {
			Random searchRand = new Random();
			int chance = searchRand.nextInt(100);
			int winout = new Random().nextInt(1000);
			logger.debug(user + " got a chance of " + chance);
			int search = Commands.searchMap.get(user);
			int reward = 0;
			int multiplier = 1;
			if(winout == 0) {
				multiplier = 3;
				reward = search * multiplier;
				successMap.put(user, true);
				rewardMap.put(user, Integer.valueOf(reward));
				logger.debug("Reward: " + Integer.toString(reward));
			} else if (chance >= 0 && chance <= 40) {
				multiplier = 2;
				reward = search * multiplier;
				successMap.put(user, true);
				rewardMap.put(user, Integer.valueOf(reward));
				logger.debug("Reward: " + Integer.toString(reward));
			} else if (chance > 40) {
				reward = 0;
				rewardMap.put(user, Integer.valueOf(reward));
				logger.debug("Reward: " + Integer.toString(reward));
			}
			try {
				sql = "SELECT POINTS FROM USERS WHERE USER = \'" + user + "\';";
				ResultSet set = query.executeQuery(sql);
				while (set.next()) {
					int current = set.getInt("POINTS");
					int total = current + reward;
					sql = "UPDATE USERS SET POINTS = " + total + " WHERE USER = \'" + user + "\';";
					query.executeUpdate(sql);
				}
			} catch (SQLException e) {
				logger.error("SQLException occurred while performing !search command: ");
				e.printStackTrace();
			}
		}
		boolean print = true;
		logger.debug("searchMap.size(): " + Commands.searchMap.size());
		logger.debug("successMap.size(): " + successMap.size());
		if(successMap.size() == Commands.searchMap.size()) {
			 bot.send().message("#helpfulstranger999", "... and had great success! Everyone found some scarabs. FeelsAmazingMan");
			 print = true;
		} else if (successMap.size() == 0) {
			bot.send().message("#helpfulstranger999", "... but failed completely. No one found any scarabs. BibleThump");
			print = false;
		} else {
			bot.send().message("#helpfulstranger999", "... and had partial success. Some people found scarabs.");
			print = true;
		}
		String printMessage = "Results: ";
		Iterator<Entry<String, Integer>> iter = rewardMap.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) iter.next();
			logger.debug(pair.getKey());
			logger.debug(Integer.toString(pair.getValue()));
			printMessage = printMessage + pair.getKey() + " - " + pair.getValue() + " (" + Commands.searchMap.get(pair.getKey()) + ")";
			if(iter.hasNext()) printMessage = printMessage + ", ";
		}
		if (print) {
			bot.send().message("#helpfulstranger999", printMessage);
		}
		Commands.cooldown = true;
		Commands.cooldownSearch.schedule(new CooldownTimer(), 180000);
		Commands.searchMap.clear();
		Commands.searchUsers.clear();
		Commands.searchPoints.clear();
		rewardMap.clear();
		successMap.clear();
	}

}
