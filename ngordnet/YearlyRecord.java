package ngordnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

public class YearlyRecord {
    private HashMap<String, Integer> keyToValMap;
    private HashMap<Integer, HashSet<String>> valToKeyMap;
    private HashMap<String, Integer> keyToRankMap;
    private Collection<Number> sortedVals;
    private boolean cached;
    
    /** Creates a new empty YearlyRecord. */
    public YearlyRecord() {
        keyToValMap = new HashMap<String, Integer>();
        valToKeyMap = new HashMap<Integer, HashSet<String>>();
        keyToRankMap = new HashMap<String, Integer>();
        cached = false;
    }

    /** Creates a YearlyRecord using the given data. */
    public YearlyRecord(HashMap<String, Integer> otherCountMap) {
        this();
        if (otherCountMap == null) {
            return;
        }
        Set<String> keys = otherCountMap.keySet();
        for (String key: keys) {
            this.put(key, otherCountMap.get(key));
        }
    }

    /** Returns the number of times WORD appeared in this year. */
    public int count(String word) {
        Integer temp = (Integer) keyToValMap.get(word);
        if (temp != null) {
            return temp.intValue();
        }
        return 0;
    }

    /** Records that WORD occurred COUNT times in this year. */
    public void put(String word, int count) {
        this.keyToValMap.put(word, new Integer(count));
        HashSet<String> temp = valToKeyMap.get(count);
        if (temp != null) {
            this.valToKeyMap.get(count).add(word);
        } else {
            temp = new HashSet<String>();
            temp.add(word);
            this.valToKeyMap.put(new Integer(count), temp);
        }
        cached = false;   // To force rebuild the rank map
    }

    /** Returns the number of words recorded this year. */
    public int size() {
        return keyToValMap.size();
    }
    

    /** Returns all words in ascending order of count. */
    public Collection<String> words() {
        Collection<String> allWords = new ArrayList<String>();
        for (Number count: counts()) {
            for (String word: valToKeyMap.get(count)) {
                allWords.add(word);
            }
        }

        return allWords;
    }

    /** Returns all counts in ascending order of count. */
    public Collection<Number> counts() {
        Collection<Number> allCounts = new TreeSet<Number>();
        for (String key: keyToValMap.keySet()) {
            allCounts.add(keyToValMap.get(key));
        }

        return allCounts;
    }

    /** Returns rank of WORD. Most common word is rank 1. 
      * If two words have the same rank, break ties arbitrarily. 
      * No two words should have the same rank.
      */
    public int rank(String word) {
        if (cached) {
            return keyToRankMap.get(word);
        } else {
            keyToRankMap = new HashMap<String, Integer>();
            int rank = keyToValMap.size();
            for (String w : words()) {
                keyToRankMap.put(w, rank);
                rank--;
            }

            cached = true;
            return keyToRankMap.get(word);
        }
    }
} 
