package engine;

import java.awt.image.BufferedImage;

public class MetubeVid {
    public String title;
    public String desc;
    public int sanityEffect;
    public BufferedImage thumbnail;
    public BufferedImage video;
    public MetubeVid(String title, String desc, int sanityEffect, BufferedImage thumbnail,
            BufferedImage video) {
        this.title = title;
        this.desc = desc;
        this.sanityEffect = sanityEffect;
        this.thumbnail = thumbnail;
        this.video = video;
    }
}
