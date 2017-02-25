package com.gmail.helpfulstranger999.ix21bot;

import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.*;
import static com.gmail.helpfulstranger999.ix21bot.IX21Bot.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.*;

public class MessageListener extends ListenerAdapter implements Listener {
	
	public MessageListener () {
	
	}

	@Override
	public void onMessage (MessageEvent event) throws Exception {
		String message = event.getMessage().trim();
		//String messageEdit = message;
		timeoutRegex(event, "\\w+[.]{1}\\w+", message, 60);
		/*int emoteCount = 0;
		//out.println("Size: " + emotes.size());
		for(int i = 0; i < emotes.size(); i ++) {
			while(messageEdit.indexOf(emotes.get(i)) < messageEdit.length() && messageEdit.indexOf(emotes.get(i)) != -1) {
				messageEdit.replaceFirst(emotes.get(i), "");
				emoteCount++;
			}
		}
		out.println("Emote Count: " + emoteCount);
		if(emoteCount >= 5) {
			if(!(message.startsWith("!") || isAuth(event.getTags()))) {
				event.respondWith("/timeout" + event.getUser().getNick() + " 60");
			}
		}*/
		
		if(message.startsWith("!commands")) {
			Commands.command(event);
			return;
		} else if (message.startsWith("!dcratusbot")) {
			if(isAuthBroad(event.getTags())) {
				Commands.disconnectBot(event);
				//logger.debug("Disconnecting from message...");
			}
			return;
		} else if (message.startsWith("!points")) {
			Commands.points(event);
			return;
		} else if (message.startsWith("!me")) {
			Commands.points(event);
			return;
		} else if (message.startsWith("!scarabs")) {
			Commands.points(event);
			return;
		} else if (message.startsWith("!gamble")) {
			Commands.search(event);
			return;
		} else if (message.startsWith("!search")) {
			Commands.search(event);
			return;
		} else if (message.startsWith("!title")) {
			event.respondWith("This command has not been implemented yet; it shall be added in version 2.0 of IX21Bot. Thanks for understanding! <3");
			//Commands.title(event);
			// TODO !title command and all that stuff
			return;
		} else if (message.startsWith("!uptime")) {
			Commands.uptime(event);
			return;
		} else if (message.startsWith("!shoutout")) {
			Commands.shoutout(event);
			return;
		} else if (message.startsWith("!caster")) {
			Commands.caster(event);
			return;
		} else if (message.startsWith("!followage")) {
			Commands.followage(event);
			return;
		} else if (message.startsWith("!ping")) {
			
		}
		Iterator<Entry<String, String>> iter = commands.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> triggers = (Map.Entry<String, String>) iter.next();
			String trigger = triggers.getKey();
			String response = triggers.getValue();
			if(message.equals(trigger)) {
				event.respondWith(response);
			}
		}
		return;
	}
	
	public void timeoutRegex (MessageEvent event, String regex, String message, int timeoutDuration) {
		if (Pattern.compile(regex).matcher(message).find()) {
			if (!(message.startsWith("!") || isAuth(event.getTags()))) {
				event.respondWith("/timeout " + event.getUser().getNick() + " " + timeoutDuration);
			}
		}
	}
	
}
