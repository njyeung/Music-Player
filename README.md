## Muply

A simple music player project (and my first project) that builds upon java's Clip class.

## Controls

|`q` - volume down|`w` - mute|`e` - volume up|`r` - scroll up|
|`a` - previous|`s` - play/pause|`d` - next|`f` - scroll up|
|`z` - skip back|`x` - shuffle|`c` - skip forward|`v` - interact menu|
|`space` - home|

## Folder Structure

The workspace contains 4 folders:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies. This project uses Jansi to color code console outputs
- `music`: folder to maintain audio files
- `ffmpeg`: youtube-dl uses ffmpeg to convert youtube downloads into wav format

## Notable Files

The project contains 3 notable files

- `Muply.jar`: Executable to run the program
- `Settings.txt`: Stores settings for the music player
- `Playlists.txt`: Stores playlists for the music player


## Credit

`youtube-dl` is used to download wav files from youtube - [youtube-dl](https://github.com/ytdl-org/youtube-dl)
`ffmpeg` is used by youtube-dl to convert mp3 files to wav files - [ffmpeg](https://github.com/FFmpeg/FFmpeg)
`jansi` is used to color code console outputs - [jansi](https://github.com/fusesource/jansi)
