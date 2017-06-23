[![Build Status](https://travis-ci.org/bitctrl/de.bsvrz.dua.langfehlerlve.svg?branch=master)](https://travis-ci.org/bitctrl/de.bsvrz.dua.langfehlerlve)
[![Build Status](https://api.bintray.com/packages/bitctrl/maven/de.bsvrz.dua.langfehlerlve/images/download.svg)](https://bintray.com/bitctrl/maven/de.bsvrz.dua.langfehlerlve)

# Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung

Version: ${version}

## Übersicht

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


## Versionsgeschichte

### Version 2.0.3

- Applikationsname für MessageSender entsprechend NERZ-Vorgabe gesetzt
- DEBUG-Ausgaben optimiert
- Java8 - Code-Anpassungen

### Version 2.0.2

Release-Datum: 28.07.2016

de.bsvrz.dua.langfehlerlve.modell.ausw.DELzFhMessQuerschnitt.MQDatum
- Klasse hatte keine geeignete Hash-Funktion wurde aber in einem Set verwendet

de.bsvrz.dua.langfehlerlve.modell.online.Intervall
- hashCode entsprechend equals-Funktion ergänzt

de.bsvrz.dua.langfehlerlve.modell.ausw.DELzFhMessStelle
- Prüfung auf null-Object nach der Verwendung bei der Senderanmeldung

- Obsolete SVN-Tags aus Kommentaren entfernt
- Obsolete inheritDoc-Kommentare entfernt

### Version 2.0.1

Release-Datum: 22.07.2016

- Umpacketierung gemäß NERZ-Konvention

### Version 2.0.0

Release-Datum: 31.05.2016

#### Neue Abhängigkeiten

Die SWE benötigt nun das Distributionspaket de.bsvrz.sys.funclib.bitctrl.dua
in Mindestversion 1.5.0 und de.bsvrz.sys.funclib.bitctrl in Mindestversion 1.4.0,
sowie die Kernsoftware in Mindestversion 3.8.0.

#### Änderungen

Folgende Änderungen gegenüber vorhergehenden Versionen wurden durchgeführt:

- Der Text der Betriebsmeldung wurde gemäß neuen AFo angepasst und versandte
  Betriebsmeldungen werden jetzt auch über die Debug-Funktion mit Level INFO
  ausgegeben.

### Version 1.6.0

- Umstellung auf Java 8 und UTF-8

### Version 1.5.0

- Umstellung auf Funclib-Bitctrl-Dua

### Version 1.4.0

- Umstellung auf Maven-Build

### Version 1.3.2

 - Bei Betriebsmeldungen wird ab sofort der betroffene Messquerschnitt im Meldungstext
   mit erwaehnt (vor nur Systemobjekt-Referenz).  

### Version 1.3.1

 - Senden von reinen Betriebsmeldungen in DUA um die Umsetzung von Objekt-PID/ID nach
   Betriebsmeldungs-ID erweitert.  

### Version 1.3.0

 - Laufzeitexception bei veraltetem Zeitstempel entfernt.
         
### Version 1.2.2

 - FIX: Sämtliche Konstruktoren DataDescription(atg, asp, sim) ersetzt durch
        DataDescription(atg, asp)

### Version 1.2.0

 - Bash-Startskript hinzu
  
### Version 1.1.0

 - Verfeinerung der Testumgebung
  
### Version 1.0.0

 - Erste Auslieferung

## Bemerkungen

Diese SWE ist eine eigenständige Datenverteiler-Applikation, welche über die Klasse
de.bsvrz.dua.langfehlerlve.langfehlerlve.DELangZeitFehlerErkennung mit folgenden
Parametern gestartet werden kann (zusaetzlich zu den normalen Parametern jeder
Datenverteiler-Applikation):
	-KonfigurationsBereichsPid=pid(,pid)

## Disclaimer

Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
Copyright (C) 2007 BitCtrl Systems GmbH 

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 5A
Franklin Street, Fifth Floor, Boston, MA 02AA0-A30A, USA.


## Kontakt

BitCtrl Systems GmbH
Weißenfelser Straße 67
04229 Leipzig
Phone: +49 341-490670
mailto: info@bitctrl.de
