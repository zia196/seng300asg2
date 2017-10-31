
package ca.ucalgary.seng300.a1.test;

import org.lsmr.vending.*;

import org.lsmr.vending.hardware.DeliveryChute;
import org.lsmr.vending.hardware.EmptyException;
import org.lsmr.vending.hardware.PopCanChannel;

import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;

import org.lsmr.vending.hardware.DisabledException;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.ucalgary.seng300.a1.Controller;

/**
 * @author Vending Solutions Incorporated
 * Developed by: Nguyen Viktor, Michaela Olšáková, Roman Sklyar
 *
 */
public class ControllerTest {
	private Controller myVending;
	private Coin coin;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Controller vending = new Controller();
		myVending = vending;

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ca.ucalgary.seng300.a1.Controller#Controller()}.
	 */
	@Test
	public void testController() {
		System.out.println(myVending.getTotal());

		Coin coin = new Coin(100);

		myVending.insertCoin(coin);
		myVending.insertCoin(coin);
		myVending.insertCoin(coin);

		int[] popCans = new int[6];
		for (int i = 0; i < 6; i++) {
			popCans[i] = 10;
		}

		myVending.getVending().loadPopCans(popCans);
		myVending.pushButton(1);
		System.out.println(myVending.getTotal());

	}

	/**
	 * Test method for {@link ca.ucalgary.seng300.a1.Controller#getTotal()}.
	 * Determine if the total is returned as expected
	 */
	@Test
	public void testGetTotal() {
		Coin coin = new Coin(100);

		myVending.insertCoin(coin);
		myVending.insertCoin(coin);
		myVending.insertCoin(coin);
		myVending.insertCoin(coin);
		myVending.insertCoin(coin);

		assertEquals(myVending.getTotal(), 500);
	}

	/**
	 * Test method to determine if the total is incremented properly when coins are inserted
	 * {@link ca.ucalgary.seng300.a1.Controller#incrementTotal(int)}.
	 */
	@Test
	public void testIncrementTotal() {
		fail("Not yet implemented");
	}

	/**
	 * Test method to determine if a valid coin insert will actually update the total
	 * {@link ca.ucalgary.seng300.a1.Controller#insertCoin(org.lsmr.vending.Coin)}.
	 */
	@Test
	public void testValidInsertCoin() {
		coin = new Coin(5);
		myVending.insertCoin(coin);
		assertEquals(5, myVending.getTotal());
	}

	/**
	 * Test method to determine if invalid button presses do anything
	 */
	@Test
	public void testInvalidButton() {

		try {
			myVending.pushButton(17);
		} catch (Exception e) {
			// System.out.println(e);
			assertEquals(e.toString(), "Nested exception: Invalid button pressed");
		}
	}

	/**
	 * Test method to determine if the program will allow negative values in the machine
	 */
	@Test
	public void testNegativeTotal() {

		try {
			myVending.decrementTotal(5);
			// myVending.getTotal();
		} catch (Exception e) {
			// System.out.println(e);
			assertEquals(e.toString(), "Nested exception: Decrement cannot result in total being a negative value");
		}
	}

	/**
	 * Test method to determine whether an empty pop rack will still dispense pop cans
	 */
	@Test
	public void testEmptyPopRack() {

		Coin coin = new Coin(100);

		myVending.insertCoin(coin);
		myVending.insertCoin(coin);
		myVending.insertCoin(coin);

		try {
			myVending.pushButton(1);
			// myVending.getTotal();
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			assertEquals(e.toString(), "Nested exception: Pop rack empty");
		}
	}

	/**
	 * Test method to determine whether a disabled pop rack will still dispense pop
	 */
	@Test
	public void testDisabledPopRack() {

		Coin coin = new Coin(100);

		myVending.insertCoin(coin);
		myVending.insertCoin(coin);
		myVending.insertCoin(coin);

		try {
			myVending.getVending().getPopCanRack(1).disable();
			myVending.pushButton(1);
			// myVending.getTotal();
		} catch (Exception e) {
			// System.out.println(e.getLocalizedMessage());
			assertEquals(e.toString(), "Nested exception: Pop rack is disabled");
		}
	}

	/**
	 * Test method to determine if a pop rack can be filled over capacity
	 */
	@Test
	public void testCapacityPopRack() {

		Coin coin = new Coin(200);

		myVending.insertCoin(coin);
		myVending.insertCoin(coin);
		myVending.insertCoin(coin);

		int[] popCans = new int[6];
		for (int i = 0; i < 6; i++) {
			popCans[i] = 10;
		}

		System.out.println(myVending.getVending().getPopCanRack(1).getCapacity());
		DeliveryChute zeroCapacity = new DeliveryChute(1);
		PopCanChannel myChannel = new PopCanChannel(zeroCapacity);

		myVending.getVending().getPopCanRack(2).connect(myChannel);
		myVending.getVending().loadPopCans(popCans);

		try {

			myVending.pushButton(2);
			myVending.pushButton(2);
			// myVending.getTotal();
		} catch (Exception e) {
			// System.out.println(e.toString());
			assertEquals(e.toString(), "Nested exception: Capacity exceeded");
		}
	}

	/**
	 * Test method to see if an invalid coin will be accepted
	 * {@link ca.ucalgary.seng300.a1.Controller#insertCoin(org.lsmr.vending.Coin)}.
	 */
	@Test
	public void testInvalidInsertCoin() {
		coin = new Coin(3);
		myVending.insertCoin(coin);
		assertEquals(0, myVending.getTotal());
	}

	/**
	 * Tests for an exception when the coin slot is disabled.
	 */
	@Test(expected = DisabledException.class)
	public void coinSlotDisabled() {
		Coin coin = new Coin(10);
		myVending.getVending().enableSafety();
		myVending.insertCoin(coin);
	}

	/**
	 * Tests if the enabled method of the listener is called
	 */
	@Test
	public void coinSlotDisabledListener() {
		myVending.getVending().disableSafety();
		assertEquals(true, myVending.coinSlotEnabled());
	}

	/**
	 * Tests if the disabled method of the listener is called
	 */
	@Test
	public void coinSlotEnabledListener() {
		myVending.getVending().enableSafety();
		assertEquals(false, myVending.coinSlotEnabled());
	}

	/**
	 * Tests if the disabled method of the listener is called
	 */
	@Test
	public void buttonEnabledListener() {
		myVending.getVending().getSelectionButton(0).disable();
		assertEquals(false, myVending.buttonEnabled());
	}

	/**
	 * Tests if the disabled method of the listener is called
	 */
	@Test
	public void buttonDisabledListener() {
		myVending.getVending().getSelectionButton(0).enable();
		assertEquals(true, myVending.buttonEnabled());
	}

}
