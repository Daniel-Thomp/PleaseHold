package engine;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import Events.Calculator;
import Events.Clock;
import Events.Emails;
import Events.Event;
import Events.Hold;
import Events.Metube;
import Events.MusicPlayer;
import Events.News;
import Events.Settings;

import javax.swing.JPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends JPanel implements Runnable {
    private JFrame window;
    private boolean gaming;
    private int sanity = 100;
    private Event currentEvent;
    public static final int windowWidth = 1024;
    public static final int windowHeight = 1152;
    public static final int phoneWidth = 36 * 8;
    public static final int phoneHeight = 64 * 8;
    public static final int phoneX = 46 * 8;
    public static final int phoneY = 4 * 8;
    private BufferedImage background;
    private final BufferedImage screenBuffer = new BufferedImage(144, 256, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage notificationImage;
    private BufferedImage needle;
    private BufferedImage homeScreen;
    private List<Notification> notifications = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private boolean swapping = false;
    private boolean flipped = false;
    private boolean starting = false;
    public int zeroX = 0;
    public int zeroY = 0;
    private Event nextEvent;
    public int stage = 1;
    private long time;
    private Hold hold;
    private Random random = new Random();
    private List<String> normalMessages = new ArrayList<>();
    private List<String> susMessages = new ArrayList<>();
    private List<String> freakyMessages = new ArrayList<>();
    private List<String> spookyMessages = new ArrayList<>();
    public boolean dragging = false;
    private int mouseX;
    private int mouseY;
    public Sound sound;
    private Thread gameThread;
    private int frameCount = 0;

    public Game(JFrame window) {
        this.window = window;
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setDoubleBuffered(true);
        setOpaque(false);
        Input input = new Input(this);
        addMouseListener(input);
        addMouseMotionListener(input);
    }

    public int getSanity() {
        return sanity;
    }

    public void changeSanity(int diff) {
        int newSanity = sanity + diff;
        if (newSanity > 100) {
            sanity = 100;
        } else if (newSanity < 0) {
            sanity = 0;
            dead(currentEvent.deathMessage);
        } else {
            sanity = newSanity;
        }
    }

    public void dead(String deathMessage) {
        swap(new News(this, deathMessage, "GOnews"));
    }

    public void notify(String text) {
        notifications.add(new Notification(text, 2500));
    }

    public void handleClick(int x, int y) {
        if (!swapping && x >= phoneX && x <= phoneX + phoneWidth &&
                y >= phoneY && y <= phoneY + phoneHeight) {

            double scaleX = (double) screenBuffer.getWidth() / phoneWidth;
            double scaleY = (double) screenBuffer.getHeight() / phoneHeight;

            int screenX = (int) ((x - phoneX) * scaleX);
            int screenY = (int) ((y - phoneY) * scaleY);

            if (currentEvent != null) {
                currentEvent.click(screenX, screenY);
            }
        }
    }

    public void handleDrag(int x, int y) {
        if (!starting) {
            if (!dragging) {
                dragging = true;
                mouseX = x;
                mouseY = y;
            } else {
                int xDiff = mouseX - x;
                int yDiff = mouseY - y;
                window.setLocation(window.getX() - xDiff, window.getY() - yDiff);
                mouseX = x;
                mouseY = y;
            }

        }
    }

    public void handleMove(int x, int y) {
        if (currentEvent != null) {
            double scaleX = (double) screenBuffer.getWidth() / phoneWidth;
            double scaleY = (double) screenBuffer.getHeight() / phoneHeight;

            int screenX = (int) ((x - phoneX) * scaleX);
            int screenY = (int) ((y - phoneY) * scaleY);

            boolean clickable = false;
            for (Button button : currentEvent.buttons) {
                if (button.clickable && button.contains(screenX, screenY)) {
                    clickable = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    break;
                }
            }
            if (!clickable) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

    }

    public void loadEvents() {
        events.add(new Calculator(this));
        events.add(new Emails(this));
        events.add(new MusicPlayer(this));
        events.add(new Settings(this));
        events.add(new Clock(this));
    }

    public void load() {
        hold = new Hold(this);
        currentEvent = hold;
        sound = new Sound();
        sound.load();
        loadEvents();

        normalMessages.add("You're moving up in the queue.");
        normalMessages.add("Estimated wait time: 2 minutes.");
        normalMessages.add("Estimated wait time: 7 minutes.");
        normalMessages.add("You're almost there.");
        normalMessages.add("Just a little longer.");
        normalMessages.add("Still checking that for you.");
        normalMessages.add("This shouldn't take much longer.");

        susMessages.add("You've been on hold for a while.");
        susMessages.add("Are you still there?");
        susMessages.add("Just making sure you're still connected.");
        susMessages.add("Some customers disconnect around this point.");
        susMessages.add("You're very patient.");
        susMessages.add("Most people hang up by now.");

        freakyMessages.add("Please remain calm.");
        freakyMessages.add("Everything is functioning normally.");
        freakyMessages.add("You are doing well.");
        freakyMessages.add("Please continue waiting.");
        freakyMessages.add("The system appreciates your cooperation.");
        freakyMessages.add("Your patience is noted.");

        spookyMessages.add("I can see you're still here.");
        spookyMessages.add("You haven't hung up yet.");
        spookyMessages.add("Not many people make it this far.");
        spookyMessages.add("You're one of our most patient callers.");
        spookyMessages.add("You've been waiting for quite some time.");
        spookyMessages.add("You're very persistent.");

        try {
            background = ImageIO.read(new File("src//assets//images//Background.png"));
            notificationImage = ImageIO.read(new File("src//assets//images//Notification.png"));
            homeScreen = ImageIO.read(new File("src//assets//images//homeScreen.png"));
            needle = ImageIO.read(new File("src//assets//images//sanityNeedle.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void darryl() {
        if (sanity > 75) {
            notify(getRandomString(normalMessages));
        } else if (sanity > 50) {
            notify(getRandomString(susMessages));
        } else if (sanity > 25) {
            notify(getRandomString(freakyMessages));
        } else {
            notify(getRandomString(spookyMessages));
        }
    }

    public void startLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        load();
        startAnimation();
        currentEvent.start();
        gaming = true;
        while (gaming) {
            update();
            repaint();
            try {
                Thread.sleep(32);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startAnimation() {
        window.setLocation(window.getX(), 1080);
        starting = true;
    }

    private void update() {
        frameCount++;
        if (frameCount % 600 * 3 == 0) {
            if (!sound.isInterimPlaying()) {
                if (sanity > 66) {
                    sound.playStandard();
                } else if (sanity > 33) {
                    sound.playCreepy();
                } else {
                    sound.playSpooky();
                }
            }
        }
        if (!swapping && currentEvent.finished) {
            hold.finished = false;
            nextEvent();
        }
        Graphics2D sg = screenBuffer.createGraphics();
        sg.setComposite(AlphaComposite.Clear);
        sg.fillRect(0, 0, screenBuffer.getWidth(), screenBuffer.getHeight());
        sg.setComposite(AlphaComposite.SrcOver);

        if (swapping) {
            if (!flipped) {
                zeroY -= 15;
                if (zeroY <= -phoneHeight) {
                    zeroY = -phoneHeight;
                    flipped = true;
                    currentEvent = nextEvent;
                }

            } else {

                zeroY += 15;
                if (zeroY >= 0) {
                    zeroY = 0;
                    flipped = false;
                    swapping = false;
                    if (currentEvent != hold) {
                        currentEvent.start();
                    } else {
                        hold.next();
                    }
                }
            }
            sg.drawImage(homeScreen, 0, 0, null);
        }
        if (starting) {
            if (window.getY() > 50) {
                window.setLocation(window.getX(), window.getY() - 20);
            } else {
                starting = false;
            }
        }
        if (!currentEvent.finished) {
            currentEvent.update();
        }
        currentEvent.render(sg);

        long now = System.currentTimeMillis();
        for (int i = 0; i < notifications.size(); i++) {
            notifications.get(i).update((int) (now - time));
            if (notifications.get(i).remainingTime <= 0) {
                notifications.remove(notifications.get(i));
                i -= 1;
            } else {
                sg.drawImage(notificationImage, 0, 0, null);
                sg.setColor(Color.BLACK);
                sg.setFont(new Font("Monospaced", Font.BOLD, 9));
                printMultiLineStringCentered(notifications.get(i).text, 34, 25, sg, 20);
            }
        }
        time = now;
        sg.dispose();
    }

    // public void restart() {
    // gaming = false;
    // sound.mute();
    // sound.jazz.stop();
    // sound.load();
    // notifications.clear();
    // events.clear();
    // sanity = 100;
    // stage = 1;
    // hold = new Hold(this);
    // currentEvent = hold;
    // loadEvents();
    // currentEvent.start();
    // gaming = true;
    // }

    public String getRandomString(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    public void swap(Event newEvent) {
        nextEvent = newEvent;
        swapping = true;
    }

    public void nextEvent() {
        if (currentEvent != hold) {
            stage++;
            hold.finished = false;
            hold.incrementTime();
            swap(hold);
        } else {
            if (stage == 3) {
                swap(new Metube(this));
                notify(nextEvent.startMessage);
            } else if (events.size() > 0) {
                swap(events.remove(random.nextInt(events.size())));
                notify(nextEvent.startMessage);
            } else {
                swap(new News(this, "Man Makes Phone Call, Survives", "WinNews"));
                sound.darryl.start();
            }

        }
    }

    public void printMultiLineString(String s, int x, int y, Graphics g, int lineLength) {
        int c = 0;
        String msg = "";
        String[] text = s.split(" ");
        for (String string : text) {
            if (c + string.length() > lineLength) {
                g.drawString(msg, x, y);
                y += 10;
                c = 0;
                msg = "";
            }
            if (c == 0) {
                msg = string;
            } else {
                msg += " " + string;
            }
            c += string.length();
        }
        g.drawString(msg, x, y);
    }

    public void printMultiLineStringCentered(String s, int x, int middleY, Graphics g, int lineLength) {
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();

        ArrayList<String> lines = new ArrayList<>();
        StringBuilder msg = new StringBuilder();
        int c = 0;

        String[] words = s.split(" ");
        for (String word : words) {
            if (c + word.length() > lineLength) {
                lines.add(msg.toString());
                msg = new StringBuilder(word);
                c = word.length();
            } else {
                if (msg.length() != 0)
                    msg.append(" ");
                msg.append(word);
                c += word.length() + (msg.length() != 0 ? 1 : 0);
            }
        }
        if (msg.length() > 0) {
            lines.add(msg.toString());
            msg = new StringBuilder();
            c = 0;
        }

        int totalHeight = lines.size() * lineHeight;
        int startY = middleY - totalHeight / 2 + fm.getAscent();

        for (String line : lines) {
            g.drawString(line, x, startY);
            startY += lineHeight;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, windowWidth, windowHeight, null);

        g.drawImage(screenBuffer, phoneX, phoneY, phoneWidth, phoneHeight, null);
        g.drawImage(needle, 192, (int) (32 + (100 - sanity) * ((532 - 32) / 100.0)), 12, 12, null);
    }


}
