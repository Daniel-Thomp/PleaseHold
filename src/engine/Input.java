package engine;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

public class Input implements MouseInputListener {
    private Game game;

    public Input(Game game) {
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        game.handleClick(e.getX(), e.getY());

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        game.handleMove(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        game.dragging = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        game.handleDrag(e.getXOnScreen(), e.getYOnScreen());
    }

}
