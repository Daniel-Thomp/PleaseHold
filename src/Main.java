import java.awt.Color;

import javax.swing.JFrame;

import engine.Game;

public class Main {
        public static JFrame window;
        public static void main(String[] args) {

            window = new JFrame("Please Hold");
            window.setUndecorated(true);
            window.setBackground(new Color(0, 0, 0, 0));
            Game game = new Game(window);

            window.add(game);
            window.pack();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            game.startLoop();
        }
}
