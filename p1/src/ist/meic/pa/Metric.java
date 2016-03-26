package ist.meic.pa;

import java.util.HashMap;
import java.util.Map;

public class Metric {
    public static Map<String, MethodMetrics> metrics = new HashMap<>();


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
            //res += entry.getKey() + " " + entry.getValue().toString() + "\n";
        	res += entry.getValue().print(entry.getKey()) + "\n";
        }
        return res;
    }

    public static class MethodMetrics {
        private static Map<String, Integer> unboxedMetrics = new HashMap<>();
        private static Map<String, Integer> boxedMetrics = new HashMap<>();

        public String print(String key) {
            String res = "";
            for (Map.Entry<String, Integer> entry : unboxedMetrics.entrySet()) {
                res += key + " " + entry.getValue() + " unboxed " + entry.getKey() + "\n";
            }
           // res = res.substring(0, res.length() - 1);

            for (Map.Entry<String, Integer> entry : boxedMetrics.entrySet()) {
                res += entry.getValue()  + entry.getKey() + "\n";
            }
            //res = res.substring(0, res.length() - 1);
            return res;
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
