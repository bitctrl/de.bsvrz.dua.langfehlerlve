package de.bsvrz.dua.langfehlerlve;

import de.bsvrz.sys.funclib.bitctrl.dua.lve.DuaVerkehrsNetz;

/**
 * Ermoeglicht die statische instanzen der Klasse Fahrstreifen neustarten
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class DUAVerkehrsNetzTest 
extends DuaVerkehrsNetz{

	/**
	 * Setzt den initial-Status der Klasse
	 */
	public static void Reset() {
		INITIALISIERT = false;
	}
	
}
