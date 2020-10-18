#!/bin/sh

java -cp ./target/backendapi-0.0.1-SNAPSHOT.jar -Dloader.main=org.songdb.importer.Importer org.springframework.boot.loader.PropertiesLauncher coro.stevisco.info ./attachments/ListaCanti_2018.txt
