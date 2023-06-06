import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Playlist extends MusicCollection {

    ImageIO playlistCover;
    
    public Playlist(String name) {
        super(name);
    }

    public Playlist() {
        super("New Playlist");
    }

    public void setName(String name) {
        this.name = name;
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
            super.originalSongList = new ArrayList<Song>(super.songList);

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

    /*
     *  Reorders a song in the playlist given the index of a song and the index of the new position
     */
    public boolean reorder(int oldIndex, int newIndex) {
        if(oldIndex == newIndex || oldIndex < 0 || oldIndex >= songList.size() || newIndex < 0 || newIndex >= songList.size()) {
            return false;
        }
        Song temp = getSongAt(oldIndex);
        this.remove(temp);
        this.add(temp, newIndex);
        DataManager.savePlaylists();
        return true;
    }
}
