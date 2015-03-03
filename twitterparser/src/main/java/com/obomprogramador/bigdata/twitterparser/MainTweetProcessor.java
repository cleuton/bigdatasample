package com.obomprogramador.bigdata.twitterparser;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.obomprogramador.bigdata.twitterparser.CollectTweets.Config;

public class MainTweetProcessor {
	private static Logger logger = LoggerFactory.getLogger(MainTweetProcessor.class);
	public static void main(String [] args) {
		if (args.length < 1) {
			logger.error(">>> Missing arguments: MainTweetProcessor <config file path>");
		}
		else {
			String configPath = args[0];
			try {
				CollectTweets.collect(configPath);
				CollectTweets.Config c2 = getConfigFromJson(configPath);
				List<String> tweets = getTweetsFromFile(c2.output + "/tweets.txt");
				TranslateTweets trans = new TranslateTweets(c2.googleApiKey);
				List<String> translated = trans.translateAll(tweets);
				FileWriter fw = new FileWriter(c2.output + "/tweets-translated.txt");
				for (String linha : translated) {
					fw.write(linha + "\n");
				}
				fw.close();
				
			} catch (Exception e) {
				logger.error(">>> Exception processing tweets: " + e.getMessage());
			}
		}
	}
	private static List<String> getTweetsFromFile(String outputPath) {
		List<String> output = null;
		try {
			List<String>saida = new ArrayList<String>();
			FileInputStream input = new FileInputStream(new File(outputPath));
			if(input == null) {
				logger.error(">>> Path not found: " + outputPath);
				throw new Exception("Path not found");
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		    StringBuilder out = new StringBuilder();
		    String line;
		    while ((line = reader.readLine()) != null) {
		        saida.add(line);
		    }	
		    output = saida;
		}
		catch (Exception ex) {
			logger.error(">>> Exception: " + ex.getMessage());
		}
		return output;
		
	}

	private static Config getConfigFromJson(String path) throws Exception {
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
