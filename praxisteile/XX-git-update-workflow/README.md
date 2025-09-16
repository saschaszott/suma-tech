# Demo zum Git Update Workflow

## Ausgangszustand

Zuerst holen wir die neuesten Änderungen aus dem Remote Repository:

```sh
git pull
```

Wir finden nun im Verzeichnis `praxisteile` das Unterverzeichnis `XX-git-update-workflow`. 

In diesem Verzeichnis liegt das Python-Script `current-datetime.py`, das die aktuelle Uhrzeit auf der Kommandozeile ausgibt.

## Ausführung einer lokalen Änderung

Wir werden nun die Datei `current-datetime.py` lokal ändern. Dazu führen wir zwischen Zeile 4 und Zeile 5 einen Kommentar ein, so dass die Datei anschließend wie folgt aussieht:

```py
from datetime import datetime

def main():
    now = datetime.now()
    # Ausgabe der aktuellen Systemzeit <-- neu eingefügte Zeile
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))

if __name__ == "__main__":
    main()
```

Wir können uns die lokal ausgeführten Änderungen mit dem folgenden Befehl auflisten lassen:

```sh
git status
```

In diesem Fall sollte bei Ihnen folgende Ausgabe erscheinen:

```
On branch main
Your branch is up to date with 'origin/main'.

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
	modified:   current-datetime.py

no changes added to commit (use "git add" and/or "git commit -a")
```

Solange Sie die Änderung nicht mittels `git add current-datetime.py` in die _Staging Area_ übernehmen, bekommt git von ihrer lokalen Änderung nichts mit.

## Ausführung einer Änderung durch den Dozenten

Nun wird der Dozent die Datei `current-datetime.py` im Remote-Repository (bei GitHub) ändern.

Der Dozent fügt hierzu nach Zeile 5 eine neue Zeile in die Datei ein:

```py
print("Aktuelles Datum:", now.strftime("%Y-%m-%d"))
```

Auf der GitHub-Website können wir uns den Zustand der Datei nach der Änderung ansehen (https://github.com/saschaszott/suma-tech/blob/main/praxisteile/XX-git-update-workflow/current-datetime.py):

```py
from datetime import datetime

def main():
    now = datetime.now()
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))
    print("Aktuelles Datum:", now.strftime("%Y-%m-%d")) # <-- neue Zeile hinzugefügt

if __name__ == "__main__":
    main()
```

Aufgrund der lokalen Änderung sowie der gleichzeitigen Änderung der Datei im GitHub-Repository haben wir einen **Konflikt** erzeugt.

## Aktualisierung des lokalen Arbeitsverzeichnisses

Um die Änderung des Dozenten aus dem GitHub-Repository zu übernehmen und in das lokale Arbeitsverzeichnis zu übernehmen, kann der Befehl 

```sh
git pull
```

verwendet werden.

Aufgrund des Konflikts erhalten wir folgende Ausgabe:

```
remote: Enumerating objects: 11, done.
remote: Counting objects: 100% (11/11), done.
remote: Compressing objects: 100% (4/4), done.
remote: Total 6 (delta 1), reused 6 (delta 1), pack-reused 0 (from 0)
Unpacking objects: 100% (6/6), 1.40 KiB | 204.00 KiB/s, done.
From https://github.com/saschaszott/ir-hdm-2025
   4b7ebd7..1e342ae  main       -> origin/main
Updating 4b7ebd7..1e342ae
error: Your local changes to the following files would be overwritten by merge:
	praxisteile/XX-git-update-workflow/current-datetime.py
Please commit your changes or stash them before you merge.
Aborting
```

Wenn git die entfernte Änderung des Dozenten übernehmen würde, dann wäre die lokal ausgeführte Änderung verloren. Daher wird in diesem Fall der Aktualisierungsvorgang abgebrochen (`Aborting`).

Um den Konflikt aufzulösen, kann die lokale Änderung in eine Zwischenablage (Git Stash) verschoben werden. Anschließend kann `git pull` (erfolgreich) aufgerufen werden, um die entfernte Änderung zu übernehmen und auf die lokale (unveränderte) Datei zu übernehmen. Nun kann die Änderung, die in der lokalen Zwischenablage "geparkt" wurde, auf die Datei `current-datetime.py` angewendet werden. 

Durch die folgende Befehlsfolge wird das Ziel erreicht:

```sh
git stash
git pull
git stash pop
```

Im Ergebnis hat die Datei `current-datetime.py` nun folgenden Zustand (beide Änderungen, sowohl die lokale als auch die entfernte Änderung des Dozenten sind in der Datei enthalten):

```py
from datetime import datetime

def main():
    now = datetime.now()
    # Ausgabe der aktuellen Systemzeit <-- neu eingefügte Zeile
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))
    print("Aktuelles Datum:", now.strftime("%Y-%m-%d")) # <-- neue Zeile hinzugefügt

if __name__ == "__main__":
    main()
```

Die lokale Änderung ist weiterhin über den Befehl `git status` ersichtlich, der folgende Ausgabe liefert:

```
On branch main
Your branch is up to date with 'origin/main'.

Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git restore <file>..." to discard changes in working directory)
	modified:   current-datetime.py

no changes added to commit (use "git add" and/or "git commit -a")
```

## Gelingt `git stash pop` immer?

Es kann Fälle geben, in denen der obige Ansatz versagt. Nehmen wir an, dass auch der Dozent zwischen Zeile 4 und Zeile 5 einen Kommentar einfügt und die Änderung mit einem Commit übernimmt, so dass die Datei im entfernten GitHub-Repository des Dozenten folgende Struktur hat:

```py
from datetime import datetime

def main():
    now = datetime.now()
    # ein Kommentar, der vom Dozenten eingefügt wurde <-- weitere Änderung
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))
    print("Aktuelles Datum:", now.strftime("%Y-%m-%d")) # <-- neue Zeile hinzugefügt

if __name__ == "__main__":
    main()
```

Wir wissen bereits, dass die Ausführung von `git pull` aufgrund der lokalen Änderung in der Datei `current-datetime.py` mit einem Fehler abbricht. Daher wenden wir erneut die Befehlskaskade `git stash` - `git pull` - `git stash pop` an.

Die ersten beiden Befehle werden erfolgreich ausgeführt.

Bei der Ausführung von `git stash pop` kommt es nun aber zu einer Fehlermeldung:

```
Auto-merging praxisteile/XX-git-update-workflow/current-datetime.py
CONFLICT (content): Merge conflict in praxisteile/XX-git-update-workflow/current-datetime.py
On branch main
Your branch is up to date with 'origin/main'.

Unmerged paths:
  (use "git restore --staged <file>..." to unstage)
  (use "git add <file>..." to mark resolution)
	both modified:   current-datetime.py

no changes added to commit (use "git add" and/or "git commit -a")
The stash entry is kept in case you need it again.
```

git kann in diesem Fall die beiden Änderungen in der Datei `current-datetime.py` nicht zusammenführen, da sie sich auf die gleiche Zeile beziehen. git vermerkt die nicht auflösbaren Konflikte direkt in der Datei:

```py
from datetime import datetime

def main():
    now = datetime.now()
<<<<<<< Updated upstream
    # ein Kommentar, der vom Dozenten eingefügt wurde <-- weitere Änderung
=======
    # Ausgabe der aktuellen Systemzeit <-- neu eingefügte Zeile
>>>>>>> Stashed changes
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))
    print("Aktuelles Datum:", now.strftime("%Y-%m-%d")) # <-- neue Zeile hinzugefügt

if __name__ == "__main__":
    main()
```

Zuerst wird die Änderung des Dozenten im entfernten GitHub-Repository ausgegeben (im Bereich zwischen `<<<<<<< Updated upstream` und `=======`). Direkt darauffolgend wird die lokale Änderung ausgegeben (zwischen `=======` und `>>>>>>> Stashed changes`).

Der Konflikt muss nun von Ihnen manuell aufgelöst werden. Dazu entfernen Sie entweder den ersten Teil (mit der entfernten Änderung) oder alternativ den zweiten Teil (mit ihrer lokalen Änderung).

### Variante 1: Entscheidung für die Remote-Änderung des Dozenten

Um die entfernte Änderung des Dozenten in der Datei `current-datetime.py` zu übernehmen, entfernen Sie den Teil der von `=======` und `>>>>>>> Stashed changes` (z.B. in VS Code) begrenzt wird sowie die Zeile `<<<<<<< Updated upstream`, so dass die Datei schließlich folgende Struktur aufweist:

```py
from datetime import datetime

def main():
    now = datetime.now()
    # ein Kommentar, der vom Dozenten eingefügt wurde <-- weitere Änderung
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))
    print("Aktuelles Datum:", now.strftime("%Y-%m-%d")) # <-- neue Zeile hinzugefügt

if __name__ == "__main__":
    main()
```

Anschließend führen Sie die folgenden git Befehle aus:

```sh
git add current-datetime.py
git commit -m "Konflikt gelöst: Remote-Version des Dozenten behalten"
```

Die Ausgabe von `git status` zeigt keine Konflikte mehr an:

```
On branch main
Your branch is ahead of 'origin/main' by 1 commit.
  (use "git push" to publish your local commits)

nothing to commit, working tree clean
```

In der Zwischenablage (Git Stash) ist die lokale Änderung allerdings immer noch vorhanden. Sie sollte daher noch entfernt werden, um später nicht mit anderen zwischengespeicherten Änderungen durcheinanderzukommen. Mit dem folgenden Befehl wird der oberste Stash-Eintrag entfernt:

```sh
git stash drop
```

Anschließend sollte der Stash leer sein, so dass der folgende Befehl ein leeres Ergebnis liefert:

```sh
git stash list
```

### Variante 2: Entscheidung für ihre lokale Änderung

Um ihre lokale Änderung in der Datei `current-datetime.py` zu übernehmen, entfernen Sie den Teil der von `<<<<<<< Updated upstream` und `=======` begrenzt wird (z.B. in VS Code) und die Zeile `>>>>>>> Stashed changes`, so dass die Datei schließlich folgende Struktur aufweist:

```py
from datetime import datetime

def main():
    now = datetime.now()
    # Ausgabe der aktuellen Systemzeit <-- neu eingefügte Zeile
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))
    print("Aktuelles Datum:", now.strftime("%Y-%m-%d")) # <-- neue Zeile hinzugefügt

if __name__ == "__main__":
    main()
```

Anschließend führen Sie die folgenden git Befehle aus:

```sh
git add current-datetime.py
git commit -m "Konflikt gelöst: lokale Änderung vorziehen"
```

Die Ausgabe von `git status` zeigt keine Konflikte mehr an:

```
On branch main
Your branch is ahead of 'origin/main' by 1 commit.
  (use "git push" to publish your local commits)

nothing to commit, working tree clean
```

In der Zwischenablage (Git Stash) ist die lokale Änderung allerdings immer noch vorhanden. Sie sollte daher noch entfernt werden, um später nicht mit anderen zwischengespeicherten Änderungen durcheinanderzukommen. Mit dem folgenden Befehl wird der oberste Stash-Eintrag entfernt:

```sh
git stash drop
```

Anschließend sollte der Stash leer sein, so dass der folgende Befehl ein leeres Ergebnis liefert:

```sh
git stash list
```

## Alternatives Vorgehen mit einem eigenen Branch

Die Verwendung eines eigenen (lokalen) Branch minimiert Konfliktsituationen und ermöglicht gleichzeitig Änderungen aus dem main-Branch zu integrieren.

### Anlegen eines eigenen Branches

Zuerst muss dazu ein eigener Branch mit einem beliebigen Namen (z.B. `local-changes`) mit dem folgenden Befehl angelegt werden:

```sh
git checkout -b local-changes
```

### Hinzufügen von Änderungen zum eigenen Branch

Nun können wir im eigenen Branch eine Änderung an der Datei `current-datetime.py` vornehmen, z.B. fügen wir eine neue Kommentarzeile am Dateianfang ein.

Anschließend können wir die Änderung versionieren, indem wir folgende Befehle aufrufen:

```sh
git add current-datetime.py
git commit -m "Änderung am Dateianfang"
```

Durch dieses Vorgehen können Sie später die Änderungen an der Datei besser nachverfolgen. Sie können mehrere Änderungen zu einer logischen Einheit durch einen Commit zusammenfassen.

### Übernahme von Änderungen aus dem Remote-Repository des Dozenten

Die entfernten Änderungen des Dozenten können Sie durch dieses Vorgehen konfliktfrei in den Hauptzweig (Main Branch) `main` übernehmen, da Sie im Branch `main` keine Änderungen vornehmen.

Wechseln Sie zuerst zum `main` Branch mit folgendem Befehl:

```sh
git checkout main
```

Die Änderungen aus dem Remote-Repository bei GitHub holen Sie mit folgendem Befehl:

```sh
git pull
```

Nun wechseln Sie zurück in ihren lokalen Branch (z.B. `local-changes`):

```sh
git checkout local-changes
```

Schließlich übernehmen Sie die Änderungen mit einem _Merge_ durch folgenden Befehl:

```sh
git merge main
```

### Ausführung einer manuellen Konfliktauflösung

Es kann bei der Ausführung des Merge-Befehls zu einem Konflikt kommen, der von Ihnen aufgelöst werden muss. Schauen wir uns auch dafür ein Beispiel an.

Wechseln Sie in ihren lokalen Branch (falls Sie dort nicht bereits sind):

```sh
git checkout local-changes
```

Führen Sie eine Änderung in der Datei `current-datetime.py` aus, indem Sie in dem Methodenkommentar zwei neue Zeile einfügen, so dass ihre Version der Datei folgendermaßen aussieht:

```py
from datetime import datetime

def main():
    """
    Diese Funktion gibt die aktuelle Systemzeit.


    """
    now = datetime.now()    
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))    

if __name__ == "__main__":
    main()
```

Führen Sie nun (wie oben beschrieben) einen Commit aus:

```sh
git add current-datetime.py
git commit -m "Änderung im Methodenkommentar"
```

Nun wird auch ihr Dozent eine Änderung im Methodenkommentar vornehmen, so dass die Version der Datei im Remote-Repository bei GitHub folgende Struktur aufweist:

```py
from datetime import datetime

def main():
    """
    Diese Funktion gibt die aktuelle Systemzeit.
    Es wird kein Argument erwartet.
    """
    now = datetime.now()    
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))    

if __name__ == "__main__":
    main()
```

Führen Sie nun die oben beschriebenen Befehle zur Integration dieser Änderung im entfernten GitHub-Repository aus:

```sh
git checkout main
git pull
git checkout local-changes
```

Führen wir nun den Befehl `git merge main` aus, so kommt es zu einer Konfiktsituation, wie wir in der Befehlsausgabe erkennen:

```
Auto-merging praxisteile/XX-git-update-workflow/current-datetime.py
CONFLICT (content): Merge conflict in praxisteile/XX-git-update-workflow/current-datetime.py
Automatic merge failed; fix conflicts and then commit the result.
```

Wir müssen in diesem Fall den Konflikt manuell auflösen, wobei die Konflikte innerhalb der Datei wie folgt markiert werden:

```py
from datetime import datetime

def main():
    """
    Diese Funktion gibt die aktuelle Systemzeit.
<<<<<<< HEAD


=======
    Es wird kein Argument erwartet.
>>>>>>> main
    """
    now = datetime.now()
    print("Aktuelle Systemzeit:", now.strftime("%H:%M:%S"))

if __name__ == "__main__":
    main()
```

Wir können uns für eine der beiden Änderungen entscheiden, indem wir den Teil oberhalb oder unterhalb von `=======` belassen und den anderen Konfliktteil löschen (sowie die Markierungszeilen). Anschließend können wir das Ergebnis versionieren, indem wir einen Commit ausführen:

```sh
git commit -m 'Konflikt aufgelöst: Änderungen des Dozenten übernommen'
```

Dadurch ist die Konfliktauflösung abgeschlossen, wie wir auch an der Ausgabe von `git status` sehen können:

```sh
On branch local-changes
nothing to commit, working tree clean
```
