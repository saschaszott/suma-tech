import requests
import time

SOLR_CORE_NAME = "gutenberg"
SOLR_URL = f"http://localhost:8983/solr/{SOLR_CORE_NAME}/schema/fieldtypes"

def add_classic_similarity_fields():
    
    field_types = [{
        "name": "gb_text_general_classic",
        "class": "solr.TextField",
        "positionIncrementGap": "100", # verhindert, dass multiValued-Feldwerte wie ein einziger Textblock behandelt werden
        "similarity": {
            "class": "solr.ClassicSimilarityFactory"
        },
        "analyzer": {
            "tokenizer": {"class": "solr.StandardTokenizerFactory"},
            "filters": [{"class": "solr.LowerCaseFilterFactory"}],
        },
    },
    {
        "name": "gb_text_custom_classic",
        "class": "solr.TextField",
        "positionIncrementGap": "100", # verhindert, dass multiValued-Feldwerte wie ein einziger Textblock behandelt werden
        "similarity": {
            "class": "solr.ClassicSimilarityFactory"
        },
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
    }]

    for field_type in field_types:
        payload = {
            "add-field-type": field_type
        }
        response = requests.post(SOLR_URL, json=payload)
        if response.status_code == 200:
            print(f"Feldtyp {field_type['name']} erfolgreich hinzugefügt.")
        else:
            print(f"Fehler beim Hinzufügen des Feldtyps {field_type['name']}: {response.status_code} - {response.text}")
    
    # wir brauchen die zusätzlichen Felden nicht auf stored=true setzen
    fields = [
    {"name": "title_classic", "type": "gb_text_general_classic", "stored": False, "indexed": True},
    {"name": "summaries_classic", "type": "gb_text_general_classic", "stored": False, "indexed": True, "multiValued": True},
    {"name": "fulltext_classic", "type": "gb_text_custom_classic", "stored": False, "indexed": True}]

    for field in fields:
        payload = {
            "add-field": field
        }
        response = requests.post(SOLR_URL, json=payload)
        if response.status_code == 200:
            print(f"Feld {field['name']} erfolgreich hinzugefügt.")
        else:
            print(f"Fehler beim Hinzufügen des Felds {field['name']}: {response.status_code} - {response.text}")

if __name__ == "__main__":
    add_classic_similarity_fields()
