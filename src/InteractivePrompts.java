import java.io.File;
import java.util.ArrayList;

public class InteractivePrompts {
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

    // MAIN INTERACT METHODS

    /*
     *  Interactive interface to edit a library
     *  Uses downloadMusic(), createPlaylistdeletePlaylist()
     */
    public static void editLibrary() {
        while(true) {
            // Sets canPrint to false after each loop because sub-prompts will end by setting canPrint to true
            App.canPrint = false;

            //Clear terminal
            System.out.print("\033[H\033[2J");  
            System.out.flush();

            System.out.println(ANSI_YELLOW + "Library Operations:" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "1. Download a song/playlist from youtube" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "2. Create a new playlist" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "3. [RENAME] a song" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "4. [DELETE] a playlist" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "5. [DELETE] a song" + ANSI_RESET);
            System.out.println();
            System.out.println(ANSI_CYAN + "ENTER A NUMBER OR HIT ENTER TO EXIT" + ANSI_RESET);

            String input = App.scan.nextLine();
            if(input.equals("")) {
                break;
            }

            if(input.equals("1")) {
                downloadMusic();
            }
            if(input.equals("2")) {
                createPlaylist();
            }
            if(input.equals("3")) {
                renameSong();
            }
            if(input.equals("4")) {
                deletePlaylist();
            }
            if(input.equals("5")) {
                deleteSong();
            }
        }
        App.canPrint = true;
        DataManager.saveSettings();
    }

    /*
     *  Interactive interface to edit a playlist
     *  Uses addSongToPlaylist() removeSongFromPlaylist(), reorderPlayList(), and renamePlaylist()
     */
    public static void editPlaylist(Playlist playlist) {
        while(true){
            // Sets canPrint to false after each loop because sub-prompts will end by setting canPrint to true
            App.canPrint = false;

            //Clear terminal
            System.out.print("\033[H\033[2J");  
            System.out.flush();

            System.out.println(ANSI_YELLOW + "Playlist Operations:" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "1. Add a song to this playlist" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "2. Remove a song from this playlist" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "3. Download a youtube song/playlist and add it to this playlist" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "4. Reorder this playlist" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "5. Rename this playlist" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "6. Delete this playlist" + ANSI_RESET);
            System.out.println();
            System.out.println(ANSI_YELLOW + "Library Operations:" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "7. Download a song/playlist from youtube" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "8. Create a new playlist" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "9. [RENAME] a song" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "10. [DELETE] a playlist" + ANSI_RESET);
            System.out.println(ANSI_PURPLE + "11. [DELETE] a song" + ANSI_RESET);
            System.out.println();
            System.out.println(ANSI_CYAN + "ENTER A NUMBER OR HIT ENTER TO EXIT" + ANSI_RESET);
            
            String input = App.scan.nextLine();
            if(input.equals("")) {
                break;
            }

            if(input.equals("1")) {
                addSongToPlaylist((Playlist) playlist);
            }
            if(input.equals("2")) {
                removeSongFromPlaylist((Playlist) playlist);
            }
            if(input.equals("3")) {
                downloadMusic();
                addSongToPlaylist(App.library.originalSongList.get(0), playlist);
            }
            if(input.equals("4")) {
                reorderPlaylist((Playlist) playlist);
            }
            if(input.equals("5")) {
                renamePlaylist((Playlist) playlist);
            }
            if(input.equals("6")) {
                System.out.print("\033[H\033[2J");  
                System.out.flush(); 
                System.out.println(ANSI_RED + "ARE YOU SURE YOU WANT TO DELETE THIS PLAYLIST? (Y/N)" + ANSI_RESET);
                String confirm = App.scan.nextLine();
                if(confirm.equals("Y") || confirm.equals("y")) {
                    deletePlaylist((Playlist) playlist);
                    break;
                }
            }

            if(input.equals("7")) {
                downloadMusic();
            }
            if(input.equals("8")) {
                createPlaylist();
            }
            if(input.equals("9")) {
                renameSong();
            }
            if(input.equals("10")) {
                deletePlaylist();
            }
            if(input.equals("11")) {
                deleteSong();
            }
            
        }
        App.canPrint = true;
        DataManager.saveSettings();
    }

    

    // SPAWN SUB-PROMPT INTERACTIVE METHODS

    /*
     *  Interactive interface to download a youtube song or playlist
     */
    public static void downloadMusic() {
        //Clear terminal
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 

        // Sets canPrint to false
        App.canPrint = false;

        // Initial print message
        System.out.println(ANSI_CYAN + "ENTER THE URL OF THE YOUTUBE VIDEO OR PLAYLIST YOU WANT TO DOWNLOAD" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "HIT ENTER TO EXIT DOWNLOADER" + ANSI_RESET);

        // Gets the url from the user
        while(true) {
            String url = App.scan.nextLine();
            if(url.equals("")) {
                System.out.println(ANSI_RED + "EXITING DOWNLOADER" + ANSI_RESET);
                App.canPrint = true;
                return;
            }
            // Downloads the song
            try {
                System.out.println(ANSI_YELLOW + "DOWNLOADING...   " + ANSI_RESET + ANSI_YELLOW_BACKGROUND + url + ANSI_RESET);
                ArrayList<String> titles = DataManager.download(url);
                for(int i = 0; i<titles.size(); i++) {
                    System.out.println(ANSI_GREEN + "DOWNLOADED   -   " + ANSI_RESET + ANSI_GREEN_BACKGROUND + titles.get(i) + ANSI_RESET);
                }
                System.out.println("(Title may change from the youtube video; Only ASCII characters are allowed in the title.)");

                // Updates library for each song downloaded
                for(int i = titles.size()-1; i>=0; i--) {  
                    // Add song into library from file path return from download method
                    App.library.add(new Song(new File("music\\" + titles.get(i))),0);
                    // Updates working list and collection
                    if(App.workingCollection instanceof Library) {
                        App.workingList = App.library.getSongs();
                        App.workingCollection = App.library;
                        App.currIndex = App.workingList.indexOf(App.currSong);
                    }
                    // Updates viewing list and collection
                    if(App.viewingCollection instanceof Library) {
                        App.viewingList = App.library.getSongs();
                        App.viewingCollection = App.library;
                    }
                }
                
                System.out.println(ANSI_CYAN + "Press enter to exit, or paste another link to download" + ANSI_RESET);
            } catch (Exception e) {
                System.out.println(ANSI_RED + "ERROR: INVALID URL, TRY AGAIN" + ANSI_RESET);
                e.printStackTrace();
            }
        }
    }

    /*
     *  Interactive interface that creates a new playlist
     */
    public static void createPlaylist() {
        //Clear terminal
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 

        // Sets canPrint to false
        App.canPrint = false;

        // Creates a new playlist
        System.out.println(ANSI_CYAN + "ENTER THE NAME OF THE NEW PLAYLIST (ENTER BLANK TO EXIT)" + ANSI_RESET);
        Playlist playlist;
        while(true) {
            String name = App.scan.nextLine();
            if(name.equals("")) {
                System.out.println(ANSI_RED + "EXITING PLAYLIST CREATOR" + ANSI_RESET);
                App.canPrint = true;
                return;
            }
            playlist = new Playlist(name);
            boolean canBreak = true;
            for(int i = 0; i<App.playlists.size(); i++) {
                if(App.playlists.get(i).name.equals(name)) {
                    System.out.println(ANSI_RED + "THERE IS ALREADY A PLAYLIST NAMED " + ANSI_RESET + ANSI_RED_BACKGROUND + name + ANSI_RESET);
                    System.out.println(ANSI_CYAN + "ENTER A NEW NAME OR HIT ENTER TO EXIT PLAYLIST CREATOR" + ANSI_RESET);
                    canBreak = false;
                }
            }
            if(canBreak == true) {
                break;
            }
        }
        
        boolean clear = true;

        // Adding songs to the playlist
        // The user can exit the prompt by pressing enter
        while(true){
            if(clear == true) {
                // Clear terminal
                System.out.print("\033[H\033[2J");  
                System.out.flush(); 
            }
            clear = true;
            
            // Ensures the library isn't in a shuffled state
            if(App.library.isShuffled == 1 || App.library.isShuffled == 2) {
                App.library.shuffle(0);
            }

            // Prints the library and the playlist
            App.printDisplayMessageBasic(App.library, false);
            System.out.println("-------------------------------------------------");
            System.out.println();
            App.printDisplayMessageBasic(playlist, false);
            System.out.println("----------- Enter Name/Library Number -----------");
            System.out.println("------------- (enter blank to exit) -------------");
            String input = App.scan.nextLine();

            if(input.equals("")) {
                break;
            }

            try{
                // If the user is trying to add by library number
                int index = Integer.parseInt(input);
                if(playlist.add(App.library.getSongAt(index-1))==false) {System.out.println(ANSI_RED + "DUPLICATE SONG" + ANSI_RESET); clear = false;}
            } catch(IndexOutOfBoundsException e) {System.out.println(ANSI_RED + "PLEASE ENTER A VALID INDEX" + ANSI_RESET); clear = false;}
            catch (NumberFormatException e) {
                // If user is trying to add by name
                boolean added = false;
                for(int i=0; i<App.library.getNumSongs(); i++) {
                    if(App.library.getSongAt(i).getTitle().equals(input)) {
                        if(playlist.add(App.library.getSongAt(i))==false) {System.out.println(ANSI_RED + "DUPLICATE SONG" + ANSI_RESET); clear = false;}
                        added = true;
                    }
                }
                if(added == false) {
                    System.out.println(ANSI_RED + "COULD NOT FIND THE TITLE. MAKE SURE IT IS SPELLED CORRECTLY" + ANSI_RESET);
                    clear = false;
                }
            }
            
        }
        App.playlists.add(playlist);
        DataManager.savePlaylists();
        App.canPrint = true;
    }

    /*
     *  Interactive interface to rename a song
     */
    public static void renameSong() {
        // Sets canPrint to false
        App.canPrint = false;
        while(true) {
            //Clear terminal
            System.out.print("\033[H\033[2J");  
            System.out.flush();

            // Prints available songs
            App.printDisplayMessageBasic(App.viewingCollection, false);

            System.out.println();
            System.out.println(ANSI_PURPLE + "ENTER THE NAME/LIBRARY NUMBER OF THE SONG YOU WANT TO RENAME (HIT ENTER TO EXIT)" + ANSI_RESET);
            String input = App.scan.nextLine();
            if(input.equals("")) {
                break;
            }

            Song songToRename = null;

            // Find the song from the library
            try{
                int index = Integer.parseInt(input);
                songToRename = App.viewingCollection.getSongAt(index-1);
            } catch(IndexOutOfBoundsException e) {
                System.out.println(ANSI_RED + "PLEASE ENTER A VALID INDEX (HIT ENTER TO CONTINUE)" + ANSI_RESET);
                App.scan.nextLine();
                continue;
            }
            catch (NumberFormatException e) {
                for(int i=0; i<App.viewingCollection.getNumSongs(); i++) {
                    if(App.viewingCollection.getSongAt(i).getTitle().equals(input)) {
                        songToRename = App.viewingCollection.getSongAt(i);
                    }
                }
            }
            if(songToRename == null) {
                System.out.println(ANSI_RED + "COULD NOT FIND THE TITLE. MAKE SURE IT IS SPELLED CORRECTLY (HIT ENTER TO CONTINUE)" + ANSI_RESET);
                App.scan.nextLine();
                continue;
            }
            System.out.println(ANSI_YELLOW + "RENAME " + ANSI_RESET + ANSI_YELLOW_BACKGROUND + songToRename.getTitle() + ANSI_RESET + ANSI_YELLOW + " TO (HIT ENTER TO EXIT) : " + ANSI_RESET);
            String newName = App.scan.nextLine();
            if(newName.equals("")) {
                break;
            }

            boolean flag = DataManager.rename(songToRename, newName);
            if(flag == true) {
                System.out.println(ANSI_GREEN + "SUCCESSFULLY RENAMED TO " + ANSI_RESET + ANSI_GREEN_BACKGROUND + songToRename.getTitle() + ANSI_RESET);
                System.out.println(ANSI_CYAN + "HIT ENTER TO CONTINUE" + ANSI_RESET);
                App.scan.nextLine();
                continue;
            } else {
                System.out.println(ANSI_RED + "FAILED TO RENAME" + ANSI_RESET);
                System.out.println(ANSI_CYAN + "HIT ENTER TO CONTINUE" + ANSI_RESET);
                App.scan.nextLine();
                continue;
            }
        }

        DataManager.saveSettings();
        App.canPrint = true;
    }

    /*
     *  Interactive interface to delete a song
     */
    public static void deleteSong() {
        // Sets canPrint to false
        App.canPrint = false;

        while(true){
            //Clear terminal
            System.out.print("\033[H\033[2J");  
            System.out.flush();

            // Prints available songs
            App.printDisplayMessageBasic(App.viewingCollection, false);

            System.out.println();
            System.out.println(ANSI_PURPLE + "ENTER THE NAME/LIBRARY NUMBER OF THE SONG YOU WANT TO DELETE (HIT ENTER TO EXIT)" + ANSI_RESET);
            String input = App.scan.nextLine();
            if(input.equals("")) {
                break;
            }

            Song songToDelete = null;

            // Find the song from the library
            try{
                int index = Integer.parseInt(input);
                songToDelete = App.viewingCollection.getSongAt(index-1);
            } catch(IndexOutOfBoundsException e) {
                System.out.println(ANSI_RED + "PLEASE ENTER A VALID INDEX (HIT ENTER TO CONTINUE)" + ANSI_RESET);
                App.scan.nextLine();
                continue;
            }
            catch (NumberFormatException e) {
                for(int i=0; i<App.viewingCollection.getNumSongs(); i++) {
                    if(App.viewingCollection.getSongAt(i).getTitle().equals(input)) {
                        songToDelete = App.viewingCollection.getSongAt(i);
                    }
                }
            }
            if(songToDelete == null) {
                System.out.println(ANSI_RED + "COULD NOT FIND THE TITLE. MAKE SURE IT IS SPELLED CORRECTLY (HIT ENTER TO CONTINUE)" + ANSI_RESET);
                App.scan.nextLine();
                continue;
            }
            System.out.println(ANSI_YELLOW + "DELETE " + ANSI_RESET + ANSI_YELLOW_BACKGROUND + songToDelete.getTitle() + ANSI_RESET + ANSI_YELLOW + " PERMENANTLY FROM FILES? (Y/N)" + ANSI_RESET);
            String confirm = App.scan.nextLine();
            if(confirm.equals("Y") || confirm.equals("y")) {
                // Checks if the song is currently playing, if it is, it cannot be deleted
                if(songToDelete == App.currSong) {
                    System.out.println(ANSI_RED + "YOU CANNOT DELETE THE SONG THAT IS CURRENTLY PLAYING (HIT ENTER TO CONTINUE)" + ANSI_RESET);
                    App.scan.nextLine();
                    continue;
                }

                // Deletes song from library
                boolean deleted = DataManager.delete(songToDelete);

                if(deleted == true){
                    // Update new currIndex
                    App.currIndex = App.workingCollection.indexOf(App.currSong);

                    System.out.println(ANSI_GREEN + "DELETED " + ANSI_RESET + ANSI_GREEN_BACKGROUND + songToDelete.getTitle() + ANSI_RESET);
                    System.out.println(ANSI_CYAN + "HIT ENTER TO CONTINUE" + ANSI_RESET);
                    App.scan.nextLine();
                    continue;
                }
                if(deleted == false) {
                    System.out.println(ANSI_RED + "ERROR: COULD NOT DELETE THE FILE." + ANSI_RESET);
                    System.out.println(ANSI_CYAN + "HIT ENTER TO CONTINUE" + ANSI_RESET);
                    App.scan.nextLine();
                    continue;
                }
                
            } else {
                System.out.println(ANSI_RED + "NOT DELETING " + ANSI_RESET + ANSI_RED_BACKGROUND + songToDelete.getTitle() + ANSI_RESET);
                System.out.println(ANSI_CYAN + "HIT ENTER TO CONTINUE" + ANSI_RESET);
                App.scan.nextLine();
                continue;
            } 
        }
        App.canPrint = true;
    }
    /*
     *  Interactive interface that deletes a playlist
     */
    public static void deletePlaylist() {
        // Sets canPrint to false
        App.canPrint = false;

        while(true){
            //Clear terminal
            System.out.print("\033[H\033[2J");  
            System.out.flush();

            // Prints available playlists
            System.out.print(ANSI_RED_BACKGROUND + " Playlists: " + ANSI_RESET + " ");
            for(int i = 0; i<App.playlists.size(); i++) {
                System.out.print(ANSI_RED + App.playlists.get(i).name + ANSI_RESET);
                if(i!=App.playlists.size()-1) {
                    System.out.print(" | ");
                }
            }

            System.out.println();
            System.out.println(ANSI_CYAN + "ENTER THE NAME OF THE PLAYLIST YOU WANT TO DELETE (HIT ENTER TO EXIT)" + ANSI_RESET);

            String input = App.scan.nextLine();
            if(input.equals("")) {
                break;
            }
            boolean deleted = false;
            for(int i = 0; i<App.playlists.size(); i++) {
                if(App.playlists.get(i).name.equals(input)) {
                    App.playlists.remove(i);
                    deleted = true;
                    break;
                }
            }
            // If the playlist doesn't exist
            if(deleted == false) {
                System.out.println(ANSI_RED + "PLAYLIST DOES NOT EXIST" + ANSI_RESET);
            }
        }

        DataManager.savePlaylists();
        App.canPrint = true;
    }

    /*
     *  Interactive interface that adds a song to a playlist
     */
    public static void addSongToPlaylist(Playlist playlist) {
        // Sets canPrint to false
        App.canPrint = false;
        boolean clear = true;

        // Adding songs to the playlist
        // The user can exit the prompt by pressing enter
        while(true){
            if(clear == true) {
                // Clear terminal
                System.out.print("\033[H\033[2J");  
                System.out.flush(); 
            }
            clear = true;

            // Ensures the library isn't in a shuffled state
            if(App.library.isShuffled == 1 || App.library.isShuffled == 2) {
                App.library.shuffle(0);
            }

            // Prints the library and the playlist
            App.printDisplayMessageBasic(App.library, false);
            System.out.println("-------------------------------------------------");
            System.out.println();
            App.printDisplayMessageBasic(playlist, false);
            System.out.println("----------- Enter Name/Library Number -----------");
            System.out.println("------------- (enter blank to exit) -------------");
            String input = App.scan.nextLine();

            if(input.equals("")) {
                break;
            }

            try{
                // If the user is trying to add by library number
                int index = Integer.parseInt(input);
                if(playlist.add(App.library.getSongAt(index-1))==false) {System.out.println(ANSI_RED + "DUPLICATE SONG" + ANSI_RESET); clear = false;}
            } catch(IndexOutOfBoundsException e) {System.out.println(ANSI_RED + "PLEASE ENTER A VALID INDEX" + ANSI_RESET); clear = false;} 
            catch (NumberFormatException e) {
                // If user is trying to add by name
                boolean added = false;
                for(int i=0; i<App.library.getNumSongs(); i++) {
                    if(App.library.getSongAt(i).getTitle().equals(input)) {
                        if(playlist.add(App.library.getSongAt(i))==false) {System.out.println(ANSI_RED + "DUPLICATE SONG" + ANSI_RESET); clear = false;}
                        added = true;
                    }
                }
                if(added == false) {
                    System.out.println(ANSI_RED +"COULD NOT FIND THE TITLE. MAKE SURE IT IS SPELLED CORRECTLY"+ ANSI_RESET); clear = false;
                }
            }
            
        }
        DataManager.savePlaylists();
        App.canPrint = true;
    }
    
    /*
     *  Interactive interface that removes a song from a playlist
     */
    public static void removeSongFromPlaylist(Playlist playlist) {
        // Sets canPrint to false
        App.canPrint = false;
        boolean clear = true;

        while(true){
            if(clear == true) {
                // Clear terminal
                System.out.print("\033[H\033[2J");  
                System.out.flush(); 
            }
            clear = true;

            // Ensures the library isn't in a shuffled state
            if(App.library.isShuffled == 1 || App.library.isShuffled == 2) {
                App.library.shuffle(0);
            }

            // Prints the playlist
            App.printDisplayMessageBasic(playlist, false);
            System.out.println("----------- Enter Name/Library Number -----------");
            System.out.println("------------- (enter blank to exit) -------------");
            String input = App.scan.nextLine();

            if(input.equals("")) {
                break;
            }

            try{
                // If the user is trying to remove by library number
                int index = Integer.parseInt(input)-1;
                // If the removed is behind the current song, currIndex-- so that the index is not skipped
                if(playlist.getSongs().contains(playlist.getSongAt(index)) && App.workingCollection.equals(playlist)) {
                    if(index <= App.currIndex) {
                        App.currIndex --;
                    }
                }
                if((playlist.remove(playlist.getSongAt(index)))==false) {System.out.println(ANSI_RED + "SONG NOT FOUND" + ANSI_RESET); clear = false;}
            } catch(IndexOutOfBoundsException e) {System.out.println(ANSI_RED + "PLEASE ENTER A VALID INDEX" + ANSI_RESET); clear = false;}
            catch (NumberFormatException e) {
                // If user is trying to remove by name
                boolean removed = false;
                for(int i=0; i<playlist.getNumSongs(); i++) {
                    Song toRemove = playlist.getSongAt(i);
                    if(toRemove.getTitle().equals(input)) {
                        // If the removed is behind the current song, currIndex-- so that the index is not skipped
                        if(playlist.getSongs().contains(toRemove) && App.workingCollection.equals(playlist)) {
                            if(App.workingCollection.getSongs().indexOf(toRemove) <= App.currIndex) {
                                App.currIndex --;
                            }
                        }
                        if(playlist.remove(toRemove)==false) {System.out.println(ANSI_RED + "SONG NOT FOUND" + ANSI_RESET); clear = false;}
                        removed = true;
                    }
                }
                if(removed == false) {
                    System.out.println(ANSI_RED +"COULD NOT FIND THE TITLE. MAKE SURE IT IS SPELLED CORRECTLY"+ ANSI_RESET); clear = false;
                }
            }
        }
        DataManager.savePlaylists();
        App.canPrint = true;
    }

    /*
     *  Interactive interface that renames a playlist
     */
    public static void renamePlaylist(Playlist playlist) {
        // Sets canPrint to false
        App.canPrint = false;

        while(true){
            // Clear terminal
            System.out.print("\033[H\033[2J");  
            System.out.flush(); 

            System.out.println(ANSI_CYAN + "RENAME PLAYLIST: " + ANSI_RESET + ANSI_YELLOW + playlist.getName() + ANSI_RESET);
            System.out.println("------------- (enter new name below) ------------");
            System.out.println("------------- (enter blank to exit) -------------");

            String input = App.scan.nextLine();

            if(input.equals("")) {
                break;
            }
            for(int i = 0; i<App.playlists.size(); i++) {
                if(App.playlists.get(i).getName().equals(input)) {
                    System.out.println(ANSI_RED + "PLAYLIST ALREADY EXISTS" + ANSI_RESET);
                    continue;
                }
            }
            playlist.setName(input);
            DataManager.savePlaylists();
        }
        App.canPrint = true;
    }
    
    /*
     *  Interactive interface to reorder a playlist
     */
    public static void reorderPlaylist(Playlist playlist) {
        // Sets canPrint to false
        App.canPrint = false;

        while(true){
            // Clear terminal
            System.out.print("\033[H\033[2J");  
            System.out.flush(); 

            // Ensures the library isn't in a shuffled state
            if(App.library.isShuffled == 1 || App.library.isShuffled == 2) {
                App.library.shuffle(0);
            }

            // Prints the playlist
            App.printDisplayMessageBasic(playlist, false);
            System.out.println(ANSI_PURPLE + "Enter the name/library number of the song you want to reorder (enter blank to exit): " + ANSI_RESET);
            String input = App.scan.nextLine();

            if(input.equals("")) {
                break;
            }

            // Find the index of the song to reorder
            int toReorder = -1;
            try {
                int index = Integer.parseInt(input) -1;
                if(index < 0 || index >= playlist.getNumSongs()) {
                    System.out.println(ANSI_RED + "PLEASE ENTER A VALID INDEX" + ANSI_RESET);
                    System.out.println("Press any key to continue");
                    App.scan.nextLine();
                    continue;
                }
                toReorder = index;
            } catch(NumberFormatException | IndexOutOfBoundsException e) {
                for(int i = 0; i<playlist.getNumSongs(); i++) {
                    Song song = playlist.getSongAt(i);
                    if(song.getTitle().equals(input)) {
                        toReorder = playlist.indexOf(song);
                    }
                }
            }
            if(toReorder == -1) {
                System.out.println(ANSI_RED + "SONG NOT FOUND" + ANSI_RESET);
                System.out.println("Press any key to continue");
                App.scan.nextLine();
                continue;
            }
            System.out.println("You are moving " + ANSI_YELLOW_BACKGROUND + playlist.getSongAt(toReorder).getTitle() + ANSI_RESET + " to a new index");

            // Prompt user for the index to move the song to
            System.out.println(ANSI_PURPLE + "Enter the index you want to move the song to (enter blank to exit): " + ANSI_RESET);
            input = App.scan.nextLine();
            if(input.equals("")) {
                break;
            }
            try {
                int index = Integer.parseInt(input) -1;
                // Reorder operation
                playlist.reorder(toReorder, index);
                // Check if the reorder operation affected currIndex
                if(App.workingCollection.equals(playlist)) {
                    App.currIndex = playlist.indexOf(App.currSong);
                }
                System.out.println( "Successfully moved " + ANSI_GREEN_BACKGROUND + playlist.getSongAt(index).getTitle() + ANSI_RESET);
                System.out.println("Press any key to continue");
                App.scan.nextLine();
            } catch(NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println(ANSI_RED + "PLEASE ENTER A VALID INDEX" + ANSI_RESET);
                System.out.println("Press any key to continue");
                App.scan.nextLine();
                continue;
            }

        }
        
        DataManager.savePlaylists();
        App.canPrint = true;
    }

    // NON-INTERACTIVE METHODS

    /*
     *  Deletes a playlist without prompting the user
     */
    public static boolean deletePlaylist(Playlist playlist) {
        if(App.playlists.remove(playlist) == false) {
            return false;
        }
        DataManager.savePlaylists();
        return true;
    }

    /*
     *  Adds a song to a playlist without prompting the user
     */
    public static boolean addSongToPlaylist(Song song, Playlist playlist) {
        boolean result = false;
        if(App.library.getSongs().contains(song)) {
            result = playlist.add(song);
            DataManager.savePlaylists();
        }
        return result;
    }

    /*
     *  Removes a song from a playlist without prompting the user
     */
    public static boolean removeSongFromPlaylist(Song song, Playlist playlist) {
        // If the removed is behind the current song, currIndex-- so that the index is not skipped
        if(playlist.getSongs().contains(song) && App.workingCollection.equals(playlist)) {
            if(App.workingCollection.getSongs().indexOf(song) <= App.currIndex) {
                App.currIndex --;
            }
        }
        boolean result = playlist.remove(song);
        DataManager.savePlaylists();
        return result;
    }
}
