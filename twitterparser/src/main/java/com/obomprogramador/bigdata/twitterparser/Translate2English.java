package com.obomprogramador.bigdata.twitterparser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.logging.Level;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Translate2English {

	private HttpClient client = new DefaultHttpClient();
	private Logger logger = LoggerFactory.getLogger(Translate2English.class);
	private String apiKey;
	private String beginURL = "https://www.googleapis.com/language/translate/v2?key=";
	private class Result {
		Translation [] translations;
	}
	private class Translation {
		String translatedText;
		String detectedSourceLanguage;
	}
	private class TranslationResult {
		Result data;
	}
	public Translate2English(String apiKey) {
		super();
		this.apiKey = apiKey;
	}
	
	public String translate(String source) {
		String output = null;
		logger.debug("@@@ Translate: " + source);
		HttpGet request = new HttpGet(
				this.beginURL 
				+ this.apiKey
				+ "&target=en&q="
				+ URLEncoder.encode(source)
				);
		try {
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader
			  (new InputStreamReader(response.getEntity().getContent()));
			StringBuilder sb = new StringBuilder();    
			String line = "";
			while ((line = rd.readLine()) != null) {
			  sb.append(line);
			} 		
			Gson gson = new GsonBuilder().create();
			TranslationResult res = gson.fromJson(sb.toString(),TranslationResult.class);
			output = res.data.translations[0].translatedText;
		}
		catch (Exception ex) {
			logger.error(">>> Error translating text: " + ex.getMessage());
			client.getConnectionManager().shutdown();
		}

	
		return output;
	}
}
