package com.obomprogramador.bigdata.twitterparser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.obomprogramador.bigdata.twitterparser.CollectTweets.Config;

public class TestBasicTweetCollect {

	@Test
	public void test() throws Exception {
		assertFalse(CollectTweets.collect(""));
		ResourceBundle res = ResourceBundle.getBundle("twitterparser");
		String path = res.getString("testpath");
		CollectTweets.Config config = CollectTweets.getConfig(path);
		assertTrue(config != null);
		CollectTweets.Config c2 = getConfigFromJson(path);
		assertTrue(config.equals(c2));
		assertTrue(CollectTweets.collect(path));
	}

	private Config getConfigFromJson(String path) throws Exception {
		CollectTweets.Config c = null;
		FileInputStream input = new FileInputStream(new File(path));
		if(input == null) {
			throw new Exception("Path not found");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	    }
	    Gson gson = new GsonBuilder().create();
	    c = gson.fromJson(out.toString(), Config.class); 				
		return c;
	}

}
