package engine;

import javax.sound.sampled.*;

import java.util.*;
import java.io.File;
import java.io.IOException;

public class Sound {
    private List<Clip> interims = new ArrayList<>();
    private List<Clip> standards = new ArrayList<>();
    private List<Clip> creepies = new ArrayList<>();
    private List<Clip> spookies = new ArrayList<>();
    public List<Clip> colours = new ArrayList<>();
    private Random random = new Random();
    public Clip jazz;
    public Clip click;
    public Clip beep;
    public Clip boop;
    public Clip tick;
    public Clip darryl;
    
    private int interim;

    public void load() {
        interim = 0;

        interims.add(newClip("Opening"));
        jazz = newClip("jazz");
        click = newClip("click");
        beep = newClip("beep");
        boop = newClip("boop");
        tick = newClip("tick");
        darryl = newClip("darryl");

        loadHelper(interims, "interim", 6);
        loadHelper(standards, "standard", 5);
        loadHelper(creepies, "creepy", 5);
        loadHelper(spookies, "spooky", 5);
        loadHelper(colours, "Colour-0", 4);

    }

    public Clip newClip(String file) {
        Clip clip = null;
        try {
            AudioInputStream audio = AudioSystem
                    .getAudioInputStream(new File("src\\assets\\sounds\\" + file + ".wav"));
            clip = AudioSystem.getClip();
            clip.open(audio);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return clip;
    }

    public void loadHelper(List<Clip> l, String file, int x) {
        for (int i = 1; i < x + 1; i++) {
            try {
                AudioInputStream audio = AudioSystem
                        .getAudioInputStream(new File("src\\assets\\sounds\\" + file + i + ".wav"));
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                l.add(clip);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }

    }

    public void playCreepy() {
        Clip clip = creepies.get(random.nextInt(creepies.size()));
        clip.setFramePosition(0);
        clip.start();
    }

    public void playStandard() {
        Clip clip = standards.get(random.nextInt(standards.size()));
        clip.setFramePosition(0);
        clip.start();
    }
    
    public void playSpooky() {
        Clip clip = spookies.get(random.nextInt(spookies.size()));
        clip.setFramePosition(0);
        clip.start();
    }

    public void playNextInterim() {
        jazz.stop();
        interims.get(interim).start();
        interim++;
    }
    public Clip getCurrentInterim(){
        return interims.get(interim-1);
    }

    public boolean isInterimPlaying() {
        if (interims.get(interim-1).isRunning()) {
            return true;
        }
        return false;
    }
    public void mute(){
        for (Clip clip : interims) {
            clip.stop();
        }
    }

}
