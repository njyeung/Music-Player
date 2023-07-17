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
