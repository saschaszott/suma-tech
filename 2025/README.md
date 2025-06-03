# Installation der erforderlichen Infrastruktur

## Git

**Git** ist ein Versionskontrollsystem, das wir für die Verwaltung von Quellcode verwenden werden. Der Quellcode sämtlicher Programme sowie die benötigten Konfigurationsdateien werden über Git verteilt. Hierzu existiert bei GitHub das öffentliches Projekt suma-tech (https://github.com/saschaszott/suma-tech). Git erlaubt es Ihnen zudem lokal ausgeführte Änderungen an Quelltexten und Konfigurationsdateien zu versionieren, so dass Sie den Änderungsverlauf später einfach nachvollziehen können. Auch im Hinblick auf eine Masterarbeit mit Implementierungsanteil ist die Beschäftigung mit Git sehr zu empfehlen.

Die Installation von Git unterscheidet sich je nach Betriebssystem:

*	Windows: Git-Installer von der offiziellen Website https://git-scm.com herunterladen und ausführen (Standardoptionen des Installationsassistenten sind in der Regel ausreichend).
*	macOS: sofern Git noch nicht installiert ist (Prüfung mittels `git --version`) kann Git über Homebrew (`brew install git`) installiert werden.
*	Linux (z. B. Ubuntu, Debian): Terminal öffnen und Git mittels Apt-Paketmanager installieren: `sudo apt update && sudo apt install git`.

Nach der Installation von Git kann mit dem Befehl `git --version` geprüft werden, ob es korrekt installiert wurde. Beachten Sie, dass Sie eine aktuelle Git-Version (Versionsnummer 2.40 oder höher) besitzen.

Sie können Git mit dem Kommandozeilenbefehl `git <command>` bedienen (oder später mit Visual Studio Code). Sofern Sie zusätzlich eine graphische Benutzeroberfläche bevorzugen, können Sie optional einen graphischen Git-Client installieren. Eine Liste der verfügbaren Git-Clients finden Sie beispielsweise unter https://git-scm.com/downloads/guis

## Visual Studio Code

**Visual Studio Code** (auch **VS Code**) ist ein Quelltexteditor aus dem Hause Microsoft. Er bietet Syntax-Highlighting, Autovervollständigung, integrierte Git-Unterstützung, Debugging-Tools und eine große Auswahl an Erweiterungen für viele Programmiersprachen und Entwicklungsumgebungen.

Die Installationsdatei von VS Code kann kostenfrei unter https://code.visualstudio.com/Download heruntergeladen werden. Für Windows und macOS empfiehlt sich der Installationsassistent. Unter Linux können Sie VS Code auch über die Kommandozeile installieren. Für Ubuntu gibt es sogar ein Snap-Paket.

## Python

**Python** ist eine weit verbreitete, leicht verständliche Programmiersprache, die sich besonders gut für Einsteiger eignet. Sie wird häufig in der Datenanalyse, Künstlichen Intelligenz, Webentwicklung oder der Automatisierung eingesetzt. Python ist bekannt für seine klare Syntax, eine große Standardbibliothek und eine aktive Community. Bitte beachten Sie, dass wir Python in einer aktuellen Version (Versionsnummer 3.10 oder höher) verwenden.

Den Installer für Windows können Sie unter https://www.python.org/downloads/windows/ herunterladen (Sie benötigen Windows 8 oder höher). Unter macOS bietet sich - analog zu git - die Installation mittels Homebrew an (`brew install python`). Linux-Nutzende können ebenfalls Python mittels Paket-Manager installieren, z.B. unter Ubuntu mittels `sudo apt update && sudo apt install python3 python3-pip`.

Nach der Installation können Sie mit dem Befehl `python3 --version` prüfen, ob Sie eine aktuelle Version auf Ihrem System installiert haben. Es wird empfohlen die Python-Version mit der Versionsnummer 3.10 oder höher zu verwenden.

Wir werden im Verlauf der Vorlesung verschiedene Python-Programmbibliotheken benötigen. Für die Installation der erforderlichen Python-Pakete werden wir den Python-Paketmanager `pip` nutzen. Optional kann auch der Paketmanager `conda` verwendet werden, der z.B. bei der Installation von Anaconda automatisch installiert wird.

## Docker

Wir werden uns im Modul Suchmaschinentechnologie intensiv mit der Open Source Software **Apache Solr** auseinandersetzen.

Zur (einfachen) Installation und Ausführung von Solr werden wir **Docker** verwenden.

Docker ist eine Plattform zur Container-Virtualisierung, die es ermöglicht, Softwareanwendungen samt ihrer Abhängigkeiten in einer isolierten Umgebung – einem sogenannten **Container** – auszuführen.
Im Gegensatz zu klassischen virtuellen Maschinen (VMs) sind Container deutlich leichtgewichtiger. Ein Docker Container ist eine abgeschlossene Umgebung, die auf jedem System (Docker Host) identisch
funktioniert, unabhängig davon, welches Betriebssystem oder welche Software auf dem Docker Host installiert ist. 

Wir werden Docker verwenden, um einen Solr-Server in einem Docker Container auszuführen. Der Docker Container stellt hierbei sicher, 
dass alle für die Ausführung des Solr-Servers erforderlichen Komponenten (z.B. Java, Konfigurationsdateien) zur Verfügung stehen. 
Für Sie wird dadurch der Installationsaufwand deutlich reduziert.

Ein **Docker Image** ist eine Art Vorlage oder Bauplan für einen Docker-Container. Das Image enthält alle benötigten Dateien, Programme und Programmbibliotheken 
sowie Konfigurationen, um eine gewünschte Anwendung ausführen zu können. 

Ein **Docker Container** ist eine laufende Instanz eines Images, vergleichbar mit einem Programm, das aus einer ausführbaren Datei gestartet wird.

Normalerweise müsste Solr manuell installiert, konfiguriert und mit den richtigen Abhängigkeiten versehen werden. 
Mit Docker genügt ein einzelner Befehl, um Solr in einer standardisierten Umgebung zu starten – unabhängig vom Betriebssystem des Hosts. 
Das macht die Nutzung für alle Studierenden einheitlich und vermeidet typische Installationsprobleme.

## Docker Desktop

Die **Docker Engine** ist das Herzstück von Docker. Sie führt die Container aus und verwaltet sie. 
Auf Linux-Systemen wird die Docker Engine direkt im Betriebssystem installiert. 
Da Windows und macOS jedoch anders funktionieren, stellt **Docker Desktop** eine benutzerfreundliche Oberfläche zur Verfügung,
die die Docker Engine in einer speziellen Umgebung (z. B. per WSL2 oder virtueller Maschine) integriert. 
Docker Desktop ermöglicht es uns, Container einfach zu starten, zu stoppen und zu verwalten, 
entweder per grafischer Oberfläche (GUI) oder über die Kommandozeile (über docker Befehle).

Für Docker Desktop stehen Installationsdateien für alle gängigen (aktuellen) Betriebssysteme zur Verfügung:

* Docker Desktop für **Windows** (10 und 11) unter https://docs.docker.com/desktop/setup/install/windows-install/
* Docker Desktop für **MacOS** unter https://docs.docker.com/desktop/setup/install/mac-install/
* Docker Desktop für **Linux** unter https://docs.docker.com/desktop/setup/install/linux/

Bitte stellen Sie sicher, dass Sie eine aktuelle Version (4.41.0 oder höher) von Docker Desktop installieren.
Sofern Sie Docker Desktop installiert haben, können Sie die Versionsnummer in der graphischen Oberfläche von Docker Desktop
ermitteln (die Versionsnummer steht unten rechts in der Statuszeile).

Wichtige Hinweise zur Installation von Docker Desktop unter **Windows** finden Sie in der Dokumentation unter:

https://docs.docker.com/desktop/setup/install/windows-install/

Beachten Sie insbesondere folgenden Hinweis:

> If your admin account is different to your user account, you must add the user to the `docker-users` group:

```
net localgroup docker-users <user> /add
```

Für Linux sollten Sie Ihren Benutzeraccount zur Gruppe `docker` hinzufügen:

```sh
sudo usermod -aG docker $USER
```

Wir benötigen zudem die Software **Docker Compose**. Docker Compose ist in der aktuellen Version von Docker Desktop 
bereits enthalten, so dass keine zusätzliche Installation erforderlich ist.

## Test der Docker-Installation: Ausführung eines Docker Containers

Nach der Installation von Docker Desktop können Sie testweise einen Container starten, der `Hello from Docker!` auf der Kommandozeile ausgibt. Starten Sie dazu ein Terminal / die Kommandozeile und geben Sie anschließend folgenden Befehl ein:

```sh
docker run hello-world
```

Die erfolgreiche Ausführung des Befehls sollte folgende Ausgabe ergeben (die Ausgabe kann je nach Host-Betriebssytem leicht variieren):

```
Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
c9c5fd25a1bd: Pull complete
Digest: sha256:7e1a4e2d11e2ac7a8c3f768d4166c2defeb09d2a750b010412b6ea13de1efb19
Status: Downloaded newer image for hello-world:latest

Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (arm64v8)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker ID:
 https://hub.docker.com/

For more examples and ideas, visit:
 https://docs.docker.com/get-started/
```

Nun erklären wir kurz, was nach der Eingabe des Befehls `docker run hello-world` passiert ist. 

Zunächst überprüft Docker, ob das benötigte Docker Image mit dem Namen `hello-world` bereits auf dem Rechner vorhanden ist. 
Ist das nicht der Fall, so wird das Docker Image automatisch aus **Docker Hub** heruntergeladen. 
Docker Hub ist ein öffentliches Repository für Container-Images.
Aus Sicherheitsgründen sollten Sie darauf achten, nur vertrauenswürdige Docker Images aus dem Docker Hub herunterzuladen.
Vertrauenswürdige Images erkennen Sie z.B. an dem Zusatz _Docker Official Image_.

Nachdem das Docker Image heruntergeladen wurde, startet Docker anschließend einen Container, d.h. eine isolierte Umgebung, in der das Image ausgeführt wird.

Das Docker Image `hello-world` enthält ein kleines Programm, das lediglich eine Begrüßungsnachricht ausgibt. 
Sobald der Container gestartet ist, führt er dieses Programm aus und zeigt die Meldung `Hello from Docker!` an. 
Damit bestätigt Docker, dass die Installation erfolgreich ist und korrekt funktioniert.
Nachdem die Nachricht ausgegeben wurde, beendet sich der Container automatisch, da er seine Aufgabe erfüllt hat.

## Installation von Apache Solr in einem Docker Container

Starten Sie nun das zuvor installierte Programm Visual Studio Code. Wählen Sie im Menü _Anzeigen_ den Eintrag _Quellcodeverwaltung_.

Auf der linken Seite erscheint der Bereich _Quellcodeverwaltung_. Klicken Sie auf den Button _Repository klonen_. Geben Sie nun die Repository-URL ein: https://github.com/saschaszott/suma-tech.git

Sie können nun ein beliebiges Arbeitsverzeichnis auf Ihrem Rechner festlegen, in dem das Git-Repository `suma-tech` heruntergeladen wird. Nehmen wir an, dass Sie als Arbeitsverzeichnis `sumatech2025` auswählen, dann existiert in diesem Verzeichnis nach dem Klonen das Unterverzeichnis `suma-tech`.

Wählen Sie nach dem Klonen im Dialog _Möchten Sie das geklonte Repository öffnen?_ den Button _Öffnen_.

Auf der linken Seite wird nun der Inhalt des Verzeichnis `suma-tech` angezeigt. Wählen Sie das Unterverzeichnis `2025` und anschließend das Unterverzeichnis `solr`. Klicken Sie im Kontextmenü (rechte Maustaste) den Eintrag _In integriertem Terminal öffnen_.

Führen Sie folgenden Befehl aus, um einen Docker Container mit dem Namen `solr-server` zu erzeugen, in dem schließlich ein Solr Server gestartet wird:

```sh
docker compose up -d
```

Der `docker compose` Befehl liest die Datei `docker-compose.yml` ein (aus dem Verzeichnis `2025/solr`). In dieser Datei sind die Dienste (Services) definiert, die beim Start (`up`) in einzelnen Docker Containern gestartet werden. In unserem Fall steht in der Datei nur ein Service mit dem Namen `solr`. Dazu wird zuerst das offizielle Docker Image `solr:9.8.1` aus dem Docker Hub heruntergeladen. Anschließend wird ein Docker Container mit dem Namen `solr-server` erzeugt, in dem schließlich ein Solr-Server gestartet wird. Die Option `-d` im obigen Befehl führt dazu, dass der Container im Hintergrund ausgeführt wird und nach der Beendigung des Befehls weiterhin ausgeführt wird. 

Damit haben wir unser Ziel erreicht.

Nachdem die Befehlsausführung beendet wurde, sollten Sie folgende Meldung sehen:

```sh
 ✔ Container solr-server  Started  
 ```

Anschließend können Sie auf ihrem Rechner die Web-Admin-Oberfläche des Solr-Servers im Browser unter der URL

```
http://localhost:8983
```

aufrufen. Schauen Sie sich in der Admin-Oberfläche etwas um. Wir werden im Praxistag intensiv mit der Oberfläche arbeiten.

### Basisbefehle für das Arbeiten mit dem Docker Container

Nun möchte ich Ihnen noch einige Befehle vorstellen, die Sie für die Verwaltung des Docker Containers nutzen können.

Zum **Stoppen** des Docker Containers `solr-server` können Sie folgenden Befehl verwenden:

```sh
docker stop solr-server
```

Anschließend lässt sich der Container wieder hochfahren mittels:

```sh
docker start solr-server
```

Um zu prüfen, ob der Container `solr-server` bereits ausgeführt wird, kann folgender Befehl genutzt werden:

```sh
docker ps -f "name=solr-server"
```

Wenn der Container nicht ausgeführt wird, so kann folgender Befehl genutzt werden (um zu prüfen, ob der Container gestoppt wurde):

```sh
docker ps -a -f "name=solr-server"
```

Nach der Änderung von Konfigurationsdateien im Solr-Server muss der Solr-Server ggf. neu gestartet werden. Dies kann in einem Befehl erreicht werden:

```sh
docker restart solr-server
```

Der Docker Container `solr-server` kann entfernt werden mittels

```sh
docker compose down
```

Das lokale Verzeichnis `solr/solrdata` wird beim Entfernen des Docker Containers **nicht** entfernt, da es als Bind Mount eingebunden ist (siehe unten).

Ein neuer Docker Container kann erzeugt werden mittels

```sh
docker compose up -d
```

### Bind Mounts

Docker-Container sind isolierte Umgebungen, und standardmäßig gehen alle darin gespeicherten Daten verloren, sobald der Container gelöscht wird. Um Daten persistent zu speichern, nutzt man **Docker Volumes** oder **Bind Mounts** (Volumes eher für Produktivszenarien; Bind Mounts für lokale Entwicklungsumgebungen).

Ein **Bind Mount** ermöglicht es, ein Verzeichnis oder eine Datei vom Docker Host direkt in einen Docker-Container einzubinden. Anders als **Docker Volumes**, die von Docker verwaltet werden, nutzt ein Bind Mount einen festen Pfad im Dateisystem des Docker Host. Alle Änderungen in diesem Verzeichnis wirken sich sowohl im Docker Container als auch auf dem Docker Host aus. Ein Bind Mount ist praktisch für die lokale Entwicklung, da Änderungen an Dateien (Quellcode, Konfigurationsdateien), die auf dem Docker Host erfolgen, sofort im Container verfügbar sind. Ferner erlaubt ein Bind Mount den Zugriff auf Konfigurationsdateien oder Protokolldatein (Log-Files) von Diensten, die im Docker Container ausgeführt wird, außerhalb des Containers.

In der Datei `docker-compose.yml` ist ein Bind Mount definiert:


```yml
    volumes:
      - ./solrdata:/var/solr
```

Dadurch wird das Verzeichnis `solrdata` (innerhalb des Verzeichnisses, in dem die Datei `docker-compose.yml` gespeichert ist, d.h. `2025/solr/solrdata`) des Docker Host mit dem Verzeichnis `/var/solr` im Docker Container verbunden. Alle Änderungen, die innerhalb dieses Verzeichnisses (auch in Unterverzeichnissen) ausgeführt werden, sind im Docker Host und Container sichtbar. Werden z.B. vom Solr-Server (der im Container ausgeführt wird) Dateien in diesem Verzeichnis gespeichert, so können Sie im Docker Host ebenfalls auf diese Dateien zugreifen.

Im Unterverzeichnis `solrdata/logs` werden die Protkolldateien (Log-Files) des Solr-Servers gespeichert. Die wichtigste Logdatei eines Solr-Servers heißt `solr.log`. Falls unerwartete Probleme beim Betrieb eines Solr-Servers bzw. bei Indexierung oder Suche auftreten, kann man dort nach möglichen Fehlerursachen suchen. 

Die Logdatei wird automatisch rotiert (`solr.log.1` usw.).

Die Logausgabe des Solr-Servers kann auch mit folgenden Befehl (ausgeführt auf dem Docker Host) fortlaufend ausgegeben werden:

```sh
docker logs -f solr-server
```

Alternativ kann die Datei `solrdata/logs/solr.log` im Dateisystem des Docker Hosts aufgerufen werden, um die Lognachrichten des Solr-Servers einzusehen.
