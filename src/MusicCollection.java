import java.util.ArrayList;

public class MusicCollection {
    // Collection metadata
    public String name;
    public int numSongs; // number of songs in the list
    public int totalLength; // total length in seconds
    public Long byteSize; // combined bytes of all songs in the list

    // Song list that music player reads from. Subject to change if shuffle is toggled
    // All getters and metadata calculations are done on this list
    public ArrayList<Song> songList; 

    // Toggle for shuffling
    public Boolean isShuffled = false;

    // (Starts null) Creates a deep copy of the songList when shuffle is toggled on. 
    //Always stays the same so that we can revert back to it once shuffle is toggled off
    public ArrayList<Song> originalSongList = null;
    

    public MusicCollection(String name) {
        songList = new ArrayList<Song>();
        originalSongList = new ArrayList<Song>();
        this.name = name;
    }

    /*
     *  Returns true if a song has been added, false if the song already exists in the collection
     */
    public boolean add(Song song) {
        // Duplicates songs are not allowed
        if(songList.contains(song)) {
            return false;
        }

        // When adding while shuffled, add to the original list and leave the current list alone
        // The song will update in the current list when shuffle is toggled off
        if(isShuffled == true) {
            originalSongList.add(song);
            return true;
        }

        songList.add(song);
        originalSongList.add(song);
        recalculateMetadata();
        return true;
    }

    public boolean add(Song song, int index) {
        // When adding while shuffled, add to the original list and leave the current list alone
        // The song will update in the current list when shuffle is toggled off
        if(isShuffled == true) {
            originalSongList.add(index, song);
            return true;
        }

        songList.add(index, song);
        originalSongList.add(index, song);
        recalculateMetadata();
        return true;
    }

    /*
     *  Removes a song from the collection
     *  Returns true if the song was found and removed, and false otherwise
     */
    public boolean remove(Song song) {
        boolean result1 = originalSongList.remove(song);
        boolean result2 = songList.remove(song);
        recalculateMetadata();
        return result1&&result2;
    }

    /*
     *  Getters use songList and NOT originalSongList
     */
    public ArrayList<Song> getSongs() {
        return songList;
    }
    public Song getSongAt(int i) {
        return songList.get(i);
    }
    public Song next(int i) {
        return songList.get(i+1);
    }
    public int indexOf(Song song) {
        return songList.indexOf(song);
    }

    /*
     *  Get functions for metadata
     */
    public String getName() {
        return name;
    }
    public int getNumSongs() {
        return numSongs;
    }
    public int getTotalLength() { // Returns total length in seconds
        return totalLength;
    }
    public String getTotalLengthString() { // Returns total length as a formated string
        int hours = totalLength/3600;
        int hoursRemainder = totalLength%3600;

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
    public Long getByteSize() {
        return byteSize;
    }

    /*
     *  Fully recalculates metadata
     */
    protected void recalculateMetadata() {
        // Updates originalSongList
        originalSongList = new ArrayList<Song>(songList);

        // Calculates numSongs
        numSongs = songList.size();

        // Calculates totalLength
        int tempTotalLength = 0;
        for(int i = 0; i<songList.size(); i++) {
            tempTotalLength = tempTotalLength + songList.get(i).getLengthInSeconds();
        }
        totalLength = tempTotalLength;

        // Calculates byteSize
        Long tempByteSize = 0L;
        for(int i = 0; i<songList.size(); i++) {
            tempByteSize = tempByteSize + songList.get(i).getFileSize();
        }
        byteSize = tempByteSize;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof MusicCollection) {
            // Checks if the names are the same
            if(((MusicCollection)o).getName().compareTo(this.name) == 0) {
                // Checks each song in the list for matches
                for(int i = 0; i<((MusicCollection)o).getNumSongs(); i++) {
                    if(!((MusicCollection)o).getSongAt(i).equals(this.getSongAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
