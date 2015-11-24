package ngordnet;

import java.util.Collection;

public class WordLengthProcessor implements YearlyRecordProcessor {
    public double process(YearlyRecord yearlyRecord) {
    	
    	Collection<String> words = yearlyRecord.words();
    	long wordLen = 0;
    	long total = 0;
    	for (String w : words) {
    		wordLen = wordLen + w.length() * yearlyRecord.count(w);
    		total = total + yearlyRecord.count(w);
    	}
    	return ((double) wordLen) / total;
    }
}
