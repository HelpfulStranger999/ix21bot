package com.gmail.helpfulstranger.ix21bot.timers;

//import static java.lang.System.out;

//import java.io.*;
//import java.net.*;
//import java.nio.charset.StandardCharsets;
import java.util.*;

//import org.json.*;

//import static com.gmail.helpfulstranger999.ix21bot.IX21Bot.*;

public class PollingTimer extends TimerTask {

	public PollingTimer () {
		super();
		
	}
	
	@Override
	public void run() {
		/*URL requestEmotes;
		HttpURLConnection emoteConnection = null;
		try {
			out.println("Attempting emoticon fetch...");
			requestEmotes = new URL("https://api.twitch.tv/kraken/chat/emoticon_images");
			emoteConnection = (HttpURLConnection) requestEmotes.openConnection();
			emoteConnection.setRequestMethod("GET");
			emoteConnection.setRequestProperty("Accept", "application/vnd.twitchtv.v3+json");
			emoteConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			BufferedReader emotesBR = new BufferedReader(new InputStreamReader(emoteConnection.getInputStream()));
			String inputLine = emotesBR.readLine();
			out.println("Fetch complete! Now printing....");
			JSONObject emoteset = new JSONObject(inputLine);
			JSONArray emoticons = emoteset.getJSONArray("emoticons");
			for(int i = 0; i < emoticons.length(); i++) {
				String code = emoticons.getJSONObject(i).getString("code");
				emotes.add(code);
			}
			Set<String> set = new LinkedHashSet<String>();
			set.addAll(emotes);
			emotes.clear();
			emotes.addAll(set);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				Reader response =
					    new InputStreamReader(emoteConnection.getErrorStream(), StandardCharsets.UTF_8);
				int c;
				while ((c = response.read()) >= 0) {
					out.print((char) c);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}*/

	}

}
