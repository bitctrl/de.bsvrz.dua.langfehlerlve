/**
 * Segment 4 Daten�bernahme und Aufbereitung (DUA), SWE 4.DELzFh DE Langzeit-Fehlererkennung
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
 * Wei�enfelser Stra�e 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.langfehlerlve;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;


import junit.framework.Assert;
import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.ClientSenderInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.SenderRole;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.langfehlerlve.langfehlerlve.DELangZeitFehlerErkennung;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.dua.bm.BmClient;
import de.bsvrz.sys.funclib.bitctrl.dua.bm.IBmListener;

/**
 * Testet den Modul DE Langzeit-Fehlererkennung
 * 
 * @author BitCtrl Systems GmbH, Bachraty
 *
 */
public class DELzFhTester extends DELangZeitFehlerErkennung 
	implements ClientSenderInterface, ClientReceiverInterface, IBmListener {

	/**
	 * Pfade zum Testdaten
	 */
	public static final String datenQuelle1 = "c:\\temp\\dav\\SWE_DE_LZ_Fehlererkennung_positiv.csv";
	public static final String datenQuelle2 = "c:\\temp\\dav\\SWE_DE_LZ_Fehlererkennung_negativ.csv";
	public static final String datenQuelle3 = "c:\\temp\\dav\\SWE_DE_LZ_Fehlererkennung_nicht_ermittelbar.csv";
	
	/**
	 * Die aktuell benutzte Pfade
	 */
	private static String datenQuelle;
	
	/**
	 * Verbindungsdaten
	 */
	public static final String[] CON_DATA = new String[] {
			"-datenverteiler=localhost:8083", 
			"-benutzer=Tester", 
			"-authentifizierung=c:\\passwd",
			"-debugLevelStdErrText=WARNING",
			"-debugLevelFileText=WARNING",
			"-KonfigurationsBereichsPid=kb.deLzFhTest"};
	
	/***
	 * Verbindung zum DAV
	 */
	private static ClientDavInterface dav;
	
	/**
	 * Konstanten
	 */
	private final long MINUTE_IN_MS = 60 * 1000;
	private final long STUNDE_IN_MS = 60 * MINUTE_IN_MS;
	private final int IVS_MS_OFFSET = 0;
	private final int IVS_MQ_OFFSET = 4;
	private final int BILANZ_OFFSET = 8;
	private final int ABW_MG_OFFSET = 10;
	private final int ABW_VOR_OFFSET = 12;
	
	/**
	 * Die Messtellengruppe
	 */
	private static SystemObject mg;
	
	/**
	 * TestdatenImporter
	 */
	private static TestDatenImporter dtImporter;
	
	/**
	 * datenbeschreibung fuer testdaten
	 */
	private static DataDescription DD_VDKZ, DD_IVS_DATEN1, DD_IVS_DATEN2, DD_BVS_DATEN, DD_ABWVS1_DATEN, DD_ABWVS2_DATEN, DD_PARAM;
	
	/**
	 * Die erwarteten Ausgabedaten
	 */
	private static long [] ausgabe;
	
	/**
	 * Die erwarteten Betriebsmeldungen
	 */
	private static long [] betriebsmeldung;
	
	/**
	 * Menge aller Messquerschnitte in der Messstellengruppe
	 */
	private static ArrayList<SystemObject> mengeMq = new ArrayList<SystemObject>();
	
	/**
	 * Menge aller MessStellen in der Messstellengruppe
	 */
	private static ArrayList<SystemObject> mengeMs = new ArrayList<SystemObject>();

	/**
	 * Index der letzten �berpr�ften Ausgabedatei
	 */
	private static int idx = 0;
	
	/**
	 * Der Zeitstempel der Ausgabedaten
	 */
	private static long ausgabeZeitStempel = 0;
	
	/**
	 * Der Typ der getesteten Fahrzeugen
	 */
	private static String fahrzeugTyp = "QKfz"; 
	
	/**
	 *  Aggreagtionsintervall der Kurzzeitdaten
	 */
	private long intervallKurzZeit = 0;
	
	/**
	 * Parametriert die Messstellengruppe
	 * @param mg MessStellengruppe
	 * @param kurzZeitAgg Aggregationsintervall der Kurzzeitdaten in Minuten 
	 * @param langZeitAgg Aggregationsintervall der Langzeitdaten in Stunden 
	 * @param maxAbwVor Maximale Abweichung fuer KZD zum Vorgaenger
	 * @param maxAbwGrp Maximale Abweichung fuer KZD zur Gruppe
	 * @throws Exception Wird beim Sende-fehler geworfen
	 */
	protected void parametriere(SystemObject mg, long kurzZeitAgg, long langZeitAgg, long maxAbwVor, long maxAbwGrp) throws Exception {
		
		Data data;
		ResultData resultat;
		data = dav.createData(dav.getDataModel().getAttributeGroup("atg.parameterMessStellenGruppe"));
		
		data.getItem("VergleichsIntervallKurzZeit").asUnscaledValue().set(kurzZeitAgg);
		data.getItem("maxAbweichungVorg�ngerKurzZeit").asUnscaledValue().set(maxAbwVor);
		data.getItem("maxAbweichungMessStellenGruppeKurzZeit").asUnscaledValue().set(maxAbwGrp);
		
		data.getItem("VergleichsIntervallLangZeit").asUnscaledValue().set(langZeitAgg);
		data.getItem("maxAbweichungVorg�ngerLangZeit").asUnscaledValue().set(100);
		data.getItem("maxAbweichungMessStellenGruppeLangZeit").asUnscaledValue().set(100);
		
		this.intervallKurzZeit = kurzZeitAgg;
		resultat = new ResultData(mg, DD_PARAM, System.currentTimeMillis(), data);
		dav.sendData(resultat);
	}
	
	/**
	 * Schneidet den Zeitstempel ab 
	 * @param zeitStempel Originalwert
	 * @param stunden <code>true</code> wenn die Stunden abgeschnitten werden sollen
	 * @param minuten  <code>true</code> wenn die Minuten abgeschnitten werden sollen
	 * @param sekunden  <code>true</code> wenn die Sekunden abgeschnitten werden sollen
	 * @return Ergebniss
	 */
	public static long abschneiden(long zeitStempel, boolean stunden, boolean minuten, boolean sekunden) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(zeitStempel);
		if(sekunden) {
			cal.set(Calendar.SECOND, 0);
		}
		if(minuten) {
			cal.set(Calendar.MINUTE, 0);
		}
		if(stunden) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
		}
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTimeInMillis();
	}
	
	/**
	 * Der Test #1
	 * 
	 */
	@Test
	public void test1() {
		try {
			System.out.println("===== QKFz Test ===== Pozitiver MessFehler ==== Quelle: " + datenQuelle1 + " ==== ");
			test("QKfz", datenQuelle1, 5, 5, 20);
		} catch (Exception e) {
			System.out.println("FEHLER BEIM TEST AUFGETRETEN:\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Der Test #2
	 * 
	 */
	@Test
	public void test2() {
		try {
			System.out.println("===== QPkw Test ===== Negatover MessFehler ==== Quelle: " + datenQuelle2 + " ==== ");
			test("QPkw", datenQuelle2, 5, 8, 12);
		} catch (Exception e) {
			System.out.println("FEHLER BEIM TEST AUFGETRETEN:\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Der Test #3
	 * 
	 */
	@Test
	public void test3() {
		try {

			System.out.println("===== QLkw Test  ===== Nicht ermittelbare Eingabedaten ==== Quelle: " + datenQuelle3 + " ==== ");
			test("QLkw", datenQuelle3, 5, 10, 15);
			
		} catch (Exception e) {
			System.out.println("FEHLER BEIM TEST AUFGETRETEN:\n" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Allgemeine Testmethode
	 *  
	 * @param fahrzeugTyp Bezeichnet den Typ des Fahrzeuges - "QKfz", "QPkw", "QLkw" 
	 * @param datenQuelle Pfade zur CSV-Datei
	 * @param intervallKurzzeit der Aggregationsintervall
	 * @param maxAbwVor Maximale Abweichung fuer KZD zum Vorgaenger
	 * @param maxAbwGrp Maximale Abweichung fuer KZD zur Gruppe
	 * @throws Exception  
	 */
	public void test(final String fahrzeugTyp, final String datenQuelle, final long intervallKurzzeit, final long maxAbwVor, final long maxAbwGrp) throws Exception {
		DELzFhTester.fahrzeugTyp = fahrzeugTyp;
		DELzFhTester.datenQuelle = datenQuelle;
		
		DELzFhTester tester = new DELzFhTester();
		String connArgs [] =   new String [CON_DATA.length] ;
		for(int i=0; i<CON_DATA.length; i++)
			connArgs[i] = CON_DATA[i];
		StandardApplicationRunner.run(tester, connArgs);
		
		Thread.sleep(1000);
		
		ResultData resultat;
		tester.parametriere(mg, intervallKurzzeit, 1, maxAbwVor, maxAbwGrp);
		
		long zeitStempel = System.currentTimeMillis() - STUNDE_IN_MS;
		zeitStempel = abschneiden(zeitStempel, false, true, true);
		
		Collection<Data> dsListe = dtImporter.getDatenSaetzeNaechstenIntervall(fahrzeugTyp);
		
		while(dsListe != null) {
			synchronized (dav) {
				int i = 0;
				for(Data ds : dsListe) {
					resultat = new ResultData(mengeMq.get(i++), DD_VDKZ, zeitStempel, ds );
					dav.sendData(resultat);
				}
				if(dtImporter.hatAusgabe()) {
					while(ausgabe != null) dav.wait();
					ausgabe = dtImporter.getAusgabe();
					
					// Die erwaretete Betriebsmeldungen 0 bedeutet keine
					for(int j=0; j<2; j++) {
						if(Math.abs(ausgabe[ABW_MG_OFFSET+j]) >  maxAbwGrp && ausgabe[ABW_MG_OFFSET+j] > -100000) 
							betriebsmeldung[j] = ausgabe[ABW_MG_OFFSET+j];
						else betriebsmeldung[j] = 0;
						if(Math.abs(ausgabe[ABW_VOR_OFFSET+j]) >  maxAbwVor && ausgabe[ABW_VOR_OFFSET+j] > -100000) 
							betriebsmeldung[2+j] = ausgabe[ABW_VOR_OFFSET+j];
						else betriebsmeldung[2+j] = 0;
					}

					ausgabeZeitStempel = zeitStempel - (intervallKurzzeit - 1)*MINUTE_IN_MS;
					dav.notify();
				}
			}
			zeitStempel += MINUTE_IN_MS;
			dsListe = dtImporter.getDatenSaetzeNaechstenIntervall(fahrzeugTyp);
		}
		dav.disconnect(false, "");
		Thread.sleep(3000);
	}
	
	/**
	 * Loescht alle instanzen von statischen Klassen
	 */
	private static void Reset() {
		AtgParameterMessStellenGruppeTest.Reset();
		BmClientTest.Reset();
		FahrstreifenTest.Reset();
		MessQuerschnittTest.Reset();
		MessQuerschnittVirtuellTest.Reset();
		MessStellenGruppeTest.Reset();
		MessStelleTest.Reset();
		PublikationsKanalTest.Reset();
		mengeMs.clear();
		mengeMq.clear();
		ausgabe = null;
		ausgabeZeitStempel = 0;
		idx = 0;
		dav = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override 
	public void initialize(ClientDavInterface dav) throws Exception {
		
		Reset();
		super.initialize(dav);
		
		DELzFhTester.dav = dav;
		
		BmClient.getInstanz(dav).addListener(this);
		
		try {
			dtImporter = new TestDatenImporter(dav, datenQuelle);
		} catch (Exception e) {
			System.out.println("Fehler beim Oeffnen der Testdaten " + e.getMessage());
			e.printStackTrace();
		}
		
		SystemObject ms1, ms2, ms3, ms4;
		SystemObject ms1_mq, ms1_mq_zu1, ms1_mq_zu2, ms1_mq_ab1, ms1_mq_ab2, ms2_mq, ms2_mq_zu1, ms2_mq_ab1, ms3_mq, ms4_mq;
		
		
		mg = dav.getDataModel().getObject("gr1");
			
		ms1 = dav.getDataModel().getObject("gr1.ms1");
		ms2 = dav.getDataModel().getObject("gr1.ms2");
		ms3 = dav.getDataModel().getObject("gr1.ms3");
		ms4 = dav.getDataModel().getObject("gr1.ms4");
		
		ms1_mq     =  dav.getDataModel().getObject("gr1.ms1.mq");
		ms1_mq_zu1 =  dav.getDataModel().getObject("gr1.ms1.zu1");
		ms1_mq_zu2 =  dav.getDataModel().getObject("gr1.ms1.zu2");
		ms1_mq_ab1 =  dav.getDataModel().getObject("gr1.ms1.ab1");
		ms1_mq_ab2 =  dav.getDataModel().getObject("gr1.ms1.ab2");
		ms2_mq     =  dav.getDataModel().getObject("gr1.ms2.mq");
		ms2_mq_zu1 =  dav.getDataModel().getObject("gr1.ms2.zu1");
		ms2_mq_ab1 =  dav.getDataModel().getObject("gr1.ms2.ab1");
		ms3_mq     =  dav.getDataModel().getObject("gr1.ms3.mq");
		ms4_mq     =  dav.getDataModel().getObject("gr1.ms4.mq");
		
		// fuer 2 Messsquerschnitte und 2 Attributsgruppen werden Betriebsmeldungen erzeugt
		betriebsmeldung = new long[4];
		
		mengeMq.add(ms1_mq);
		mengeMq.add(ms1_mq_ab1);
		mengeMq.add(ms1_mq_ab2);
		mengeMq.add(ms1_mq_zu1);
		mengeMq.add(ms1_mq_zu2);
		mengeMq.add(ms2_mq);
		mengeMq.add(ms2_mq_ab1);
		mengeMq.add(ms2_mq_zu1);
		mengeMq.add(ms3_mq);
		mengeMq.add(ms4_mq);
		
		mengeMs.add(ms1);
		mengeMs.add(ms2);
		mengeMs.add(ms3);
		mengeMs.add(ms4);
		
		DD_VDKZ = new DataDescription(dav.getDataModel().getAttributeGroup("atg.verkehrsDatenKurzZeitMq"), 
				dav.getDataModel().getAspect("asp.analyse"));
		
		DD_PARAM = new DataDescription(dav.getDataModel().getAttributeGroup("atg.parameterMessStellenGruppe"),
				dav.getDataModel().getAspect("asp.parameterVorgabe"));
		
		
		dav.subscribeSender(this, mengeMq, DD_VDKZ, SenderRole.source());
		dav.subscribeSender(this, mg, DD_PARAM, SenderRole.sender());
		
	
		DD_IVS_DATEN1 = new DataDescription(dav.getDataModel().getAttributeGroup("atg.intervallVerkehrsSt�rke"),
				dav.getDataModel().getAspect("asp.messQuerschnittKurzZeit"));
		
		DD_IVS_DATEN2 = new DataDescription(dav.getDataModel().getAttributeGroup("atg.intervallVerkehrsSt�rke"),
				dav.getDataModel().getAspect("asp.messStelleKurzZeit"));
		
		DD_BVS_DATEN = new DataDescription(dav.getDataModel().getAttributeGroup("atg.bilanzVerkehrsSt�rke"),
				dav.getDataModel().getAspect("asp.messQuerschnittKurzZeit"));
		
		DD_ABWVS1_DATEN = new DataDescription(dav.getDataModel().getAttributeGroup("atg.abweichungVerkehrsSt�rke"),
				dav.getDataModel().getAspect("asp.messQuerschnittDerMessStellenGruppeKurzZeit"));
		
		DD_ABWVS2_DATEN = new DataDescription(dav.getDataModel().getAttributeGroup("atg.abweichungVerkehrsSt�rke"),
				dav.getDataModel().getAspect("asp.messQuerschnittZumVorg�ngerKurzZeit"));
	
		dav.subscribeReceiver(this, mengeMs, DD_IVS_DATEN1, ReceiveOptions.normal(), ReceiverRole.receiver());
		dav.subscribeReceiver(this, mengeMs, DD_IVS_DATEN2, ReceiveOptions.normal(), ReceiverRole.receiver());
		dav.subscribeReceiver(this, mengeMs, DD_BVS_DATEN, ReceiveOptions.normal(), ReceiverRole.receiver());
		dav.subscribeReceiver(this, mengeMs, DD_ABWVS1_DATEN, ReceiveOptions.normal(), ReceiverRole.receiver());
		dav.subscribeReceiver(this, mengeMs, DD_ABWVS2_DATEN, ReceiveOptions.normal(), ReceiverRole.receiver());

	}

	/**
	 * {@inheritDoc}
	 */
	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
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
	public void update(ResultData[] results) {
		for(ResultData resDatei : results) {
				Data data = resDatei.getData();
				String pid = resDatei.getObject().getPid();
				long zeitSempel = resDatei.getDataTime();
				if(data == null) continue;
				
				int ms_index=0;
			
				
			synchronized (dav) {
				
				/*
				 * Die Verkehrstaerke ist fuer alle Messstellen berechnet 
				 */
				if(pid.equals("gr1.ms1")) ms_index = 0;
				else if(pid.equals("gr1.ms2")) ms_index = 1;
				else if(pid.equals("gr1.ms3")) ms_index = 2;
				else if(pid.equals("gr1.ms4")) ms_index = 3;

				if(resDatei.getDataDescription().getAttributeGroup().getPid().equals(
						"atg.intervallVerkehrsSt�rke") &&
						resDatei.getDataDescription().getAspect().getPid().equals(
						"asp.messQuerschnittKurzZeit")) 
				{
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals("IVS MQ", ausgabe[ms_index + IVS_MQ_OFFSET],x);
					System.out.println(String.format("[ %4d ] IVS MQ %8d = %8d", idx, ausgabe[ms_index + IVS_MQ_OFFSET], x));
					ausgabe[ms_index + IVS_MQ_OFFSET] = Long.MAX_VALUE;
					
					Assert.assertEquals(String.format("DIFFERENZ: %d, %s", ausgabeZeitStempel - zeitSempel, data), ausgabeZeitStempel, zeitSempel);
					System.out.println(String.format("[ %4d ] Zeitstempel OK ---  %d (%s)", idx, zeitSempel, new Date(zeitSempel)));
					
				}
				
				if(resDatei.getDataDescription().getAttributeGroup().getPid().equals(
						"atg.intervallVerkehrsSt�rke") &&
					resDatei.getDataDescription().getAspect().getPid().equals(
							"asp.messStelleKurzZeit"))
				{
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals("IVS MS", ausgabe[ms_index + IVS_MS_OFFSET],x);
					System.out.println(String.format("[ %4d ] IVS MS %8d = %8d", idx, ausgabe[ms_index + IVS_MS_OFFSET], x));
					ausgabe[ms_index + IVS_MS_OFFSET] = Long.MAX_VALUE;
					
					Assert.assertEquals(String.format("DIFFERENZ: %d, %s", ausgabeZeitStempel - zeitSempel, data), ausgabeZeitStempel, zeitSempel);
					System.out.println(String.format("[ %4d ] Zeitstempel OK ---  %d (%s)", idx, zeitSempel, new Date(zeitSempel)));
					
				}
				
				/*
				 * Die Bilanz und die Abweichungen werden nur fuer die MessStellen 2 und 3 ausgewertet
				 * 
				 */
				if(pid.equals("gr1.ms2")) ms_index = 0;
				else if(pid.equals("gr1.ms3")) ms_index = 1;
				else ms_index = 5;
				
				if(ms_index < 3 && resDatei.getDataDescription().getAttributeGroup().getPid().equals(
						"atg.bilanzVerkehrsSt�rke")) 
				{
				
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();

					Assert.assertEquals("BILANZ",ausgabe[BILANZ_OFFSET+ms_index],x);
					System.out.println(String.format("[ %4d ] BILANZ %8d = %8d", idx, ausgabe[BILANZ_OFFSET+ms_index], x));
					ausgabe[BILANZ_OFFSET+ms_index] = Long.MAX_VALUE;
					
					Assert.assertEquals(String.format("DIFFERENZ: %d, %s", ausgabeZeitStempel - zeitSempel, data), ausgabeZeitStempel, zeitSempel);
					System.out.println(String.format("[ %4d ] Zeitstempel OK ---  %d (%s)", idx, zeitSempel, new Date(zeitSempel)));
					
				}
				
				if(ms_index < 3 && resDatei.getDataDescription().getAttributeGroup().getPid().equals(
						"atg.abweichungVerkehrsSt�rke") &&
					resDatei.getDataDescription().getAspect().getPid().equals(
					"asp.messQuerschnittZumVorg�ngerKurzZeit")) 
				{
					
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals("ABW_VOR",ausgabe[ABW_VOR_OFFSET+ms_index],x);
					System.out.println(String.format("[ %4d ] ABW_VOR %8d = %8d", idx, ausgabe[ABW_VOR_OFFSET+ms_index], x));
					ausgabe[ABW_VOR_OFFSET+ms_index] = Long.MAX_VALUE;
					
					Assert.assertEquals(String.format("DIFFERENZ: %d, %s", ausgabeZeitStempel - zeitSempel, data), ausgabeZeitStempel, zeitSempel);
					System.out.println(String.format("[ %4d ] Zeitstempel OK ---  %d (%s)", idx, zeitSempel, new Date(zeitSempel)));
					
				}
				
				if(ms_index < 3 &&  resDatei.getDataDescription().getAttributeGroup().getPid().equals(
						"atg.abweichungVerkehrsSt�rke") &&
					resDatei.getDataDescription().getAspect().getPid().equals(
							"asp.messQuerschnittDerMessStellenGruppeKurzZeit"))
				{

					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals("ABW_GRP",ausgabe[ABW_MG_OFFSET+ms_index],x);
					System.out.println(String.format("[ %4d ] ABW_GRP %8d = %8d", idx, ausgabe[ABW_MG_OFFSET+ms_index], x));
				
					ausgabe[ABW_MG_OFFSET+ms_index] = Long.MAX_VALUE;
					Assert.assertEquals(String.format("DIFFERENZ: %d, %s", ausgabeZeitStempel - zeitSempel, data), ausgabeZeitStempel, zeitSempel);
					System.out.println(String.format("[ %4d ] Zeitstempel OK ---  %d (%s)", idx, zeitSempel, new Date(zeitSempel)));
					
				}
				
				//  Wenn alle Werte im Array ausgabe schon kontroliert worden sind
				//  die mit Long.MAX_VALUE gekennzeichnet sind, koennen wir den array loeschen 
				uberprufeAusgaben();
				
			}
		}
	}
	
	/**
	 * Ueberprufet ob wir schon alle ausgaben kontrolliert haben, falls ja, 
	 * signalisiert, dass wir weitere Eingabedaten an DAV abschicken koennen
	 */
	public void uberprufeAusgaben() {
		int i;
		for(i=0; i<ausgabe.length; i++) {
			if(ausgabe[i] != Long.MAX_VALUE) break;
		}
		int j;
		for(j=0; j<betriebsmeldung.length; j++) {
			if(betriebsmeldung[j] != 0) break;
		}
		if(i>=ausgabe.length && j>=betriebsmeldung.length) {
			System.out.println(String.format("[ %4d ] ------ Alle parameter OK ---- ", idx));
			ausgabeZeitStempel = 0;
			ausgabe = null;
			idx++;
			try {
				dav.notify();
				while(ausgabe == null) dav.wait();
			} catch (Exception e) { }
		}
	}

	/**
	 * Vergleicht die einkommenden Betriebsmeldungen mit den erwarteten
	 */
	public void aktualisiereBetriebsMeldungen(SystemObject obj, long zeit,
			String text) {
		int mq_index = 0;
		int typ_offset = 2;
		long lint = 0, lproc = 0;
		Date dvon = new Date(), dbis  = new Date();
		
		// Wir  vergleichen nur Betriebsmeldungen von MQ 2 und 3
		if(obj.getPid().equals("gr1.ms3.mq")) mq_index = 1;
		else if(obj.getPid().equals("gr1.ms2.mq")) mq_index = 0;
		else {
			return;
		}

		try {
			
			String typ, von, bis, intervall, prozent, abwTyp;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		
			typ = text.replaceFirst("Der Wert ", "");
			prozent = typ.replaceFirst("Q... weicht um mehr als ", "");
			abwTyp = prozent.replaceFirst("-?[\\d]+% vom erwarteten Wert im Intervall \\(Vergleich mit ", "");
			von = abwTyp.replaceFirst("[\\w]+\\) ", "");
			bis = von.replaceFirst("[\\.\\d]+ [\\d:]+ - ", "");
			intervall = bis.replaceFirst("[\\.\\d]+ [\\d:]+ \\(", "");
			
			typ = typ.substring(0, 4);
			von = von.substring(0, 17);
			bis = bis.substring(0, 17);
			prozent = prozent.split("% ")[0];
			intervall = intervall.split(" Min")[0];
			
			if( abwTyp.startsWith("Vorgaenger") ) typ_offset = 2;
			else if( abwTyp.startsWith("Nachbarn") ) typ_offset = 0;
			
			lproc = Long.parseLong(prozent);
			lint = Long.parseLong(intervall);
			dvon = dateFormat.parse(von);
			dbis = dateFormat.parse(bis);
			
		} catch (ParseException e) {
			System.out.println("Fehler beim Parsing des Datums :" + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Fehler beim Parsing einer Nummer :" + e.getMessage());
		}
		
		synchronized (dav) {

			System.out.println(String.format("[ %4d ] VOR BETRIEBSMELDUNG  %s ( %d %%)", idx, obj.getPid(), lproc) + "Erwartet: " + betriebsmeldung[0] + " " + betriebsmeldung[1] + " " + betriebsmeldung[2] + " " + betriebsmeldung[3]);
			
			Assert.assertEquals("Falsche Betriebsmedlung " + obj + " " + text + "Erwartet: " + betriebsmeldung[0] + " " + betriebsmeldung[1] + " " + betriebsmeldung[2] + " " + betriebsmeldung[3], 
					betriebsmeldung[mq_index + typ_offset], lproc);
			betriebsmeldung[mq_index + typ_offset] = 0;
			Assert.assertEquals("Intervall Von : ", dvon.getTime(), ausgabeZeitStempel);
			Assert.assertEquals("Intervall Dauer : ", dbis.getTime() - dvon.getTime(), lint * MINUTE_IN_MS);
			Assert.assertEquals("Intervall Dauer : ", lint * MINUTE_IN_MS, intervallKurzZeit * MINUTE_IN_MS);
				
			System.out.println(String.format("[ %4d ] BETRIEBSMELDUNG OK %s ( %d %%)", idx, obj.getPid(), lproc) + "Erwartet: " + betriebsmeldung[0] + " " + betriebsmeldung[1] + " " + betriebsmeldung[2] + " " + betriebsmeldung[3]);

			//  Wenn alle Werte im Array ausgabe schon kontroliert worden sind
			//  die mit Long.MAX_VALUE gekennzeichnet sind, koennen wir den array loeschen 
			uberprufeAusgaben();
		}
	}
}
