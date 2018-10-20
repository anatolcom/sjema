package ru.anatol.sjema.producer;

import java.util.HashMap;
import java.util.Map;

public class Numerator {
    private Map<String, Integer> map = new HashMap<>();

    public String getNumbered(String id) {
        Integer number = map.get(id);
        if (number == null) {
            number = new Integer(0);
        }
        map.put(id, ++number);
        return new StringBuilder(id).append(number).toString();
    }
}
