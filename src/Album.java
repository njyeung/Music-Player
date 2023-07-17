import javax.imageio.ImageIO;

public class Album extends MusicCollection{
    ImageIO albumCover;
    
    public Album(String name) {
        super(name);
    }

    @Override
    public boolean add(Song song) {
        if(super.getSongs().contains(song)) {
            return false;
        }
        return super.add(song);
    }
}
