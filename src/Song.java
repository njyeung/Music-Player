import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Song {
    // Basic song metadata
    public String title; // Name of the song
    public int lengthInSeconds = 0; // Length of the song in seconds
    public File file; // File object of the song
    public Long fileSize = 0L; // Size in bytes

    // Advanced song metadata
    private Path path; // Path of the song
    private FileTime creationTime; // Time the song was created
    
    // For playing the song
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private DataLine.Info info;
    private Clip audioClip;
    private Long position = 0L;
    private WhenDoneOperations lineListener = new WhenDoneOperations();

    public Song(File file) throws IOException {
        // Loading generic metadata from File
        this.file = file;
        this.title = file.getName().substring(0, file.getName().length()-4);
        fileSize = file.length();

        // Loading advanced metadata from File
        path = file.toPath();
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        creationTime = attr.creationTime();

        // Attempting to create audio stream and audio clip from file
        try {
            audioStream = AudioSystem.getAudioInputStream(file);
            audioFormat = audioStream.getFormat();
            info = new DataLine.Info(Clip.class, audioFormat);
            audioClip = (Clip) AudioSystem.getLine(info);

            // Opens the clip, which is required to pull the length of it
            audioClip.open(audioStream);

            // Converts the length from microseconds to seconds
            lengthInSeconds = (int)(audioClip.getMicrosecondLength()*0.000001);
            // Closes the clip to save memory
            audioClip.close();
        } 
        // Error loading message
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            System.out.println("///// ERROR LOADING A SONG ///// ");
        }
    }

    /*
     *  Returns the "human volume" by converting the dB (log scale) of the clip to a linear scale
     *  Returns an int range [0, 10]
     */
    public int getVolume() {
        // Code and formula taken from some random math guy on stackoverflow
        FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
        return (int) Math.round(Math.pow(10f, gainControl.getValue() / 20f)*10);
    }

    /*
     *  Reverses the conversion and sets the clip's volume
     *  Range [0, 10]
     */
    public void setVolume(int volume) {
        float conversion = (float)volume/10;
        if (conversion >= 0f && conversion <= 1f) {
            // Code and formula taken from some random math guy
            FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);        
            gainControl.setValue(20f * (float) Math.log10(conversion));
        }
    }

    public void playPause() {
        // Play method can be called for pausing too
        if(audioClip != null && audioClip.isRunning()){
            // saves position so clip will start at the same time when it is played
            position = audioClip.getMicrosecondPosition();
            // During manual stop, removes line listener beforehand so the next clip will not play
            audioClip.removeLineListener(lineListener);
            // "Pauses" the clip
            audioClip.stop();
            audioClip.close();
            return;
        }

        try {
            resetAudioInputStream();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        audioClip.setMicrosecondPosition(position);
        // Adds line listener so when the clip reaches the end, the next clip will play
        audioClip.addLineListener(lineListener);
        // Start playing the clip
        audioClip.start();
    }

    public void stop() {
        // sets the position to 0 so the clip will restart the next time
        position = 0L;
        // During manual stop, removes line listener beforehand so the next clip will not play
        audioClip.removeLineListener(lineListener);
        // "Pauses" the clip
        audioClip.stop();
        audioClip.close();
    }

    public void resetAudioInputStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        audioStream = AudioSystem.getAudioInputStream(file);
        audioClip.open(audioStream);
    }

    /*
     *  Returns the percent that the track has been played at the current moment as an int
     *  If clip is paused, returns the percent that the track was played before it was paused
     */
    public int getPlayThroughPercent() {
        if(audioClip.isRunning())
            return (int)((double)audioClip.getMicrosecondPosition()/audioClip.getMicrosecondLength()*100L);
        else
            return (int)((double)position/audioClip.getMicrosecondLength()*100L);
    }

    /*
     *  Sets clip's setMicroSecondPosition() based on a percentage input
     *  Range [0, 100];
     */
    public void setPlayThroughPercent(int number) {
        Double percentage = number/100.0;
        position = Double.valueOf(percentage * (double) audioClip.getMicrosecondLength()).longValue();
        audioClip.setMicrosecondPosition(position);
    }

    /*
     *  Get functions for data
     */
    public String getTitle() {
        return title;
    }
    public File getFile() {
        return file;
    }
    public int getLengthInSeconds() {
        return lengthInSeconds;
    }
    public Long getExactPosition() {
        return audioClip.getMicrosecondPosition();
    }
    public Long getFileSize() {
        return fileSize;
    }
    public Clip getAudioClip() {
        return audioClip;
    }
    @Override
    public String toString() {
        return (title + " - " + this.getLengthToString());
    }
    public FileTime getCreationTime() {
        return creationTime;
    }

    /*
     *  Other song MUST be the exact same file path to return true
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof Song) {
            if(((Song)o).getFile().getAbsolutePath().compareTo(this.file.getAbsolutePath()) == 0) {
                return true;
            }
        }
        return false;
    }

    /*
     *  Will return true if the other song is named differently but has the same length and file size
     */
    public boolean hiddenEquals(Song other) {
        if(this.lengthInSeconds == other.getLengthInSeconds() && this.fileSize == other.getFileSize()) {
            return true;
        }
        return false;
    }

    /*
     *  Returns a string of the length of the clip formated in:
     *  H:M:S
     *  If seconds is less than 10, will automatically put a 0 in front of it to look nice
     *  If 
     */
    public String getLengthToString() {
        int hours = lengthInSeconds/3600;
        int hoursRemainder = lengthInSeconds%3600;

        int minutes = hoursRemainder/60;
        int minutesRemainder = hoursRemainder%60;

        // Adds a zero in front of the seconds if it's less than 10 so it's like :05 and doesn't look weird
        boolean secondsRequiresAZero = false;
        int seconds = minutesRemainder;
        if(seconds<10) {
            secondsRequiresAZero = true;
        }

        // Adds a zero in front of the minutes if it's less than 10 and hours > 0
        boolean minutesRequiresAZero = false;
        if(minutes<10&&hours>0){
            minutesRequiresAZero = true;
        }

        String returnString = "";
        if(hours>0) {
            returnString = returnString + hours + ":";
        }
        if(minutesRequiresAZero) {
            returnString = returnString + "0";
        }
        returnString = returnString + minutes + ":";
        if(secondsRequiresAZero) {
            returnString = returnString + "0";
        }
        return returnString = returnString + seconds;
    }


    // Method will run when a clip is done playing, resets local variables and plays the next clip
    private class WhenDoneOperations implements LineListener {
        @Override public void update(LineEvent event) {
            if (event.getType().equals(LineEvent.Type.STOP)) {
                //Line soundClip = event.getLine();

                // When a clip automatically ends, 
                position = 0L;
                // plays the next available clip
                App.playNext();
            }
        }
    }

    /*
     *  Releases all resources so the file can be deleted
     */
    public void getReadyToDelete() {
        // Removes line listener so the next clip will not play
        audioClip.removeLineListener(lineListener);
        audioClip.stop();
        audioClip.close();
        try {
            audioStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
