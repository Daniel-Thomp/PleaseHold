package Events;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

import engine.Button;
import engine.Buttons;
import engine.Game;

public class MusicPlayer extends Event {
    private BufferedImage musicPaused;
    private BufferedImage musicPlaying;
    private List<String> failMessages = new ArrayList<>();
    private Color[] pattern;
    private Game game;
    private Color highlight;
    private int highlightTimer = 0;
    private int highlightIndex = 0;
    private boolean highlighting = false;
    private long time = System.currentTimeMillis();
    private Random random = new Random();

    private Button clicked;
    private boolean blinking;
    private boolean playing = false;
    private boolean nextPattern = false;
    private boolean fail = false;
    private boolean dark = false;

    private int currentColour = 0;
    private int score = 0;

    private int patternLength;

    public MusicPlayer(Game game) {
        this.game = game;
        startMessage = "we need to verify the audio system";
        deathMessage = "Memory Test Causes Mental Breakdown in Regional Caller";

        buttons.add(new Button(61, 42, 84, 65, Buttons.UP, Color.GREEN));
        buttons.add(new Button(61, 96, 84, 119, Buttons.DOWN, Color.BLUE));
        buttons.add(new Button(88, 69, 111, 92, Buttons.RIGHT, Color.RED));
        buttons.add(new Button(34, 69, 57, 92, Buttons.LEFT, Color.YELLOW));

        failMessages.add("Was that how  this song went?");
        failMessages.add("Bzzt");
        failMessages.add("Don't worry lots of people are tone deaf");

        try {
            musicPaused = ImageIO.read(new File("src//assets//images//MusicPlayerPaused.png"));
            musicPlaying = ImageIO.read(new File("src//assets//images//MusicPlayerPlaying.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(Graphics g) {

        for (Button button : buttons) {
            if (button.colour == highlight || clicked == button || dark) {
                g.setColor(Color.BLACK);
            } else {
                g.setColor(button.colour.darker());
            }

            g.fillRect(button.x + game.zeroX, button.y + game.zeroY, button.x2 - button.x, button.y2 - button.y);
        }
        if (highlighting) {
            g.drawImage(musicPlaying, game.zeroX, game.zeroY, null);
        } else {
            g.drawImage(musicPaused, game.zeroX, game.zeroY, null);
        }
        g.setColor(Color.WHITE);

        double progress = ((score * 1.0) / patternLength) * (121 - 24);

        g.drawLine(23 + game.zeroX, 176 + game.zeroY, 23 + ((int) progress) + game.zeroX, 176 + game.zeroY);
        g.fillRect(22 + ((int) progress) + game.zeroX, 175 + game.zeroY, 3, 3);

        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        String text = String.valueOf(score);
        int nudge = 0;
        if (score > 9) {
            nudge = 7;
        }
        g.drawString(text, 65 - nudge + game.zeroX, 88 + game.zeroY);
    }

    public void createPattern(int length) {
        pattern = new Color[length];

        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = buttons.get(random.nextInt(4)).colour;
        }
        showPattern();
    }

    public void showPattern() {
        disableAllButtons();
        currentColour = 0;
        playing = false;
        highlightTimer = 0;
        time = System.currentTimeMillis();
        nextPattern = true;
    }

    @Override
    public void start() {
        patternLength = game.stage * 2 + 2;
        createPattern(patternLength);
    }

    @Override
    public void update() {
        if (highlighting) {
            highlightTimer += (System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
            if (highlightTimer > 750) {
                highlightTimer = 0;
                if (highlightIndex < score + 1) {
                    highlight = pattern[highlightIndex];
                    boop(highlight);
                    highlightIndex++;
                } else {
                    enableAllButtons();
                    highlighting = false;
                    highlight = null;
                    highlightIndex = 0;
                    playing = true;
                }
            } else if (highlightTimer > 500) {
                highlight = null;
            }
        } else if (blinking) {
            highlightTimer += (System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
            if (highlightTimer > 500) {
                highlightTimer = 0;
                blinking = false;
                clicked = null;
            }
        } else if (nextPattern) {
            highlightTimer += (System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
            if (highlightTimer > 500) {
                highlightTimer = 0;
                nextPattern = false;
                highlighting = true;
            }
        } else if (fail) {
            highlightTimer += (System.currentTimeMillis() - time);
            time = System.currentTimeMillis();
            if ((highlightTimer > 200 && highlightTimer < 400) || (highlightTimer > 600 && highlightTimer < 800)) {
                dark = true;
            } else if (highlightTimer > 1000) {
                fail = false;
                createPattern(patternLength);
            } else {
                dark = false;
            }
        }

    }

    @Override
    public void click(int x, int y) {
        if (playing) {
            for (Button button : buttons) {
                if (button.clickable && button.contains(x, y)) {

                    highlightTimer = 0;
                    time = System.currentTimeMillis();
                    if (pattern[currentColour] == button.colour) {
                        boop(button.colour);
                        blinking = true;
                        clicked = button;
                        currentColour++;
                        if (currentColour == patternLength) {
                            game.notify("Wait what were we doing?");
                            finished = true;
                        } else if (currentColour > score) {
                            score++;
                            showPattern();
                        } else {
                            while (random.nextInt(1, 100) > game.getSanity()) {
                                colourSwap();
                            }
                        }
                    } else {
                        game.sound.boop.setFramePosition(0);
                        game.sound.boop.start();
                        game.notify(failMessages.get(random.nextInt(failMessages.size())));
                        playing = false;
                        fail = true;
                        game.changeSanity(-15);
                        score = 0;
                    }
                    break;
                }
            }
        }
    }

    public void boop(Color colour) {
        int boop = 0;
        if (colour == Color.BLUE) {
            boop = 1;
        } else if (colour == Color.YELLOW) {
            boop = 2;
        } else if (colour == Color.GREEN) {
            boop = 3;
        }
        Clip sound = game.sound.colours.get(boop);
        sound.setFramePosition(0);
        sound.start();
    }

    private void colourSwap() {
        game.notify("Switcheroo!");
        int x = random.nextInt(4);
        int y = random.nextInt(4);
        while (x == y) {
            y = random.nextInt(4);
        }
        Color temp = buttons.get(x).colour;
        buttons.get(x).colour = buttons.get(y).colour;
        buttons.get(y).colour = temp;
    }
}
