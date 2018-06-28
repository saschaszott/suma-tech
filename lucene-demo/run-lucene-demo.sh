#!/bin/bash

# Projekt kompilieren
./mvnw clean compile

if [ "$?" -eq 0 ]; then
  # Main-Class ausführen
  ./mvnw exec:java -Dexec.mainClass="de.suma.lucene.LuceneDemo"
fi
