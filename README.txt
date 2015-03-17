***********************************************************************************************
*  Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung  *
***********************************************************************************************

Version: 1.4.0


Übersicht
=========

Die SWE DE Langzeit-Fehlererkennung dient zur Erkennung von systematischen Fehlern bei der
Verkehrdatenerfassung einzelner Messquerschnitte, die durch die Plausibilisierungsstufen
nicht ermittelbar sind. Mit diesen Fehlern sind z. B. dauerhaft zu niedrige oder zu hohe
Fahrzeugmengenwerte (QPkw, QLkw, QKfz) gemeint. Diese lassen sich nur durch einen längerfristigen
Vergleich von verkehrlich ähnlichen Messquerschnitten nachweisen. Um solche Fehler erkennen zu
können, werden hier Funktionen realisiert, die sowohl laufend Indikatoren für systematische
Abweichungen im Rahmen einer permanent durchgeführten kurzfristigen Bilanzierung (i. d. R. 
5 Minuten-Intervalle) als auch relative Fehlerangaben für einen längeren Bilanzierungszeitraum
(i. d. R. ein Tag) ermitteln. Diese Daten werden permanent publiziert. Bei der Überschreitung
gewisser pro Messstellengruppe (topographisch sinnvolle Zusammenfassung mehrerer Messstellen
bzw. Messquerschnitte) definierter Grenzwerte werden Betriebsmeldungen ausgegeben.


Versionsgeschichte
==================
1.4.0
- Umstellung auf Maven-Build

1.3.2

  - Bei Betriebsmeldungen wird ab sofort der betroffene Messquerschnitt im Meldungstext
    mit erwaehnt (vor nur Systemobjekt-Referenz).  

1.3.1

  - Senden von reinen Betriebsmeldungen in DUA um die Umsetzung von Objekt-PID/ID nach
    Betriebsmeldungs-ID erweitert.  

1.3.0

  - Laufzeitexception bei veraltetem Zeitstempel entfernt.
         
1.2.2

  - FIX: Sämtliche Konstruktoren DataDescription(atg, asp, sim) ersetzt durch
         DataDescription(atg, asp)

1.2.0

  - Bash-Startskript hinzu
  
1.1.0

  - Verfeinerung der Testumgebung
  
1.0.0

  - Erste Auslieferung

Bemerkungen
===========

Diese SWE ist eine eigenständige Datenverteiler-Applikation, welche über die Klasse
de.bsvrz.dua.langfehlerlve.langfehlerlve.DELangZeitFehlerErkennung mit folgenden
Parametern gestartet werden kann (zusaetzlich zu den normalen Parametern jeder
Datenverteiler-Applikation):
	-KonfigurationsBereichsPid=pid(,pid)
	
- Tests:

Alle Tests befinden sich unterhalb des Verzeichnisses junit und sind als JUnit-Tests ausführbar.
Dabei wird implizit eine Instanz der SWE 4.DELzFh DE Langzeit-Fehlererkennung gestartet. Die implizit
gestartete SWE 4.DELzFh DE Langzeit-Fehlererkennung wird dann ueber den Datenverteiler mit Testdaten
versorgt waehrend die Rueckmeldungen (auch ueber DAV empfangen) mit den erwarteten Rueckmeldungen
verglichen werden.

Voraussetzungen:
- Start der Test-Konfiguration (extra/testKonfig.zip)
- Anpassung der DAV-Start-Parameter (Variable CON_DATA) innerhalb von 
	junit/de.bsvrz.dua.langfehlerlve.Verbindung.java

Inhalt/Verlauf DAV-Tests:

Da die Provokation aller moeglichen Zustaende fuer das System nicht moeglich ist,
wird im Folgenden versucht besonders prominente Fehlersituationen zu ueberpruefen.
Dies wird in drei Testroutinen fuer die drei Q-Attribute wie folgt vorgenommen
(test1-3 aus junit/de.bsvrz.dua.langfehlerlve.DELzFsTester.java):

Es werden folgende Daten gesendet (bzw. erwartet): 
- extra/testDaten/SWE_DE_LZ_Fehlererkennung_negativ.csv
  (Provoziert negative Bilanz- und Abweichungsergebnisse)
- extra/testDaten/SWE_DE_LZ_Fehlererkennung_nicht_ermittelbar.csv
  (Provoziert als "nicht ermittelbar" gekennzeichnet Ergebnis-Werte)
- extra/testDaten/SWE_DE_LZ_Fehlererkennung_positiv.csv 
  (Provoziert normale (positive) Ergebnis-Werte)

In den Tabellen stehen jeweils links, die fuer die MQs des Testkonfigurationsbereichs
(kb.deLzFhTest) gesendeten Werte. Rechts stehen die Werte, die als Ergebnisse in den
unterschiedlichen Attributgruppen erwartet werden.
		
Die Tests wurden so bereits erfolgreich ausgeführt.


Disclaimer
==========

Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
Copyright (C) 2007 BitCtrl Systems GmbH 

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 2 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 5A
Franklin Street, Fifth Floor, Boston, MA 02AA0-A30A, USA.


Kontakt
=======

BitCtrl Systems GmbH
Weißenfelser Straße 67
04229 Leipzig
Phone: +49 34A-490670
mailto: info@bitctrl.de
