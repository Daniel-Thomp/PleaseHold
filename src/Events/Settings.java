package Events;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.Button;
import engine.Game;

public class Settings extends Event {
    private BufferedImage settingsScreen;
    private BufferedImage topSetting;
    private BufferedImage bottomSetting;
    private BufferedImage middleSetting;
    private BufferedImage enabled;
    private BufferedImage disabled;

    private Random random = new Random();

    private Game game;
    private int buttonCount;
    private List<String> names = new ArrayList<>();
    private List<String> freakyNames = new ArrayList<>();
    private boolean superFreaky = false;
    private int jazzTimer = 0;
    private int lifeSupportTimer = 0;
    private long time;
    private Button freakyButton = null;

    private boolean started = false;

    public Settings(Game game) {
        this.game = game;
        startMessage = "It looks like some settings need to be enabled";
        deathMessage = "Local Man Disabled His Own Controls, Suffers Breakdown";
        try {
            settingsScreen = ImageIO.read(new File("src//assets//images//SettingsScreen.png"));
            topSetting = ImageIO.read(new File("src//assets//images//topSetting.png"));
            bottomSetting = ImageIO.read(new File("src//assets//images//bottomSetting.png"));
            middleSetting = ImageIO.read(new File("src//assets//images//middleSetting.png"));
            enabled = ImageIO.read(new File("src//assets//images//SettingEnabled.png"));
            disabled = ImageIO.read(new File("src//assets//images//SettingDisabled.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(settingsScreen, game.zeroX, game.zeroY, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.BOLD, 9));

        for (int i = 0; i < buttonCount; i++) {
            BufferedImage img;
            if (i == 0) {
                img = topSetting;
            } else if (i == buttonCount - 1) {
                img = bottomSetting;
            } else {
                img = middleSetting;
            }
            g.drawImage(img, 4 + game.zeroX, 34 + i * 18 + game.zeroY, null);
            if (buttons.get(i).enabled) {
                img = enabled;
            } else {
                img = disabled;
            }
            g.drawImage(img, 125 + game.zeroX, 41 + i * 18 + game.zeroY, null);

            g.drawString(buttons.get(i).name, 10 + game.zeroX, 46 + i * 18 + game.zeroY);
        }

        if (freakyButton != null && freakyButton.clickable) {
            g.drawImage(topSetting, 4 + game.zeroX, 214 + game.zeroY, null);
            g.drawString(freakyButton.name, 10 + game.zeroX, 226 + game.zeroY);
            BufferedImage img;
            if (freakyButton.enabled) {
                img = enabled;
            } else {
                img = disabled;
            }
            g.drawImage(img, 125 + game.zeroX, 221 + game.zeroY, null);
        }
    }

    @Override
    public void start() {
        buttonCount = game.stage + 3;

        names.add("WiFi");
        names.add("Bluetooth");
        names.add("Airplane Mode");
        names.add("Mobile Data");
        names.add("Location");
        names.add("Hotspot");
        names.add("VPN");
        names.add("Do Not Disturb");
        names.add("Auto Rotate");
        names.add("Dark Mode");
        names.add("Battery Saver");
        names.add("Notifications");
        names.add("Background Data");
        names.add("Sync");
        names.add("App Updates");
        names.add("Microphone Access");
        names.add("Camera Access");
        names.add("File Sharing");
        names.add("Auto Brightness");
        names.add("Screen Timeout");

        freakyNames.add("Darryl");
        freakyNames.add("More Darryl");
        freakyNames.add("Maximum Darryl");
        freakyNames.add("Please Wait");
        freakyNames.add("Still Waiting");
        freakyNames.add("Almost There");
        freakyNames.add("Trust The Process");
        freakyNames.add("Do Not Panic");
        freakyNames.add("Panic");

        for (int i = 0; i < buttonCount; i++) {
            String name;
            if (game.getSanity() < random.nextInt(200)) {
                name = freakyNames.remove(random.nextInt(freakyNames.size()));
            } else {
                name = names.remove(random.nextInt(names.size()));
            }
            buttons.add(new Button(4, 34 + i * 18, 141, 51 + i * 18, name));
        }

        for (Button button : buttons) {
            int connections = random.nextInt(2);
            for (int i = 0; i < connections; i++) {
                Button partner = buttons.get(random.nextInt(buttonCount));
                while (partner == button) {
                    partner = buttons.get(random.nextInt(buttonCount));
                }
                button.relationships.add(partner);
            }
        }

        for (int i = 0; i < 20; i++) {
            flip(buttons.get(random.nextInt(buttonCount)));
        }
        if (allButtonsEnabled()) {
            flip(buttons.get(0));
        }
        time = System.currentTimeMillis();
        started = true;
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();

        if (jazzTimer > 0) {
            jazzTimer -= now - time;
            time = now;
            if (jazzTimer <= 0) {
                freakyButton.enabled = true;
                game.notify("Jazz is eternal");
            }
        } else if (lifeSupportTimer > 0) {
            if (lifeSupportTimer > 500 && lifeSupportTimer - (now - time) < 500) {
                freakyButton.clickable = false;
            }
            lifeSupportTimer -= now - time;
            time = now;
            if (lifeSupportTimer <= 0) {
                freakyButton.name = "Back-up Life Support";
                freakyButton.clickable = true;
                freakyButton.enabled = true;
            }
        }
    }

    @Override
    public void click(int x, int y) {
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if (button.contains(x, y)) {
                game.sound.click.setFramePosition(0);
                game.sound.click.start();
                flip(button);
                if (superFreaky && y > 214) {
                    time = System.currentTimeMillis();
                    if (button.name.equals("Jazz")) {
                        jazzTimer = 200;
                    } else if (button.name.equals("Life Support")) {
                        game.notify("That looked important");
                        lifeSupportTimer = 1000;
                    } else {
                        freakyButton.clickable = false;
                        game.dead("I told you that looked important");
                    }
                } else if (!superFreaky && game.getSanity() < random.nextInt(15)) {
                    superFreaky();
                }

                if (allButtonsEnabled()) {
                    finished = true;
                }
            }
        }
    }

    public void flip(Button button) {
        toggle(button);
        for (Button partner : button.relationships) {
            toggle(partner);
        }
    }

    public boolean allButtonsEnabled(){
        for (Button button : buttons) {
            if (!button.enabled) {
                return false;
            }
        }
        return true;
    }

    public void toggle(Button button) {
        if (button.enabled) {
            button.enabled = false;
            if (started) {
                game.changeSanity(-3);
            }  
        } else {
            button.enabled = true;
        }
    }

    public void superFreaky() {
        superFreaky = true;
        String name = "";
        switch (random.nextInt(2)) {
            case 0:
                name = "Life Support";
                break;

            case 1:
                name = "Jazz";
                break;
        }
        freakyButton = new Button(4, 214, 141, 231, name);
        freakyButton.enabled = true;
        buttons.add(freakyButton);
    }
}
