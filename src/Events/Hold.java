package Events;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

import engine.Button;
import engine.Buttons;
import engine.Game;

public class Hold extends Event {
    private BufferedImage holdScreen;
    private boolean started = false;
    private Game game;
    private long callTime = 0;
    private long time;

    public Hold(Game game) {
        this.game = game;
        buttons.add(new Button(62, 202, 83, 224, Buttons.HANG_UP));
        buttons.add(new Button(35, 169, 55, 188, Buttons.SPEAKER));
        buttons.add(new Button(62, 169, 84, 188, Buttons.MUTE));
        buttons.add(new Button(91, 169, 111, 188, Buttons.DIAL));
        buttons.add(new Button(35, 135, 55, 154, Buttons.ADD));
        buttons.add(new Button(62, 135, 84, 154, Buttons.HOLD));
        buttons.add(new Button(91, 135, 111, 154, Buttons.BTH));
        try {
            holdScreen = ImageIO.read(new File("src//assets//images//holdScreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(holdScreen, game.zeroX, game.zeroY, null);
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        int hours = (int) (callTime / 3600000);
        int minutes = (int) ((callTime % 3600000) / 60000);
        int seconds = (int) ((callTime % 60000) / 1000) - 9;
        String printTime = "";
        if (String.valueOf(hours).length() == 1) {
            printTime += "0";
        }
        printTime += hours + ":";
        if (String.valueOf(minutes).length() == 1) {
            printTime += "0";
        }
        printTime += minutes + ":";
        if (String.valueOf(seconds).length() == 1) {
            printTime += 0;
        }
        if (seconds < 0) {
            printTime += "00";
        } else {
            printTime += seconds;
        }
        int nudge = 0;
        if (printTime.length() > 8) {
            nudge = 7 * (printTime.length() - 8);
        }
        g.drawString(printTime, 18 - nudge + game.zeroX, 88 + game.zeroY);
    }

    @Override
    public void start() {
        callTime = 0;
        time = System.currentTimeMillis();
        game.sound.playNextInterim();
        started = true;
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        if (started) {
            callTime += now - time;
        }
        time = now;

        Clip interim = game.sound.getCurrentInterim();

        interim.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                finished = true;
                game.sound.jazz.loop(-1);
            }
        });
    }

    public void next() {
        game.darryl();
        game.sound.playNextInterim();
        started = true;
    }

    public void incrementTime() {
        callTime += Math.pow(10, game.stage) * 1000;
    }

    @Override
    public void click(int x, int y) {
        for (Button button : buttons) {
            if (button.contains(x, y)) {
                if (button.action == Buttons.HANG_UP) {
                    game.dead("Man Fails To Stay On Phone");
                    game.sound.mute();
                } else {
                    game.sound.boop.setFramePosition(0);
                    game.sound.boop.start();
                }
            }
        }
    }
}
