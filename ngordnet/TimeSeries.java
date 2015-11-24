package ngordnet;
import java.util.TreeMap;
import java.util.Collection;
import java.util.ArrayList;

public class TimeSeries<T extends Number> extends TreeMap<Integer, T> {
    
    public TimeSeries() {
        super();
    }

    public TimeSeries(TimeSeries<T> ts) {
        super();
        if (ts == null) {
            return;
        }

        for (Integer timeKey : ts.keySet()) {
            this.put(timeKey, ts.get(timeKey));
        }
    }

    public TimeSeries(TimeSeries<T> ts, int startYear, int endYear) {
        super();
        if (ts == null) {
            return;
        }

        for (Integer timeKey: ts.keySet()) {
            if (timeKey >= startYear && timeKey <= endYear) {
                this.put(timeKey, ts.get(timeKey));
            }
        }
    }

    public Collection<Number> data() {
        return (Collection<Number>) this.values();
    }

    public TimeSeries<Double> dividedBy(TimeSeries<? extends Number> ts) 
        throws IllegalArgumentException {
        TimeSeries<Double> toReturn = new TimeSeries<Double>();
        for (Integer thisKey : this.keySet()) {
            if (ts.get(thisKey) == null) {
                throw new IllegalArgumentException("This value does not exist for " + thisKey);
            } else {
                double quotient = new Double(this.get(thisKey).doubleValue()
                                                 / ts.get(thisKey).doubleValue());
                toReturn.put(thisKey, quotient);
            }
        }
        return toReturn;
    }

    public Collection<Number> years() {
        ArrayList<Number> t = new ArrayList<Number>(this.keySet());
        return (Collection<Number>) t;
    }   

    public TimeSeries<java.lang.Double> plus(TimeSeries<? extends java.lang.Number> ts) {
        TimeSeries<Double> toReturn = new TimeSeries<Double>();

        for (Integer thisKey : this.keySet()) {
            double valInThisSet = this.get(thisKey).doubleValue();
            if (ts.get(thisKey) == null) {
                toReturn.put(thisKey, (Double) valInThisSet);
            } else {
                Double sum = new Double(valInThisSet + ts.get(thisKey).doubleValue());
                toReturn.put(thisKey, sum);
            }
        }
        
        for (Integer tsKey : ts.keySet()) {
            double valInTS = ts.get(tsKey).doubleValue();
            if (this.get(tsKey) == null) {
                toReturn.put(tsKey, (Double) valInTS);
            }
        }
        return toReturn;
    }
}
