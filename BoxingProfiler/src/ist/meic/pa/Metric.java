package ist.meic.pa;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Metric {
    public static Map<String, MethodMetrics> metrics = new HashMap<>();
    public static Map<String, Map<String, List<String>>> result = new TreeMap<>();

    public static void add(String from, String cl, Operation op) {
        if (!metrics.containsKey(from)) {
            metrics.put(from, new MethodMetrics());
        }
        metrics.get(from).add(cl, op);
    }

    public enum Operation {
        Boxed,
        Unboxed
    }

    @Override
    public String toString() {
        String res = "";
        for (Map.Entry<String, MethodMetrics> entry : metrics.entrySet()) {
        	result.put(entry.getKey(), new TreeMap<String, List<String>>());
        	entry.getValue().print(entry.getKey());
        }
        
        for (Map.Entry<String, Map<String, List<String>>> entry : result.entrySet()) {
        	for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
        		for (String element : entry2.getValue())
        		res += entry.getKey() + " " + element + " " + entry2.getKey() + "\n";
        	}
        }
        
        //res = res.substring(0, res.length() - 1);        
        return res;
    }

    public static class MethodMetrics {
        private Map<String, Integer> unboxedMetrics = new HashMap<>();
        private Map<String, Integer> boxedMetrics = new HashMap<>();

        public void print(String key) {

        	for (Map.Entry<String, Integer> entry : unboxedMetrics.entrySet()) {
        		result.get(key).put(entry.getKey(), new ArrayList<String>());
        	}
        	
        	for (Map.Entry<String, Integer> entry : boxedMetrics.entrySet()) {
        		result.get(key).put(entry.getKey(), new ArrayList<String>());
        	}
        	
        	for (Map.Entry<String, Integer> entry : boxedMetrics.entrySet()) {
        		result.get(key).get(entry.getKey()).add("boxed " + entry.getValue());
        	}
        	
        	for (Map.Entry<String, Integer> entry : unboxedMetrics.entrySet()) {
        		result.get(key).get(entry.getKey()).add("unboxed " + entry.getValue());
        	}
        	
        }

        void add(String cl, Operation op) {
            switch (op) {
                case Boxed:
                    if (!boxedMetrics.containsKey(cl))
                        boxedMetrics.put(cl, 1);
                    else
                        boxedMetrics.put(cl, boxedMetrics.get(cl) + 1);
                    break;
                case Unboxed:
                    if (!unboxedMetrics.containsKey(cl))
                        unboxedMetrics.put(cl, 1);
                    else
                        unboxedMetrics.put(cl, unboxedMetrics.get(cl) + 1);
                    break;
            }
        }

    }
}
