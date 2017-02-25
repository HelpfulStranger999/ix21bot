package com.gmail.helpfulstranger.ix21bot.timers;

import java.util.*;
import java.sql.*;

import static com.gmail.helpfulstranger999.ix21bot.IX21Bot.*;

public class PointsTimer extends TimerTask {
	
	private String user = null;

	public PointsTimer(String nickname) {
		super();
		user = nickname;
	}

	@Override
	public void run() {
		try {
			sql = "SELECT POINTS FROM USERS";
			ResultSet set = query.executeQuery(sql);
			while (set.next()) {
				int points = set.getInt("POINTS"); 
				int newpoints = points + 1;
				sql = "UPDATE USERS set POINTS = " + newpoints +
						" where USER=\'" + user + "\'";
				query.executeUpdate(sql);
				logger.debug("Successfully added " + newpoints + " to " + user + "'s previous total of " + points + ".");
			}
		} catch (SQLException e) {
			logger.error("SQLException occurred while updating scarabs in " + this.getClass().toString() + ": \n");
			e.printStackTrace();
		}
		
		
	}


}
