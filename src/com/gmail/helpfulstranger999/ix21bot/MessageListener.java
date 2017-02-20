package com.gmail.helpfulstranger999.ix21bot;

import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.*;

public class MessageListener extends ListenerAdapter implements Listener {
	
	public MessageListener () {
	
	}

	@Override
	public void onMessage (MessageEvent event) throws Exception {
		if(event.getMessage().startsWith("!commands")) {
			Commands.command(event);
		} else {
			return;
		}
	}
	
	@Override
	public void onPrivateMessage (PrivateMessageEvent event) throws Exception {
		if(event.getMessage().equals("!disconnect")) {
			if(event.getUser().getNick().equals("helpfulstranger999")) {
				Commands.disconnectBot(event);
				return;
			}
		} else {
			return;
		}
	}
}
