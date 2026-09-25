package Events;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import engine.Button;
import engine.Buttons;
import engine.Email;
import engine.Game;

public class Emails extends Event {
    private BufferedImage emailScreen;
    private Email end;
    private Game game;
    private Email currentEmail;
    private Random random = new Random();
    private List<String> repeatMessage = new ArrayList<>();
    private List<Email> weirds = new ArrayList<>();
    private Set<Email> visited = new HashSet<>();
    private List<Email> chain = new ArrayList<>();
    private Boolean started = false;

    public Emails(Game game) {
        this.game = game;
        startMessage = "I've emailed you the instructions";
        deathMessage = "Caller Trapped in Endless Email Loop, Loses Sanity";
        buttons.add(new Button(0, 175, 144, 185));
        buttons.add(new Button(0, 185, 144, 195));
        buttons.add(new Button(0, 195, 144, 205));
        buttons.add(new Button(0, 205, 144, 215));
        buttons.add(new Button(4, 18, 10, 24, Buttons.BACK));
        try {
            emailScreen = ImageIO.read(new File("src//assets//images//EmailScreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(Graphics g) {

        g.drawImage(emailScreen, game.zeroX, game.zeroY, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 9));

        if (started) {
            g.drawString(currentEmail.title, 5 + game.zeroX, 45 + game.zeroY);
            g.drawString(currentEmail.senderName, 28 + game.zeroX, 60 + game.zeroY);

            g.setFont(new Font("Monospaced", Font.PLAIN, 9));
            int y = 110;
            int c = 0;
            String msg = "";
            String[] text = currentEmail.text.split(" ");
            for (String string : text) {
                if (c + string.length() > 25) {
                    g.drawString(msg, 2 + game.zeroX, y + game.zeroY);
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
            g.drawString(msg, 2 + game.zeroX, y + game.zeroY);

            g.setColor(Color.blue);
            for (int i = 0; i < currentEmail.links.size(); i++) {
                g.drawString(currentEmail.links.get(i).title, 2 + game.zeroX, 180 + i * 10 + game.zeroY);
            }
        }

    }

    @Override
    public void update() {

    }

    @Override
    public void click(int x, int y) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).clickable && buttons.get(i).contains(x, y)) {
                if (buttons.get(i).action == Buttons.BACK) {
                    game.sound.click.setFramePosition(0);
                    game.sound.click.start();
                    chain.removeLast();
                    nextLink(chain.getLast());
                } else if (buttons.get(i).action == Buttons.HOME) {
                    finished = true;
                } else {
                    game.sound.click.setFramePosition(0);
                    game.sound.click.start();
                    nextLink(currentEmail.links.get(i));
                }
            }
        }
    }

    public void nextLink(Email next) {
        currentEmail = next;
        disableAllButtons();
        if (currentEmail == end) {
            buttons.add(new Button(67, 245, 80, 257, Buttons.HOME));
        } else {
            if (visited.contains(currentEmail)) {
                game.changeSanity(-3);
                game.notify(repeatMessage.get(random.nextInt(repeatMessage.size())));
            }
            visited.add(currentEmail);

            if (chain.isEmpty() || chain.getLast() != currentEmail) {
                chain.add(currentEmail);
            }

            if (currentEmail.links.size() < 4 && random.nextInt(0, 100) > game.getSanity()) {
                currentEmail.links.add(weirds.get(random.nextInt(weirds.size())));
            }
            for (int i = 0; i < currentEmail.links.size(); i++) {
                buttons.get(i).clickable = true;
            }

            if (chain.size() > 1) {
                buttons.get(4).clickable = true;
            } else {
                buttons.get(4).clickable = false;
            }
        }
    }

    @Override
    public void start() {
        Email start = new Email(
                "Darryl",
                "Support Request",
                "Hello,This is Darryl from Waiting Inc, to solve your problem please follow the instructions bellow");
        end = new Email("Darryl",
                "Solved",
                "Thank you for your time, you may now return to the call");

        Email support1 = new Email(
                "Darryl",
                "Support Ticket Received",
                "Hello,This is Darryl from Waiting Inc.To begin troubleshooting please open the support guide below.");

        Email support2 = new Email(
                "Darryl",
                "Troubleshooting Step 1",
                "Thank you for contacting support.Please ensure your device is turned on.If the issue persists continue below.");

        Email support3 = new Email(
                "Darryl",
                "Troubleshooting Step 2",
                "We recommend restarting your device.Once completed please proceed to the next instruction.");

        Email corporate1 = new Email(
                "Automated System",
                "Satisfaction Initiative",
                "At Waiting Inc we are committed to providing an excellent customer experience.Please review the helpful resources below.");

        Email corporate2 = new Email(
                "User Experience Team",
                "Your Call Is Important",
                "We noticed you are still waiting.Your patience helps us improve our services.Please continue following the support steps.");
        Email deadEnd1 = new Email(
                "File Server",
                "Attachment Missing",
                "The file you attempted to download could not be found.Please return to the previous instructions.");

        Email deadEnd2 = new Email(
                "Support Portal",
                "Access Denied",
                "You do not have permission to access this document.Please contact support for assistance.");
        Email loop1 = new Email(
                "Support Bot",
                "FAQ Article",
                "This article may answer your question.If your problem is not listed please return to the support guide.");

        Email loop2 = new Email(
                "Help Centre",
                "More Help Required?",
                "If the problem persists please consult the troubleshooting guide again.");

        Email weird1 = new Email(
                "Darryl",
                "Follow Up",
                "Hello again.I see you are still navigating the troubleshooting process.Great work.");

        Email weird2 = new Email(
                "Darryl",
                "Important Notice",
                "We admire your persistence.Very few customers make it this far.");

        Email weird3 = new Email(
                "Unknown Sender",
                "Re: Re: Re: Re: Support",
                "You are getting closer.Or maybe further away.Hard to say.");

        start.links.add(support1);
        start.links.add(corporate1);
        start.links.add(deadEnd1);

        support1.links.add(support2);
        support1.links.add(loop1);

        support2.links.add(support3);
        support2.links.add(deadEnd2);

        corporate1.links.add(corporate2);
        corporate2.links.add(support1);

        support3.links.add(corporate1);
        support3.links.add(end);

        loop1.links.add(loop2);
        loop1.links.add(corporate2);
        loop2.links.add(loop1);
        loop2.links.add(deadEnd1);

        weird1.links.add(start);
        weird1.links.add(support3);
        weird1.links.add(end);
        weird2.links.add(corporate1);
        weird2.links.add(loop1);
        weird3.links.add(support2);
        weird3.links.add(loop2);

        weirds.add(weird3);
        weirds.add(weird2);
        weirds.add(weird1);

        nextLink(start);

        repeatMessage.add("This looks familiar");
        repeatMessage.add("Hey I've seen this one");
        repeatMessage.add("Haven't we already had this one?");
        repeatMessage.add("Again?");
        started = true;
    }
}
