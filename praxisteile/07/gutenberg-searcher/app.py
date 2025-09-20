from flask import Flask, request, render_template, redirect, url_for
import pysolr
import math
from urllib.parse import urlencode

app = Flask(__name__)

solr_host = "localhost"
solr_port = "8983"
solr_core = "gutenberg"

solr_url = f"http://{solr_host}:{solr_port}/solr/{solr_core}"

solr = pysolr.Solr(solr_url)

@app.route("/", methods=["GET"])
def index():
    """
        Startseite (ohne Suchanfrage) anzeigen.
    """
    return render_template("index.html")

@app.route("/", methods=["POST"])
@app.route("/search", methods=["GET", "POST"])
def search():
    """
        Suchanfrage verarbeiten und Ergebnisse anzeigen.
        GET-Anfrage: Suchanfrage aus den URL-Parametern extrahieren.
        POST-Anfrage (Formular absenden): Redirect durchführen
    """
    if request.method == "POST":
        # Post-Redirect-Get: Formulardaten auslesen und auf GET umleiten
        query = request.form.get("query")
        return redirect(url_for('search', query=query))

    # GET-Anfrage: Suchanfrage aus URL-Parameter query extrahieren
    query = request.args.get("query")
    if not query:
        # Wenn keine Suchanfrage angegeben ist, auf die Startseite umleiten
        return redirect(url_for('index'))

    # Paginierung: Seite aus den URL-Parametern extrahieren, Standardwert ist 1
    # Wenn keine Seite angegeben ist, wird die erste Seite angezeigt
    page = int(request.args.get("page", 1)) # Page-Nummer aus den URL-Parametern
    results = []

    params = {
        "qf": "title^5 author_names^3 summaries^2 fulltext subjects spellcheck_base", # Gewichtung der Felder, die durchsucht werden
        "defType": "edismax", # erweiterte DisMax-Suche            
        "fl": "*,score", # Felder, die im Solr-Ergebnis zurückgegeben werden
        "debugQuery": "true",
        "bf": "log(download_count)^2", # Gewichtung (Boosting) nach Download-Anzahl
        "start": (page - 1) * 10, # Start-Offset für die Paginierung
        "spellcheck.collate": "true", # Spellchecking bei Mehr-Term-Suchanfragen
        "spellcheck.maxCollations": 5, # maximale Anzahl von Korrekturvorschlägen
        "facet": "true",
        "facet.limit": 15, # maximale Anzahl von Facetten pro Feld
        # "f.author_names_ss.facet.limit": 30, # maximale Anzahl von Facetten pro Feld
        "facet.mincount": 1, # mindestens ein Treffer pro Facette
        "facet.field": ["author_names_ss", "bookshelves_ss", "subjects_ss"],
    }

    fq = request.args.getlist("fq")
    if fq:
        params["fq"] = fq

    solr_results = solr.search(query, **params)
    explanations = solr_results.raw_response.get('debug', {}).get('explain', {})
    total_pages = math.ceil(solr_results.hits / 10)
    
    for solr_result in solr_results.docs:
        result = solr_result
        result['explanation'] = explanations.get(result['id'], "Keine Erklärung verfügbar.")
        results.append(result)
    
    print(f"Spellcheck: {solr_results.spellcheck}")
    
    return render_template(
        "index.html",
        query=query,
        results=results,
        num_of_hits=solr_results.hits,
        page=page,
        total_pages=total_pages,
        explanations=explanations,
        collations=solr_results.spellcheck.get("collations", None),
        facets=solr_results.facets.get("facet_fields", None),
        filters=get_filters(request),
        query_string=get_query_string_wo_page(request),
    )

def get_query_string_wo_page(request):
    params = request.args.to_dict(flat=False)
    if 'page' in params:
        del params['page']
    return urlencode(params, doseq=True)

def get_filters(request):
    result = []
    for f in request.args.getlist("fq"):
        if f.startswith("{!term f=author_names_ss}"):
            f = f.replace("{!term f=author_names_ss}", "")
            result.append(f"Author: {f}")
        elif f.startswith("{!term f=bookshelves_ss}"):
            f = f.replace("{!term f=bookshelves_ss}", "")
            result.append(f"Bookshelf: {f}")
        elif f.startswith("{!term f=subjects_ss}"):
            f = f.replace("{!term f=subjects_ss}", "")
            result.append(f"Subject: {f}")
    return result

if __name__ == "__main__":
    app.run(debug=True)