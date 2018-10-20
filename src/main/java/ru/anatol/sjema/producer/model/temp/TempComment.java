package ru.anatol.sjema.producer.model.temp;

import java.util.LinkedList;
import java.util.List;

public class TempComment {

    private List<String> comments = new LinkedList<>();

    public TempComment() {
    }

    public String getComment() {
        return String.join("    ", comments);
    }

    public void addComment(String comment) {
        comments.add(comment);
    }

}
