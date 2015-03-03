package com.obomprogramador.bigdata.twitterparser;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestUnitTranslate {

	@Test
	public void test() {
		Translate2English trans = new Translate2English("<Google translate API Key>");
		String saida = trans.translate("Este é um teste de tradução. Parece que este texto é ruim!");
		System.out.println(saida);
		assertTrue(saida != null);
	}

}
