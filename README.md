## Muply

A simple music player project (and my first project) that builds upon java's Clip class. This app utilizes youtube-dl to download music from youtube and replay it ad-free. Users can also place their own wav files into the /music folder.

## Controls

Shortcuts to controls can be activated by pressing the corresponding letter on the keyboard, or by spelling out the command entirely:
| | | | |
| ------------- | ------------- |  -------------  | ------------- |
| q - volume down | w - mute | e - volume up | r - scroll up |
| a - previous | s - play/pause | d - next | f - scroll up |
| z - skip back | x - shuffle | c - skip forward | v - interact menu |
| | | space - home |

## Folder Structure

The workspace contains 4 folders:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies. This project uses Jansi to color code console outputs
- `music`: folder to maintain audio files. The project is automatically loaded with Bruh.wav. Users can place their own wav files in this folder for them to be automatically loaded upon the next startup of the app.
- `ffmpeg`: youtube-dl uses ffmpeg to convert youtube downloads into wav format

## Notable Files

The project contains 3 notable files

- `Muply.jar`: Executable to run the program
- `Settings.txt`: Stores settings for the music player
- `Playlists.txt`: Stores playlists for the music player


## Credit

- `youtube-dl` is used to download wav files from youtube - [youtube-dl](https://github.com/ytdl-org/youtube-dl)
- `ffmpeg` is used by youtube-dl to convert mp3 files to wav files - [ffmpeg](https://github.com/FFmpeg/FFmpeg)
- `jansi` is used to color code console outputs - [jansi](https://github.com/fusesource/jansi)
