package ngordnet;


import java.util.Collection;
import java.util.HashMap;

import edu.princeton.cs.introcs.In;

public class NGramMap {
    /** Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME. */
    private HashMap<Integer, YearlyRecord> recsForYears = new HashMap<Integer, YearlyRecord>();
    private TimeSeries<Long> yearToTotal = new TimeSeries<Long>();
    private int stYr;
    private int endYr;
    
    public NGramMap(String wordsFilename, String countsFilename) {
        
        In theFile = new In(wordsFilename);
        String[] currLn;

        Integer currYear;
        String currWord;
        Integer currCount;

        //Read the file and construct the YearlyRecords
        while (theFile.hasNextLine()) {
            currLn = theFile.readLine().split("\t");
            currWord = currLn[0];
            currYear = Integer.parseInt(currLn[1]);
            currCount = Integer.parseInt(currLn[2]);
            
            YearlyRecord yr = recsForYears.get(currYear);
            if (yr == null) {
                yr = new YearlyRecord();
            }
            yr.put(currWord, new Integer(currCount));
            
            recsForYears.put(currYear, yr);
        }
        
        Long currTotal;
        //Read the file and construct the TimeSeries records
        In countsFile = new In(countsFilename);
        while (countsFile.hasNextLine()) {
            currLn = countsFile.readLine().split(",");
            currYear = Integer.parseInt(currLn[0]);
            currTotal = Long.parseLong(currLn[1]);
            yearToTotal.put(currYear, currTotal);
        }

        stYr = yearToTotal.firstKey();
        endYr = yearToTotal.lastKey();
    }
    
    /** Returns the absolute count of WORD in the given YEAR. If the word
      * did not appear in the given year, return 0. */
    public int countInYear(String word, int year) {
        YearlyRecord yr = recsForYears.get(new Integer(year));
        if (yr == null) {
            return 0;
        }
        return yr.count(word);
    }

    /** Returns a defensive copy of the YearlyRecord of WORD. */
    public YearlyRecord getRecord(int year) {
        YearlyRecord yr = recsForYears.get(new Integer(year));
        if (yr != null) {
            YearlyRecord yrCopy = new YearlyRecord();
            Collection<String> words = yr.words();
            for (String w : words) {
                yrCopy.put(w, yr.count(w));
            }
            return yrCopy;
        }
        return null;
    }

    /** Returns the total number of words recorded in all volumes. */
    public TimeSeries<Long> totalCountHistory() {
        return (TimeSeries<Long>) yearToTotal;
    }

    /** Provides the history of WORD between STARTYEAR and ENDYEAR . */
    public TimeSeries<Integer> countHistory(String word, int startYear, int endYear) {
        TimeSeries<Integer> ch = new TimeSeries<Integer>();
        for (Integer yrKey : recsForYears.keySet()) {
            if ((yrKey.intValue() >= startYear) && (yrKey.intValue() <= endYear)) {
                YearlyRecord yrRec = recsForYears.get(yrKey);
                int noOfOccurs = yrRec.count(word);
                if (noOfOccurs > 0) {
                    ch.put(yrKey, new Integer(noOfOccurs));
                }
            }   
        }
        return ch;
    }

    /** Provides a defensive copy of the history of WORD. */
    public TimeSeries<Integer> countHistory(String word) {
        TimeSeries<Integer> ch = new TimeSeries<Integer>();
        for (Integer yrKey : recsForYears.keySet()) {
            YearlyRecord yrRec = recsForYears.get(yrKey);
            int noOfOccurs = yrRec.count(word);
            if (noOfOccurs > 0) { 
                ch.put(yrKey, new Integer(noOfOccurs));
            }
        }
        return ch;      
    }

    /** Provides the relative frequency of WORD between STARTYEAR and ENDYEAR. */
    public TimeSeries<Double> weightHistory(String word, int startYear, int endYear) {
        TimeSeries<Integer> ch = countHistory(word, startYear, endYear);
        return ch.dividedBy(yearToTotal);       
    }

    /** Provides the relative frequency of WORD. */
    public TimeSeries<Double> weightHistory(String word) {
        TimeSeries<Integer> ch = countHistory(word);
        return ch.dividedBy(yearToTotal);
    }

    /** Provides the summed relative frequency of all WORDS between
      * STARTYEAR and ENDYEAR. */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words, 
                              int startYear, int endYear) {
        TimeSeries<Double> swh = new TimeSeries<Double>();
        for (String w : words) {
            swh = swh.plus(weightHistory(w, startYear, endYear));
        }
        return swh;
    }

    /** Returns the summed relative frequency of all WORDS. */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words) {
        TimeSeries<Double> swh = new TimeSeries<Double>();
        for (String w : words) {
            swh = swh.plus(weightHistory(w));
        }
        return swh;
    }

    /** Provides processed history of all words between STARTYEAR and ENDYEAR as processed
      * by YRP. */
    public TimeSeries<Double> processedHistory(int startYear, int endYear,
                                               YearlyRecordProcessor yrp) {
        TimeSeries<Double> toReturn = new TimeSeries<Double>();
        for (int yr = startYear; yr <= endYear; yr++) { 
            if (recsForYears.containsKey(yr)) {
                YearlyRecord yearRecords = recsForYears.get(yr);
                double toAdd = yrp.process(yearRecords);
                toReturn.put(yr, toAdd);
            }
        }
        return toReturn;
    }

    /** Provides processed history of all words ever as processed by YRP. */
    public TimeSeries<Double> processedHistory(YearlyRecordProcessor yrp) {
        return processedHistory(stYr, endYr, yrp);
    }
}
