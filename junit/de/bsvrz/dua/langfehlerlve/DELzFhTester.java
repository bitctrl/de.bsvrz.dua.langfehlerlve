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

package de.bsvrz.dua.langfehlerlve;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;

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

/**
 * Testet den Modul DE Langzeit-Fehlererkennung
 * 
 * @author BitCtrl Systems GmbH, Bachraty
 *
 */
public class DELzFhTester extends DELangZeitFehlerErkennung 
	implements ClientSenderInterface, ClientReceiverInterface {

	/**
	 * Pfade zum Testdaten
	 */
	public static final String datenQuelle1 = "c:\\temp\\dav\\SWE_DE_LZ_Fehlererkennung_positiv.csv";
	public static final String datenQuelle2 = "c:\\temp\\dav\\SWE_DE_LZ_Fehlererkennung_positiv2.csv";
	public static final String datenQuelle3 = "c:\\temp\\dav\\SWE_DE_LZ_Fehlererkennung_negativ.csv";
	
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
			"-KonfigurationsBeriechsPid=kb.deLzFhTest"};
	
	/***
	 * Verbindung zum DAV
	 */
	private static ClientDavInterface dav;
	
	/**
	 * Konstanten
	 */
	private static long MINUTE_IN_MS = 60 * 1000;
	private static long STUNDE_IN_MS = 60 * MINUTE_IN_MS;
	
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
	private static DataDescription DD_VDKZ, DD_IVS_DATEN, DD_BVS_DATEN, DD_ABWVS1_DATEN, DD_ABWVS2_DATEN, DD_PARAM;
	
	/**
	 * Die erwerteten Ausgabedaten
	 */
	private static long [] ausgabe;
	
	/**
	 * Menge aller Messquerschnitte in der Messstellengruppe
	 */
	private static ArrayList<SystemObject> mengeMq = new ArrayList<SystemObject>();
	
	/**
	 * Menge aller MessStellen in der Messstellengruppe
	 */
	private static ArrayList<SystemObject> mengeMs = new ArrayList<SystemObject>();

	/**
	 * Index der letzten überprüften ausgabedatei
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
	 * Parametriert die Messstellengruppe
	 * @param mg MessStellengruppe
	 * @param kurzZeitAgg Aggregationsintervall der Kurzzeitdaten in Minuten 
	 * @param langZeitAgg Aggregationsintervall der Langzeitdaten in Stunden 
	 * @throws Exception Wird beim Sende-fehler geworfen
	 */
	protected void parametriere(SystemObject mg, long kurzZeitAgg, long langZeitAgg) throws Exception {
		
		Data data;
		ResultData resultat;
		data = dav.createData(dav.getDataModel().getAttributeGroup("atg.parameterMessStellenGruppe"));
		
		data.getItem("VergleichsIntervallKurzZeit").asUnscaledValue().set(kurzZeitAgg);
		data.getItem("maxAbweichungVorgängerKurzZeit").asUnscaledValue().set(0);
		data.getItem("maxAbweichungMessStellenGruppeKurzZeit").asUnscaledValue().set(0);
		
		data.getItem("VergleichsIntervallLangZeit").asUnscaledValue().set(langZeitAgg);
		data.getItem("maxAbweichungVorgängerLangZeit").asUnscaledValue().set(0);
		data.getItem("maxAbweichungMessStellenGruppeLangZeit").asUnscaledValue().set(0);
		
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
		//cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	/**
	 * Der Test
	 * 
	 * @throws Exception
	 */
	public void test() {
		try {
			test("QKfz", datenQuelle1, 5);
			test("QPkw", datenQuelle1, 5);
			test("QLkw", datenQuelle1, 5);
			
		} catch (Exception e) {
			System.out.println("FEHLER BEIM TEST AUFGETRETEN:\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Test nr. 2
	 * @throws Exception
	 */
	
	/**
	 * Allgemeine Testmethode
	 *  
	 * @param fahrzeugTyp Bezeichnet den Typ des Fahrzeuges - "QKfz", "QPkw", "QLkw" 
	 * @param datenQuelle Pfade zur CSV-Datei
	 * @param intervallKurzzeit der Aggregationsintervall
	 * @throws Exception  
	 */
	public void test(final String fahrzeugTyp, final String datenQuelle, final long intervallKurzzeit) throws Exception {
		DELzFhTester.fahrzeugTyp = fahrzeugTyp;
		DELzFhTester.datenQuelle = datenQuelle;
		
		DELzFhTester tester = new DELzFhTester();
		StandardApplicationRunner.run(tester, CON_DATA);
		Thread.sleep(1000);
		
		ResultData resultat;
		tester.parametriere(mg, intervallKurzzeit, 1);
		
		long zeitStempel = System.currentTimeMillis() - STUNDE_IN_MS;
		zeitStempel = abschneiden(zeitStempel, false, true, true);
		
		Collection<Data> dsListe = dtImporter.getDatenSaetzeNaechstenIntervall(fahrzeugTyp);
		
		while(dsListe != null) {
			int i = 0;
			for(Data ds : dsListe) {
				resultat = new ResultData(mengeMq.get(i++), DD_VDKZ, zeitStempel, ds );
				dav.sendData(resultat);
			}
			if(dtImporter.hatAusgabe()) {
				while(ausgabe != null) Thread.sleep(100);
				ausgabe = dtImporter.getAusgabe();
				ausgabeZeitStempel = zeitStempel - (intervallKurzzeit - 1)*MINUTE_IN_MS;
			}
			zeitStempel += MINUTE_IN_MS;
			dsListe = dtImporter.getDatenSaetzeNaechstenIntervall(fahrzeugTyp);
		}
		dav.disconnect(false, "");
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override 
	public void initialize(ClientDavInterface dav) throws Exception {
		super.initialize(dav);
		
		DELzFhTester.dav = dav;
		
		try {
			dtImporter = new TestDatenImporter(dav, datenQuelle);
		} catch (Exception e) {
			System.out.println("Fehler beim oeffnen der Testdaten " + e.getMessage());
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
		
		DD_IVS_DATEN = new DataDescription(dav.getDataModel().getAttributeGroup("atg.intervallVerkehrsStärke"),
				dav.getDataModel().getAspect("asp.messQuerschnittKurzZeit"));
		
		DD_BVS_DATEN = new DataDescription(dav.getDataModel().getAttributeGroup("atg.bilanzVerkehrsStärke"),
				dav.getDataModel().getAspect("asp.messQuerschnittKurzZeit"));
		
		DD_ABWVS1_DATEN = new DataDescription(dav.getDataModel().getAttributeGroup("atg.abweichungVerkehrsStärke"),
				dav.getDataModel().getAspect("asp.messQuerschnittDerMessStellenGruppeKurzZeit"));
		
		DD_ABWVS2_DATEN = new DataDescription(dav.getDataModel().getAttributeGroup("atg.abweichungVerkehrsStärke"),
				dav.getDataModel().getAspect("asp.messQuerschnittZumVorgängerKurzZeit"));
		
		DD_PARAM = new DataDescription(dav.getDataModel().getAttributeGroup("atg.parameterMessStellenGruppe"),
				dav.getDataModel().getAspect("asp.parameterVorgabe"));
	
		
		dav.subscribeSender(this, mengeMq, DD_VDKZ, SenderRole.source());
		dav.subscribeSender(this, mg, DD_PARAM, SenderRole.sender());
		
		dav.subscribeReceiver(this, mengeMs, DD_IVS_DATEN, ReceiveOptions.normal(), ReceiverRole.receiver());
		dav.subscribeReceiver(this, mengeMs, DD_BVS_DATEN, ReceiveOptions.normal(), ReceiverRole.receiver());
		dav.subscribeReceiver(this, mengeMs, DD_ABWVS1_DATEN, ReceiveOptions.normal(), ReceiverRole.receiver());
		dav.subscribeReceiver(this, mengeMs, DD_ABWVS2_DATEN, ReceiveOptions.normal(), ReceiverRole.receiver());

	}

	/**
	 * {@inheritDoc}
	 */
	public void dataRequest(SystemObject object,
			DataDescription dataDescription, byte state) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRequestSupported(SystemObject object,
			DataDescription dataDescription) {
		// TODO Auto-generated method stub
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
			
			final int IVS_MS_OFFSET = 0;
			final int IVS_MQ_OFFSET = 4;
			final int BILANZ_OFFSET = 8;
			final int ABW_MG_OFFSET = 10;
			final int ABW_VOR_OFFSET = 12;
			
			int ms_index = 0;
			
			if(pid.equals("gr1.ms1")) ms_index = 0;
			else if(pid.equals("gr1.ms2")) ms_index = 1;
			else if(pid.equals("gr1.ms3")) ms_index = 2;
			else if(pid.equals("gr1.ms4")) ms_index = 3;
			
			Assert.assertEquals(ausgabeZeitStempel, zeitSempel);
			System.out.println(String.format("[ %4d ] Zeitstempel OK ", idx));
			
			if(resDatei.getDataDescription().getAttributeGroup().getPid().equals(
					"atg.intervallVerkehrsStärke") &&
					resDatei.getDataDescription().getAspect().getPid().equals(
					"asp.messQuerschnittKurzZeit")) 
			{
				long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
				Assert.assertEquals(ausgabe[ms_index + IVS_MQ_OFFSET],x);
				System.out.println(String.format("[ %4d ] %8d = %8d", idx, ausgabe[ms_index + IVS_MQ_OFFSET], x));
				ausgabe[ms_index + IVS_MQ_OFFSET] = Long.MAX_VALUE;
			}
			
			if(resDatei.getDataDescription().getAttributeGroup().getPid().equals(
					"atg.intervallVerkehrsStärke") &&
				resDatei.getDataDescription().getAspect().getPid().equals(
						"asp.messStelleKurzZeit"))
			{
				long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
				Assert.assertEquals(ausgabe[ms_index + IVS_MS_OFFSET],x);
				System.out.println(String.format("[ %4d ] %8d = %8d", idx, ausgabe[ms_index + IVS_MS_OFFSET], x));
				ausgabe[ms_index + IVS_MS_OFFSET] = Long.MAX_VALUE;
			}
			
			
			if(resDatei.getDataDescription().getAttributeGroup().getPid().equals(
					"atg.bilanzVerkehrsStärke")) 
			{
				if(pid.equals("gr1.ms2")) {
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals(ausgabe[BILANZ_OFFSET],x);
					System.out.println(String.format("[ %4d ] %8d = %8d", idx, ausgabe[BILANZ_OFFSET], x));
					ausgabe[BILANZ_OFFSET] = Long.MAX_VALUE;
				}
				else if(pid.equals("gr1.ms3")) {
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals(ausgabe[BILANZ_OFFSET+1],x);
					System.out.println(String.format("[ %4d ] %8d = %8d", idx, ausgabe[BILANZ_OFFSET+1], x));
					ausgabe[BILANZ_OFFSET+1] = Long.MAX_VALUE;
				}
			}
			
			if(resDatei.getDataDescription().getAttributeGroup().getPid().equals(
					"atg.abweichungVerkehrsStärke") &&
				resDatei.getDataDescription().getAspect().getPid().equals(
				"asp.messQuerschnittZumVorgängerKurzZeit")) 
			{
				if(pid.equals("gr1.ms2")) {
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals(ausgabe[ABW_VOR_OFFSET],x);
					System.out.println(String.format("[ %4d ] %8d = %8d", idx, ausgabe[ABW_VOR_OFFSET], x));
					ausgabe[ABW_VOR_OFFSET] = Long.MAX_VALUE;
				}
				else if(pid.equals("gr1.ms3")) {
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals(ausgabe[ABW_VOR_OFFSET+1],x);
					System.out.println(String.format("[ %4d ] %8d = %8d", idx, ausgabe[ABW_VOR_OFFSET+1], x));
					ausgabe[ABW_VOR_OFFSET+1] = Long.MAX_VALUE;
				}
			}
			
			if(resDatei.getDataDescription().getAttributeGroup().getPid().equals(
					"atg.abweichungVerkehrsStärke") &&
				resDatei.getDataDescription().getAspect().getPid().equals(
						"asp.messQuerschnittDerMessStellenGruppeKurzZeit"))
			{
				if(pid.equals("gr1.ms2")) {
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals(ausgabe[ABW_MG_OFFSET],x);
					System.out.println(String.format("[ %4d ] %8d = %8d", idx, ausgabe[ABW_MG_OFFSET], x));
					ausgabe[ABW_MG_OFFSET] = Long.MAX_VALUE;
				}
				else if(pid.equals("gr1.ms3")) {
					long x = data.getItem(fahrzeugTyp).asUnscaledValue().longValue();
					Assert.assertEquals(ausgabe[ABW_MG_OFFSET+1],x);
					System.out.println(String.format("[ %4d ] %8d = %8d", idx, ausgabe[ABW_MG_OFFSET+1], x));
					ausgabe[ABW_MG_OFFSET+1] = Long.MAX_VALUE;
				}
			}
			
			//  Wenn alle Werte im Array ausgabe schon kontroliert worden sind
			//  die mit LongMAX_VALUE gekennzeichnet sind, koennen wir den array loeschen 
			
			if(ausgabe != null) {
				int i;
				for(i=0; i<ausgabe.length; i++) {
					if(ausgabe[i] != Long.MAX_VALUE) break;
				}
				if(i>=ausgabe.length) {
					ausgabe = null;
					idx++;
					System.out.println(String.format("[ %4d ] ------ Alle parameter OK ---- ", idx));
				}
			}
		}
	}
}
