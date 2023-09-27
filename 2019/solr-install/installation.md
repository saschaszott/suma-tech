# Installation von Solr und erste Schritte

Öffnen Sie das Terminal (Konsole).

Zuerst wechseln wir in unser Arbeitsverzeichnis
```
cd ~/sumatech
```

Nun laden wir das Solr-Binary als TAR herunter
```
wget https://archive.apache.org/dist/lucene/solr/8.1.1/solr-8.1.1.tgz
```

Entpacken Sie das TAR mittels (dauert einige Sekunden)
```
tar xfz solr-8.1.1.tgz
```

Wechseln Sie ins Solr-Basisverzeichnis
```
cd solr-8.1.1
```

Jetzt wird es spannend. Wir werden den Solr-Server zum ersten Mal starten, indem Sie folgenden Befehl aufrufen:
```
bin/solr start
```

Beim Start werden zwei Warnungen angezeigt, um die wir uns gleich kümmern.
```
*** [WARN] *** Your open file limit is currently 1024.  
It should be set to 65000 to avoid operational disruption. 
If you no longer wish to see this warning, set SOLR_ULIMIT_CHECKS to false in your profile or solr.in.sh
*** [WARN] ***  Your Max Processes Limit is currently 15606. 
It should be set to 65000 to avoid operational disruption. 
If you no longer wish to see this warning, set SOLR_ULIMIT_CHECKS to false in your profile or solr.in.sh
```

Nach einigen Sekunden sollte der Solr-Server erfolgreich gestartet sein. Das sehen Sie an folgender Ausgabe:
```
# Started Solr server on port 8983 (pid=1254). Happy searching!
```
Damit haben Sie Ihren Solr-Server erfolgreich gestartet.
Öffnen Sie nun noch die Startseite der Weboberfläche von Solr (https://localhost:8983) in einem Browser unter
```
firefox https://localhost:8983 &
```

Herzlichen Glückwunsch! Sie haben Ihren Solr-Server erfolgreich installiert.

Jetzt kümmern wir uns um die beiden Warnungen von oben, die nur für den Produktivbetrieb wichtig sind.
Damit die Meldungen nicht bei jedem Start des Solr-Servers angezeigt werden, editieren wir die Datei `solr.in.sh`
im Unterverzeichnis `bin` des Solr-Basisverzeichnis.

Die Änderungen an der Datei können Sie mit dem grafischen Editor Leafpad vornehmen (Sie können aber auch einen anderen Editor verwenden):
```
leafpad bin/solr.in.sh
```

In der Datei `solr.in.sh` ersetzen wir die Zeile
```
#SOLR_ULIMIT_CHECKS=
```
durch
```
SOLR_ULIMIT_CHECKS=false
```
Achten Sie bitte unbedingt darauf, dass am Anfang der Zeile **kein** Kommentarzeichen (Raute) steht!

Damit die Änderungen wirksam werden, speichern Sie die Datei ab und schließen Leafpad.

Nun starten wir Solr neu.
```
bin/solr restart
```

Durch das Argument 'restart' wird der Solr-Server zuerst heruntergefahren und anschließend automatisch gestartet. 
Am Ende sollten Sie wieder die Meldung sehen
```
Started Solr server on port 8983 (pid=1912). Happy searching!
```
Die beiden Warnungen sollten nun verschwunden sein.

Sollten Sie einmal nicht wissen, ob der Solr-Server gerade läuft oder nicht, dann können Sie den Serverstatus mit dem Befehl prüfen:
```
bin/solr status
```

Läuft der Solr-Server zum Zeitpunkt des Aufrufs, so ergibt sich folgende Ausgabe
```
Solr process 1912 running on port 8983
```

Zusätzlich wird ein JSON ausgegeben, in dem u.a. Basisverzeichnis, Versionsnummer, Uptime und Speichervebrauch angegeben sind 
(diese Informationen stehen auch in der Solr-Weboberfläche, die Sie bereits im Browser geöffnet haben):
```
{
  "solr_home":"/home/sumatech/sumatech/solr-8.1.1/server/solr",
  "version":"8.1.1 fcbe46c28cef11bc058779afba09521de1b19bef - ab - 2019-05-22 15:20:01",
  "startTime":"2019-06-30T05:11:51.038Z",
  "uptime":"0 days, 0 hours, 13 minutes, 24 seconds",
  "memory":"130.5 MB (%25.5) of 512 MB"}
```

Um den Solr-Server herunterzufahren, nutzen Sie folgenden Befehl
```
bin/solr stop
```

