package ca.ucalgary.seng300.a1;

import org.lsmr.vending.hardware.*;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.*;
import org.lsmr.vending.*;

/**
 * @author Vending Solutions Incorporated
 * Developed by: Nguyen Viktor(10131322), Michaela Olšáková(30002591), Roman Sklyar(10131059)
 * 
 *
 */

public class Controller {

		/**
		 * Set-up machine variables
		 */
	
	private int total;
	private static boolean validCoin;
	private VendingMachine client;
	private MySlotListener slotListener;
	private MyButtonListener buttonListener;
	private MyPopRackListener popRackListener;
	private MyDeliveryChuteListener chuteListener;
	private static boolean coinSlotEnabled;
	private static boolean buttonEnabled;
	
	//Controller constructor to hook up all parts in the vending machine together
	public Controller(){
		total = 0;
		validCoin = false;
		coinSlotEnabled = true;
		
		int[] CAD = { 5, 10, 25, 100, 200 };

		//According to Clients specifications:
		//Canadian Currency, 6 types of pop, capacity of coinRack=15, 10 pops per rack, 200 coins in receptacle
		client = new VendingMachine(CAD, 6, 15, 10, 200);

		slotListener = new MySlotListener();
		client.getCoinSlot().register(slotListener);

		buttonListener = new MyButtonListener();
		for (int i = 0; i < client.getNumberOfSelectionButtons(); i++) {
			client.getSelectionButton(i).register(buttonListener);
		}

		popRackListener = new MyPopRackListener();
		for (int i = 0; i < client.getNumberOfPopCanRacks(); i++) {
			client.getPopCanRack(i).register(popRackListener);
			
		}

		chuteListener = new MyDeliveryChuteListener();
		client.getDeliveryChute().register(chuteListener);
		
		
		// set up the pop cans and prices and add them to the rack
		List<String> popCanNames = new ArrayList<String>();
		List<Integer> popCanCosts = new ArrayList<Integer>();
		
		for (int i = 0; i<6; i++) {
			popCanNames.add(Integer.toString(i));
			popCanCosts.add(250);

		}

		client.configure(popCanNames, popCanCosts);
		
	}
	
	
	/**
	 * Add to the total when a coin has been inserted
	 * @param coin
	 */
	public int getTotal(){
		return total;
	}
	
	/**
	 * Getter for the VM instance
	 * @param coin
	 */
	public VendingMachine getVending(){
		return client;
	}
	/**
	 * Add to the total when a coin has been inserted
	 * @param coin
	 */
	private void incrementTotal(int coin){
		total += coin;
	}
	
	/**
	 * Used primarily for testing purposes of the listener
	 * @return whether or not the coin slot is enabled
	 */
	public boolean coinSlotEnabled(){
		return coinSlotEnabled;
	}
	
	
	/**
	 * Used primarily for testing purposes of the listener
	 * @return whether or not the button is enabled
	 */
	public boolean buttonEnabled(){
		return buttonEnabled;
	}
	
	/**
	 * Decrement the total when a pop has been purchased
	 * @param price
	 * @throws SimulationException
     *  If total becomes negative.
	 */
	public void decrementTotal(int price) throws SimulationException{
		if (total>price) {
		total -= price;}
		else throw new SimulationException("Decrement cannot result in total being a negative value");

		
	}
	
	/**
	 * User has entered a coin. Insert it into the hardware and listen to whether its valid. Update total accordingly.
	 * @param coin
	 */
	public void insertCoin(Coin coin){
		CoinSlot slot = client.getCoinSlot();
		try {
			slot.addCoin(coin);
		} catch (DisabledException e) {
			System.out.println("Coin slot is disabled");
		}
		if (validCoin == true){
			incrementTotal(coin.getValue());
			validCoin = false;
		}
	}
	
	/**
	 * The logic of button presses. Ensures there is enough money in the machine before dispensing pop
	 * Updates the total accordingly. Does nothing if the pop can rack is empty.
	 * @param button
	 * @throws SimulationException
	 */
	public void pushButton(Integer button) throws SimulationException{
		
		if(button >=client.getNumberOfSelectionButtons()) {
			throw new SimulationException("Invalid button pressed");
		}
		else {
			
			if (getTotal() >= client.getPopKindCost(button)){
		  		
		  		client.getSelectionButton(button).press();
		  		try {
					client.getPopCanRack(button).dispensePopCan();
					decrementTotal(client.getPopKindCost(button));
				} catch (DisabledException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Pop rack is disabled");
				} catch (EmptyException e) {
					throw new SimulationException("Pop rack empty");
					// TODO Auto-generated catch block
				
				} catch (CapacityExceededException e) {
					// TODO Auto-generated catch block
					throw new SimulationException("Capacity exceeded");
				}
		  	}
			else{
				//Currently do nothing
			}
		}
		
		
	}

	/**
	 * A class for the CoinSlotListener to get events from the machine
	 * 
	 */
	private static class MySlotListener implements CoinSlotListener {

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("slot enabled");
			coinSlotEnabled = true;
		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("slot disabled");
			coinSlotEnabled = false;
		}

		@Override
		public void validCoinInserted(CoinSlot slot, Coin coin) {
			System.out.println("Valid coin inserted");
			validCoin = true;
		}

		@Override
		public void coinRejected(CoinSlot slot, Coin coin) {
			System.out.println("Coin rejected");
			validCoin = false;
		}

	}

	/**
	 * A class for the ButtonListener to get events from the machine
	 * 
	 */
	private static class MyButtonListener implements SelectionButtonListener {

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("button enabled.");
			buttonEnabled = true;
		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("button disabled");
			buttonEnabled = false;
		}

		@Override
		public void pressed(SelectionButton button) {
			System.out.println("pressed");
		}
	}

	/**
	 * A class for the PopRackListener to get events from the machine
	 * 
	 */
	private static class MyPopRackListener implements PopCanRackListener {

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Pop Rack enabled");

		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Pop Rack Disabled");

		}

		@Override
		public void popCanAdded(PopCanRack popCanRack, PopCan popCan) {
			System.out.println("Pop Can Added");

		}

		@Override
		public void popCanRemoved(PopCanRack popCanRack, PopCan popCan) {
			System.out.println("Pop can removed");

		}

		@Override
		public void popCansFull(PopCanRack popCanRack) {
			// DO NOTHING
		}

		@Override
		public void popCansEmpty(PopCanRack popCanRack) {
			System.out.println("This rack is empty");

		}

		@Override
		public void popCansLoaded(PopCanRack rack, PopCan... popCans) {
			// DO NOTHING

		}

		@Override
		public void popCansUnloaded(PopCanRack rack, PopCan... popCans) {
			// DO NOTHING

		}

	}

	/**
	 * A class for the DeliveryChuteListener to get events from the machine
	 */
	private static class MyDeliveryChuteListener implements DeliveryChuteListener {

		@Override
		public void enabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Chute Enabled");

		}

		@Override
		public void disabled(AbstractHardware<? extends AbstractHardwareListener> hardware) {
			System.out.println("Chute Disabled");

		}

		@Override
		public void itemDelivered(DeliveryChute chute) {
			System.out.println("Item Delivered");
			

		}

		@Override
		public void doorOpened(DeliveryChute chute) {
			// DO NOTHING

		}

		@Override
		public void doorClosed(DeliveryChute chute) {
			// DO NOTHING

		}

		@Override
		public void chuteFull(DeliveryChute chute) {
			System.out.println("Chute Full");

		}

	}

}
