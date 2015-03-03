package com.obomprogramador.bigdata.twitterparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class CollectTweets {
	private static Logger logger = LoggerFactory.getLogger(CollectTweets.class);
	private static List<String> messages = new ArrayList<String>();
	static class Config {
		String consumerKey;
		String consumerSecret;
		String token;
		String secret;
		int limit;
		String googleApiKey;
		String [] terms;
		String output;
		@Override
		public boolean equals(Object arg0) {
			CollectTweets.Config c = (CollectTweets.Config) arg0;
			return (this.consumerKey.equals(c.consumerKey)) 
					&& (this.consumerSecret.equals(c.consumerSecret))
					&& (this.limit == c.limit)
					&& (this.secret.equals(c.secret))
					&& (Arrays.deepEquals(this.terms, c.terms))
					&& (this.token.equals(c.token));
		}
		
	}
	class Tweet {
		String text;
	}
	public static class Background implements Runnable {
		
		private BlockingQueue<String> queue;
		private Config config;
		private Client client;
		public Background(BlockingQueue<String> queue, Config config, Client client) {
			this.queue = queue;
			this.config = config;
			this.client = client;
		}

		private String formatUnicode(String inString) throws UnsupportedEncodingException {
		    byte[] converttoBytes = inString.getBytes("UTF-8");
		    return new String(converttoBytes, "UTF-8");				
		}
		@Override
		public void run() {
			boolean gravar = false;
			FileWriter fw = null;
			logger.info("@@@ Starting new thread!");
			if (config.output != null) {
				gravar = true;
				try {
					fw = new FileWriter(config.output + "/tweets.txt");
				} catch (IOException e) {
					logger.error(">>> Error openning file: " + e.getMessage());
					return;
				}
			}
			Gson gson = new GsonBuilder().create();
			while (!client.isDone()) {
				  try {
					String msg = queue.take();
					Tweet tweet = gson.fromJson(msg, Tweet.class);
					messages.add(tweet.text);
					if(gravar) {
						fw.write(tweet.text + "\n");
					}
					if(messages.size() >= config.limit) {
						logger.info("@@@ Finished collecting!");
						client.stop();
						if(gravar) {
							fw.close();
						}
						break;
					}
				} catch (InterruptedException e) {
					logger.error(">>> Queue interrupted!");
					break;
				} catch (IOException e) {
					logger.error(">>> Error writing file: " + e.getMessage());
					return;
				}
				  
			}
		}
	}
	
	public static boolean collect(String configPath) {
		boolean resultado = false;
		logger.info("@@@ Starting tweet collecting process...");
		try {
			Config config = getConfig(configPath);
			messages.clear();

		    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
		    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		    // add some track terms
		    endpoint.trackTerms(Arrays.asList(config.terms));

		    Authentication auth = new OAuth1(config.consumerKey, config.consumerSecret, 
		    		config.token, config.secret);
		    // Authentication auth = new BasicAuth(username, password);

		    // Create a new BasicClient. By default gzip is enabled.
		    Client client = new ClientBuilder()
		            .hosts(Constants.STREAM_HOST)
		            .endpoint(endpoint)
		            .authentication(auth)
		            .processor(new StringDelimitedProcessor(queue))
		            .build();

		    // Establish a connection
		    client.connect();
			
			Thread t = new Thread(new Background(queue,config,client));
			t.start();
			t.join();
			/*
		    for (int msgRead = 0; msgRead < config.limit; msgRead++) {
		        String msg = queue.take();
		        System.out.println(msg);
		        messages.add(msg);
		      }
		      */
			resultado = true;
			
		}
		catch (Exception ex) {
			logger.error(">>> Error getting config file. Path: " 
					+ configPath + ", error: " + ex.getMessage());
		}
		return resultado;
	}
	public static Config getConfig(String configPath) throws Exception {
		Config config = null;
		FileInputStream input = new FileInputStream(new File(configPath));
		if(input == null) {
			logger.error(">>> Path not found: " + configPath);
			throw new Exception("Path not found");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line);
	    }
	    Gson gson = new GsonBuilder().create();
	    config = gson.fromJson(out.toString(), Config.class);   
		return config;
	}
}
