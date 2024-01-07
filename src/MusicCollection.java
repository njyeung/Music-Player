import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

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
    public int isShuffled = 0;

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
        if(isShuffled == 1 || isShuffled == 2) {
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
        if(isShuffled == 1 || isShuffled == 2) {
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





    /*
     *          SHUFFLE ALGORITHM, I ACTUALLY DON'T UNDERSTAND MOST OF THIS EVEN
     *          THOUGH I WROTE IT. BUT IT'S PRETTY GOATED THO
     * 
     *          4:30AM 7/5/2023 - this code was written on a plane with no copilot... ikr
     */




    /*
     *  If toggled, replaces the songList with a shuffled version of the songList
     *  If toggled a second time, replaces the songList with an advanced shuffle version of songList
     *  If toggled again, reverts songList with the original version
     *  Returns true if shuffle has been switched on, and false otherwise
     */
    public int shuffle() {
        if(isShuffled == 2) {
            songList = new ArrayList<Song>(originalSongList);
            isShuffled = 0;
            // Recalculates metadata if the originalSongList has been modified
            recalculateMetadata();
            return 0;
        } else if (isShuffled == 0) {
            simpleShuffle(songList);
            isShuffled = 1;
            return 1;
            
        } else if(isShuffled == 1) {
            songList = advancedShuffle(originalSongList);
            isShuffled = 2;
            return 2;
        }
        return -1;
    }

    /*
     * No toggling required
     *  0 - no shuffle
     *  1 - simple shuffle
     *  2 - advanced shuffle
     */
    public void shuffle(int specific) {
        if(specific == 0) {
            songList = new ArrayList<Song>(originalSongList);
            isShuffled = 0;
            // Recalculates metadata if the originalSongList has been modified
            recalculateMetadata();
        } else if (specific == 1) {
            simpleShuffle(songList);
            isShuffled = 1;
            
        } else if(specific == 2) {
            songList = advancedShuffle(originalSongList);
            isShuffled = 2;
        }
        else {
            throw new IllegalArgumentException("NOT VALID SHUFFLE NUMBER");
        }
    }

    /**
     * Shuffles an arraylist using fisher yates shuffle
     * Done locally on the list, so no return is required
     * 
     * @param originalList - arraylist to shuffle
     */
    private void simpleShuffle(ArrayList<Song> list) {
        // Perform simple fisher yates shuffle on songList
        for(int i = list.size()-1; i>0; i--) {
            int j = (int)(Math.random()*(i+1));
            Song temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    /**
     * Shuffles a list, separating songs by the same artist while maintaining randomness
     * 
     * @param originalList - list of songs to shuffle
     * @return a shuffled list of songs
     */
    private ArrayList<Song> advancedShuffle(ArrayList<Song> originalList) {
        // List for storing the new list of songs
        ArrayList<Song> table = new ArrayList<Song>(originalList);
        
        // Queue of lists containing songs by the same artist, with each list shuffled before it is returned
        Song[] temp = new Song[originalList.size()];
        PriorityQueue<LinkedList<Song>> artistFrequency = getArtistFrequency(originalList.toArray(temp));
        
        artistFrequency.forEach((x)->System.out.println(x));

        // ArrayList containing "spreaded" arrays of songs by the same artist. Each array is the size of the originalList
        // with all the songs spread out among the indexes. The length of the ArrayList is the number of artists + 1 if
        // null songs exist in the originalList
        ArrayList<Song[]> expanded = new ArrayList<Song[]>();
        while(!artistFrequency.isEmpty()) {
            expanded.add(spread(table.size(), artistFrequency.poll()));
        }

        for(int i = 0; i<expanded.size(); i++){
            for(int j = 0; j<expanded.get(i).length; j++) {
                System.out.print(expanded.get(i)[j]);
            }
            System.out.println();
        }

        // Takes all the lists of artist's songs and compresses them back into a single array
        table = compress(expanded);

        // Lightly shuffle the array as final touchup
        table = stir(table, 0.5);

        System.out.println(table.size()); 
        System.out.println(table); 

        return table;
    }

    /**
     * Lightly shuffles an array of songs
     * Meant to be used as "final touch-ups" after a shuffle algorithm
     * 
     * @param array - to stir
     * @param scalor - chance to stir = scalor / length of array
     * @return an ArrayList that has been lightly stirred
     */
    private ArrayList<Song> stir(ArrayList<Song> arr, double scalor) {
        ArrayList<Song> array = new ArrayList<Song>(arr);

        Random randGen = new Random();
        double chance = scalor/array.size();

        for(int i = 0; i<array.size(); i++) {
            if(randGen.nextDouble() < chance) {
                // yates shuffle
                int j = (int)(randGen.nextInt(array.size()));
                Song temp = array.get(i);
                array.set(i, array.get(j));
                array.set(j, temp);
            }
        }

        return array;
    }

    /**
     * Receives an arraylist of arrays of songs, a random generator, and compresses the songs from all the lists into one list.
     * Returns an array the size of all the elements in the expanded arraylist
     * 
     * Example:
     *   arr1  =  [a] [ ] [b] [c] [ ] [ ] [ ] [d]
     *   arr2  =  [ ] [1] [ ] [2] [ ] [3] [ ] [ ]
     *   arr3  =  [ ] [ ] [ ] [ ] [#] [ ] [ ] [ ]
     *   result = [a] [1] [b] [c] [2] [#] [3] [d]
     * @param expanded - arraylist of arrays of songs
     * @param rand - random number generator
     * @return an compressed array of songs
     */
    private ArrayList<Song> compress(ArrayList<Song[]> expanded) {
        ArrayList<Song> returnList = new ArrayList<Song>();
        for(int i = 0; i<expanded.get(0).length; i++) {
            // Gets all songs placed at the index from all the arrays
            LinkedList<Song> toChooseFrom = new LinkedList<Song>();
            for(int j = 0; j<expanded.size(); j++) {
                if(expanded.get(j)[i]!=null) {
                    toChooseFrom.add(expanded.get(j)[i]);
                }
            }
            Collections.shuffle(toChooseFrom);
            while(!toChooseFrom.isEmpty()) {
                returnList.add(toChooseFrom.pop());
            }
        }
        return returnList;
    }

    /**
     * Creates an array of length "size" attempts to spread out the linkedlist of songs amongst the array
     * Indexes that do not contain a song are null
     * 
     * @param size - length of the array to create
     * @param songs - LinkedList of songs to spread
     * @return an array of spread out songs, with indexes that do not contain a song represented with null
     */
    private Song[] spread(int size, LinkedList<Song> songs) {
        Song[] returnArray = new Song[size];

        // Represents the "exact" index where each song should be slotted at
        double factor = returnArray.length / songs.size();

        // Sample situations of what the below code will do
        // 9/5
        // 1.8 + 1.8 + 1.8 + 1.8 + 1.8
        // 1.8 -> 3.6 -> 5.4 -> 7.2 -> 9
        // 2 -> 4 -> 5 -> 7 -> 9
        // 1 -> 3 -> 4 -> 6 -> 8 (-1 to refactor into array)

        // 5/4
        // 1.25 + 1.25 + 1.25 + 1.25
        // 1.25 -> 2.5 -> 3.75 -> 5
        // 1 -> 3 -> 4 -> 5
        // 0 -> 2 -> 3 -> 4 (-1 to refactor into array)

        double currIndex = factor;
        while(!songs.isEmpty()) { 
            System.out.println("CurrIndex: " + currIndex);
            returnArray[((int) Math.round(currIndex))-1] = songs.pop();
            currIndex = currIndex + factor;
        }

        return returnArray;
    }

    /**
     * Takes an array of Songs and creates a ArrayList of LinkedLists of Songs by the same artist
     * Songs that have a null artist are put together in a Linked List. This Linked List 
     * Each LinkedList of Songs by the same artist is shuffled before the result is returned
     * 
     * @param array - array of songs to sort 
     * @return a PriorityQueue of LinkedLists of Songs sorted by the length of the linked lists
     */
    private PriorityQueue<LinkedList<Song>> getArtistFrequency(Song[] array) {
        // Compares the size of the linked lists
        Comparator<LinkedList<Song>> comparator = new LinkedListComparator();
        PriorityQueue<LinkedList<Song>> queue = new PriorityQueue<LinkedList<Song>>(1, comparator);

        // Well there goes my plans of efficiency by using a PQ...
        ArrayList<LinkedList<Song>> sorter = new ArrayList<LinkedList<Song>>();
        
        LinkedList<Song> nullSongs = new LinkedList<Song>();
        for(int i = 0; i<array.length; i++) {
            Song currSong = array[i];

            // Always use protection
            if(currSong.getArtist() == null) {
                nullSongs.add(currSong);
                continue;
            }
            String currArtist = array[i].getArtist();

            for(int j = 0; j<=sorter.size(); i++) {
                // If a linked list containing the artist isn't found, create a new one
                if(j==sorter.size()) {
                    sorter.add(new LinkedList<Song>());
                    sorter.get(j).add(currSong);
                }
                // Adds the song to a linked list of songs by the artist
                if(sorter.get(j).get(0).getArtist().equals(currArtist)) {
                    sorter.get(j).add(currSong);
                    break;
                }
            }
        }

        // Adds the null songs back in lol
        if(nullSongs.size() != 0) {
            sorter.add(nullSongs);
        }

        for(int i = 0; i<sorter.size(); i++) {
            // Shuffle n insert
            Collections.shuffle(sorter.get(i));
            queue.add(sorter.get(i));
        }

        return queue;
    }

    /*
     * Compares the sizes of the linked lists
     */
    class LinkedListComparator implements Comparator<LinkedList<Song>> {
        @Override
        public int compare(LinkedList<Song> o1, LinkedList<Song> o2) {
            if(o1 == null || o2 == null) {
                throw new IllegalArgumentException("Dawg your linked lists are null, I can't compare that bruh");
            }
            return o1.size() - o2.size();
        }
    }
}
