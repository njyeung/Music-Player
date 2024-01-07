import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class DataManager {

    /*
     *  Loads ALL settings into App.java
     * 
     *  Calls loadPlaylists()
     */
    public static void loadSettings() {     
        
        try{
            // Hard coded block in case of empty/misformatted settings file
            {
                // Initializes main library object that holds all songs
                App.library = new Library("music");
                App.library.loadSongs();
                // Loading all other playlists
                loadPlaylists();
                // Initializes default working list and collection
                App.workingList = App.library.getSongs();
                App.workingCollection = App.library;
                // Initializes default viewing list and collection
                App.viewingList = App.library.getSongs();
                App.viewingCollection = App.library;
                App.currSong = App.library.getSongs().get(0);
            }
            
            File settings = new File("Settings.txt");
            Scanner settingScanner = new Scanner(settings);

            // Volume settings
            String volumeSettingString = settingScanner.nextLine();
            if(!volumeSettingString.contains("volume:")) {settingScanner.close(); throw new FileNotFoundException();}
            try{App.volume = Integer.parseInt(volumeSettingString.substring(7));} catch(NumberFormatException e) {settingScanner.close(); throw new FileNotFoundException();}

            // Load working directory
            String workingDirectoryString = settingScanner.nextLine();
            if(!workingDirectoryString.contains("workingDirectory:")) {settingScanner.close(); throw new FileNotFoundException();}
            workingDirectoryString = workingDirectoryString.substring(17);

            // If the working directory isn't the library, load the playlist as the working and viewing collection+list
            if(!workingDirectoryString.equals("Library (All Songs)")) {
                for(int i = 0; i<App.playlists.size()+1; i++) {
                    // Base case: if the playlist doesn't exist, use default currSong and currIndex and currPosition
                    if(i == App.playlists.size()) {
                        settingScanner.close();
                        throw new Exception("Playlist not found");
                    }
                    if(App.playlists.get(i).getName().equals(workingDirectoryString)) {
                        App.workingCollection = App.viewingCollection = App.playlists.get(i);
                        App.workingList = App.viewingList = App.playlists.get(i).getSongs();
                        break;
                    }
                }
            } 

            // Currently playing settings
            String currSettingString = settingScanner.nextLine();
            if(!currSettingString.contains("curr:")) {settingScanner.close(); throw new FileNotFoundException();}
            // Update currIndex
            try{App.currIndex = Integer.parseInt(currSettingString.substring(5));} catch(NumberFormatException e) {settingScanner.close(); throw new FileNotFoundException();}
            // Update CurrSong from currIndex ^
            App.currSong = App.workingList.get(App.currIndex);

            // Current song position
            String currPositionString = settingScanner.nextLine();
            if(!currPositionString.contains("position:")) {settingScanner.close(); throw new FileNotFoundException();}
            try{App.currSong.setPlayThroughPercent(Integer.parseInt(currPositionString.substring(9)));} catch(NumberFormatException e) {settingScanner.close(); throw new FileNotFoundException();}

            // Closes scanner because good habit lol
            settingScanner.close();
        } catch (Exception e) {
            System.out.println("SETTINGS FILE NOT FOUND / NOT CORRECTED FORMATED");
        }
        
    }

    /*
     *  Saves into the contents of Settings.txt
     *  Saves volume, currIndex, and currSong position
     *  Overwrites the file with current data
     *  
     * 
     *  Calls savePlaylists()
     */
    public static void saveSettings() {
        // Save all other data
        savePlaylists();

        try{
            File settings = new File("Settings.txt");
            FileWriter fw = new FileWriter(settings);
            
            // Save working library

            // Volume settings
            fw.write("volume:" + App.volume);
            fw.write(System.getProperty( "line.separator" ));

            // Save working directory
            fw.write("workingDirectory:" + App.workingCollection.getName());
            fw.write(System.getProperty( "line.separator" ));

            // Currently playing settings
            fw.write("curr:" + App.currIndex);
            fw.write(System.getProperty( "line.separator" ));

            // Current song position (percent int from [0,100])
            fw.write("position:" + App.currSong.getPlayThroughPercent());
            fw.write(System.getProperty( "line.separator" ));
            
            // Closes filewriter because good habit lol
            fw.close();
        } catch(FileNotFoundException e) {System.out.println("SETTINGS FILE NOT FOUND");}
        catch(IOException i) {System.out.println("COULDN'T WRITE TO SETTINGS FILE");}
    }

    /*
     *  Loads playlists into App.java
     */
    public static void loadPlaylists() {
        try {
            File playlistsFile = new File("Playlists.txt");
            Scanner playlistScanner = new Scanner(playlistsFile);

            Playlist current = null;
            while(playlistScanner.hasNextLine()) {
                // New playlist header
                if(playlistScanner.nextLine().equals("NEW PLAYLIST")) {
                    // Creates a new playlist with the name on the next line
                    current = new Playlist(playlistScanner.nextLine());
                    // Gets the next line, which should be the file name of a song
                    String nextLine = playlistScanner.nextLine();
                    while(!nextLine.equals("END PLAYLIST")) {
                        File tempFile = new File(nextLine);
                        Song tempSong = null;
                        // Instead of creating new objects, just use the ones in the library
                        for(int i = 0; i<App.library.getSongs().size(); i++) {
                            if(App.library.getSongs().get(i).getFile().equals(tempFile)) {
                                tempSong = App.library.getSongs().get(i);
                                break;
                            }
                        }
                        if(tempSong == null) {
                            System.out.println("PLAYLIST FILE CONTAINS A SONG NOT FOUND IN LIBRARY");
                        }
                        else{
                            current.add(tempSong);
                        }
                        nextLine = playlistScanner.nextLine();
                    }
                    App.playlists.add(current);
                }
            }
            playlistScanner.close();
        } catch (Exception e) {
            System.out.println("PLAYLISTS FILE NOT FOUND / NOT CORRECTED FORMATED");
            e.printStackTrace();
        }
    }

    /*
     *  Saves playlists in the playlists.txt file
     *  Overwrites the contents of the file with the current state of App.playlists variable
     * 
     *  Playlists formated like this:
     *  NEW PLAYLIST
     *  [name of playlist]
     *  [File names of songs in playlist]
     *  END OF PLAYLIST
     */
    public static void savePlaylists() {
        ArrayList<Playlist> playlists = App.playlists;
        try{
            File playlistFile = new File("Playlists.txt");
            FileWriter fw = new FileWriter(playlistFile);

            for(int i = 0; i<playlists.size(); i++) {
                fw.write("NEW PLAYLIST");
                fw.write(System.getProperty( "line.separator" ));
                Playlist current = playlists.get(i);
                fw.write(current.name);
                fw.write(System.getProperty( "line.separator" ));
                ArrayList<Song> originalSongList = current.originalSongList;
                for(int j = 0; j<originalSongList.size(); j++) {
                    Song song = originalSongList.get(j);
                    fw.write(song.getFile().getPath());
                    fw.write(System.getProperty( "line.separator" ));
                }
                fw.write("END PLAYLIST");
                fw.write(System.getProperty( "line.separator" ));
            }
            
            fw.close();
        } catch(FileNotFoundException e) {System.out.println("PLAYLISTS FILE NOT FOUND");}
        catch(IOException i) {System.out.println("COULDN'T WRITE TO PLAYLISTS FILE");}
    }

    /*
     *  Downloads a song or playlist from youtube
     *  Throws an exception if the youtube url is invalid or an error has occured 
     *  Returns the file path(s) of the song(s) downloaded
     */
    public static ArrayList<String> download(String youtubeURL) throws Exception {
        ArrayList<String> titles = new ArrayList<String>();

        // Updates youtube-dl
        try{
            Process p = Runtime.getRuntime().exec("youtube-dl -U");
            p.waitFor();
        } catch(Exception e) {
            throw new Exception("An issue occured while updating youtube-dl");
        }
        
        // Downloads song in wav format into the music folder
        try {
            //Test.runTest();
            // constructs youtube-dl command and executes it
            String command = "youtube-dl -x --restrict-filenames --audio-format wav --audio-quality 0 -o music/%(title)s.%(ext)s " + youtubeURL;
            Process p = Runtime.getRuntime().exec(command);

            // Retreives InputStream and ErrorStream from command
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // Find the name of the song downloaded
            String line = null;
            while((line = stdInput.readLine()) != null) {
                if(line.startsWith("[ffmpeg] Destination: music\\")) {
                    // [ffmpeg] Destination: music\FILENAME.wav
                    titles.add(line.substring(28));
                }
            }

            // If there are any errors, throw an exception
            String errorMessage = stdError.readLine();
            if(errorMessage != null) {
                throw new Exception(errorMessage);
            }

            // Waits for process to finish
            p.waitFor();
        } catch (Exception e) {
            throw new Exception("An issue occured that is unrelated to youtube-dl");
        }

        // Returns the name of the song downloaded, if it is null, throw an exception
        if(titles.isEmpty()) {
            throw new Exception("An issue occured and the name of the file(s) could not be read");
        } else {
            return titles;
        }
    }

    public static boolean rename(Song song, String newName) {
        // Get the file to delete and check if it exists
        File fileToDelete = song.getFile();
        if(!fileToDelete.exists()) {
            return false;
        }

        // I DONT KNOW WHY I NEED TO DO THIS BUT IT DOESNT WORK OTHERWISE
        // Delete the song from all playlists but save the playlist and the index of the song in the playlist
        Object[][] data = new Object[2][App.library.getSongs().size()];
        for(int i = 0; i<App.playlists.size(); i++) {
            Playlist playlist = App.playlists.get(i);
            for(int j = 0; j<playlist.originalSongList.size(); j++) {
                Song currSong = playlist.originalSongList.get(j);
                if(currSong.getFile().equals(fileToDelete)) {
                    data[0][i] = playlist;
                    data[1][i] = j;
                    playlist.remove(currSong);
                    break;
                }
            }
            playlist.remove(song);
        }
        
        // DONT TOUCH THIS. I DONT KNOW WHY IT WORKS, BUT IT DOES
        // For some reason you need to change the working list and viewing list to the library. Otherwise delete operation throws an exception saying that another process is using the file
        MusicCollection tempWorkingCollection = App.workingCollection;
        MusicCollection tempViewingList = App.viewingCollection;
        Song currSong = App.currSong;
        boolean isPlaying = currSong.getAudioClip().isRunning();
        int position = currSong.getPlayThroughPercent();
        App.changeWorkingList(App.library);
        App.changeViewingList(App.library);

        // Save the old file and set the song's file to null
        File oldFile = song.getFile();
        song.file = null;

        // Prompts song to release all resources
        song.getReadyToDelete();

        // Rename the file
        File newFile = new File("music\\" + newName + ".wav");
        boolean flag = oldFile.renameTo(newFile);

        // Set the song's file and title to the new file and name
        song.file = newFile;
        song.title = newName;

        // Add the song back to all playlists
        for(int i = 0; i<data[0].length; i++) {
            if(data[0][i] == null) {
                continue;
            }
            Playlist playlist = (Playlist)data[0][i];
            int index = (int)data[1][i];
            playlist.add(song, index);
        }

        // DONT CHANGE THIS. I DONT KNOW WHY IT WORKS, BUT IT DOES
        // Restore working list and viewing list
        App.changeWorkingList(tempWorkingCollection);
        App.changeViewingList(tempViewingList);
        
        if(isPlaying) {
            App.play(App.workingCollection.indexOf(currSong));
        }
        currSong.setPlayThroughPercent(position);

        return flag;
    }

     /*
     *  Permenantly deletes a song from the library, all playlists and all albums
     *  Removes the wav file from music folder
     *  Returns true if the song was successfully deleted, and false otherwise
     */
    public static boolean delete(Song song) {
        // Get the file to delete and check if it exists
        File fileToDelete = song.getFile();
        if(!fileToDelete.exists()) {
            return false;
        }

        // Remove the song from all playlists, albums, and the library
        for(int i = 0; i<App.playlists.size(); i++) {
            App.playlists.get(i).remove(song);
        }
        for(int i = 0; i<App.albums.size(); i++) {
            App.albums.get(i).remove(song);
        }
        App.library.remove(song);     

        // DONT CHANGE THIS. I DONT KNOW WHY IT WORKS, BUT IT DOES
        // For some reason you need to change the working list and viewing list to the library. Otherwise delete operation throws an exception saying that another process is using the file
        MusicCollection tempWorkingCollection = App.workingCollection;
        MusicCollection tempViewingList = App.viewingCollection;
        Song currSong = App.currSong;
        boolean isPlaying = currSong.getAudioClip().isRunning();
        int position = currSong.getPlayThroughPercent();
        App.changeWorkingList(App.library);
        App.changeViewingList(App.library);
        
        // Prompts song to release all resources
        song.getReadyToDelete();

        // Delete the file
        try {
            Files.delete(fileToDelete.toPath());
            // Save playlists file
            DataManager.savePlaylists();

            // DONT CHANGE THIS. I DONT KNOW WHY IT WORKS, BUT IT DOES
            // Restore working list and viewing list
            App.changeWorkingList(tempWorkingCollection);
            App.changeViewingList(tempViewingList);
            if(isPlaying) {
                App.play(App.workingCollection.indexOf(currSong));
            }
            currSong.setPlayThroughPercent(position);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

