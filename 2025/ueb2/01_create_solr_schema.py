import requests
import json
from configuration import SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME

def disable_auto_create_fields_mode(solr_host, solr_port, core_name):
    """
    Deaktiviert den Auto-Create-Fields-Modus im Solr-Core mit dem übergebenen Namen.
    Wir müssen an dieser Stelle direkt auf die Solr Config API zugreifen, da PySolr
    diese Funktionalität nicht unterstützt.
    """
    config_url = f"http://{solr_host}:{solr_port}/solr/{core_name}/config"
    r = requests.post(config_url,
                      headers={"Content-Type": "application/json"},
                      data=json.dumps({"set-user-property": {"update.autoCreateFields": "false"}}))
    if r.status_code == 200:
        print(f"Auto-Create-Fields-Modus im Solr-Core {core_name} erfolgreich deaktiviert.")
    else:
        print(f"Fehler beim Deaktivieren des Auto-Create-Fields-Modus im Solr-Core {core_name}: {r.text}")

def print_overlays(solr_host, solr_port, core_name):
    """
    Gibt die aktuellen Konfigurations-Overlays des Solr-Cores mit dem übergebenen Namen aus.
    Das ist nützlich, um zu überprüfen, welche Änderungen an der Konfiguration vorgenommen wurden.
    PySolr kann hierfür nicht verwendet werden, da es keine Unterstützung für die Solr Config API bietet.
    """
    config_url = f"http://{solr_host}:{solr_port}/solr/{core_name}/config/overlay"
    r = requests.get(config_url)
    if r.status_code == 200:
        overlays = r.json()
        print(f"Aktuelle Konfigurations-Overlays im Solr-Core {core_name}:")
        print(json.dumps(overlays, indent=2))
    else:
        print(f"Fehler beim Abrufen der Konfigurations-Overlays im Solr-Core {core_name}: {r.text}")

def create_index_field_types(solr_host, solr_port, core_name):
    """
    Erstellt benutzerdefinierte Feldtypen im Solr-Schema des Solr-Cores mit dem übergebenen Namen
    gemäß den Vorgaben aus der Aufgabenstellung. Auch hier verwenden wir die Solr Schema API direkt,
    da PySolr diese Funktionalität nicht unterstützt.
    """
    field_types = [
        {
            "name": "pg_pint",
            "class": "solr.IntPointField",
            "docValues": True
        },
        {
            "name": "pg_text_de",
            "class": "solr.TextField",
            "positionIncrementGap": "100",
            "analyzer": {
                "tokenizer": {"class": "solr.StandardTokenizerFactory"},
                "filters": [
                    {"class": "solr.LowerCaseFilterFactory"},
                    {"class": "solr.GermanNormalizationFilterFactory"},
                    {"class": "solr.GermanLightStemFilterFactory"}
                ]
            }
        }
    ]

    # TODO: Überprüfen, ob die Feldtypen bereits existieren, um Fehler zu vermeiden

    schema_url = f"http://{solr_host}:{solr_port}/solr/{core_name}/schema"
    for field_type in field_types:
        r = requests.post(schema_url,
                          headers={"Content-Type": "application/json"},
                          data=json.dumps({"add-field-type": field_type}))
        if r.status_code == 200:
            print(f"Feldtyp {field_type['name']} erfolgreich im Solr-Core {core_name} hinzugefügt.")
        else:
            print(f"Fehler beim Hinzufügen des Feldtyps {field_type['name']} im Solr-Core {core_name}: {r.text}")

def create_index_fields(solr_host, solr_port, core_name, fields):

    # TODO: Überprüfen, ob die Indexfelder bereits existieren, um Fehler zu vermeiden

    schema_url = f"http://{solr_host}:{solr_port}/solr/{core_name}/schema"
    for field in fields:
        r = requests.post(schema_url,
                          headers={"Content-Type": "application/json"},
                          data=json.dumps({"add-field": field}))
        if r.status_code == 200:
            print(f"Indexfeld {field['name']} erfolgreich im Solr-Core {core_name} hinzugefügt.")
        else:
            print(f"Fehler beim Hinzufügen des Indexfelds {field['name']} im Solr-Core {core_name}: {r.text}")

if __name__ == "__main__":
    disable_auto_create_fields_mode(SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME)
    print_overlays(SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME)

    create_index_field_types(SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME)
    # TODO Liste gemäß Aufgabenstellung in README.md erweitern
    fields = [
        {"name": "title_stemmed", "type": "pg_text_de", "stored": False, "indexed": True, "multiValued": False, "required": True},
    ]
    create_index_fields(SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME, fields)