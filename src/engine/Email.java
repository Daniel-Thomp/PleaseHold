package engine;

import java.util.List;
import java.util.ArrayList;

public class Email {
    public String senderName;
    public String title;
    public String text;
    public List<Email> links = new ArrayList<>();

    public Email(String senderName, String title, String text) {
        this.senderName = senderName;
        this.title = title;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Email [title=" + title + "]";
    }

}
