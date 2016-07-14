/*
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.langfehlerlve.tests.
 * 
 * de.bsvrz.dua.langfehlerlve.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.langfehlerlve.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.langfehlerlve.tests.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.langfehlerlve.tests;

import de.bsvrz.dua.langfehlerlve.langfehlerlve.DELangZeitFehlerErkennung;
import de.bsvrz.dua.tests.DuATestBase;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;
import de.bsvrz.sys.funclib.kappich.annotations.NotNull;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessageInterface;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessageSink;
import org.junit.Before;

/**
 * TBD Dokumentation
 *
 * @author Kappich Systemberatung
 */
public class DuaLangFehlerLveTestBase extends DuATestBase {
	private DELangZeitFehlerErkennung _deLangZeitFehlerErkennung;
	
	static {
		OperatingMessageSink.register(new OperatingMessageSink() {
			                              @Override
			                              public void publish(final OperatingMessageInterface message) {
				                              System.out.println("BM: " + message);
			                              }
		                              }
		);
	}
	
	@NotNull
	@Override
	protected String[] getConfigurationAreas() {
		return new String[]{"kb.deLzFhTest1", "kb.deLzFhTest2"};
	}


	@Before
	public void setUp() throws Exception {
		super.setUp();
		_deLangZeitFehlerErkennung = new DELangZeitFehlerErkennung();
		_deLangZeitFehlerErkennung.parseArguments(new ArgumentList(new String[]{"-KonfigurationsBereichsPid=kb.deLzFhTest1,kb.deLzFhTest2"}));
		_deLangZeitFehlerErkennung.initialize(_connection);
	}
}
