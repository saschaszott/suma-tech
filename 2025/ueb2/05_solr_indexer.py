import os
from lxml import etree
import pysolr
from configuration import SOLR_HOST, SOLR_PORT, SOLR_CORE_NAME

METADATA_DIR = 'pg-metadata'
FULLTEXTS_DIR = 'pg-fulltexts'

# IDs von E-Books, die aktuell noch unter dem deutschen Urheberrecht stehen und daher nicht analysiert werden sollen
# Thomas Mann (Urheberrecht endet am 1.1.2026) und Alfred Döblin (Urheberrecht endet am 1.1.2028)
ID_BLACKLIST = [
    12108,
    12053,
    38692,
    31660,
    23313,
    36779,
    34811,
    35328,
    43932,
    36766,
    65662,
    65661,
    43987,
    13810,
    43931,
]

NAMESPACES = {
    'dcterms': 'http://purl.org/dc/terms/',
    'rdf': 'http://www.w3.org/1999/02/22-rdf-syntax-ns#',
    'pgterms': 'http://www.gutenberg.org/2009/pgterms/',
}

EXTRACTORS = [
    {
        'key': 'title',
        'xpath': './/dcterms:title'
    },
    {
        'key': 'title_stemmed',
        'xpath': './/dcterms:title'
    },
    {
        'key': 'author',
        'xpath': './/dcterms:creator//pgterms:name'
    },
    {
        'key': 'author_exact',
        'xpath': './/dcterms:creator//pgterms:name'
    },
    {
        'key': 'numOfDownloadsLast30Days',
        'xpath': './/pgterms:downloads'
    },
    {
        'key': 'docType',
        'xpath': './/dcterms:type/rdf:value'
    },
    {
        'key': 'language',
        'xpath': './/dcterms:language//rdf:value'
    },
    {
        'key': 'subjectHeadings',
        'xpath': './/dcterms:subject//rdf:value'
    },
]

def extract(node, doc_id):
    metadata = {'id': doc_id}
    for extractor in EXTRACTORS:
        element = node.xpath(extractor['xpath'], namespaces=NAMESPACES)
        metadata[extractor['key']] = element[0].text if element else None
    return metadata

def index_ebook(metadata, fulltext_path):
    '''Indexiert ein E-Book aus dem Project Gutenberg (d.h. Metadaten und Volltext) in Solr.'''
    solr_server = pysolr.Solr(f'http://{SOLR_HOST}:{SOLR_PORT}/solr/{SOLR_CORE_NAME}', always_commit=True, timeout=10)
    solr_server.ping()
    with open(fulltext_path, 'r', encoding='utf-8') as f:
        fulltext = f.read()

    doc = {
        'id': metadata.get('id'),
        'title': metadata.get('title'),
        'title_stemmed': metadata.get('title'),
        # TODO Erweiterung auf die anderen Indexfelder, die zuvor im Schema angelegt wurden
    }
    solr_server.add([doc])

if __name__ == '__main__':
    # iteriere über alle RDF-Metadatendateien und prüfe für jede RDF-Datei, ob eine zugehörige Volltextdatei existiert
    # nur in diesem Fall wird das E-Book (Metadaten und Volltext) mit Solr indexiert
    num_of_files_indexed = 0
    for filename in os.listdir(METADATA_DIR):
        if filename.endswith('.rdf'):
            doc_id = int((filename[:-4])[2:])
            if doc_id in ID_BLACKLIST:
                continue
            # prüfe, ob eine zugehörige Volltextdatei existiert
            fulltext_path = os.path.join(FULLTEXTS_DIR, f'pg{doc_id}.txt')
            if not os.path.exists(fulltext_path):
                print(f'Keine Volltextdatei für E-Book mit ID {doc_id} gefunden, überspringe Indexierung.')
                continue

            print(f'Indexiere E-Book mit ID {doc_id}...')
            root = etree.parse(os.path.join(METADATA_DIR, filename))
            metadata = extract(root, doc_id)
            index_ebook(metadata, fulltext_path)
            num_of_files_indexed += 1

    print(f'Insgesamt wurden {num_of_files_indexed} E-Books (Metadaten und Volltext) in den Index aufgenommen.')