#!/usr/bin/env bash

./mvnw clean package assembly:assembly

java -jar target/finite-state-transducer-1.0-SNAPSHOT-jar-with-dependencies.jar

# eine Kante wird rot eingefärbt, wenn sie auf einen Knoten zeigt, der folgende Bedingung erfüllt:
# the target node of this arc follows it in the compressed automaton structure (no goto field)
# das ist eine Eigenschaft, die uns nicht interessiert, so dass wir alle Kanten schwarz einfärben
sed -i -e 's/color="red"/color="black"/g' fst.dot

dot -Tpng -o fst.png fst.dot
