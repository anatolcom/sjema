package ru.anatol.sjema.mapper;

import java.util.Map;

public class MapperParams {

    private final Map<String, String> map;

    public MapperParams(Map<String, String> map) {
        this.map = map;
    }

//    public Map<String, String> getMap() {
//        return map;
//    }

//    public boolean contain(String name) {
//        return map.containsKey(name);
//    }

    private boolean checkContains(String name, boolean required) throws MapperException {
        if (map.containsKey(name)) {
            return true;
        } else {
            if (required) {
                throw new MapperException("param with name \"" + name + "\" is required and not found");
            }
            return false;
        }
    }

    public String getString(String name, boolean required) throws MapperException {
        if (!checkContains(name, required)) {
            return null;
        }
        return map.get(name);

    }

    public Boolean getBool(String name, boolean required) throws MapperException {
        if (!checkContains(name, required)) {
            return null;
        }
        return Boolean.parseBoolean(map.get(name));
    }

    public Integer getInt(String name, boolean required) throws MapperException {
        if (!checkContains(name, required)) {
            return null;
        }
        try {
            return Integer.parseInt(map.get(name));
        } catch (NumberFormatException ex) {
            throw new MapperException("value with name \"" + name + "\" is not int", ex);
        }
    }

    public Long getLong(String name, boolean required) throws MapperException {
        if (!checkContains(name, required)) {
            return null;
        }
        try {
            return Long.parseLong(map.get(name));
        } catch (NumberFormatException ex) {
            throw new MapperException("value with name \"" + name + "\" is not long", ex);
        }
    }
}
