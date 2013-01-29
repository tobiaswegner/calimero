/*
    Calimero 2 - A library for KNX network access
    Copyright (c) 2009, 2011 B. Malinowsky

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package tuwien.auto.calimero.dptxlator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;
import tuwien.auto.calimero.Util;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import tuwien.auto.calimero.log.LogManager;

/**
 * @author B. Malinowsky
 *
 */
public class DPTXlator4ByteSignedTest extends TestCase
{
	private DPTXlator4ByteSigned t;

	private final String min = "-2147483648";
	private final String max = "2147483647";
	private final String value1 = "412";
	private final String value2 = "1294961111";

	private final byte[] dataMin = { 0, 0, 0, 0 };
	private final byte[] dataMax = { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
	private final byte[] dataValue1 = { 0, 0, 1, (byte) 0x9c };
	// offset = 2, 3 empty bytes at end
	private final byte[] dataValue2 =
		{ 0, 0, (byte) 0x4D, (byte) 0x2F, (byte) 0x89, (byte) 0xD7, 0, 0, 0 };

	private final String[] strings = { max, min, value1, value2, };
	private final int[] ints = { 2147483647, -2147483648, 412, 1294961111, };
	private final byte[] data =
		{ (byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, 0, 0, 1,
			(byte) 0x9c, (byte) 0x4D, (byte) 0x2F, (byte) 0x89, (byte) 0xD7 };


	/**
	 * @param name
	 */
	public DPTXlator4ByteSignedTest(final String name)
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		LogManager.getManager().addWriter("DPTXlator", Util.getLogWriter());
		t = new DPTXlator4ByteSigned(DPTXlator4ByteSigned.DPT_COUNT);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		Thread.sleep(100);
		LogManager.getManager().removeWriter("DPTXlator", Util.getLogWriter());
		super.tearDown();
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.dptxlator.DPTXlator4ByteSigned#getAllValues()}.
	 * @throws KNXFormatException
	 */
	public void testGetAllValues() throws KNXFormatException
	{
		assertEquals(1, t.getItems());
		Helper.assertSimilar("0", t.getAllValues()[0]);

		t.setData(data);
		assertEquals(data.length / 4, t.getItems());
		assertEquals(t.getItems(), t.getItems());
		Helper.assertSimilar(strings, t.getAllValues());
		// value = 0xCCCCCCCC
		t.setValue(343597383);
		assertEquals(1, t.getItems());
		Helper.assertSimilar("343597383", t.getAllValues()[0]);

		t.setData(dataValue1);
		assertEquals(1, t.getItems());
		Helper.assertSimilar(value1, t.getAllValues()[0]);
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.dptxlator.DPTXlator#setValues(java.lang.String[])}.
	 * @throws KNXFormatException
	 */
	public void testSetValues() throws KNXFormatException
	{
		t.setValues(strings);
		assertEquals(strings.length, t.getItems());
		Helper.assertSimilar(strings, t.getAllValues());
	
		t.setValues(new String[0]);
		assertEquals(strings.length, t.getItems());
		Helper.assertSimilar(strings, t.getAllValues());
	
		final String[] s = { value1 };
		t.setValues(s);
		assertEquals(s.length, t.getItems());
		Helper.assertSimilar(s, t.getAllValues());
	
		t.setValues(new String[] { t.getValue(), t.getValue() });
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.dptxlator.DPTXlator#setValue(java.lang.String)}.
	 * @throws KNXFormatException
	 */
	public void testSetValueString() throws KNXFormatException
	{
		t.setValue(value2);
		Helper.assertSimilar(value2, t.getValue());
		String s = t.getValue();
		t.setValue(s);
		Helper.assertSimilar(value2, t.getValue());
		assertEquals(s, t.getValue());

		t.setValue(value1);
		Helper.assertSimilar(value1, t.getValue());
		s = t.getValue();
		t.setValue(s);
		Helper.assertSimilar(value1, t.getValue());
		assertEquals(s, t.getValue());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.dptxlator.DPTXlator4ByteSigned#getValue()}.
	 * @throws KNXFormatException
	 */
	public void testGetValue() throws KNXFormatException
	{
		Helper.assertSimilar("0", t.getValue());
		t.setValues(new String[0]);
		Helper.assertSimilar("0", t.getValue());
		t.setValue(ints[0]);
		Helper.assertSimilar(strings[0], t.getValue());
		t.setData(dataValue2, 2);
		Helper.assertSimilar(value2, t.getValue());
		t.setData(data);
		Helper.assertSimilar(max, t.getValue());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.dptxlator.DPTXlator#setData(byte[], int)}.
	 */
	public void testSetDataByteArrayInt()
	{
		t.setData(dataMin, 0);
		try {
			t.setData(new byte[] {}, 0);
			fail("should throw");
		}
		catch (final KNXIllegalArgumentException e) {}
		assertTrue(Arrays.equals(dataMin, t.getData()));

		t.setData(dataMax, 0);
		assertTrue(Arrays.equals(dataMax, t.getData()));

		t.setData(dataValue2, 2);
		byte[] d = t.getData();
		assertEquals(4, d.length);
		for (int i = 0; i < d.length; i++) {
			assertEquals(d[i], dataValue2[i + 2]);
		}

		final byte[] array = new byte[data.length + 6];
		System.arraycopy(data, 0, array, 3, data.length);
		t.setData(array, 3);
		d = t.getData();
		assertEquals(data.length, d.length);
		assertTrue(Arrays.equals(data, d));
		Helper.assertSimilar(strings, t.getAllValues());
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.dptxlator.DPTXlator#getData(byte[], int)}.
	 * @throws KNXFormatException
	 */
	public void testGetDataByteArrayInt() throws KNXFormatException
	{
		byte[] d = new byte[5];
		Arrays.fill(d, (byte) 0xAA);
		assertEquals(5, t.getData(d, 1).length);
		final byte[] empty = new byte[4];
		assertTrue(Arrays.equals(empty, t.getData(new byte[4], 1)));

		t.setData(data);
		d = t.getData(new byte[25], 6);
		for (int i = 0; i < 6; i++)
			assertEquals(0, d[i]);
		for (int i = 6; i < 22; i++)
			assertEquals(data[i - 6], d[i]);
		for (int i = 22; i < 25; i++)
			assertEquals(0, d[i]);

		for (int i = 0; i < ints.length; i++) {
			t.setValue(ints[i]);
			d = t.getData(new byte[4 + i], i);
			for (int j = 0; j < 4; j++) {
				assertEquals(data[4 * i + j], d[i + j]);
			}
		}
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.dptxlator.DPTXlator4ByteSigned#getSubTypes()}.
	 */
	public void testGetSubTypes()
	{
		assertEquals(9, t.getSubTypes().size());
		final Map types = t.getSubTypes();
		System.out.println("\n4 Byte Signed DPTs:");
		final Collection c = types.values();
		for (final Iterator i = c.iterator(); i.hasNext();) {
			final DPT dpt = (DPT) i.next();
			System.out.println(dpt.toString());
		}
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.dptxlator.DPTXlator4ByteSigned#getValueSigned()}.
	 * @throws KNXFormatException
	 */
	public void testGetValueSigned() throws KNXFormatException
	{
		assertEquals(0, t.getValueSigned());

		for (int i = 0; i < ints.length - 1; i++) {
			t.setData(data, 4 * i);
			assertEquals(ints[i], t.getValueSigned());
		}
		t.setData(dataValue2, 2);
		assertEquals(ints[3], t.getValueSigned());

		for (int i = 0; i < strings.length; i++) {
			t.setValue(strings[i]);
			assertEquals(ints[i], t.getValueSigned());
		}
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.dptxlator.DPTXlator4ByteSigned#setValue(int)}.
	 * @throws KNXFormatException
	 */
	public void testSetValueInt() throws KNXFormatException
	{
		for (int i = 0; i < ints.length; i++) {
			t.setValue(ints[i]);
			assertEquals(ints[i], t.getValueSigned());
			Helper.assertSimilar(strings[i], t.getValue());
		}
	}
}