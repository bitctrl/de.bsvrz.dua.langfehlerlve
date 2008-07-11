#!/bin/bash

# In das Verzeichnis des Skripts wechseln, damit relative Pfade funktionieren
cd `dirname $0`

source ../../../skripte-bash/einstellungen.sh

################################################################################
# SWE-Spezifische Parameter	(�berpr�fen und anpassen)                          #
################################################################################

kb="kb.MS_Konfig_A5,kb.MS_Konfig_A6,kb.MS_Konfig_A8,kb.MS_Konfig_A81,kb.MS_Konfig_B27"

################################################################################
# Folgende Parameter m�ssen �berpr�ft und evtl. angepasst werden               #
################################################################################

# Parameter f�r den Java-Interpreter, als Standard werden die Einstellungen aus # einstellungen.sh verwendet.
#jvmArgs="-Dfile.encoding=ISO-8859-1"

# Parameter f�r den Datenverteiler, als Standard werden die Einstellungen aus # einstellungen.sh verwendet.
#dav1="-datenverteiler=localhost:8083 -benutzer=Tester -authentifizierung=passwd -debugFilePath=.."

jconPort="10444"

if [ "$testlauf" ]; then
	jvmArgs=$jvmArgs" -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port="$jconPort" -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
fi

################################################################################
# Ab hier muss nichts mehr angepasst werden                                    #
################################################################################

# Applikation starten
$java $jvmArgs -jar ../de.bsvrz.dua.langfehlerlve-runtime.jar \
	$dav1 \
	-KonfigurationsBereichsPid=$kb \
	-debugLevelStdErrText=ERROR \
	-debugLevelFileText=FINE \
	-debugLevelFileXML=OFF \
	-debugLevelFileExcel=OFF \
	-debugLevelFileHTML=OFF \
	&
