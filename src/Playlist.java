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
