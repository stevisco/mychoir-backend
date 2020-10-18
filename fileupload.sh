#!/bin/sh
echo LOADING
java -cp ./target/backendapi-0.0.1-SNAPSHOT.jar -Dloader.main=org.songdb.importer.FileImporter org.springframework.boot.loader.PropertiesLauncher localhost ./uploads
