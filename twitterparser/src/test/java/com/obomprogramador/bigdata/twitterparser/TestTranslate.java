package com.obomprogramador.bigdata.twitterparser;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTranslate {

	@Test
	public void test() {
		TranslateTweets trans = new TranslateTweets("<Google translate API key>");
		Logger logger = LoggerFactory.getLogger(TestTranslate.class);
		List<String> tweets = getTweets();
		assertTrue(tweets != null);
		List<String> translated = trans.translateAll(tweets);
		assertTrue(translated != null);
		ResourceBundle res = ResourceBundle.getBundle("twitterparser");
		String outpath = res.getString("transpath");
		try {
			FileWriter fw = new FileWriter(outpath);
			for (String linha : translated) {
				fw.write(linha + "\n");
			}
			fw.close();
		}
		catch(Exception ex) {
			logger.error(">>> Error writting translated tweets: " + ex.getMessage());
		}
	}

	private List<String> getTweets() {
		List<String> output = null;
		ResourceBundle res = ResourceBundle.getBundle("twitterparser");
		String path = res.getString("outpath");
		try {
			List<String>saida = new ArrayList<String>();
			FileInputStream input = new FileInputStream(new File(path));
			if(input == null) {
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
			System.out.println("Exception: " + ex.getMessage());
			fail(ex.getMessage());
		}
		
		return output;
	}

}
