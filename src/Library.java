import java.io.File;
import java.util.ArrayList;

/*
 * ONLY OBJECT OF INSTANCE LIBRARY SHOULD EXIST AT ONCE. THIS OBJECT HOLDS ALL THE SONGS AND HANDLES LOADING IN SONGS
 */
public class Library extends MusicCollection {

    // Used to load in Songs folder
    private String fileName;
    
    /*
     *  Constructor takes in the name of the folder which all songs are stored at
     */
    public Library(String fileName) {
        super("Library (All Songs)");
        this.fileName = fileName;
    }

    /*
     *  If toggled, replaces the songList with a shuffled version of the songList
     *  If toggled again, reverts songList with the original version
     *  Returns true if shuffle has been switched on, and false otherwise
     */
    public boolean shuffle() {
        if(isShuffled == true) {
            songList = new ArrayList<Song>(originalSongList);
            isShuffled = false;
            // Recalculates metadata if the originalSongList has been modified
            super.recalculateMetadata();
            return false;
        } else {
            // Save the latest unshuffled version of the songList
            super.originalSongList = new ArrayList<Song>(super.getSongs());

            // Perform fisher yates shuffle on songList
            for(int i = super.getSongs().size()-1; i>0; i--) {
                int j = (int)(Math.random()*(i+1));
                Song temp = super.getSongs().get(i);
                super.getSongs().set(i, super.getSongs().get(j));
                super.getSongs().set(j, temp);
            }
            isShuffled = true;
            return true;
        }
    }

    public void loadSongs() {
        File musicPath = new File(fileName);
        File[] allFiles = musicPath.listFiles();
        
        for(int i = 0; i<allFiles.length; i++){ 
            try{
                super.add(new Song(allFiles[i]));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        // Sorts songList by creation time from last to first
        super.songList.sort((Song s1, Song s2) -> s2.getCreationTime().compareTo(s1.getCreationTime()));
        super.originalSongList = new ArrayList<Song>(super.songList);
    }
}
