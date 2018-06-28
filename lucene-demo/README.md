# Lucene Demo Projekt

Diese Demo besteht aus zwei Teilen:

## Lucene Demo

`LuceneDemo` zeigt die Erzeugung eines _in-memory_ Index. Anschließend werden einige Suchanfragen
auf dem Index ausgeführt. Der Index besteht aus einem Dokument, das drei Felder besitzt. Nach dem
Hinzufügen eines weiteren Dokuments wird der invertierte Index ausgegeben.

## Indexer / Searcher

Die beiden Klassen `Indexer` und `Searcher` können genutzt werden, um die XML-Dateien der 37 Werke
von Shakespeare zu indexieren und schließlich interaktiv Suchanfragen auf dem Index auszuführen. Der
Index wird im Dateisystem persistent gespeichert. Das Ablageverzeichnis ist als statische Variable
in der Klasse `Indexer` definiert, ebenso das Ausgangsverzeichnis, in dem sich die zu indexierenden
XML-Dateien befinden.

## Hinweis zur Ausführung

Die einzelnen Programme können über die beigefügten Bash-Scripte kompiliert und ausgeführt werden.
Für das _Dependency Management_ verwenden wir Apache Maven. Durch die Bereitstellung des _Maven Wrappers_
`mvnw` muss _Maven_ auf dem auszuführenden System nicht installiert werden.
