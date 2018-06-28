#!/bin/bash

# Projekt kompilieren
./mvnw clean compile

# Projekt ausführen
./mvnw exec:java
