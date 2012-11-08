/**
 * @author Tom Hedges
 * 
 * SearchTwitter twitter for a term, and list the other most common words in results
 * 
 */
package twitterRelatedWords;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class SearchTwitter extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TextField txtfSearchTerm;
	Button btnSearch;
	TextArea txtaResults;
	

	SearchTwitter(String title) {
		setTitle(title);

		Label lblSearchInstructions = new Label("Please enter your seach term:");
		txtfSearchTerm = new TextField(20);
		txtaResults = new TextArea(10, 61);
		btnSearch = new Button("Search Twitter");

		this.add(lblSearchInstructions);
		this.add(txtfSearchTerm);
		this.add(btnSearch);
		this.add(txtaResults);
		txtaResults.setFont(new Font("Courier",Font.PLAIN, 12));
		btnSearch.addActionListener(this);

		this.setResizable(false);

		this.setLayout(new FlowLayout(FlowLayout.LEFT));

		this.setSize(465, 300);
		this.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		SearchTwitter searchWindow = new SearchTwitter("Search Twitter for related words"); 
		searchWindow.setVisible(true); 
		searchWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public String getTweets(String urlTweetsSource) {
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
		final int NUM_OF_TWEETS = 100;
		boolean resultsPrinted = false;
		while ((!txtfSearchTerm.getText().equals("")) && (!resultsPrinted)){

			String searchText = txtfSearchTerm.getText().toUpperCase();
			//txtaResults.setText(searchText + "SearchTwitterEvent: " + searchTwitterEvent + "\n");

			txtaResults.setText("Collecting tweets containing search term: " + searchText + "\n");

			String tweetsForParsing = getTweets("http://search.twitter.com/search.json?q=" + "\"" + searchText.replaceAll(" " , "%20") + "\"" + "&rpp=" + NUM_OF_TWEETS + "&geocode=54.673830,1.889648,500mi");

			searchText = txtfSearchTerm.getText().toUpperCase();

			//txtaResults.setText(txtaResults.getText() + tweetsForParsing + "\n");

			//System.out.println(searchText);

			//System.out.println("=======decode=======");

			Object tweetObject = JSONValue.parse(tweetsForParsing);
			JSONObject tweetResults=(JSONObject)tweetObject;

			JSONArray tweets=(JSONArray)tweetResults.get("results");

			//System.out.println("======field \"results\"==========");


			ArrayList<String> tweetWordList = new ArrayList<String>();
			ArrayList<Integer> tweetWordCount = new ArrayList<Integer>();
			String[] commonWords = {
					".",
					",",
					"-",
					"!",
					"?",
					"'S",
					"A",
					"AM",
					"AN",
					"AND",
					"AT",
					"BE",
					"BUT",
					"BY",
					"CAN",
					"CAN'T",
					"DO",
					"FOR",
					"FROM",
					"GET",
					"GETTING",
					"GOT",
					"GO",
					"HI",
					"I",
					"IF",
					"IN",
					"IS",
					"IT",
					"IT'S",
					"I'M",
					"MY",
					"ME",
					"NO",
					"OF",
					"ON",
					"OR",
					"RT",
					"THAT",
					"THE",
					"THIS",
					"TO",
					"TOO",
					"WHAT",
					"WHY",
					"WHEN",
					"WITH",
					"YOU",
					"YOUR",
					"YOU'RE",
			};

			txtaResults.append(NUM_OF_TWEETS + " tweets collected.\nNow processing");

			for (Object tweetCounter : tweets){
				txtaResults.append(".");

				JSONObject singleTweetDetails = (JSONObject) tweetCounter;
				String tweetText = (String) singleTweetDetails.get("text");
				//System.out.println(tweetText);
				tweetText = tweetText.toUpperCase() + " ";
				//System.out.println(tweetText);

				//System.out.println(searchText);
				tweetText = tweetText.replace(searchText, "");
				//System.out.println(tweetText);

				if (tweetText.contains(" ")){
					String word = "";
					for (int tweetCharCounter = 0; tweetCharCounter < tweetText.length(); tweetCharCounter++ ){
						//System.out.println("xx"+tweetCharCounter);
						if (tweetText.charAt(tweetCharCounter) != ' ' && !word.equals(" ")) {
							word = word + tweetText.charAt(tweetCharCounter);
							//System.out.println("a" +word);
						}
						else {
							if (!word.equals(" ") && !word.equals("")){
								//System.out.println("b" + tweetCharCounter);
								//System.out.println(word + " BUILT----------------------------------------------------");

								////int commonWordsLoop = 0;
								//System.out.println(commonWordsLoop);
								//System.out.println(commonWords[commonWordsLoop]);

								boolean commonWordFound = false;
								for (int commonWordsLoop = 0; commonWordsLoop<commonWords.length; commonWordsLoop++){
									if (word.equals(commonWords[commonWordsLoop])) {
										commonWordFound = true;
									}
								}

								if (!commonWordFound) {
									//System.out.println(word);
									boolean wordFound = false;
									if (tweetWordList.size()==0){
										tweetWordList.add(word);
										//System.out.println(word + "added");
										tweetWordCount.add(1);
										wordFound = true;
									}
									int tweetWordListAdvancer = 0;
									while (!wordFound && tweetWordListAdvancer<tweetWordList.size() && tweetWordList.size()>1){
										if (word.equals(tweetWordList.get(tweetWordListAdvancer))){
											//System.out.println(word);
											wordFound = true;
											tweetWordCount.set(tweetWordListAdvancer, tweetWordCount.get(tweetWordListAdvancer)+1);
										}
										tweetWordListAdvancer++;
									}
									if (!wordFound) {
										tweetWordList.add(word);
										//System.out.println(word + "added - " + commonWordsLoop + commonWords[commonWordsLoop]);
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



			//System.out.println("end list?" + tweetWordList.size());
			//System.out.println("end count?" + tweetWordCount.size());

			txtaResults.append("\nNow sorting " + tweetWordCount.size() + "results for display...");

			int pos = 1;

			while (pos<tweetWordList.size()){
				if (tweetWordCount.get(pos).compareTo(tweetWordCount.get(pos-1))>0){
					int tempNumMem = tweetWordCount.get(pos-1);
					tweetWordCount.set(pos-1,tweetWordCount.get(pos));
					tweetWordCount.set(pos,tempNumMem);
					String tempStrMem = tweetWordList.get(pos-1);
					tweetWordList.set(pos-1,tweetWordList.get(pos));
					tweetWordList.set(pos,tempStrMem);
					pos = 1;
				} else {
					pos++;
				}
			}

			txtaResults.setText("Searched " + NUM_OF_TWEETS + " tweets for search term \"" + searchText + "\".\nResults contain " + tweetWordList.size() + " words.\nResults are:\n");

			int tweetWordListAdvancer = 0;
			while (tweetWordListAdvancer<tweetWordList.size()){
				//	System.out.println(tweetWordList.get(tweetWordListAdvancer) + " occurences=" + tweetWordCount.get(tweetWordListAdvancer));
				String tempTextMem = String.format("%-25s%4d\n", tweetWordList.get(tweetWordListAdvancer), tweetWordCount.get(tweetWordListAdvancer));
				txtaResults.append(tempTextMem);
				tweetWordListAdvancer++;
			}

			//for (int tweetWordListAdvancer = 0; tweetWordListAdvancer==tweetWordList.size()-1; tweetWordListAdvancer++){
			//	System.out.println(tweetWordList.get(tweetWordListAdvancer) + " occurences=" + tweetWordCount.get(tweetWordListAdvancer));
			//} 



			//txtaResults.setText(txtaResults.getText() + getTweets("http://search.twitter.com/search.json?q=" + txtfSearchTerm.getText()) + "\n");
			resultsPrinted = true;
		}
		resultsPrinted = false;
	}
}