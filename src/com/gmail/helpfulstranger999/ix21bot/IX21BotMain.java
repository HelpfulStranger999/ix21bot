package com.gmail.helpfulstranger999.ix21bot;

import static java.lang.System.out;
import org.jibble.pircbot.*;

public class IX21BotMain {

	public static int port = 6667;
	public static void main(String[] args) {
		
		
		IX21Bot_V2 bot = new IX21Bot_V2();
		bot.setVerbose(true);
		try {
			bot.connect("irc.twitch.tv", port, "oauth:bvolwngwvomszb1ubj90ghc9y736xc");
		} catch (NickAlreadyInUseException e) {
			out.println("Nickname already in use! Please try again.");
			e.printStackTrace();
		} catch (Exception e) {
			out.println("An error happened: ");
			e.printStackTrace();
		}
		bot.joinChannel("#helpfulstranger999");
	}

}
