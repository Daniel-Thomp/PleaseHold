package Events;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import engine.Button;
import engine.Buttons;
import engine.Game;
import engine.MetubeVid;

public class Metube extends Event {
    private BufferedImage homePage;
    private BufferedImage videoPage;
    private Game game;
    private List<BufferedImage> mysteryThumbnails = new ArrayList<>();
    private List<MetubeVid> videos = new ArrayList<>();
    private List<String> petNames = new ArrayList<>();
    private List<String> mysteryNames = new ArrayList<>();
    private MetubeVid video1;
    private MetubeVid video2;
    private MetubeVid vidMissing;
    private boolean onVideoPage;

    private Random random = new Random();

    public Metube(Game game) {
        this.game = game;
        startMessage = "Okay! let's take a break";
        deathMessage = "Caller Distracted by Videos, Ends in Mental Collapse";
        try {
            homePage = ImageIO.read(new File("src//assets//images//Metube.png"));
            videoPage = ImageIO.read(new File("src//assets//images//MetubePlayer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        petNames.add("Pet videos *HEALING*");
        petNames.add("Checkout my pet");
        petNames.add("TOP 10 FUNNIEST PET VIDEOS");
        petNames.add("356 Reasons Cats are better than Dogs");
        petNames.add("357 Reasons Dogs are better than Cats");

        mysteryNames.add("YOU WILL NOT BELEIVE");
        mysteryNames.add("TOP 10 THINGS");
        mysteryNames.add("???");
        mysteryNames.add("RANKING EVERYTHING EVER");
        mysteryNames.add("WHY UPPERCASE RULES");

        mysteryThumbnails.add(readThumbnail("Mystery"));
        mysteryThumbnails.add(readThumbnail("Mystery2"));
        mysteryThumbnails.add(readThumbnail("Mystery2"));
        mysteryThumbnails.add(readThumbnail("Clickbait"));

        BufferedImage petThumbnail = readThumbnail("Pet");

        videos.add(new MetubeVid(getName(petNames), "Meow", 15, petThumbnail,
                readVid("CatVid")));
        videos.add(new MetubeVid(getName(petNames), "Eww",-10, petThumbnail,
                readVid("HairlessCatVid")));
        videos.add(new MetubeVid(getName(petNames), "Woof",
             15, petThumbnail,
                readVid("DogVid")));
        videos.add(new MetubeVid("DARRYL FACEREVEAL", "Due to privacy reasons Darryl's face will not be revealed", -20, readThumbnail("FaceReveal"),
                readVid("RevealVid")));
        videos.add(new MetubeVid(getName(mysteryNames), "Ha ha gottem",
                 -20, getImage(mysteryThumbnails),
                readVid("RickVid")));
        videos.add(new MetubeVid(getName(mysteryNames), "BOO!",
                 -5, getImage(mysteryThumbnails),
                readVid("SpookyVid")));
        videos.add(new MetubeVid(getName(mysteryNames), "<3",
                 30, getImage(mysteryThumbnails),
                readVid("SunsetVid")));
        videos.add(new MetubeVid(getName(mysteryNames), "fish",
                 20, getImage(mysteryThumbnails),
                readVid("Fish")));

        vidMissing = new MetubeVid("", "", 0, readThumbnail("Error"), null);

        video1 = newVideo();
        video2 = newVideo();
    }

    @Override
    public void render(Graphics g) {

        if (!onVideoPage) {
            g.drawImage(video1.thumbnail, 1+game.zeroX, 50+game.zeroY, null);
            g.drawImage(video2.thumbnail, 1+game.zeroX, 145+game.zeroY, null);
            g.drawImage(homePage, game.zeroX, game.zeroY, null);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 9));

            printMultiLineString(video1.title, 20, 125, g, 20);
            printMultiLineString(video2.title, 20, 220, g, 20);

        } else {
            g.drawImage(video1.video, 1+game.zeroX, 11+game.zeroY, null);
            g.drawImage(video2.thumbnail, 1+game.zeroX, 145+game.zeroY, null);
            g.drawImage(videoPage, game.zeroX, game.zeroY, null);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 9));
            printMultiLineString(video1.title, 7, 85, g, 25);
            printMultiLineString(video1.desc, 7, 110, g, 25);
            printMultiLineString(video2.title, 20, 220, g, 20);
        }

    }

    public void printMultiLineString(String s, int x, int y, Graphics g, int lineLength) {
        int c = 0;
        String msg = "";
        String[] text = s.split(" ");
        for (String string : text) {
            if (c + string.length() > lineLength) {
                g.drawString(msg, x+game.zeroX, y+game.zeroY);
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
        g.drawString(msg, x+game.zeroX, y+game.zeroY);
    }

    @Override
    public void start() {
        buttons.add(new Button(1, 50, 143, 115, Buttons.ONE));
        buttons.add(new Button(35, 145, 143, 210, Buttons.TWO));
        buttons.add(new Button(67, 245, 80, 257,Buttons.HOME));
    }

    public MetubeVid newVideo() {
        return videos.remove(random.nextInt(videos.size()));
    }

    public String getName(List<String> list) {
        return list.remove(random.nextInt(list.size()));
    }

    public BufferedImage getImage(List<BufferedImage> list) {
        return list.remove(random.nextInt(list.size()));
    }

    public BufferedImage readVid(String vidName) {
        try {
            return ImageIO.read(new File("src//assets//images//Vids//" + vidName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage readThumbnail(String thumbnailName) {
        try {
            return ImageIO.read(new File("src//assets//images//Thumbnails//" + thumbnailName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void click(int x, int y) {
        for (Button button : buttons) {
            if (button.contains(x, y)) {
                if (button.action == Buttons.HOME) {
                    finished = true;
                } else {
                    game.sound.click.setFramePosition(0);
                    game.sound.click.start();
                    if (!onVideoPage) {
                        if (button.action == Buttons.TWO) {
                            video1 = video2;
                        }
                        video2 = newVideo();
                        onVideoPage = true;
                        buttons.get(0).clickable = false;
                    } else {
                        video1 = video2;
                        if (videos.size()>0) {
                            video2 = newVideo();
                        } else {
                            video2 = vidMissing;
                            buttons.get(1).clickable = false;
                            game.notify("Last one, Ok?");
                        }
                        
                    }
                    game.changeSanity(video1.sanityEffect);
                }
            }
        }
    }
}
