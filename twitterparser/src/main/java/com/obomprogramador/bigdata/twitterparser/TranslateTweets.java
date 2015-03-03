package com.obomprogramador.bigdata.twitterparser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslateTweets {
	private List<String> tweets;
	private Logger logger = LoggerFactory.getLogger(TranslateTweets.class);
	private String apiKey;
	
	public TranslateTweets(String apiKey) {
		super();
		this.apiKey = apiKey;
	}
	
	public List<String> translateAll(List<String> tweets) {
		List<String> output = null;
		logger.info("@@@ Starting translation");
		Translate2English trans = new Translate2English(this.apiKey);
		try {
			List<String> saida = new ArrayList<String>();
			for(String tweet : tweets) {
				String translated = trans.translate(tweet);
				logger.debug("@@@ Translated: " + translated);
				saida.add(translated);
			}
			output = saida;
		}
		catch (Exception ex) {
			logger.error(">>> Error translating tweets: " + ex.getMessage());
		}

		return output;
	}
}
