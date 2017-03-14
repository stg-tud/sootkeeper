package de.tud.cs.peaks.test.sootconfig;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import soot.options.Options;
import de.tud.cs.peaks.sootconfig.FluentOptions;

public class FluentOptionTest {

	@Test
	public void simple() {
		FluentOptions o = new FluentOptions();
		o = o.keepLineNumbers();
		assertEquals("Options(keep line numbers)", o.toString());
		assertEquals(true, o.applyTo(Options.v()).keep_line_number());
	}

}
