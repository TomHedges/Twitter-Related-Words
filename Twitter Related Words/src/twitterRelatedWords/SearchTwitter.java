/**
 * @author Tom Hedges
 *
 * SearchTwitter twitter for a term, and list the other most common words in results
 * 
 */
package twitterRelatedWords;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SearchTwitter extends JFrame implements ActionListener {


	private static final long serialVersionUID = 1L;
	private final TextField txtfSearchTerm;
    private final TextArea txtaResults;
	

	private SearchTwitter() {

        setTitle("Search Twitter for related words");

        Label lblSearchInstructions = new Label("Please enter your search term:");
		this.add(lblSearchInstructions);

        txtfSearchTerm = new TextField(20);
        this.add(txtfSearchTerm);

        Button btnSearch = new Button("Search Twitter");
        this.add(btnSearch);
        btnSearch.addActionListener(this);

        txtaResults = new TextArea(30, 61);
        this.add(txtaResults);
		txtaResults.setFont(new Font("Courier",Font.PLAIN, 12));

		this.setResizable(true);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setSize(465, 600);
		this.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		SearchTwitter searchWindow = new SearchTwitter();
		searchWindow.setVisible(true); 
		searchWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	String getTweets(String urlTweetsSource) {

        URL urlForTweets;
		HttpURLConnection connectionTwitter;
		BufferedReader readTweets;
		String readTweetsLineByLine;
		String tweetsForParsing = "";

        try {
			urlForTweets = new URL(urlTweetsSource);
			connectionTwitter = (HttpURLConnection) urlForTweets.openConnection();
			connectionTwitter.setRequestMethod("GET");
			readTweets = new BufferedReader(new InputStreamReader(connectionTwitter.getInputStream()));
			while ((readTweetsLineByLine = readTweets.readLine()) != null) {
				tweetsForParsing += readTweetsLineByLine;
			}
			readTweets.close();
		} catch (Exception errorDetails) {
			txtaResults.setText("ERROR: " + errorDetails);
		}

		return tweetsForParsing;
	}

	@Override
	public void actionPerformed(ActionEvent searchTwitterEvent) {

        final int NUM_OF_TWEETS = 1000;
		boolean resultsPrinted = false;
		while ((!txtfSearchTerm.getText().equals("")) && (!resultsPrinted)){

			String searchText = txtfSearchTerm.getText().toUpperCase();

			txtaResults.setText("Collecting tweets containing search term: " + searchText + "\n");

			String tweetsForParsing =
                    getTweets("http://search.twitter.com/search.json?q=" + "\"" +
                    searchText.replaceAll(" " , "%20") + "\"" + "&rpp=" +
                    NUM_OF_TWEETS + "&geocode=54.673830,1.889648,500mi");

			searchText = txtfSearchTerm.getText().toUpperCase();

			Object tweetObject = JSONValue.parse(tweetsForParsing);
			JSONObject tweetResults=(JSONObject)tweetObject;
			JSONArray tweets=(JSONArray)tweetResults.get("results");

			ArrayList <String> tweetWordList = new ArrayList <String>();
			ArrayList <Integer> tweetWordCount = new ArrayList <Integer>();
			final Set <String> commonWords = new HashSet <String>(Arrays.asList(new String[]
					{
					".",",","-","!","?","'S",
					"A","AM","AN","AND","AT",
					"BE","BUT","BY",
					"CAN","CAN'T",
					"DO",
					"FOR","FROM",
					"GET","GETTING","GOT","GO",
					"HI",
					"I","IF","IN","IS","IT","IT'S","ITS",
					"I'M","IM",
                    "LOL",
					"MY","MYE","ME",
					"NO",
					"OF","ON","OR",
                    "QW",
					"RT","ROFL","ROFLMAO",
					"THAT","THE","THIS","TO","TOO",
					"WHAT","WHY","WHEN","WITH",
					"YOU","YOUR","YOU'RE",
			}));

			txtaResults.append(NUM_OF_TWEETS + " tweets collected.\nNow processing");

			for (Object tweetCounter : tweets){
				txtaResults.append(".");

				JSONObject singleTweetDetails = (JSONObject) tweetCounter;
				String tweetText = (String) singleTweetDetails.get("text");
				tweetText = tweetText.toUpperCase() + " ";
				tweetText = tweetText.replace(searchText, "");

				if (tweetText.contains(" ")){
					String word = "";
					for (int tweetCharCounter = 0; tweetCharCounter < tweetText.length(); tweetCharCounter++ ){
						if (tweetText.charAt(tweetCharCounter) != ' ' && !word.equals(" ")) {
							word = word + tweetText.charAt(tweetCharCounter);
						}
						else {
							if (!word.equals(" ") && !word.equals("")){
								boolean commonWordFound = commonWords.contains(word);

								if (!commonWordFound) {
									boolean wordFound = false;
									if (tweetWordList.size()==0){
										tweetWordList.add(word);
										tweetWordCount.add(1);
										wordFound = true;
									}
									int tweetWordListAdvancer = 0;
									while (!wordFound && tweetWordListAdvancer < tweetWordList.size() && tweetWordList.size()>1){
										if (word.equals(tweetWordList.get(tweetWordListAdvancer))){
											wordFound = true;
											tweetWordCount.set(tweetWordListAdvancer, tweetWordCount.get(tweetWordListAdvancer)+1);
										}
										tweetWordListAdvancer++;
									}
									if (!wordFound) {
										tweetWordList.add(word);
										tweetWordCount.add(1);
									}
									//System.out.println(wordFound + " " + tweetWordListAdvancer + " " + tweetWordList.size());
									//System.out.println(!wordFound && tweetWordListAdvancer<tweetWordList.size());
									//commonWordsLoop=commonWords.length-1;
								}
							}
							word = "";
						}
					}
				}
			}

			txtaResults.append("\nNow sorting " + tweetWordCount.size() + "results for display...");

			int p = 1; //position

			while (p < tweetWordList.size()){
				if (tweetWordCount.get(p).compareTo(tweetWordCount.get(p - 1)) > 0){

                    int n = tweetWordCount.get(p - 1);
					tweetWordCount.set(p - 1, tweetWordCount.get(p));
					tweetWordCount.set(p, n);
					String s = tweetWordList.get(p - 1);
					tweetWordList.set(p - 1, tweetWordList.get(p));
					tweetWordList.set(p, s);
					p = 1;
				} else {
					p++;
				}
			}
            String msg =
            "Searched " + NUM_OF_TWEETS + " tweets for search term \"" + searchText + "\".\n" +
            "Results contain " + tweetWordList.size() + " words.\n" +
            "Results are:\n";

			txtaResults.setText(msg);

			int tweetWordListAdvancer = 0;

			while (tweetWordListAdvancer < tweetWordList.size()){
				//	System.out.println(tweetWordList.get(tweetWordListAdvancer) + " occurences=" + tweetWordCount.get(tweetWordListAdvancer));
				String tempTextMem = String.format("%-25s%4d\n", tweetWordList.get(tweetWordListAdvancer), tweetWordCount.get(tweetWordListAdvancer));
				txtaResults.append(tempTextMem);
				tweetWordListAdvancer++;
			}
			resultsPrinted = true;
		}

	}
}