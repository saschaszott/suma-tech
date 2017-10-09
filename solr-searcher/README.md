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

Ebenso benötigen wir ein JDK (*Java Developer Kit*) in der Version 8. Das JDK enthält u.a. den Java-Compiler `javac`.
Zur Installation des JDK geben Sie folgenden Befehl auf der Kommandozeile ein:

````
sudo apt-get install openjdk-8-jdk-headless
````

Nach der Installation des JDK muss noch die Umgebungsvariable `JAVA_HOME` gesetzt werden, 
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

# Absicherung des Solr-Servers gegen unberechtigte Zugriffe

Um die Authentifizierung in Ihrem Solr-Server zu aktivieren, muss eine Textdatei
mit dem Namen `security.json` im Solr-Arbeitsverzeichnis angelegt werden. 
Wenn Sie sich bei der Installation des Solr-Servers an die in der Übung 1 
beschriebene Anleitung gehalten haben, so befindet sich das Solr-Arbeitsverzeichnis 
unter `~/suma/solr-6.6.0/server/solr`.

Stoppen Sie zuerst den Solr-Server, sofern er noch läuft:

````
cd ~/suma/solr-6.6.0
bin/solr stop
````

Nun legen Sie im Solr-Arbeitsverzeichnis die Datei `security.json` mit dem
Inhalt

````
{
   "authentication": {
      "blockUnknown": false,
      "class": "solr.BasicAuthPlugin"
   }
}
````

Damit wird die Basis-Authentifizierung konfiguriert, die durch das Solr-Plugin 
`BasicAuthPlugin` implementiert wird. Sie ermöglicht es, die gesamte 
Solr-Instanz durch eine Kombination aus Benutzernamen und Passwort abzusichern.
Durch das Setzen von `blockUnknown` auf den Wert `false` ist die
Authentifizierung allerdings erst einmal noch deaktiviert. Dies ermöglicht es
uns neue Benutzer (mit Passwörtern) anzulegen, indem wir entsprechende HTTP-Requests
an die REST-Schnittstelle von Solr schicken. 

Starten Sie nun den Solr-Server neu, so dass die Datei `security.json` eingelesen
wird:

````
bin/solr start
````

Nun können wir einen neuen Benutzer `sumatech` anlegen. Um eine neue 
Kombination aus Benutzername und Passwort einzurichten, können Sie
die REST-API von Solr verwenden. Führen Sie dazu mittels `curl` folgenden 
HTTP-Request aus (sollte der Befehl `curl` nicht verfügbar sein, so
installieren Sie das Paket mittels `sudo apt-get install curl`):

````
curl http://localhost:8983/solr/admin/authentication \
   -H 'Content-type:application/json' \
   -d '{"set-user": {"sumatech": "suma!tech$17"}}'
```` 

Im Erfolgsfall sollte Solr den HTTP-Request mit dem Status-Code 0 bestätigen. 
Ein Blick in die Datei `security.json` zeigt den zusätzlichen Eintrag 
mit dem Benutzernamen `sumatech` und dem gesalzenen und SHA256-gehashten 
Passwort `suma!tech$17`:

````
"sumatech": "lcSLjNynVq9Hm17USpCLZeMRK2dNJhfxLf1LW3FUWRw= INsbRua+GJaySmQd4Botd1u3q1r1QM+yWbUBW7eYX3M="
````

Das Passwort wird aus Sicherheitsgründen nicht im Klartext in die Datei 
eingetragen, sondern der SHA256-Hashwert des Passwoworts, das zuvor mit 
einem *Salt* versehen wurde. Aufgrund der Tatsache, dass das Passwort vor dem 
Anwenden der SHA256-Hashfunktion mit einem individuellen *Salt* versehen wird 
(der in jedem Solr-Server unterschiedlich ist), wird sich die neu eingefügte 
Zeile in Ihrer `security.json` von der oben angegebenen Zeile unterscheiden.

Um nun die Authentifizierung schließlich zu aktivieren, muss ein weiterer Aufruf der
REST-API von Solr durchgeführt werden:

````
curl http://localhost:8983/solr/admin/authentication \
   -H 'Content-type:application/json' \
   -d '{"set-property": {"blockUnknown": true}}'
````

Liefert der Aufruf den Status-Code 0 zurück, so wurde die Authentifizierung
erfolgreich aktiviert. Wenn Sie nun mittels eines Webbrowsers auf die 
Admin-Oberfläche zugreifen wollen (die Sie unter http://localhost:8983/solr 
erreichen), dann fragt Solr zuerst die Kombination aus Benutzername und 
Passwort ab.

Statt der Verwendung von `curl` zum Absetzen entsprechender HTTP-Requests kann
man auch das aus der Übung 1 bereits bekannte Script `bin/solr` verwendet. Als
Operationsname muss hierbei `auth` übergeben. Ein Aufruf von `bin/solr auth`
liefert die verfügbaren Operationen.

Da (in diesem einfachen Beispiel) die Kombination zwischen Client und 
Solr-Server unverschlüsselt mittels HTTP erfolgt, wird das Passwort im Klartext 
übertragen. Im realen Einsatz sollte daher ein Umstieg auf HTTPS erfolgen, so 
dass die Kombination zwischen Client und Solr-Server grundsätzlich
verschlüsselt stattfindet.

Des Weiteren ist durch ein zusätzliches Plugin auch noch die Authorisierung im Solr-Server
konfigurierbar. Damit kann prinzipiell mehreren Benutzern mit unterschiedlichen
Privilegien Zugriff auf die Solr-Instanz eingeräumt werden. Die Konfiguration der
Authorisierung werden wir uns nicht weiter ansehen. Weitere Informationen zu
den entsprechenden Solr-Plugins finden Sie unter

http://lucene.apache.org/solr/guide/6_6/rule-based-authorization-plugin.html

# Anpassung des Java-Programms `SolrSearcher` nach der Aktivierung der Authentifizierung

Da jeder Zugriff auf den Solr-Server nun die Angabe eines Benutzernamens und Passworts
erfordert, müssen wir unser Programm um die Angabe dieser beiden Daten erweitern.

Aktualisieren Sie daher den Programmcode, indem Sie sich die neueste Version der Klasse aus
dem Github-Repository herunterladen. Dazu führen Sie den Befehl

````
git pull
````

aus.

# Konfiguration der Scripte im Solr-Verzeichnis `bin`

Damit z.B. das in der Übung zur Indexierung verwendete Script `bin/post` den Benutzernamen und das Passwort
beachtet, muss im Verzeichnis `bin` die Datei `solr.in.sh` angepasst werden. 

Ohne Anpassung kann z.B. der Vitalzustand des Solr-Servers nicht abgefragt werden:

````
bin/solr status
````

liefert in diesem Fall die Ausgabe 

````
ERROR: Solr requires authentication for http://localhost:8983/solr/admin/info/system. Please supply valid credentials. HTTP code=401
````

Fügen Sie daher am Ende der Datei `bin/solr.in.sh` zwei Zeilen ein:

````
SOLR_AUTH_TYPE='basic'
SOLR_AUTHENTICATION_OPTS='-Dbasicauth=sumatech:suma!tech$17'
````

Nun sollten Sie den Vitalzustand erfolgreich abfragen können:

````
bin/solr status
````

liefert in diesem Fall die Ausgabe 

````
Found 1 Solr nodes: 

Solr process 5258 running on port 8983
{
  "solr_home":"/home/suma/suma/solr-6.6.0/server/solr",
  "version":"6.6.0 5c7a7b65d2aa7ce5ec96458315c661a18b320241 - ishan - 2017-05-30 07:32:53",
  "startTime":"2017-09-15T20:52:20.997Z",
  "uptime":"0 days, 0 hours, 56 minutes, 47 seconds",
  "memory":"60.9 MB (%12.4) of 490.7 MB"}
````

# Indexierung von Dokumenten mittels `bin/post`

Auch die Indexierung von Dokumenten mittels des SimplePost-Tools `bin/post` erfordert nun die Angabe
des Benutzernamens und Passworts. Die Übergabe erfolgt mittels des Parameters `-u`. Um die 37 XML-Dateien
der Werke von Shakespeare zu indexieren, ist folgender Befehl abzusetzen:

````
bin/post -c shakespeare -out yes -u 'sumatech:suma!tech$17' ~/suma/shakespeare/shakespeare-xml-solr/
````
