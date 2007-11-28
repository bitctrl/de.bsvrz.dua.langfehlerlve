/**
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:<br>
 * BitCtrl Systems GmbH<br>
 * Weißenfelser Straße 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.langfehlerlve.modell.ausw;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.langfehlerlve.DELangZeitFehlerErkennung;
import de.bsvrz.dua.langfehlerlve.modell.FahrzeugArt;
import de.bsvrz.dua.langfehlerlve.modell.Rechenwerk;
import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatenListener;
import de.bsvrz.dua.langfehlerlve.modell.online.IDELzFhDatum;
import de.bsvrz.dua.langfehlerlve.modell.online.Intervall;
import de.bsvrz.dua.langfehlerlve.modell.online.PublikationsKanal;
import de.bsvrz.dua.langfehlerlve.parameter.IMsgDatenartParameter;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAUtensilien;
import de.bsvrz.sys.funclib.bitctrl.konstante.Konstante;
import de.bsvrz.sys.funclib.operatingMessage.MessageCauser;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;


/**
 * Diese Klasse fuehrt alle Berechnungen durch, die zur Erkennung systematischer Detektorfehler
 * fuer eine Messstelle vorgesehen sind (Afo DUA-BW-C1C2-13 - Vergleich mit Vorgaenger). Diese
 * Daten werden hier auch publiziert
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class AbweichungVorgaenger
extends AbstraktDELzFhObjekt
implements ClientSenderInterface,
		   IDELzFhDatenListener{
	
	/**
	 * Zeitausgabeformat fuer Betriebsmeldungen
	 */
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm"); //$NON-NLS-1$
	
	/**
	 * Datenbeschreibung der zu publizierenden Daten (Langzeitvergleichsintervall)
	 */
	private static DataDescription PUB_BESCHREIBUNG_LZ = null;

	/**
	 * Datenbeschreibung der zu publizierenden Daten (Kurzzeitvergleichsintervall)
	 */
	private static DataDescription PUB_BESCHREIBUNG_KZ = null;
	
	/**
	 * Publikationskanal
	 */
	private PublikationsKanal kanal = null;
	
	/**
	 * Verbindung zu den Onlinedaten der Messstelle selbst
	 */
	private DELzFhMessStelle messStelle = null;
	
	/**
	 * Verbindung zu den Onlinedaten des Vorgaengers der 
	 * Messstelle
	 */
	private DELzFhMessStelle messStelleMinus1 = null;
	
	/**
	 * Verbindung zu den Onlinedaten des Hauptmessquerschnitts
	 * der Messstelle selbst
	 */
	private DELzFhMessQuerschnitt messQuerschnitt = null;
	
	/**
	 * puffert alle aktuellen hier benoetigten Onlinedaten zur Berechnung
	 * der (Zwischen-)Bilanzen
	 */
	private Map<SystemObject, Intervall> puffer = Collections.synchronizedMap(new HashMap<SystemObject, Intervall>());
	
	/**
	 * die maximal zulässige Toleranz für die Abweichung von Messwerten
	 * beim Vergleich mit dem Vorgänger beim Kurzzeitintervall für die
	 * Langzeitfehlererkennung von Verkehrsdaten 
	 */
	private int abweichungMax = -1;
	
	/**
	 * Die Laenge des Vergleichsintervalls als Text
	 */
	private String vergleichsIntervall = Konstante.LEERSTRING;
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Verbindung zum Datenverteiler
	 * @param messStelle Verbindung zu den Onlinedaten der Messstelle selbst
	 * @param messStellenGruppe Messstellengruppe an der diese Berechnung erfolgt
	 * @param messStelleMinus1 Verbindung zu den Onlinedaten des Vorgaengers der 
	 * Messstelle
	 * @param messQuerschnitt Verbindung zu den Onlinedaten des Hauptmessquerschnitts
	 * der Messstelle selbst
 	 * @param langZeit Indiziert, ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmern soll
	 * @throws Exception wird weitergereicht
	 */
	protected AbweichungVorgaenger(ClientDavInterface dav,
								   DELzFhMessStelle messStelle,
								   DELzFhMessStellenGruppe messStellenGruppe,
								   DELzFhMessStelle messStelleMinus1,
								   DELzFhMessQuerschnitt messQuerschnitt,
								   boolean langZeit)
	throws Exception{
		super.init(dav, messStellenGruppe, langZeit);
		
		if(PUB_BESCHREIBUNG_LZ == null){
			PUB_BESCHREIBUNG_LZ = new DataDescription(
					dav.getDataModel().getAttributeGroup("atg.abweichungVerkehrsStärke"), //$NON-NLS-1$
					dav.getDataModel().getAspect("asp.messQuerschnittZumVorgängerLangZeit")); //$NON-NLS-1$
			PUB_BESCHREIBUNG_KZ = new DataDescription(
					dav.getDataModel().getAttributeGroup("atg.abweichungVerkehrsStärke"), //$NON-NLS-1$
					dav.getDataModel().getAspect("asp.messQuerschnittZumVorgängerKurzZeit")); //$NON-NLS-1$
		}
		this.kanal = new PublikationsKanal(dav);
		this.messStelle = messStelle;
		this.messStelleMinus1 = messStelleMinus1;
		this.messQuerschnitt = messQuerschnitt;
		this.initPuffer();
				
		dav.subscribeSender(this, messStelle.getMessStelle().getSystemObject(), 
				langZeit?PUB_BESCHREIBUNG_LZ:PUB_BESCHREIBUNG_KZ, SenderRole.source());
		
		messStelleMinus1.addListener(this);
		messQuerschnitt.addListener(this);
	}
	

	/**
	 * Initialisiert (loescht) den Online-Puffer dieser Klasse
	 */
	private final synchronized void initPuffer(){
		this.puffer.put(this.messStelleMinus1.getMessStelle().getSystemObject(), null);
		this.puffer.put(this.messQuerschnitt.getObjekt(), null);
	}
	
	
	/**
	 * Versucht die Berechnung der Bilanzverkehrsstaerke
	 * 
	 * @param objekt das Systemobjekt, zu dem das gerade empfangene Datum gehoert
	 * @param intervallDatum ein gerade empfangenes Intervalldatum != null
	 */
	private final void versucheBerechnung(SystemObject objekt, Intervall intervallDatum){
		
		
		if(intervallDatum.getDatum().isKeineDaten()){
			ResultData resultat = new ResultData(messStelle.getMessStelle().getSystemObject(),
										this.langZeit?PUB_BESCHREIBUNG_LZ:PUB_BESCHREIBUNG_KZ, 
										intervallDatum.getStart(), null);
			this.kanal.publiziere(resultat);
			this.initPuffer();
		}else{
			synchronized (this.puffer) {				
				if(!intervallDatum.getDatum().isKeineDaten()){
					long pufferZeit = -1;
					for(SystemObject obj:this.puffer.keySet()){
						if(this.puffer.get(obj) != null){
							pufferZeit = this.puffer.get(obj).getStart();
							break;
						}
					}
					if(pufferZeit == -1){
						AbweichungVorgaenger.this.puffer.put(objekt, intervallDatum);
					}else{
						if(pufferZeit == intervallDatum.getStart()){
							AbweichungVorgaenger.this.puffer.put(objekt, intervallDatum);
						}else{
							this.initPuffer();
							if(pufferZeit > intervallDatum.getStart()){
								LOGGER.warning("Veralteten Datensatz fuer " +  //$NON-NLS-1$
										objekt + " empfangen:\n" + intervallDatum); //$NON-NLS-1$
							}else{
								AbweichungVorgaenger.this.puffer.put(objekt, intervallDatum);
							}
						}
					}
				}

				boolean datenVollstaendig = true;
				for(SystemObject obj:this.puffer.keySet()){
					if(this.puffer.get(obj) == null){
						datenVollstaendig = false;
						break;
					}
				}
				
				if(datenVollstaendig){
					this.erzeugeErgebnis();
					this.initPuffer();
				}
			}
		}
	}
	
	
	/**
	 * Berechnet und publiziert die Abweichung im Vergleichsintervall analog
	 * Afo DUA-BW-C1C2-13 fuer alle zur Zeit im lokalen Puffer stehenden Daten
	 */
	private final void erzeugeErgebnis(){
		IDELzFhDatum abweichung = null;
		long datenZeit = -1;
		long intervallEnde = -1;
		synchronized (this.puffer) {
			datenZeit = this.puffer.get(this.messStelleMinus1.getMessStelle().getSystemObject()).getStart();
			intervallEnde = this.puffer.get(this.messStelleMinus1.getMessStelle().getSystemObject()).getEnde();
			abweichung = Rechenwerk.multipliziere(
								Rechenwerk.dividiere(this.puffer.get(this.messQuerschnitt.getObjekt()).getDatum(),
													 this.puffer.get(this.messStelleMinus1.getMessStelle().getSystemObject()).getDatum()),
								100.0);			
		}
		
		DataDescription datenBeschreibung = this.langZeit?PUB_BESCHREIBUNG_LZ:PUB_BESCHREIBUNG_KZ;
		Data nutzDatum = DAV.createData(datenBeschreibung.getAttributeGroup());
		
		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){
			if(abweichung.getQ(fahrzeugArt) < 0.0){
				nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName()).set(DUAKonstanten.NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
			}else{
				long abweichungMinus100Abs = Math.abs(Math.round(abweichung.getQ(fahrzeugArt) - 100.0));

				synchronized (this) {
					if(this.abweichungMax >= 0){
						if(abweichungMinus100Abs > this.abweichungMax){
							MessageSender.getInstance().sendMessage(
									MessageType.APPLICATION_DOMAIN,
									DELangZeitFehlerErkennung.getName(),
									MessageGrade.ERROR,
									this.messStelle.getMessStelle().getSystemObject(),
									new MessageCauser(DAV.getLocalUser(),
													  Konstante.LEERSTRING,
													  DELangZeitFehlerErkennung.getName()),
									"Der Wert " + fahrzeugArt.getAttributName() + " weicht um mehr als " +  //$NON-NLS-1$ //$NON-NLS-2$
									abweichungMinus100Abs + "% vom erwarteten Wert im Intervall " + //$NON-NLS-1$ 
									FORMAT.format(new Date(datenZeit)) + " - " + FORMAT.format(new Date(intervallEnde)) + //$NON-NLS-1$
									" ("+ this.vergleichsIntervall + ") ab."); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}		
				}
					
				if(abweichungMinus100Abs >= 0 && abweichungMinus100Abs <= 100){ 
					nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName()).set(abweichungMinus100Abs);
				}else{
					nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName()).set(DUAKonstanten.NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
				}
			}
		}
		
		ResultData publikationsDatum = new ResultData(this.messStelle.getMessStelle().getSystemObject(), 
													  datenBeschreibung, datenZeit, nutzDatum);
		
		this.kanal.publiziere(publikationsDatum);		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		// Quellenanmeldung
	}
	

	/**
	 * {@inheritDoc}
	 */
	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereDatum(SystemObject mqObjekt,
			Intervall intervallDatum) {
		AbweichungVorgaenger.this.versucheBerechnung(mqObjekt, intervallDatum);
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void aktualisiereMsgParameter(IMsgDatenartParameter parameter) {
		this.abweichungMax = parameter.getMaxAbweichungVorgaenger();
		this.vergleichsIntervall = DUAUtensilien.getVergleichsIntervallInText(parameter.getVergleichsIntervall()); 
	}
}