package ca.ucalgary.seng300.a2.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.vending.Coin;
import org.lsmr.vending.hardware.DeliveryChute;
import org.lsmr.vending.hardware.DisabledException;
import org.lsmr.vending.hardware.PopCanChannel;

import ca.ucalgary.seng300.a2.Controller;

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
		myVending.cleanUpTimers();
	}

	//** MATTHEW, Teardown requires to clean up the timers, the 2 test methods make sure the display is working properly
	//comment them out when doing your own testing because they take 12 seconds to finish
	/**
	 * Test method for {@link ca.ucalgary.seng300.a1.Controller#Controller()}.
	 * @throws InterruptedException 
	 */
	@Test
	public void passiveDisply() throws InterruptedException{
		Thread.sleep(3*1000);//3 seconds into the simulation, no coins entered
		assertEquals(myVending.messageBeingDisplayed, "Hi There!");
		Thread.sleep(3*1000); //total of 6 seconds in, no longer displaying hi
		assertEquals(myVending.messageBeingDisplayed, "");
	}
	//important - when the display is made to display the coin amount when some coin is inserted
	//this test case will have to change to reflect that
	@Test
	public void passiveDisplyCoinInsterted() throws InterruptedException{
		Coin coin = new Coin(100);
		myVending.insertCoin(coin);
		Thread.sleep(3*1000);//credit is non 0 so null should still be displayed
		assertEquals(myVending.messageBeingDisplayed, null);
		Thread.sleep(3*1000); 
		assertEquals(myVending.messageBeingDisplayed, null);
	}
	//**END, MATTHEW
	
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
	
	//**PRESTON, the 8 test methods check to see if checkChange works properly. Had 5 cases to check if each
	//  individual coin check is working properly
	/**
	 * Tests a standard case where every value is used to check for sufficient change
	 */  
	@Test
	public void testEnoughChange() {
		int[] coins = {15,15,15,15,15};
		myVending.getVending().loadCoins(coins);
		myVending.setTotal(750);
	   	assertEquals(true,myVending.checkChange(myVending.getTotal()));
	}

	/**
	 * Tests to see if nickel check is working properly
	 */
	@Test
	public void testNickelOnly() {
	   	int[] coins = {15,0,0,0,0};
	   	myVending.getVending().loadCoins(coins);
	   	myVending.setTotal(35);
	   	assertEquals(true,myVending.checkChange(myVending.getTotal()));
	}

	/**
	 * Tests to see if dime check is working properly
	 */
	@Test
	public void testDimeOnly() {
	   	int[] coins = {0,15,0,0,0};
	   	myVending.getVending().loadCoins(coins);
	   	myVending.setTotal(50);
	   	assertEquals(true,myVending.checkChange(myVending.getTotal()));
	}

	/**
	 * Tests to see if quarter check is working properly
	 */
	@Test
	public void testQuarterOnly() {
	int[] coins = {0,0,15,0,0};
	    myVending.getVending().loadCoins(coins);
	    myVending.setTotal(250);
	    assertEquals(true,myVending.checkChange(myVending.getTotal()));
	}

	/**
	 * Tests to see if loonie check is working properly
	 */
	@Test
	public void testLoonieOnly() {
		int[] coins = {0,0,0,15,0};
		myVending.getVending().loadCoins(coins);
		myVending.setTotal(700);
		assertEquals(true,myVending.checkChange(myVending.getTotal()));
	}

	/**
	 * Tests to see if toonie check is working properly
	 */
	@Test
	public void testToonieOnly() {
		int[] coins = {0,0,0,0,15};
		myVending.getVending().loadCoins(coins);
		myVending.setTotal(800);
		assertEquals(true,myVending.checkChange(myVending.getTotal()));
	}

	/**
	 * Tests for insufficient change
	 */
	@Test
	public void testInsufficientChange() {
		int[] coins = {0,2,1,1,0};
		myVending.getVending().loadCoins(coins);
		myVending.setTotal(150);
		assertEquals(false,myVending.checkChange(myVending.getTotal()));
	}

	/**
	 * Tests when there is just enough change in the machine
	 */
	@Test
	public void testJustEnoughChange() {
		int[] coins = {1,2,1,1,0};
		myVending.getVending().loadCoins(coins);
		myVending.setTotal(150);
		assertEquals(true,myVending.checkChange(myVending.getTotal()));
	}
	// END

}
