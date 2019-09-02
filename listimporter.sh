#!/bin/sh

java -cp ./target/backendapi-0.0.1-SNAPSHOT.jar -Dloader.main=org.songdb.importer.Importer org.springframework.boot.loader.PropertiesLauncher 127.0.0.1 ./attachments/ListaCanti_2018.txt
