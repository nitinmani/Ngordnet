package ngordnet;

import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.In;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.SwingWrapper;
import com.xeiam.xchart.ChartBuilder;
import java.util.Set;

/** Provides a simple user interface for exploring WordNet and NGram data.
 *  @author Nitin Manivasagan
 */

/** Rishi Kolady helped me with the UI. 
 * He gave me the idea of the private methods for history/hypohist */

public class NgordnetUI {
    private static WordNet wnet;
    private static NGramMap ngm;
    private static int stDate;
    private static int endDate;

    private static void histMap(String[] token) { 
        Chart ch = new ChartBuilder().width(800).height(600).
                            xAxisTitle("years").yAxisTitle("data").build();

        for (String w : token) {
            TimeSeries<Double> hist = ngm.weightHistory(w, stDate, endDate);
            ch.addSeries(w, hist.years(), hist.data());
        }

        new SwingWrapper(ch).displayChart();
    }

    private static void wordLenChart() {
        Chart ch = new ChartBuilder().width(800).height(600).
                            xAxisTitle("years").yAxisTitle("data").build();
        TimeSeries<Double> avgWordLengths = 
                            ngm.processedHistory(stDate, endDate, new WordLengthProcessor());
        ch.addSeries("Avg Lengths", avgWordLengths.years(), avgWordLengths.data());

        new SwingWrapper(ch).displayChart();
    }

    private static void hypoMap(String[] tokens) {
        Chart ch = new ChartBuilder().width(800).height(600).
                            xAxisTitle("years").yAxisTitle("data").build();
        for (String token : tokens) {
            Set<String> hypos = wnet.hyponyms(token);
            TimeSeries<Double> hyp = ngm.summedWeightHistory(hypos, stDate, endDate);
            ch.addSeries(token, hyp.years(), hyp.data());
        }
        new SwingWrapper(ch).displayChart();
    }

    public static void main(String[] args) {
        In in = new In("./ngordnet/ngordnetui.config");
        System.out.println("Reading ngordnetui.config...");

        String wordFile = in.readString();
        String countFile = in.readString();
        String synsetFile = in.readString();
        String hyponymFile = in.readString();
        System.out.println("\nBased on ngordnetui.config, using the following: "
                           + wordFile + ", " + countFile + ", " + synsetFile +
                           ", and " + hyponymFile + ".");
        
        wnet = new WordNet(synsetFile, hyponymFile);
        ngm = new NGramMap(wordFile, countFile);

        TimeSeries<Long> counts = ngm.totalCountHistory();
        stDate = counts.firstKey();
        endDate = counts.lastKey();

    	String word;
        String[] words;
        Integer year;
        while (true) {
            System.out.print("> ");
            String line = StdIn.readLine();
            String[] rawTokens = line.split(" ");
            String command = rawTokens[0];
            String[] tokens = new String[rawTokens.length - 1];
            System.arraycopy(rawTokens, 1, tokens, 0, rawTokens.length - 1);
            try {
                switch (command) {
                    case "quit": 
                        return;
                    case "help":
                        In helper = new In("wordnet/help.txt");
                        String helpStr = helper.readAll();
                        System.out.println(helpStr);
                        break;  
                    case "range": 
                        stDate = Integer.parseInt(tokens[0]); 
                        endDate = Integer.parseInt(tokens[1]);
                        System.out.println("Start date: " + stDate);
                        System.out.println("End date: " + endDate);
                        break; 
                    case "count":
                    	word = tokens[0];
                    	year = Integer.parseInt(tokens[1]);
                    	System.out.println(ngm.countInYear(word, year));
                    	break;                    	
                    case "hyponyms":
                        word = tokens[0];
                    	System.out.println(wnet.hyponyms(word));
                    	break;
                    case "history":
                        words = tokens;
                        histMap(words);
                        break;
                    case "hypohist":
                        words = tokens;
                        hypoMap(words);
                        break;
                    case "wordlength":
                        wordLenChart();
                        break;
                    case "zipf":
                        int zYear = Integer.parseInt(tokens[0]);
                        Plotter.plotZipfsLaw(ngm, zYear);
                    	break;
                    default:
                        System.out.println("Invalid command.");  
                        break;
                }
            } catch (RuntimeException exc) {
                System.out.println("Your arguments are incorrect");
            }
        }
    }      
} 
