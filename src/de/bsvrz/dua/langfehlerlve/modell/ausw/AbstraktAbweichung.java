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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import de.bsvrz.sys.funclib.bitctrl.konstante.Konstante;
import de.bsvrz.sys.funclib.operatingMessage.MessageCauser;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;

/**
 * Diese Klasse fuehrt alle Berechnungen durch, die zur Erkennung systematischer Detektorfehler
 * fuer eine Messstelle vorgesehen sind (Afo DUA-BW-C1C2-11 bis Afo DUA-BW-C1C2-17: Vergleich mit
 * allen anderen Messstellen bzw. der Vorgaengermessstelle und ggf. Ausgabe einer Betriebsmeldung).
 * Diese Daten werden hier auch publiziert
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public abstract class AbstraktAbweichung
extends AbstraktDELzFhObjekt
implements ClientSenderInterface,
		   IDELzFhDatenListener{
	
	/**
	 * <code>atg.abweichungVerkehrsStärke</code>
	 */
	private static final String ATG_PID = "atg.abweichungVerkehrsStärke"; //$NON-NLS-1$

	/**
	 * Untere Grenze des Attributtyps <code>att.prozentPlusMinus<code>
	 */
	private static final long PROZENT_MIN = -100000;

	/**
	 * Obere Grenze des Attributtyps <code>att.prozentPlusMinus<code>
	 */
	private static final long PROZENT_MAX = 100000;

	/**
	 * Zustand des <code>nicht ermittelbar/fehlerhaft</code> des Attributtyps 
	 * <code>att.prozentPlusMinus<code>
	 */
	private static final long NICHT_ERMITTELBAR_BZW_FEHLERHAFT = -100003;

	/**
	 * Zeitausgabeformat fuer Betriebsmeldungen
	 */
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm"); //$NON-NLS-1$

	/**
	 * Publikationskanal
	 */
	private PublikationsKanal kanal = null;

	/**
	 * Verbindung zu den Onlinedaten der Messstelle selbst
	 */
	private DELzFhMessStelle messStelle = null;

	/**
	 * Verbindung zu den Onlinedaten des Hauptmessquerschnitts
	 * der Messstelle selbst
	 */
	private DELzFhMessQuerschnitt messQuerschnitt = null;

	/**
	 * alle restlichen Messstellen, zu denen diese Messstelle ins
	 * Verhaeltnis gesetzt werden soll
	 */
	private Set<SystemObject> restMessStellen = new HashSet<SystemObject>();

	/**
	 * puffert alle aktuellen hier benoetigten Onlinedaten zur Berechnung
	 * der (Zwischen-)Bilanzen
	 */
	protected Map<SystemObject, Intervall> puffer = Collections.synchronizedMap(new HashMap<SystemObject, Intervall>()); 

	/**
	 * die maximal zulässige Toleranz für die Abweichung von Messwerten
	 * beim Vergleich mit dem Vorgänger beim Kurzzeitintervall für die
	 * Langzeitfehlererkennung von Verkehrsdaten 
	 */
	protected int abweichungMax = -1;

	/**
	 * Die Laenge des Vergleichsintervalls als Text
	 */
	protected String vergleichsIntervall = Konstante.LEERSTRING;

	
	/**
	 * Erfragt die PID des Aspektes, unter dem hier die Daten des Kurzzeit-
	 * Vergleichsintervalls veroeffentlicht werden
	 * 
	 * @return die PID des Aspektes, unter dem hier die Daten des Kurzzeit-
	 * Vergleichsintervalls veroeffentlicht werden
	 */
	protected abstract String getKzAspPid();
	
	
	/**
	 * Erfragt die PID des Aspektes, unter dem hier die Daten des Langzeit-
	 * Vergleichsintervalls veroeffentlicht werden
	 * 
	 * @return die PID des Aspektes, unter dem hier die Daten des Langzeit-
	 * Vergleichsintervalls veroeffentlicht werden
	 */
	protected abstract String getLzAspPid();
	
	
	/**
	 * Erfragt eine Identifikation der Vergleichsmethode
	 * 
	 * @return eine Identifikation der Vergleichsmethode
	 */
	protected abstract String getVergleichsIdentifikation();
	

	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Verbindung zum Datenverteiler
	 * @param messStelle Verbindung zu den Onlinedaten der Messstelle selbst
	 * @param messStellenGruppe Messstellengruppe an der diese Berechnung erfolgt
	 * @param restMessStellen alle restlichen Messstellen, zu denen diese Messstelle ins
	 * Verhaeltnis gesetzt werden soll
	 * @param messQuerschnitt Verbindung zu den Onlinedaten des Hauptmessquerschnitts
	 * der Messstelle selbst
	 * @param langZeit Indiziert, ob sich dieses Objekt um das Langzeit-Vergleichsintervall
	 * kuemmern soll
	 * @throws Exception wird weitergereicht
	 */
	protected AbstraktAbweichung(ClientDavInterface dav,
			DELzFhMessStelle messStelle,
			DELzFhMessStellenGruppe messStellenGruppe,
			DELzFhMessStelle[] restMessStellen,
			DELzFhMessQuerschnitt messQuerschnitt,
			boolean langZeit)
	throws Exception{
		super.init(dav, messStellenGruppe, langZeit);

		this.kanal = new PublikationsKanal(dav);

		this.messStelle = messStelle;
		this.messQuerschnitt = messQuerschnitt;
		for(DELzFhMessStelle rms:restMessStellen){
			this.restMessStellen.add(rms.getMessStelle().getSystemObject());
		}
		this.initPuffer();

		dav.subscribeSender(this, messStelle.getMessStelle().getSystemObject(), 
				langZeit?
						new DataDescription(dav.getDataModel().getAttributeGroup(ATG_PID),
											dav.getDataModel().getAspect(this.getLzAspPid())):
						new DataDescription(dav.getDataModel().getAttributeGroup(ATG_PID),
											dav.getDataModel().getAspect(this.getKzAspPid())),
						SenderRole.source());

		messQuerschnitt.addListener(this);
		for(DELzFhMessStelle rms:restMessStellen){
			rms.addListener(this);
		}		
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
					langZeit?
							new DataDescription(DAV.getDataModel().getAttributeGroup(ATG_PID),
									DAV.getDataModel().getAspect(this.getLzAspPid())):
							new DataDescription(DAV.getDataModel().getAttributeGroup(ATG_PID),
									DAV.getDataModel().getAspect(this.getKzAspPid())), 
							intervallDatum.getStart(), null);
			this.kanal.publiziere(resultat);
			this.initPuffer();
		}else{
			synchronized (this.puffer) {				
				long pufferZeit = -1;
				for(SystemObject obj:this.puffer.keySet()){
					if(this.puffer.get(obj) != null){
						pufferZeit = this.puffer.get(obj).getStart();
						break;
					}
				}
				if(pufferZeit == -1){
					this.puffer.put(objekt, intervallDatum);
				}else{
					if(pufferZeit == intervallDatum.getStart()){
						this.puffer.put(objekt, intervallDatum);
					}else{	
						this.initPuffer();
						if(pufferZeit > intervallDatum.getStart()){
							LOGGER.warning("Veralteten Datensatz fuer " +  //$NON-NLS-1$
									objekt + " empfangen:\n" + intervallDatum); //$NON-NLS-1$
						}else{
							this.puffer.put(objekt, intervallDatum);
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
	 * Initialisiert (loescht) den Online-Puffer dieser Klasse
	 */
	private final synchronized void initPuffer(){
		this.puffer.put(this.messQuerschnitt.getObjekt(), null);
		for(SystemObject rms:this.restMessStellen){
			this.puffer.put(rms, null);
		}
	}


	/**
	 * Berechnet und publiziert die Abweichung im Vergleichsintervall analog
	 * Afo DUA-BW-C1C2-11 fuer alle zur Zeit im lokalen Puffer stehenden Daten
	 */
	private final void erzeugeErgebnis(){
		IDELzFhDatum abweichung = null;
		long datenZeit = -1;
		long intervallEnde = -1;

		synchronized (this.puffer) {
			datenZeit = this.puffer.get(this.messQuerschnitt.getObjekt()).getStart();
			intervallEnde = this.puffer.get(this.messQuerschnitt.getObjekt()).getEnde();

			Collection<IDELzFhDatum> restDaten = new HashSet<IDELzFhDatum>();
			for(SystemObject rms:this.restMessStellen){
				restDaten.add(this.puffer.get(rms).getDatum());
			}

			abweichung = Rechenwerk.multipliziere(
					Rechenwerk.dividiere(
							this.puffer.get(this.messQuerschnitt.getObjekt()).getDatum(),
							Rechenwerk.durchschnitt(restDaten)),
							100.0);			
		}

		DataDescription datenBeschreibung = langZeit?
				new DataDescription(DAV.getDataModel().getAttributeGroup(ATG_PID),
						DAV.getDataModel().getAspect(this.getLzAspPid())):
				new DataDescription(DAV.getDataModel().getAttributeGroup(ATG_PID),
						DAV.getDataModel().getAspect(this.getKzAspPid()));
		Data nutzDatum = DAV.createData(datenBeschreibung.getAttributeGroup());

		for(FahrzeugArt fahrzeugArt:FahrzeugArt.getInstanzen()){

			if(abweichung.isAuswertbar(fahrzeugArt)){
				long abweichungMinus100 = Math.round(abweichung.getQ(fahrzeugArt) - 100.0);

				if(abweichungMinus100 >= PROZENT_MIN && abweichungMinus100 <= PROZENT_MAX){ 
					nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName()).set(abweichungMinus100);
				}else{
					nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName()).set(NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
				}

				synchronized (this) {
					if(this.abweichungMax >= 0){
						if(Math.abs(abweichungMinus100) > this.abweichungMax){
							MessageSender.getInstance().sendMessage(
									MessageType.APPLICATION_DOMAIN,
									DELangZeitFehlerErkennung.getName(),
									MessageGrade.ERROR,
									this.messStelle.getMessStelle().getPruefling().getSystemObject(),
									new MessageCauser(DAV.getLocalUser(),
											Konstante.LEERSTRING,
											DELangZeitFehlerErkennung.getName()),
											"Der Wert " + fahrzeugArt.getAttributName() + " weicht um mehr als " +  //$NON-NLS-1$ //$NON-NLS-2$
											abweichungMinus100 + "% vom erwarteten Wert im Intervall (" + //$NON-NLS-1$ 
											this.getVergleichsIdentifikation() + ") " + //$NON-NLS-1$ 
											FORMAT.format(new Date(datenZeit)) + " - " + FORMAT.format(new Date(intervallEnde)) + //$NON-NLS-1$
											" ("+ this.vergleichsIntervall + ") ab."); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}		
				}
			}else{
				nutzDatum.getUnscaledValue(fahrzeugArt.getAttributName()).set(NICHT_ERMITTELBAR_BZW_FEHLERHAFT);
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
//		Quellenanmeldung
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
	public void aktualisiereDatum(SystemObject objekt,
			Intervall intervallDatum) {		
		this.versucheBerechnung(objekt, intervallDatum);
	}

}