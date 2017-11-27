package filestat;

import java.util.HashMap;
import java.util.Map;

public class WordStat {
    private HashMap<String, Integer> stat;

    public WordStat() {
        stat = new HashMap<>();
    }

    public void add(String word) {
        if (stat.containsKey(word)) {
            Integer val = stat.get(word);
            stat.put(word, ++val);
        } else {
            stat.put(word, 1);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : stat.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            builder.append(key).append(" : ").append(value).append("\n");
        }
        return builder.toString();
    }
}
