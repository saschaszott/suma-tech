import requests
import json
from configuration import SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME

def disable_auto_create_fields_mode(base_url, core_name):
    """
    Deaktiviert den Auto-Create-Fields-Modus im Solr-Core mit dem übergebenen Namen.
    Wir müssen an dieser Stelle direkt auf die Solr Config API zugreifen, da PySolr
    diese Funktionalität nicht unterstützt.
    """
    config_url = f"{base_url}/{core_name}/config"
    response = requests.post(config_url,
                      headers={"Content-Type": "application/json"},
                      data=json.dumps({"set-user-property": {"update.autoCreateFields": "false"}}))

    if response.status_code == 200:
        print(f"Auto-Create-Fields-Modus im Solr-Core {core_name} erfolgreich deaktiviert.")
    else:
        print(f"Fehler beim Deaktivieren des Auto-Create-Fields-Modus im Solr-Core {core_name}: {response.text}")

def print_overlays(base_url, core_name):
    """
    Gibt die aktuellen Konfigurations-Overlays des Solr-Cores mit dem übergebenen Namen aus.
    Das ist nützlich, um zu überprüfen, welche Änderungen an der Konfiguration vorgenommen wurden.
    PySolr kann hierfür nicht verwendet werden, da es keine Unterstützung für die Solr Config API bietet.
    """
    config_url = f"{base_url}/{core_name}/config/overlay"
    response = requests.get(config_url)
    if response.status_code == 200:
        overlays = response.json()
        print(f"Aktuelle Konfigurations-Overlays im Solr-Core {core_name}:")
        print(json.dumps(overlays, indent=2))
    else:
        print(f"Fehler beim Abrufen der Konfigurations-Overlays im Solr-Core {core_name}: {response.text}")

def create_index_field_types(base_url, core_name):
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

    # zuerst überprüfen wir, ob die neu anzulegenden Feldtypen bereits existieren, um Fehler zu vermeiden
    existing_field_types_url = f"{base_url}/{core_name}/schema/fieldtypes"
    response = requests.get(existing_field_types_url)
    existing_field_types = []
    if response.status_code == 200:
        existing_field_types = [ft["name"] for ft in response.json().get("fieldTypes", [])]

    schema_url = f"{base_url}/{core_name}/schema"
    for field_type in field_types:
        if field_type['name'] in existing_field_types:
            print(f"Feldtyp {field_type['name']} existiert bereits im Solr-Core {core_name}, überspringe Erstellung.")
            continue

        response = requests.post(schema_url,
                          headers={"Content-Type": "application/json"},
                          data=json.dumps({"add-field-type": field_type}))
        if response.status_code == 200:
            print(f"Feldtyp {field_type['name']} erfolgreich im Solr-Core {core_name} hinzugefügt.")
        else:
            print(f"Fehler beim Hinzufügen des Feldtyps {field_type['name']} im Solr-Core {core_name}: {response.text}")

def create_index_fields(base_url, core_name, fields):
    """
    Erstellt Indexfelder im Solr-Schema des Solr-Cores mit dem übergebenen Namen
    gemäß der übergebenen Liste von Felddefinitionen.
    Wir verwenden die Solr Schema API direkt, da PySolr diese Funktionalität nicht unterstützt.
    """

    # TODO: Überprüfen, ob die Indexfelder bereits im Schema des Solr-Cores existieren, um Fehler zu vermeiden
    existing_fields = []

    schema_url = f"{base_url}/{core_name}/schema"
    for field in fields:
        if field['name'] in existing_fields:
            print(f"Indexfeld {field['name']} existiert bereits im Solr-Core {core_name}, überspringe Erstellung.")
            continue

        response = requests.post(schema_url,
                          headers={"Content-Type": "application/json"},
                          data=json.dumps({"add-field": field}))
        if response.status_code == 200:
            print(f"Indexfeld {field['name']} erfolgreich im Solr-Core {core_name} hinzugefügt.")
        else:
            print(f"Fehler beim Hinzufügen des Indexfelds {field['name']} im Solr-Core {core_name}: {response.text}")

def print_index_fields(base_url, core_name):
    """
    Gibt die aktuellen Indexfelder (und deren Anzahl) des Solr-Cores mit dem übergebenen Namen aus.
    PySolr kann hierfür nicht verwendet werden, da es keine Unterstützung für die Solr Schema API bietet.
    """
    schema_url = f"{base_url}/{core_name}/schema/fields"
    response = requests.get(schema_url)
    if response.status_code == 200:
        fields = response.json().get("fields", [])
        print(f"{len(fields)} Indexfelder im Solr-Core {core_name}:")
        for field in fields:
            print(f"- {field['name']}")
    else:
        print(f"Fehler beim Abrufen der Indexfelder im Solr-Core {core_name}: {response.text}")

if __name__ == "__main__":
    base_url = f"http://{SOLR_HOST}:{SOLR_PORT}/solr"
    disable_auto_create_fields_mode(base_url, SOLR_CORE_NAME)
    print_overlays(base_url, SOLR_CORE_NAME)

    create_index_field_types(base_url, SOLR_CORE_NAME)

    # TODO Liste der Indexfelder gemäß Aufgabenstellung in README.md erweitern
    fields = [
        {"name": "title", "type": "text_de", "stored": True, "indexed": True, "multiValued": False, "required": True},
        {"name": "title_stemmed", "type": "pg_text_de", "stored": False, "indexed": True, "multiValued": False, "required": True},
    ]
    create_index_fields(base_url, SOLR_CORE_NAME, fields)

    print_index_fields(base_url, SOLR_CORE_NAME)