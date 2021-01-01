# DisYam
Discord Yandex Music Player

Allows you to play Yandex Music playlists on your Discord server.

#Launch as maven project
```
mvn clean compile exec:java -Dexec.args="token username password"
```

#Launch as docker container
```
docker build -t disyam .
docker run -d disyam token username password
```
