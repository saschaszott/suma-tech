package de.suma;

public class Configuration {

    // Basis-URL des Solr-Servers (greifen Sie vom Wirtsystem auf den Solr-Server in der VM zu,
    // so müssen Sie hier die IP-Adresse der VM eintragen)
    public static final String SOLR_SERVER_URL = "http://localhost:8983/solr/";

    // diesen Solr-Core haben wir in der Vorlesung bereits angelegt und mit einer Konfiguration versehen
    public static final String SOLR_CORE_NAME = "solr-ueb2";

    // Zugangsdaten für den Zugriff auf den Solr-Server
    public static final String USERNAME = "sumatech";
    public static final String PASSWORD = "suma!tech$17";

}
