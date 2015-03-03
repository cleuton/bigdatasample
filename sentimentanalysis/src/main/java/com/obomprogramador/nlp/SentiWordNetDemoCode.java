package com.obomprogramador.nlp;
//    Copyright 2013 Petter Törnberg
//
//    This demo code has been kindly provided by Petter Törnberg <pettert@chalmers.se>
//    for the SentiWordNet website.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
/*
 * Version By Cleuton Sampaio (c) 2015, based on Törnberg's work.
 * //    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 	  Part of this program was based on a StackOverflow post:
 * http://stackoverflow.com/questions/15653091/how-to-use-sentiwordnet
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentiWordNetDemoCode {

	public enum SENTIMENT {NEUTRAL,VERY_NEGATIVE, NEGATIVE, POSITIVE, VERY_POSITIVE};
	private Map<String, Double> dictionary;

	public SentiWordNetDemoCode(List<String> csv) throws IOException {
		// This is our main dictionary representation
		dictionary = new HashMap<String, Double>();

		// From String to list of doubles.
		HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

		try {
			int lineNumber = 0;

			for (String line : csv) {
				lineNumber++;

				// If it's a comment, skip this line.
				if (!line.trim().startsWith("#")) {
					// We use tab separation
					String[] data = line.split("\t");
					String wordTypeMarker = data[0];

					// Example line:
					// POS ID PosS NegS SynsetTerm#sensenumber Desc
					// a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
					// ascetic#2 practicing great self-denial;...etc

					// Is it a valid line? Otherwise, through exception.
					if (data.length != 6) {
						throw new IllegalArgumentException(
								"Incorrect tabulation format in file, line: "
										+ lineNumber);
					}

					// Calculate synset score as score = PosS - NegS
					Double synsetScore = Double.parseDouble(data[2])
							- Double.parseDouble(data[3]);

					// Get all Synset terms
					String[] synTermsSplit = data[4].split(" ");

					// Go through all terms of current synset.
					for (String synTermSplit : synTermsSplit) {
						// Get synterm and synterm rank
						String[] synTermAndRank = synTermSplit.split("#");
						String synTerm = synTermAndRank[0] + "#"
								+ wordTypeMarker;

						int synTermRank = Integer.parseInt(synTermAndRank[1]);
						// What we get here is a map of the type:
						// term -> {score of synset#1, score of synset#2...}

						// Add map to term if it doesn't have one
						if (!tempDictionary.containsKey(synTerm)) {
							tempDictionary.put(synTerm,
									new HashMap<Integer, Double>());
						}

						// Add synset link to synterm
						tempDictionary.get(synTerm).put(synTermRank,
								synsetScore);
					}
				}				
			}


			// Go through all the terms.
			for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary
					.entrySet()) {
				String word = entry.getKey();
				Map<Integer, Double> synSetScoreMap = entry.getValue();

				// Calculate weighted average. Weigh the synsets according to
				// their rank.
				// Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
				// Sum = 1/1 + 1/2 + 1/3 ...
				double score = 0.0;
				double sum = 0.0;
				for (Map.Entry<Integer, Double> setScore : synSetScoreMap
						.entrySet()) {
					score += setScore.getValue() / (double) setScore.getKey();
					sum += 1.0 / (double) setScore.getKey();
				}
				score /= sum;

				dictionary.put(word, score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Double extract(String word)
	{
	    Double total = new Double(0);
	    if(dictionary.get(word+"#n") != null)
	         total = dictionary.get(word+"#n") + total;
	    if(dictionary.get(word+"#a") != null)
	        total = dictionary.get(word+"#a") + total;
	    if(dictionary.get(word+"#r") != null)
	        total = dictionary.get(word+"#r") + total;
	    if(dictionary.get(word+"#v") != null)
	        total = dictionary.get(word+"#v") + total;
	    return total;
	}
	
	/**
	 * Analyze text based on a StackOverflow post by User Maroun Maroun:
	 * http://stackoverflow.com/questions/15653091/how-to-use-sentiwordnet
	 * @param text
	 * @return
	 */
	public SENTIMENT analyze(String text) {
		SENTIMENT sentiment = SENTIMENT.NEUTRAL;
		String[] words = text.split("\\s+"); 
		double totalScore = 0, averageScore;
		for(String word : words) {
		    word = word.replaceAll("([^a-zA-Z\\s])", "");
		    if (this.extract(word) == null)
		        continue;
		    totalScore += this.extract(word);
		}
		averageScore = totalScore;

		if(averageScore>=0.75)
			sentiment = SENTIMENT.VERY_POSITIVE;
		else if(averageScore > 0.25 && averageScore<0.5)
			sentiment = SENTIMENT.POSITIVE;
		else if(averageScore>=0.5)
			sentiment = SENTIMENT.POSITIVE;
		else if(averageScore < 0 && averageScore>=-0.25)
			sentiment = SENTIMENT.NEGATIVE;
		else if(averageScore < -0.25 && averageScore>=-0.5)
			sentiment = SENTIMENT.NEGATIVE;
		else if(averageScore<=-0.75)
			sentiment = SENTIMENT.VERY_NEGATIVE;

		return sentiment;
	}
	

}