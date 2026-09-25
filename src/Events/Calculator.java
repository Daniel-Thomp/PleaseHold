package Events;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import engine.Button;
import engine.Buttons;
import engine.Game;

public class Calculator extends Event {
    private BufferedImage calculatorScreen;
    
    private final int WIDTH = 25;
    private final int HEIGHT = 23;
    private final int COLUMN1 = 19;
    private final int ROW1 = 120;
    private Random random = new Random();
    private List<Button> disabledButtons = new ArrayList<>();
    private List<String> removeButtonMessages = new ArrayList<>();

    private int total = 0;
    private String operator = "";
    private int target;

    private int completed = 0;

    private Game game;

    public Calculator(Game game) {
        this.game = game;
        startMessage = "I need you to enter the number below";
        deathMessage = "Local Man Suffers Mental Collapse While Verifying Numbers";
        buttons.add(new Button(COLUMN1, ROW1, COLUMN1 + WIDTH, ROW1 + HEIGHT, Buttons.SEVEN));
        buttons.add(new Button(COLUMN1 + WIDTH + 2, ROW1, COLUMN1 + WIDTH * 2 + 2, ROW1 + HEIGHT, Buttons.EIGHT));
        buttons.add(new Button(COLUMN1 + WIDTH * 2 + 5, ROW1, COLUMN1 + WIDTH * 3 + 5, ROW1 + HEIGHT, Buttons.NINE));
        buttons.add(new Button(COLUMN1 + WIDTH * 3 + 7, ROW1, COLUMN1 + WIDTH * 4 + 7, ROW1 + HEIGHT, Buttons.MULT));

        buttons.add(new Button(COLUMN1, ROW1 + HEIGHT + 1, COLUMN1 + WIDTH, ROW1 + HEIGHT * 2 + 1, Buttons.FOUR));
        buttons.add(new Button(COLUMN1 + WIDTH + 2, ROW1 + HEIGHT + 1, COLUMN1 + WIDTH * 2 + 2, ROW1 + HEIGHT * 2 + 1,
                Buttons.FIVE));
        buttons.add(new Button(COLUMN1 + WIDTH * 2 + 5, ROW1 + HEIGHT + 1, COLUMN1 + WIDTH * 3 + 5,
                ROW1 + HEIGHT * 2 + 1, Buttons.SIX));
        buttons.add(new Button(COLUMN1 + WIDTH * 3 + 7, ROW1 + HEIGHT + 1, COLUMN1 + WIDTH * 4 + 7,
                ROW1 + HEIGHT * 2 + 1, Buttons.MIN));

        buttons.add(new Button(COLUMN1, ROW1 + HEIGHT * 2 + 2, COLUMN1 + WIDTH, ROW1 + HEIGHT * 3 + 2, Buttons.ONE));
        buttons.add(new Button(COLUMN1 + WIDTH + 2, ROW1 + HEIGHT * 2 + 2, COLUMN1 + WIDTH * 2 + 2,
                ROW1 + HEIGHT * 3 + 2, Buttons.TWO));
        buttons.add(new Button(COLUMN1 + WIDTH * 2 + 5, ROW1 + HEIGHT * 2 + 2, COLUMN1 + WIDTH * 3 + 5,
                ROW1 + HEIGHT * 3 + 2, Buttons.THREE));
        buttons.add(new Button(COLUMN1 + WIDTH * 3 + 7, ROW1 + HEIGHT * 2 + 2, COLUMN1 + WIDTH * 4 + 7,
                ROW1 + HEIGHT * 3 + 2, Buttons.ADD));

        buttons.add(new Button(COLUMN1 + WIDTH + 2, ROW1 + HEIGHT * 3 + 3, COLUMN1 + WIDTH * 2 + 2,
                ROW1 + HEIGHT * 4 + 3, Buttons.ZERO));
        buttons.add(new Button(COLUMN1 + WIDTH * 3 + 7, ROW1 + HEIGHT * 3 + 3, COLUMN1 + WIDTH * 4 + 7,
                ROW1 + HEIGHT * 4 + 3, Buttons.DIV));

        removeButtonMessages.add("Wasn't there more buttons?");
        removeButtonMessages.add("Its still possible, maybe");
        removeButtonMessages.add("Is something missing?");

        try {
            calculatorScreen = ImageIO.read(new File("src//assets//images//CalculatorScreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(calculatorScreen, game.zeroX, game.zeroY, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 15));

        String text = "Target: " + String.valueOf(target);

        g.drawString(text, 10+game.zeroX, 100+game.zeroY);

        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();

        if (operator == "") {
            text = String.valueOf(total);

        } else {
            text = operator;
        }

        int textWidth = fm.stringWidth(text);
        g.drawString(text, 120 - textWidth+game.zeroX, 61 + game.zeroY);

        g.setColor(new Color(239,241,243));
        for (Button button : disabledButtons) {
            g.fillRect(button.x, button.y, WIDTH, HEIGHT);
        }
    }

    public void targetGet(){
        completed++;
        if (completed >= (10 + (game.stage*2))/4) {
            game.notify("Thank you");
            finished = true;
        }else {
            game.darryl();
            newTarget();
        }
        
    }

    public void newTarget(){
        int complexity = 2 + game.stage / 2;
        int max = (int) Math.pow(5, complexity);
        target = random.nextInt(max);
    }

    public void removeButton(Button button){
        button.clickable = false;
        disabledButtons.add(button);
        game.notify(removeButtonMessages.get(random.nextInt(removeButtonMessages.size())));
    }

    @Override
    public void start() {
        newTarget();
    }

    @Override
    public void update() {
        
    }

    @Override
    public void click(int x, int y) {
        for (Button button : buttons) {
            if (button.clickable && button.contains(x, y)) {
                game.sound.beep.setFramePosition(0);
                game.sound.beep.start();
                game.changeSanity(-1);
                if (button.action.op != null) {
                    operator = button.action.op;
                } else {
                    if (game.getSanity() < 75) {
                        int rand = random.nextInt(100);
                        int chance;
                        if (game.getSanity() < 25) {
                            chance = 15;
                        } else if (game.getSanity() < 50) {
                            chance = 10;
                        } else {
                            chance = 5;
                        }
                        if (rand < chance) {
                            removeButton(button);

                        }
                    }
                    if (operator == "") {
                        total = button.action.number;
                    } else {
                        calc(button.action.number);
                    }
                }
            }
        }
    }

    public void calc(int x) {
        if (operator == "") {
            total = x;
        } else {
            switch (operator) {
                case "x":
                    total *= x;
                    break;
                case "/":
                    if (x == 0) {
                        game.dead("Man Tries To Divide By Zero, Dies");
                    } else {
                        total /= x;
                    }
                    break;
                case "+":
                    total += x;
                    break;
                case "-":
                    total -= x;
                    break;
                default:
                    break;
            }
            operator = "";
        }
        if (total == target) {
            targetGet();
        } else {
            Random random = new Random();
            if (game.getSanity() < 25 && random.nextInt(10) == 0) {
                String s = String.valueOf(target);
                String s2 = "";
                for (int i = 0; i < s.length(); i++) {
                    s2 = s.charAt(i) + s2;
                }
                target = Integer.parseInt(s2);
                game.notify("oops");
            }
            if (random.nextInt(3) == 0) {
                game.darryl();
            }
        }
    }
    
}
