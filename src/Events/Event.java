package Events;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import engine.Button;

public abstract class Event {
    public abstract void start();
    
    public abstract void update();
    
    public abstract void render(Graphics g);

    public  boolean finished = false;

    public String startMessage;
    public String deathMessage;
    
    public abstract void click(int x, int y);
    
    public List<Button> buttons = new ArrayList<>();

    protected void disableAllButtons(){
        for (Button button : buttons) {
            button.clickable = false;
        }
    }

    protected void enableAllButtons(){
        for (Button button : buttons) {
            button.clickable = true;
        }
    }
}
