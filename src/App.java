import java.util.ArrayList;
import java.util.Scanner;
import org.fusesource.jansi.AnsiConsole;

public class App {
    // Color codes for console output (Text)
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    // Color codes for console output (Background)
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static Scanner scan = new Scanner(System.in);

    // Current working directory of the program, can be exchanged
    // by other lists from library/albums/playlists to change directories
    // Default set to library when program starts up in loadSettings() method
    public static ArrayList<Song> workingList; // Holds the list in the collection for easy access
    public static MusicCollection workingCollection; // Holds the actual object of the collection

    // Temp variables that hold collections to be displayed/used.
    // Is never actually played, but instead shoved into workingList and workingCollection to be played
    // when the user inputs 
    public static ArrayList<Song> viewingList;
    public static MusicCollection viewingCollection;

    // LIBRARY OBJECT CONTAINS ALL SONGS
    public static Library library;
    // List of albums
    public static ArrayList<Album> albums = new ArrayList<Album>();
    // List of playlists
    public static ArrayList<Playlist> playlists = new ArrayList<Playlist>();

    // Important controls
    public static int volume = 10;
    public static int currIndex = 0;
    public static Song currSong = null;
    public static int scrollIndex = 0;
    // Handles if the system can print something. Used for operations such as while creating a playlist
    public static boolean canPrint = true; 

    public static void main(String[] args) throws Exception {
        AnsiConsole.systemInstall();

        DataManager.loadSettings();
        System.out.println("Total library MB size: " + library.getByteSize()/1048576L);
        runCommandLoop();
    }

    public static void runCommandLoop() {
        // Automatically start playing
        playPause();

        while(true) {
            if(canPrint == true) {
                printDisplayMessage(viewingCollection);
            }
            DataManager.saveSettings();

            // Reads input from user
            String input = scan.nextLine();
            
            // Viewing playlist
            for(int i = 0; i<playlists.size(); i++) {
                if(input.equals(playlists.get(i).getName())) {
                    try{
                        changeViewingList(playlists.get(i));
                        continue;
                    } catch(IllegalStateException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                }
            }
            // Viewing albums
            for(int i = 0; i<albums.size(); i++) {
                if(input.equals(albums.get(i).getName())) {
                    try{
                        changeViewingList(albums.get(i));
                        continue;
                    } catch(IllegalStateException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                }
            }
            if(input.equals("r") | input.equals("scroll up")) {
                scrollIndex = scrollIndex-2;
            }
            if(input.equals("f") | input.equals("scroll down")) {
                scrollIndex = scrollIndex+2;
            }
            if(input.equals("s") | input.equals("play") || input.equals("pause")) {
                playPause();
            }
            if(input.equals("stop")) {
                stop();
            }
            if(input.equals("q") | input.equals("-")) {
                setVolume(volume -1);
            }
            if(input.equals("e") | input.equals("+")) {
                setVolume(volume +1);
            }
            if(input.equals("w") | input.equals("mute")) {
                setVolume(0);
            }
            if(input.equals("d") | input.equals("next")) {
                playNext();
            }
            if(input.equals("a") | input.equals("previous")) {
                playPrevious();
            }
            if(input.equals(" ") | input.equals("back")) {
                changeViewingList(library);
            }
            if(input.equals("z") | input.equals("skip backwards")) {
                workingList.get(currIndex).setPlayThroughPercent(currSong.getPlayThroughPercent() - 3);
                continue;
            }
            if(input.equals("c") | input.equals("skip forwards")) {
                workingList.get(currIndex).setPlayThroughPercent(currSong.getPlayThroughPercent() + 3);
                continue;
            }
            if(input.equals("x") | input.equals("shuffle")) {
                shuffle();
            }
            if(input.equals("v") | input.equals("edit")) {
                if(viewingCollection instanceof Playlist) {
                    InteractivePrompts.editPlaylist((Playlist) viewingCollection);;
                }
                if(viewingCollection instanceof Library) {
                    InteractivePrompts.editLibrary();
                }
            }

            if(input.equals("")) {
                printDisplayMessage(viewingCollection);
                DataManager.saveSettings();
            }
            
            if(input.startsWith("add")) {
                if(viewingCollection instanceof Playlist) {
                    if(input.trim().equals("add")) {
                        InteractivePrompts.addSongToPlaylist((Playlist) viewingCollection);
                    }
                    else {
                        input = input.substring(4);
                        Song song = null;
                        for(int i = 0; i<library.getSongs().size(); i++) {
                            if(input.equals(library.getSongs().get(i).getTitle())) {
                                song = library.getSongs().get(i);
                                break;
                            }
                        }
                        InteractivePrompts.addSongToPlaylist(song, (Playlist) viewingCollection);
                    }
                }
            }
            if(input.startsWith("remove")) {
                if(viewingCollection instanceof Playlist) {
                    if(input.trim().equals("remove")) {
                        InteractivePrompts.removeSongFromPlaylist((Playlist) viewingCollection);
                    }
                    else {
                        input = input.substring(7);
                        System.out.println(input);
                        Song song = null;
                        for(int i = 0; i<library.getSongs().size(); i++) {
                            if(input.equals(library.getSongs().get(i).getTitle())) {
                                song = library.getSongs().get(i);
                                break;
                            }
                        }
                        InteractivePrompts.removeSongFromPlaylist(song, (Playlist) viewingCollection);
                    }
                }
            }
            // Move playhead to a specific percentage of the song
            if(input.endsWith("%")) {
                input = input.replace("%", "");
                try{
                    if(Integer.parseInt(input)<100 && Integer.parseInt(input)>=0){
                        workingList.get(currIndex).setPlayThroughPercent(Integer.parseInt(input));
                        continue;
                    }
                } catch(NumberFormatException e){}
            }

            // Play a song by index in the list
            try {
                int toPlay = Integer.parseInt(input)-1;
                // Checks if directory needs to be changed and then plays the index
                try{
                checkDirectoryAndPlay(toPlay);
                // If index is out of bounds, don't do anything
                } catch(IndexOutOfBoundsException e) {}
            } catch(NumberFormatException e) {}

            /*
             *  Interactive Prompts
             */
            // Creating a new Collection
            if(input.startsWith("new")) {
                input = input.substring(3);
                if(input.equals(" playlist")) {
                    InteractivePrompts.createPlaylist();
                }
            }
            // Deleteing a Collection
            if(input.startsWith("delete")) {
                input = input.substring(6);
                if(input.equals(" playlist")) {
                    InteractivePrompts.deletePlaylist();
                }
            }
            // Downloading a song or playlist from youtube
            if(input.equals("download")){
                InteractivePrompts.downloadMusic();
            }
        }
    }

    /*
     *  Changes the working list to a different album/playlist/library
     *  Sets the new currIndex and currSong to the new index and song
     *  Stops the current song
     *  Plays the new song at index
     */
    public static void changeWorkingList(MusicCollection collection) throws IllegalStateException {
        if(collection.getNumSongs()<1) {
            throw new IllegalStateException("NEW DIRECTORY IS EMPTY");
        }
        currSong.stop();
        workingList = collection.getSongs();
        workingCollection = collection;
    }

    /*
     *  Changes the viewing list to a different album/playlist/library
     *  Throws an exception if the collection is empty
     */
    public static void changeViewingList(MusicCollection collection) throws IllegalStateException {
        viewingList = collection.getSongs();
        viewingCollection = collection;
        scrollIndex = 0;
    }

    /*
     *  Plays the current song. Can also be used to pause a song
     *  Sets the playhead position to the saved position
     *  Sets the volume to the saved volume
     * 
     *  CANNOT BE USED TO COMMIT INTO A NEW COLLECTION
     */
    public static void playPause() {
        currSong.playPause();
        currSong.setVolume(volume);
        DataManager.saveSettings();
        if(canPrint == true) {
            printDisplayMessage(viewingCollection);
        }
    }

    /*
     *  Stops playing the current song. Sets the playhead position to 0
     */
    public static void stop() {
        currSong.stop();
        DataManager.saveSettings();
        if(canPrint == true) {
            printDisplayMessage(viewingCollection);
        }
    }

    /*
     *  Sets the volume of the currently playing song
     *  Range [0,10]
     */
    public static void setVolume(int i) {
        volume = i;
        workingList.get(currIndex).setVolume(volume);
        DataManager.saveSettings();
        printDisplayMessage(viewingCollection);
    }
    
    public static void shuffle() {
        // Can shuffle a playlist
        if(workingCollection instanceof Playlist) {
            // Shuffles the playlist
            ((Playlist)workingCollection).shuffle();
            // Gets the new list
            workingList = workingCollection.getSongs();
            // Update index so it matches with the index of the new list
            for(int i=0; i<workingList.size(); i++) {
                if(workingList.get(i).equals(currSong)) {
                    currIndex = i;
                }
            }
        }
        // Can shuffle a library
        else if(workingCollection instanceof Library) {
            // Shuffles the library
            ((Library)workingCollection).shuffle();
            // Gets the new list
            workingList = workingCollection.getSongs();
            // Update index so it matches with the index of the new list
            for(int i=0; i<workingList.size(); i++) {
                if(workingList.get(i).equals(currSong)) {
                    currIndex = i;
                }
            }
        }
    }
    /*
     *  If user is currently viewing another collection, then change the current viewing list to working list
     *  and then play the new song at index toPlay. Then returns true
     *  If the viewing collection is equal to the working collection, or viewing collection is empty, does nothing and returns false
     *  Basically comitting the viewing list to the working list.
     * 
     *  Uses toPlay(int index)
     *  throws IndexOutOfBoundsException if toPlay is out of bounds
     */
    public static boolean checkDirectoryAndPlay(int toPlay) throws IndexOutOfBoundsException {
        // If a directory switch is required
        if(!workingCollection.equals(viewingCollection)) {
            // Check index out of bounds of the directory we are switching into
            if(toPlay-1 > viewingCollection.getNumSongs()) {
                throw new IndexOutOfBoundsException("Index out of bounds");
            }
            // Try to switch into it, if it is empty, return false because we are not switching directories
            try{changeWorkingList(viewingCollection);} catch(IllegalStateException e) {return false;}
            // Play the song at the given index and return true because we are switching directories
            play(toPlay);
            return true;
        }
        // If a directory switch is not required
        // Check index out of bounds of the current directory
        if(toPlay-1 > workingCollection.getNumSongs()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        // Play the song at the given index and return false because we are not switching directories
        play(toPlay);
        return false;
    }
    /*
     *  Plays the item located at index i of the working directory
     *  To play song 1, must enter 0 : (i-1)
     *  Range: [0, length of list-1]
     * 
     *  CAN BE USED TO COMMIT INTO A NEW COLLECTION
     */
    public static void play(int i) {

        // Stops currently playing song
        currSong.stop();

        // Updates currIndex and currSong
        currIndex = i;
        currSong = workingList.get(currIndex);

        // Plays new song
        workingList.get(currIndex).playPause();  
        workingList.get(currIndex).setVolume(volume);
        // This has to be done twice for some reason
        // Otherwise it starts a few microseconds into the song :(
        workingList.get(currIndex).setPlayThroughPercent(0);
        workingList.get(currIndex).setPlayThroughPercent(0);

        // Saves settings and display print message
        DataManager.saveSettings();
        if(canPrint == true) {
            printDisplayMessage(viewingCollection);
        }
    }

    /*
     *  Plays the next track in the working directory. If it reaches the end of the list,
     *  will loop back to the top of the list
     */
    public static void playNext() {
        // Stops currently playing song
        currSong.stop();

        // Updates currIndex and currSong
        currIndex = currIndex + 1;
        // Loops index back to beginning if end of list is reached
        if(currIndex > workingList.size()-1) {
            currIndex = 0;
        }
        currSong = workingList.get(currIndex);

        // Plays new song
        play(currIndex);

        // Saves settings and display print message
        DataManager.saveSettings();
        if(canPrint == true) {
            printDisplayMessage(viewingCollection);
        }
    }

    /*
     *  Plays the previous track in the working directory. If it reaches the beginning of the list,
     *  will loop back to the end of the list
     */
    public static void playPrevious() {
        // Stops currently playing song
        currSong.stop();

        // Updates currIndex and currSong
        currIndex = currIndex - 1;
        // Loops index back to end if the beginning of list is reached
        if(currIndex < 0) {
            currIndex = workingList.size()-1;
        }
        currSong = workingList.get(currIndex);

        play(currIndex);

        // Saves settings and display print message
        DataManager.saveSettings();
        if(canPrint==true) {
            printDisplayMessage(viewingCollection);
        }
    }

    /*
     *  TOP PORTION prints the available playlists and albums
     *  MIDDLE PORTION calls printDisplayMessageBasic(collection)
     *  But BOTTOM PORTION still prints whatever is currently playing
     */
    public static void printDisplayMessage(MusicCollection collection) {
        System.out.println();
        System.out.println();
        System.out.println();

        //Clear terminal
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 

        // Prints available playlists
        System.out.print(" Playlists:  ");
        for(int i = 0; i<playlists.size(); i++) {
            if(playlists.get(i).equals(workingCollection)) {
                System.out.print(ANSI_YELLOW + playlists.get(i).name + ANSI_RESET);
            }
            else {
                System.out.print(ANSI_RED + playlists.get(i).name + ANSI_RESET);
            }
            if(i!=playlists.size()-1) {
                System.out.print(" | ");
            }
        }
        System.out.println();
        // Prints available albums
        System.out.print(" Albums:     ");
        for(int i = 0; i<albums.size(); i++) {
            System.out.print(ANSI_RED + albums.get(i).name + ANSI_RESET);
            if(i!=albums.size()-1) {
                System.out.print(", ");
            }
        }
        System.out.println();
        System.out.println();

        // Print middle portion
        printDisplayMessageBasic(collection, true);

        // Prints playthrough percent ------ title ------ volume
        // But first prints a separator of the exact length

        // Formats the playthrough string so it is two digits long. If the percent is under 10, it adds a 0 in front
        String formattedPlayThroughPercent = "";
        if(currSong.getPlayThroughPercent() < 10) {
            formattedPlayThroughPercent = "0" + currSong.getPlayThroughPercent();
        } else {
            formattedPlayThroughPercent = "" + currSong.getPlayThroughPercent();
        }

        String percentTitleVol = ANSI_YELLOW + formattedPlayThroughPercent + "%" + ANSI_RESET  + " -------- " + ANSI_CYAN + "[ " + currSong.getTitle() + " ]" + ANSI_RESET + "--------" + ANSI_YELLOW + " VOL: " + currSong.getVolume() + ANSI_RESET;
        
        // Calculates how long separator needs to be to cover the percentTitleVol string
        String separator = new String("");
        // Subtract a few characters due to ansi color codes
        for(int i = 0; i<percentTitleVol.length() - 27; i++) {
            separator = separator + "-";
        }
        // If the working collection is either an instance of Library or Playlist,
        // check if it is being shuffled. If it is, add a white SHUFFLE indicator to the end, otherwise add a yellow SHUFFLE indicator
        if(workingCollection instanceof Library) {
            separator = separator.substring (0, separator.length() - 8);
            if(((Library)workingCollection).isShuffled == 0) {
                separator = separator + " SHUFFLE";
            } else if(((Library)workingCollection).isShuffled == 1){
                separator = separator + ANSI_YELLOW + " SHUFFLE" + ANSI_RESET;
            } else {
                separator = separator + ANSI_PURPLE + " SHUFFLE" + ANSI_RESET;
            }
        }
        if(workingCollection instanceof Playlist) {
            separator = separator.substring (0, separator.length() - 8);
            if(((Library)workingCollection).isShuffled == 0) {
                separator = separator + " SHUFFLE";
            } else if(((Library)workingCollection).isShuffled == 1){
                separator = separator + ANSI_YELLOW + " SHUFFLE" + ANSI_RESET;
            } else {
                separator = separator + ANSI_PURPLE + " SHUFFLE" + ANSI_RESET;
            }
        }

        System.out.println(separator);
        System.out.println(percentTitleVol);
    }

    /*
     *  Doesn't clear terminal
     *  ONLY PRINTS THE LIST OF SONGS
     * 
     *  Param - scrollable - if true, then the list of songs will be scrollable using r and f, otherwise, will print all items in the list
     */
    public static void printDisplayMessageBasic(MusicCollection collection, boolean scrollable) {

        System.out.println(ANSI_YELLOW + collection.getName() + " - " + collection.getTotalLengthString() + ANSI_RESET);

        // Prints full list of songs if scrollable == false
        // Maximum number of items in a list = 10
        if(scrollable == false | collection.getNumSongs() <= 12) {
            scrollIndex = 0;

            for(int i = 0; i<collection.getNumSongs(); i++) {
                // If the song is the currently playing song and scrollable is true, then print it with a cyan background
                if(collection.getSongs().get(i).equals(currSong) && scrollable == true) {
                    System.out.println(ANSI_CYAN_BACKGROUND + (i+1) + ". " + collection.getSongs().get(i) + ANSI_RESET);
                    continue;
                }
                System.out.println(ANSI_CYAN + (i+1) + ". " + collection.getSongs().get(i) + ANSI_RESET);
            }

            return;
        }
        
        // If scrollable == true

        // Prevent overscrolling and underscrolling
        if(scrollIndex > collection.getNumSongs()-12) {
            scrollIndex = collection.getNumSongs()-12;
        }
        if(scrollIndex < 0) {
            scrollIndex = 0;
        }

        // Prints a portion of the list
        for(int i = scrollIndex; i<scrollIndex+12; i++) {
            // If the song is the currently playing song, then print it with a cyan background
            if(collection.getSongs().get(i).equals(currSong)) {
                System.out.println(ANSI_CYAN_BACKGROUND + (i+1) + ". " + collection.getSongs().get(i) + ANSI_RESET);
                continue;
            }
            System.out.println(ANSI_CYAN + (i+1) + ". " + collection.getSongs().get(i) + ANSI_RESET);
        }
    }
}
