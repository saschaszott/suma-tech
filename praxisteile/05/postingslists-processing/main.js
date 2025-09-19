let P1 = [],
    P2 = [],
    i = 0,
    j = 0,
    result = [],
    started = false,
    ended = false,
    op = "p1-and-p2";

const inputP1 = document.getElementById('inputP1');
const inputP2 = document.getElementById('inputP2');
const listP1 = document.getElementById('listP1');
const listP2 = document.getElementById('listP2');
const resultElem = document.getElementById('result');
const log = document.getElementById('log');
const operation = document.getElementById('operation');
const nextButton = document.getElementById('nextBtn');
nextButton.disabled = true;

function parsePostingslist(input) {
    return input.split(',').map(x => parseInt(x.trim())).filter(x => !isNaN(x));
}

function drawList(container, list, pointerIndex, matches = []) {
    container.innerHTML = '';
    const list_copy = [...list, Infinity]; // Sentinel-Wert am Ende der Liste
    list_copy.forEach((num, idx) => {
        const el = document.createElement('div');
        if (num < Infinity) {            
            el.className = 'item';
            el.textContent = num;    
        } else {
            el.className = 'item-sentinel';
            el.textContent = ' ';
        }

        if (idx === pointerIndex) {
            const ptr = document.createElement('div');
            ptr.className = 'pointer';
            ptr.textContent = '⇩';
            el.appendChild(ptr);
        }
        if (matches.includes(idx)) {
            el.classList.add('match');
        }
        container.appendChild(el);
    });
}

function addDocumentIdToResult(docId) {
    result.push(docId);
    resultElem.innerHTML = `{ ${result.join(', ')} }`;
}

function logMsg(msg) {
    log.innerHTML += `<p>${msg}</p>`;
    log.scrollTop = log.scrollHeight;
}

function start() {
    if (inputP1.value === "" || inputP2.value === "") {
        log.innerHTML = '';
        logMsg("Bitte beide Postingslisten eingeben.");
        return;
    }

    P1 = parsePostingslist(inputP1.value);
    P2 = parsePostingslist(inputP2.value);
    P1.sort((a, b) => a - b); // Dokument-ID-Einträge von P1 numerisch sortieren
    P2.sort((a, b) => a - b); // Dokument-ID-Einträge von P2 numerisch sortieren
    i = 0;
    j = 0;
    result = [];
    started = true;
    op = operation.value;
    resultElem.innerHTML = '{}';
    log.innerHTML = '';
    drawList(listP1, P1, i);
    drawList(listP2, P2, j);
    logMsg(`Operation gestartet. Klicke auf „Nächster Schritt“.`);
    ended = false;
    nextButton.disabled = false;
    nextButton.focus();
}

function step() {
    if (!started || ended) {
        return;
    }

    const currentP1 = P1[i];
    const currentP2 = P2[j];
    logMsg(`Vergleiche P1[${i}] = ${currentP1} und P2[${j}] = ${currentP2}`);

    if (op === "p1-and-p2") {        
        // "Intersection" der beiden Postingslisten
        if (currentP1 === currentP2) {
            addDocumentIdToResult(currentP1);
            logMsg(`Dokument-ID ${currentP1} in beiden Listen gefunden → ${currentP1} in Ergebnismenge aufnehmen`);
            i++;
            j++;
        } else if (currentP1 < currentP2) {
            logMsg(`Dokument-ID ${currentP1} nur in P1 → P1-Zeiger nach rechts schieben`);
            i++;
        } else {
            logMsg(`Dokument-ID ${currentP2} nur in P2 → P2-Zeiger nach rechts schieben`);
            j++;
        }

        // Ende einer Liste erreicht?
        if (i === P1.length) {
            logMsg("Ende von P1 erreicht → Anfrageverarbeitung beendet");
            endAlgorithm();
        } else if (j === P2.length) {
            logMsg("Ende von P2 erreicht → Anfrageverarbeitung beendet");
            endAlgorithm();
        }
    } else if (op === "p1-or-p2") {
        // "Union" der beiden Postingslisten
        if (currentP1 === currentP2) {
            addDocumentIdToResult(currentP1);
            logMsg(`Dokument-ID ${currentP1} in beiden Listen gefunden → ${currentP1} einmal in Ergebnismenge aufnehmen`);
            i++;
            j++;
        } else if (currentP1 < currentP2) {
            addDocumentIdToResult(currentP1);
            logMsg(`Dokument-ID ${currentP1} in P1 gefunden → ${currentP1} in Ergebnismenge aufnehmen`);
            i++;
        } else {
            addDocumentIdToResult(currentP2);
            logMsg(`Dokument-ID ${currentP2} in P2 gefunden → ${currentP2} in Ergebnismenge aufnehmen`);
            j++;
        }

        // Ende einer Liste erreicht?
        if (i === P1.length && j === P2.length) {
            logMsg("Ende von P1 und P2 erreicht → Anfrageverarbeitung beendet");
            endAlgorithm();
        }
        else if (i === P1.length) {
            logMsg(`Ende von P1 erreicht: alle verbleibenden P2-Elemente zur Ergebnismenge hinzufügen`);
            for (; j < P2.length; j++) {
                addDocumentIdToResult(P2[j]);
            }
            endAlgorithm();
        } else if (j === P2.length) {
            logMsg(`Ende von P2 erreicht: alle verbleibenden P1-Elemente zur Ergebnismenge hinzufügen`);
            for (; i < P1.length; i++) {
                addDocumentIdToResult(P1[i]);
            }
            endAlgorithm();
        }
    } else if (op === "p1-and-not-p2") {
        if (currentP1 === currentP2) {
            logMsg(`Dokument-ID ${currentP1} in beiden Postingslisten vorhanden → überspringe ${currentP1}`);            
            i++;
            j++;
        } else if (currentP1 < currentP2) {
            addDocumentIdToResult(currentP1);
            logMsg(`Dokument-ID ${currentP1} nur in P1 gefunden → ${currentP1} in Ergebnismenge aufnehmen`);
            i++;
        } else {
            logMsg(`Dokument-ID ${currentP2} nur in P2 gefunden → P2-Zeiger nach rechts schieben`);
            j++;
        }

        // Ende einer Liste erreicht?
        if (i === P1.length && j === P2.length) {
            logMsg("Ende von P1 und P2 erreicht → Anfrageverarbeitung beendet");
            endAlgorithm();
        } else if (i === P1.length) {
            logMsg("Ende von P1 erreicht → Anfrageverarbeitung beendet");
            endAlgorithm();
        } else if (j === P2.length) {
            logMsg(`Ende von P2 erreicht: alle verbleibenden P1-Elemente zur Ergebnismenge hinzufügen`);
            for (; i < P1.length; i++) {
                addDocumentIdToResult(P1[i]);
            }
            endAlgorithm();
        }
    } else if (op === "not-p1-and-p2") {
        if (currentP1 === currentP2) {
            logMsg(`Dokument-ID ${currentP1} in beiden Postingslisten vorhanden → überspringe ${currentP1}`);
            i++;
            j++;
        } else if (currentP1 < currentP2) {
            logMsg(`Dokument-ID ${currentP1} nur in P1 gefunden → P1-Zeiger nach rechts schieben`);
            i++;
        } else {
            addDocumentIdToResult(currentP2);
            logMsg(`Dokument-ID ${currentP2} nur in P2 gefunden → ${currentP2} in Ergebnismenge aufnehmen`);
            j++;
        }

        // Ende einer Liste erreicht?
        if (i === P1.length && j === P2.length) {
            logMsg("Ende von P1 und P2 erreicht → Anfrageverarbeitung beendet");
            endAlgorithm();
        } else if (i === P1.length) {
            logMsg(`Ende von P1 erreicht: alle verbleibenden P2-Elemente zur Ergebnismenge hinzufügen`);
            for (; j < P2.length; j++) {
                addDocumentIdToResult(P2[j]);
            }
            endAlgorithm();

        } else if (j === P2.length) {
            logMsg("Ende von P2 erreicht → Anfrageverarbeitung beendet");
            endAlgorithm();
        }
    }

    drawList(listP1, P1, i, result.map(val => P1.indexOf(val)));
    drawList(listP2, P2, j, result.map(val => P2.indexOf(val)));
}

function reset() {
    inputP1.value = "2,4,8,16,20,22";
    inputP2.value = "1,4,10,16,20,21,22,25";
    operation.value = "p1-and-p2";
    endAlgorithm();
    resultElem.innerHTML = '{}';
    log.innerHTML = '';
    listP1.innerHTML = '';
    listP2.innerHTML = '';
}

function endAlgorithm() {
    started = false;
    ended = true;
    nextButton.disabled = true;
}