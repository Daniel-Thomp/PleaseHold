package Events;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import engine.Button;
import engine.Game;

public class Clock extends Event {
    private BufferedImage background;
    private BufferedImage stopped;
    private BufferedImage running;
    private List<String> failMessages = new ArrayList<>();
    private Random random = new Random();
    private int winningScore;
    private Game game;
    private boolean clocking = false;
    private int handX;
    private int handY;
    private int centreX = 71;
    private int centreY = 92;
    private double angle = Math.PI * 1.5;
    private int radius = 37;
    private double speed;
    private int targetStart;
    private int targetSize;
    private int score = 0;
    private Button start;

    public Clock(Game game) {
        this.game = game;
        startMessage = "We need to calibrate your system time";
        deathMessage = "Caller Fails Timing Test, Mental Health Severely Affected";
        start = new Button(91, 193, 132, 208);
        buttons.add(start);
        failMessages.add("The indicator wasnt aligned with the target zone.");
        failMessages.add("Aim for the green");
        failMessages.add("The clock doesnt lie, unfortunately");

        try {
            stopped = ImageIO.read(new File("src//assets//images//ClockScreenStopped.png"));
            running = ImageIO.read(new File("src//assets//images//ClockScreenTicking.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        background = stopped;
        handX = (int) (centreX + Math.cos(angle) * radius);
        handY = (int) (centreY + Math.sin(angle) * radius);
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(18 + game.zeroX, 39 + game.zeroY, 110, 110);

        g.setColor(Color.GREEN);
        g.fillArc(34 + game.zeroX, 41 + game.zeroY, radius * 2, radius * 2, targetStart, targetSize);
        g.drawImage(background, game.zeroX, game.zeroY, null);
        g.setColor(Color.BLACK);
        g.drawLine(centreX + game.zeroX, centreY + game.zeroY, handX + game.zeroX, handY + game.zeroY);

        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        String text = String.valueOf(score);
        int nudge = 0;
        if (score > 9) {
            nudge = 7;
        }
        g.drawString(text, 65 - nudge + game.zeroX, 180 + game.zeroY);
    }

    @Override
    public void start() {
        targetSize = 50 - game.stage * 4;
        targetStart = 90 - (targetSize / 2);
        winningScore = game.stage + 3;
    }

    @Override
    public void update() {
        speed = (100 - game.getSanity()) / 300.0 + 0.15;
        if (clocking) {
            angle += speed;
            if (angle > Math.PI * 2) {
                angle -= Math.PI * 2;
            }
            handX = (int) (centreX + Math.cos(angle) * radius);
            handY = (int) (centreY + Math.sin(angle) * radius);
        }
    }

    @Override
    public void click(int x, int y) {
        if (start.contains(x, y)) {
            game.sound.tick.setFramePosition(0);
            game.sound.tick.start();
            if (!clocking) {
                clocking = true;
                background = running;
            } else {
                clocking = false;
                background = stopped;

                double adjustedAngle = ((angle * 180 / Math.PI) - 270) % 360;
                if (adjustedAngle > 0 - (targetSize / 2) && adjustedAngle < targetSize / 2) {
                    score++;
                    if (score == winningScore) {
                        finished = true;
                        game.notify("Calibration Completed");
                    }
                } else {
                    game.changeSanity(-10);
                    game.notify(failMessages.get(random.nextInt(failMessages.size())));
                }
            }
        }
    }

}
