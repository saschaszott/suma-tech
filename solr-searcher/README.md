# Einführung

In diesem Beispiel wird gezeigt, wie Sie mittels eines Java-Programms auf einen Solr-Server zugreifen können, 
um Suchanfragen abzusetzen und das Ergebnis mittels Java zu verarbeiten. In diesem einführenden Beispiel
werden wir die Ergebnisse auf der Kommandozeile ausgeben. Analog könnte man genauso eine kleine Webanwendung
schreiben, die die Resultate im Browser ausgibt.

Der Zugriff auf den Solr-Server erfolgt mittels SolrJ. Hierbei handelt es sich um eine Bibliothek, die Teil
des Solr-Projekts ist und genutzt werden kann, um mittels Java auf einen Solr-Server zuzugreifen. Analog gibt
es auch für andere Programmiersprachen entsprechende Solr-Client-Bibliotheken (z.B. Solarium für PHP).

# Vorbereitungen

Für diese Demo brauchen wir einen Client für das Versionsverwaltungssystem Git sowie das Build-Tool
Maven, mit dem man Java-Programme bauen und ausführen kann.

Folgende Pakete werden auf der Kommandozeile installiert (sofern nicht bereits vorhanden)

````
sudo apt-get update
sudo apt-get install git maven
````

Je nach Netzwerkverbindung kann es einen Moment dauern, bis alle abhängigen Pakete auf dem
System installiert sind.

Ebenso benötigen wir (wie schon in Übung 1) ein JDK in der Version 8. Die Installation
wurde auf dem Übungsblatt 1 beschrieben und wir hier nicht erneut wiedergegeben.

Nach der Installation der Pakete muss noch die Umgebungsvariable `JAVA_HOME` gesetzt werden, 
damit Maven richtig funktioniert.

Um zu prüfen, ob die Variable gesetzt ist, wird der Befehl

``
echo $JAVA_HOME
``

ausgeführt. Erfolgt keine Ausgabe eines entsprechenden Pfades (auf das Hauptverzeichnis des JDK), so kann die Variable gesetzt werden mittels:

unter Ubuntu Linux (mit installiertem JDK v8):

``
export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:jre/bin/java::")
``

unter MacOS:

``
export JAVA_HOME="$(/usr/libexec/java_home -v 1.8)"
``

# Quellcode herunterladen

Der Quellcode dieser Übung liegt auf dem bei Github gehosteten Git-Repository 

https://github.com/saschaszott/suma-tech

Von dort werden wir nun den Rumpf des Projekts mittels des zuvor installierten Git-Clients
herunterladen. Dazu geben wir auf der Konsole folgendes ein:

````
mkdir suma-uebung2
cd suma-uebung2
git clone https://github.com/saschaszott/suma-tech.git

````

Nun werden alle Dateien aus dem Git-Repository auf den eigenen Rechner kopiert.


# Kompilieren / Bauen

Um die Abhängigkeit auf die SolrJ-Client-Bibliothek zu definieren, müssen wir eine Datei `pom.xml`anlegen, in der wir alle Abhängigkeiten (*dependencies*) deklarieren.

Nach der Installation von maven kann das Projekt auf der Kommandozeile gebaut (kompiliert) werden mittels

``
mvn clean compile 
``

Konnte der Quellcode fehlerfrei kompiliert werden, dann sollte auf der Konsole die Ausgabe `BUILD SUCCESS` erscheinen.

Außerdem hat Maven automatisch das Verzeichnis `target` angelegt. In diesem ist das Kompilat (Class-File), das der Java Compiler
erzeugt ist (der von Maven aufgerufen wurde) abgelegt.

# Programm ausführen

Zur einfachen Ausführung des Programms benutzen wir das Maven-Plugin *Exec Maven Plugin*, damit wir das Java-Programm direkt 
auf der Konsole ausführen können. Somit können wir auch ohne eine spezielle Entwicklungsumgebung (IDE)
das Programm ausführen (also z.B. auch remote auf einem Server, den wir nur per ssh erreichen und auf dem 
keine graphische Benutzeroberfläche bzw. IDE installiert ist).

Nun können wir mittels des folgenden Befehls unser Programm auf der Konsole ausführen:

``
mvn exec:java
``




