package Events;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.Button;
import engine.Buttons;
import engine.Game;

public class News extends Event {
    private BufferedImage news;
    private Game game;
    private String message;

    public News(Game game, String message, String filename) {
        this.game = game;
        this.message = message;
        buttons.add(new Button(67, 245, 80, 257, Buttons.HOME));

        buttons.add(new Button(62, 202, 83, 224, Buttons.HANG_UP));
        try {
            news = ImageIO.read(new File("src//assets//images//" + filename + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(news, game.zeroX, game.zeroY, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        game.printMultiLineString(message, 8 + game.zeroX, 130 + game.zeroY, g, 20);
    }

    @Override
    public void start() {
        game.notify("Thank you for calling, were we able to meet your needs in a timely maner?");
    }

    @Override
    public void update() {
    }

    @Override
    public void click(int x, int y) {
        for (Button button : buttons) {
            if (button.contains(x, y)) {
                // game.restart();
                return;
            }
        }
    }
}
