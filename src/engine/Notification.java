package engine;

public class Notification {
    public String text;
    public int remainingTime;

    public Notification(String text, int remainingTime) {
        this.text = text;
        this.remainingTime = remainingTime;
    }

    public void update(int timeDiff){
        remainingTime -= timeDiff;
    }
}
