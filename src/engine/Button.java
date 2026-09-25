package engine;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class Button {
    public int x, y, x2, y2;
    public Buttons action;
    public Color colour;
    public boolean clickable = true;
    public String name;
    public boolean enabled = true;
    public Set<Button> relationships = new HashSet<>();

    public Button(int x, int y, int x2, int y2, Buttons action) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.action = action;
    }

    public Button(int x, int y, int x2, int y2) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
    }
    

    public Button(int x, int y, int x2, int y2, String name) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.name = name;
    }

    public Button(int x, int y, int x2, int y2, Buttons action, Color colour) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.action = action;
        this.colour = colour;
    }

    public boolean contains(int px, int py) {
        return px >= x && px <= x2 &&
                py >= y && py <= y2;
    }

}
