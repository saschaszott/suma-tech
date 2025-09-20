import requests

SOLR_CORE_NAME = "gutenberg"
SOLR_URL = f"http://localhost:8983/solr/{SOLR_CORE_NAME}/schema"

# 1. Definition der Feldtypen
FIELD_TYPES = [
    {
        "name": "gb_string",
        "class": "solr.StrField",
        "sortMissingLast": True,
    },
    {
        "name": "gb_boolean",
        "class": "solr.BoolField",
    },
    {
        "name": "gb_pint",
        "class": "solr.IntPointField",
    },
    {
        "name": "gb_text_general",
        "class": "solr.TextField",
        "positionIncrementGap": "100", # verhindert, dass multiValued-Feldwerte wie ein einziger Textblock behandelt werden
        "analyzer": {
            "tokenizer": {"class": "solr.StandardTokenizerFactory"},
            "filters": [{"class": "solr.LowerCaseFilterFactory"}],
        },
    },
    {
        "name": "gb_text_custom",
        "class": "solr.TextField",
        "positionIncrementGap": "100", # verhindert, dass multiValued-Feldwerte wie ein einziger Textblock behandelt werden
        "indexAnalyzer": {
            "tokenizer": {"class": "solr.StandardTokenizerFactory"},
            "filters": [
                {"class": "solr.LowerCaseFilterFactory"},
                {"class": "solr.StopFilterFactory", "words": "stopwords.txt", "ignoreCase": "true"},
                {"class": "solr.PorterStemFilterFactory"}
            ]
        },
        "queryAnalyzer": {
            "tokenizer": {"class": "solr.StandardTokenizerFactory"},
            "filters": [
                {"class": "solr.LowerCaseFilterFactory"},
                {"class": "solr.StopFilterFactory", "words": "stopwords.txt", "ignoreCase": "true"}
            ]
        }
    }    
]

# 2. Definition der Index-Felder
FIELDS = [
    {"name": "id", "type": "gb_string", "stored": True, "indexed": True, "required": True},
    {"name": "title", "type": "gb_text_general", "stored": True, "indexed": True},
    {"name": "subjects", "type": "gb_text_general", "stored": True, "indexed": True, "multiValued": True},
    {"name": "author_names", "type": "gb_text_general", "stored": True, "indexed": True, "multiValued": True},
    {"name": "author_birth_year", "type": "gb_pint", "stored": True, "indexed": True},
    {"name": "author_death_year", "type": "gb_pint", "stored": True, "indexed": True},
    {"name": "summaries", "type": "gb_text_general", "stored": True, "indexed": True, "multiValued": True},
    {"name": "bookshelves", "type": "gb_text_general", "stored": True, "indexed": True, "multiValued": True},
    {"name": "languages", "type": "gb_string", "stored": True, "indexed": True, "multiValued": True},
    {"name": "copyright", "type": "gb_boolean", "stored": True, "indexed": False},
    {"name": "media_type", "type": "gb_string", "stored": True, "indexed": False},
    {"name": "formats", "type": "gb_string", "stored": True, "indexed": False, "multiValued": True},
    {"name": "download_count", "type": "gb_pint", "stored": True, "indexed": False},
    {"name": "fulltext", "type": "gb_text_custom", "stored": False, "indexed": True},
]

def add_field_type(type_def):
    # vorhandene Feldtypen können nicht überschrieben werden, daher
    # zuerst den Feldtyp löschen, falls er existiert    
    delete_payload = {"delete-field-type": {"name": type_def["name"]}}
    delete_response = requests.post(SOLR_URL, json=delete_payload)
    if delete_response.status_code == 200:
        print(f"Feldtyp '{type_def['name']}' erfolgreich gelöscht.")
    
    # jetzt können wir den Feldtyp hinzufügen
    payload = {"add-field-type": type_def}
    response = requests.post(SOLR_URL, json=payload)
    if response.status_code == 200:
        print(f"Feldtyp '{type_def['name']}' erfolgreich hinzugefügt.")
    elif "already exists" in response.text:
        print(f"Feldtyp '{type_def['name']}' existiert bereits.")
    else:
        print(f"Fehler bei Feldtyp '{type_def['name']}': {response.text}")

def add_field(field_def):
    payload = {"add-field": field_def}
    response = requests.post(SOLR_URL, json=payload)
    if response.status_code == 200:
        print(f"Index-Feld '{field_def['name']}' erfolgreich zum Schema hinzugefügt.")
    elif "already exists" in response.text:
        print(f"Index-Feld '{field_def['name']}' existiert bereits.")
    else:
        print(f"Fehler bei Feld '{field_def['name']}': {response.text}")

if __name__ == "__main__":
    for field_type in FIELD_TYPES:
        add_field_type(field_type)

    for field in FIELDS:
        add_field(field)
