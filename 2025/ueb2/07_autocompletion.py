import pysolr
from configuration import SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME
import curses

def suggest(prefix, field_name="title"):
    """
    Führt eine Autovervollständigungssuche im Solr-Core mit dem übergebenen Präfix durch und gibt die
    Vorschläge als Liste zurück (Term mit zugehöriger Häufigkeit).
    """
    print(f"Autovervollständigungsvorschläge für Präfix '{prefix}':")
    solr_server = pysolr.Solr(f'http://{SOLR_HOST}:{SOLR_PORT}/solr/{SOLR_CORE_NAME}')
    solr_server.ping()
    # TODO Ergänzen Sie in params die Parameter für die Autovervollständigungssuche
    params = {
        "rows": 0,
        "terms": "true",
    }
    response = solr_server.search("*:*", **params)
    terms = response.raw_response["terms"][field_name]
    result = []
    if terms:
        for i in range(0, len(terms), 2):
            term = terms[i]
            count = terms[i + 1]
            result.append(f"{term} ({count})")
    return result

def main(stdscr):
    curses.curs_set(1)
    stdscr.nodelay(False)
    stdscr.keypad(True)

    prefix = ""
    while True:
        stdscr.clear()
        stdscr.addstr(0, 0, "Geben Sie ein Präfix ein (Enter zum Beenden, Ctrl+C zum Abbrechen):")
        stdscr.addstr(1, 0, prefix)

        if len(prefix) > 0:
            suggestions = suggest(prefix)
            if not suggestions:
                stdscr.addstr(3, 0, "(Keine Vorschläge)")
            else:
                for i, suggestion in enumerate(suggestions):
                    stdscr.addstr(3+i, 0, suggestion)

        stdscr.refresh()

        # Zeichen einlesen
        ch = stdscr.get_wch()

        if ch == "\n":      # Enter
            break
        elif ch == "\x03":  # Ctrl+C
            raise KeyboardInterrupt
        elif ch in ("\x7f", "\b", curses.KEY_BACKSPACE):  # Backspace
            prefix = prefix[:-1]
        else:
            prefix += str(ch)

curses.wrapper(main)
