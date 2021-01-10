<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->

![Profile views](https://gpvc.arturio.dev/aqulasoftDisYam)
[![Open Source? Yes!](https://badgen.net/badge/Open%20Source%20%3F/Yes%21/blue?icon=github)](https://github.com/aqulasoft/telegramwarden/)
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![[Telegram] aqulasoft live][telegram-shield]][telegram-url]

[![Pulls](https://shields.beevelop.com/docker/pulls/aqulasoft/disyam.svg?style=flat-square)](https://hub.docker.com/repository/docker/aqulasoft/disyam)
[![Layers](https://shields.beevelop.com/docker/image/layers/aqulasoft/disyam/latest.svg?style=flat-square)](https://hub.docker.com/repository/docker/aqulasoft/disyam)
[![Size](https://shields.beevelop.com/docker/image/image-size/aqulasoft/disyam/latest.svg?style=flat-square)](https://hub.docker.com/repository/docker/aqulasoft/disyam)

<!--
[![Pulls](https://shields.beevelop.com/docker/pulls/aqulasoft/twarden.svg?style=flat-square)](https://hub.docker.com/repository/docker/aqulasoft/twarden)
[![Layers](https://shields.beevelop.com/docker/image/layers/aqulasoft/twarden/latest.svg?style=flat-square)](https://hub.docker.com/repository/docker/aqulasoft/twarden)
[![Size](https://shields.beevelop.com/docker/image/image-size/aqulasoft/twarden/latest.svg?style=flat-square)](https://hub.docker.com/repository/docker/aqulasoft/twarden)
-->
<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/aqulasoft/disyam.svg
[contributors-url]: https://github.com/aqulasoft/disyam/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/aqulasoft/disyam.svg
[forks-url]: https://github.com/aqulasoft/disyam/network/members
[stars-shield]: https://img.shields.io/github/stars/aqulasoft/disyam.svg
[stars-url]: https://github.com/aqulasoft/disyam/stargazers
[issues-shield]: https://img.shields.io/github/issues/aqulasoft/disyam.svg
[issues-url]: https://github.com/aqulasoft/disyam/issues
[license-shield]: https://img.shields.io/github/license/aqulasoft/disyam.svg
[license-url]: https://github.com/aqulasoft/disyam/blob/master/LICENSE.txt
[telegram-shield]: https://img.shields.io/badge/telegram-aqulasoft-blue.svg
[telegram-url]: https://t.me/aqulasoft

# DisYam
Discord Yandex Music Player

### Docker image on Docker Hub =>> [DisYam](https://hub.docker.com/repository/docker/aqulasoft/disyam)

Allows you to play Yandex Music playlists, search by artist, playlist and song on your Discord server.

### Launch as maven project
```
mvn clean compile exec:java -Dexec.args="token username password"
```

### Launch as docker container from local image
```
docker build -t disyam .
docker run -d --name disyam disyam token username password
```

### Launch as docker container from docker hub
```
docker run -d --name disyam aqulasoft/disyam token username password
```

### Player

#### Example
 <img src="https://github.com/aqulasoft/DisYam/blob/master/img/urlPlaylist.png" width="50%" height="50%"/>
 
## Search

#### Playlist search
 
 <img src="https://github.com/aqulasoft/DisYam/blob/master/img/playlistSelect.png" width="50%" height="50%"/>
 
#### Artist search

<img src="https://github.com/aqulasoft/DisYam/blob/master/img/artistSearch.png" width="50%" height="50%"/>
 
#### Song search
 
 <img src="https://github.com/aqulasoft/DisYam/blob/master/img/songSearch.png" width="50%" height="50%"/>
 
#### Song download
 
 <img src="https://github.com/aqulasoft/DisYam/blob/master/img/songDownload.png" width="50%" height="50%"/>
 
 
## Help

<img src="https://github.com/aqulasoft/DisYam/blob/master/img/help.png" width="50%" height="50%"/>

Thanks [MarshalX](https://github.com/MarshalX) for his [API Yandex Music](https://github.com/MarshalX/yandex-music-api) documentation.

<sub><sup>Disclaimer:Any misuse is the responsibility of the user</sup></sub>
