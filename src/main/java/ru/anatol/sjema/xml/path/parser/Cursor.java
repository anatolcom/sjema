package ru.anatol.sjema.xml.path.parser;

public class Cursor {

    private final String text;
    private int pos = 0;

    public Cursor(String text) {
        this.text = text;
    }

    public boolean hasRead() {
        return pos < text.length();
    }

    public char readChar() throws ParserException {
        requireNonOutOfRange();
        return text.charAt(pos);
    }

    public boolean equals(char c) throws ParserException {
        requireNonOutOfRange();
        return new Character(c).equals(text.charAt(pos));
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int next() {
        return ++pos;
    }

    public void requireNonOutOfRange() throws ParserException {
        if (!hasRead()) {
            throw new ParserException("pos >= size");
        }
    }

}
