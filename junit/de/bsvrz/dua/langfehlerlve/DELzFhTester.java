package de.bsvrz.dua.langfehlerlve;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.dua.av.DAVAnmeldungsVerwaltung;
import de.bsvrz.sys.funclib.bitctrl.dua.lve.MQBestandteil;

public class DELzFhTester {

	/**
	 * Pfade zum Testdaten
	 */
	public static final String datenQuelle = "c:\\temp\\dav\\SWE_DE_LZ_Fehlererkennung_2.csv";
	
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
	
	/**
	 * TestdatenImporter
	 */
	private static TestDatenImporter dtImporter;
	/**
	 * Testmessstellen
	 */
	private static SystemObject ms1, ms2, ms3, ms4;
	
	/**
	 * Testmessquerschnitte
	 */
	private static SystemObject ms1_mq, ms1_mq_zu1, ms1_mq_zu2, ms1_mq_ab1, ms1_mq_ab2, ms2_mq, ms2_mq_zu1, ms2_mq_zu2, ms3_mq, ms4_mq;
	
	/**
	 * datenbeschreibung fuer testdaten
	 */
	private static DataDescription DD_LFTDATEN, DD_TPTDATEN, DD_FBTDATEN, DD_FBZDATEN;
	
	@Test
	void test() {
		
		
	}
	
	/* ########################
	 * 
	 * muss die initialisierung der hauptklasse des getesten moduls ueberschreiben
	 * 
	 * #########################
	 */

	//@Override 
	void init(ClientDavInterface dav) {
		//super.init()
		try {
			dtImporter = new TestDatenImporter(dav, datenQuelle);
		} catch (Exception e) {
			System.out.println("Fehler beim oeffnen der Testdaten " + e.getMessage());
			e.printStackTrace();
		}
		
		String[] messQuerschnitte = new String [] {
				"gr1.ms1.mq",
				"gr1.ms1.mq.zu1",
				"gr1.ms1.mq.zu2",
				"gr1.ms1.mq.ab1",
				"gr1.ms1.mq.ab2",
				"gr1.ms2.mq",
				"gr1.ms2.mq.zu1",
				"gr1.ms2.mq.ab1",
				"gr1.ms3.mq",
				"gr1.ms4.mq"
			};
		
		ms1 = dav.getDataModel().getObject("gr1.ms1");
		ms2 = dav.getDataModel().getObject("gr1.ms2");
		ms3 = dav.getDataModel().getObject("gr1.ms3");
		ms4 = dav.getDataModel().getObject("gr1.ms4");
		
		 dav.getDataModel().getObject("gr1.ms1.mq");
		
	}
}
