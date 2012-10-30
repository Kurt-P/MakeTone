package edu.KurtP.utils;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

/**
 * @author Kurt P
 * @version 1.2.10302012
 */
public class Tone {

    private static final int BUFFER_SIZE = 128000;
    private static File soundFile;
    private static AudioInputStream audioStream;
    private static AudioFormat audioFormat;
    private static SourceDataLine sourceLine;

    /**
     * Make a sound with java.
     *
     * @param hz - The frequency at which the sound should be.
     * @param msecs - How long the sound should play in milliseconds.
     * @throws LineUnavailableException
     */
    public static void sound(int hz, int msecs) throws LineUnavailableException {
        byte[] buf = new byte[msecs * 8];

        for (int i = 0; i < buf.length; i++) {
            double angle = i / (8000.0 / hz) * 2.0 * Math.PI;
            buf[i] = (byte) (Math.sin(angle) * 80.0);
        }

        AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        sdl.write(buf, 0, buf.length);
        sdl.drain();
        sdl.close();
    }

    /**
     * Plays a sound from a file. Mush be a *.wav file.
     *
     * @param filename - The audio file to play. Must be a *.wav file.
     */
    public static void playSound(String filename) {
        try {
            soundFile = new File(filename);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritting = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }
}
