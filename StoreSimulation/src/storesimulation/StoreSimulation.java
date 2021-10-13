package storesimulation;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
 
/**
 *
 * CS220 
 * @author Kollen Gruizenga and Travis Wahl
 * 
 * main class to run program, reads in data from arrival.txt data file, specifies number of Standard/Self
 * checkouts, creates checkout Register obj, populating Register queues with Customers, 
 * create Heap of Events upon customers' Arrival/EndShopping/EndCheckout and tracking times
 */
public class StoreSimulation {
 
    private static final int NUMBER_STANDARD_CHECKOUT = 4; // number of cashier registers
    private static final int NUMBER_SELF_CHECKOUT = 2; // number of self-scan registers
    private static double simClock = 0; // elapsed time (minutes)
    private static MyHeap events = new MyHeap(); // events that occur in the store
    private static ArrayList<Register> registers = new ArrayList(); // registers used in the store
    private static ArrayList<Customer> customerArray = new ArrayList(); // all the customers that pass thru the store
 
    public static void main(String[] args) {
       
        loadRegisters();
        loadCustomerData();
 
        // Events are stored in a priority queue, so they will always be returned in order.
        while (events.getSize() > 0) {
            Event e = events.remove();
            simClock = e.getEventTime(); // Always set the clock to the time of the new event.
            if (e.getEventType() == EventType.ARRIVAL) {
                handleArrival(e);
            } else if (e.getEventType() == EventType.END_SHOPPING) {
                handleEndShopping(e);
            } else {
                handleEndCheckout(e);
            }
        }
        printCollectedStatistics();
 
    }
   
 
    private static void loadRegisters() {
        for (int i = 0; i < NUMBER_STANDARD_CHECKOUT; i++) {
            Register r = new Register(0.01, 1.5, "STANDARD");
            registers.add(r);
        }
        for (int i = 0; i < NUMBER_SELF_CHECKOUT; i++) {
            Register r = new Register(0.04, 3.0, "SELF");
            registers.add(r);
        }
    }
 
    private static void loadCustomerData() {
        double arriveTime, avgSelectionTime;
        int items;
 
        try {
            File myFile = new File("arrival.txt");
            Scanner inputFile = new Scanner(myFile);
            while (inputFile.hasNext()) {
                arriveTime = inputFile.nextDouble();
                items = inputFile.nextInt();
                avgSelectionTime = inputFile.nextDouble();
                Customer customer = new Customer(arriveTime, items, avgSelectionTime);
                Event event = new Event(customer, arriveTime, EventType.ARRIVAL);
                events.insert(event);
            }//end while
            inputFile.close();
        } catch (FileNotFoundException e) {
            System.err.println("File was not found");
            System.exit(0);
        }
    }
 
    private static void handleArrival(Event e) {
        Customer c = e.getCustomer();
        double endShoppingTime = c.getArriveTime() + c.getNumItems() * c.getAvgSelectionTime();
        Event endShopping = new Event(c, endShoppingTime, EventType.END_SHOPPING);
        events.insert(endShopping);
    }
 
    private static void handleEndShopping(Event e) {
        Customer customer = e.getCustomer();
        int shortest = getShortestLine(customer);
       
        customer.setRegType(registers.get(shortest).getRegisterType());//store the type of register being added to line for
        customer.setCheckoutLine(shortest); // Customer will always enter shortest checkout line.
       
        registers.get(shortest).enqueue(customer); // Even if line is empty, customer must be enqueued and dequeued so that the customer gets included in the stats for the register
        customer.setStartWaitTime(simClock);//set the start waiting time
       
        if (registers.get(shortest).getLineLength() == 1) { // If new customer is the only one in line, begin checkout.
            startCheckout(customer);
        }
    }
 
    private static void handleEndCheckout(Event e) {
        int line = e.getCustomer().getCheckoutLine();
        Customer c = registers.get(line).dequeue();
        customerArray.add(c);
        if (registers.get(line).isEmpty()) {
            return;
        } else {
            Customer customer = registers.get(line).peek();
            startCheckout(customer);
        }
    }
 
    private static void startCheckout(Customer customer) {
        int line = customer.getCheckoutLine();
        customer.setWaitTime(simClock - customer.getStartWaitTime());//calculate & save the waiting time
 
        double checkoutLength = customer.getNumItems() * registers.get(line).getScanTime() + registers.get(line).getPayTime();
        Event endCheckout = new Event(customer, checkoutLength + simClock, EventType.END_CHECKOUT);
        events.insert(endCheckout);  
    }
 
    private static void printCollectedStatistics() {
        DecimalFormat df = new DecimalFormat("##.##");
        
        //beginning format
        System.out.println("= ----- Statistics Analysis ----- =");
        System.out.println("Utilizing:"
                + "\n" + NUMBER_STANDARD_CHECKOUT + " Standard-Checkout Registers (Registers #1-" + (NUMBER_STANDARD_CHECKOUT) + ")"
                + "\n" + NUMBER_SELF_CHECKOUT + " Self-Checkout Registers (Registers #" + (NUMBER_STANDARD_CHECKOUT+1) + "-" + (registers.size()) + ")\n");
       
        
        //Average Wait Time Per Customer For The Store
        System.out.println("> Store Wait Times ");
        double totalWaitTime = 0;
        for (int i = 0; i < customerArray.size(); i++) {
            totalWaitTime += customerArray.get(i).getWaitTime();
        }
        System.out.println("Average Waiting Time for Customers in the Store: " + df.format(totalWaitTime/customerArray.size()) + " minutes.\n");
 
       
        //Average Waiting Time per Register Style
        System.out.println("> Register Wait Times ");
        double standardWaitTime = 0, selfWaitTime = 0;
        int counterStandard = 0, counterSelf = 0;
         for (int i = 0; i < customerArray.size(); i++) {
            if (customerArray.get(i).getRegType() == "STANDARD"){
                standardWaitTime += customerArray.get(i).getWaitTime();
                counterStandard++;
            }else{
                selfWaitTime += customerArray.get(i).getWaitTime();
                counterSelf++;                
            }
        }
        System.out.println("Average Standard-Checkout Register Wait Time: " + df.format(standardWaitTime/counterStandard) + " minutes.");
        System.out.println("Average Self-Checkout Register Wait Time: " + df.format(selfWaitTime/counterSelf) + " minutes.\n");
 
        
        //Total Customers Passing Through Each Individual Register Line
        System.out.println("> Register Customers Processed ");
        for (int i = 0; i < registers.size(); i++){
          System.out.println("Register " + (i+1) + " Total Customers Processed: " + registers.get(i).getTotalPassThru());  
        }
        System.out.println("");
       
        
        //Maximum Length of Each Register Line
        System.out.println("> Register Max Line Lengths");
        for (int i = 0; i < registers.size(); i++) {
            System.out.println("Register " + (i+1) + ": Maximum Length of Line: " + registers.get(i).getMaxLength());
        }
        System.out.println("");
       
        
        //Percentage of Customers who waited for more than 2, 3, 5, and 10 minutes
        System.out.println("> Customer Wait Time Breakdown (%) ");
        double waitTime, counter2m = 0, counter3m = 0, counter5m = 0, counter10m = 0;
        int c=0;
        for (int i = 0; i < customerArray.size(); i++) {
            waitTime = customerArray.get(i).getWaitTime();
            if (waitTime >= 2) c=2;
            if (waitTime >= 3) c=3;
            if (waitTime >= 5) c=5;
            if (waitTime >= 10) c=10;
            
            switch(c){
                case 2:
                    counter2m++;
                    break;
                case 3:
                    counter3m++;
                    break;
                case 5:
                    counter5m++;
                    break;
                case 10:
                    counter10m++;
                    break;
            }
        }
        System.out.println("% Customers Waited 2 min+ : " + df.format((counter2m/customerArray.size())*100));
        System.out.println("% Customers Waited 3 min+ : " + df.format((counter3m/customerArray.size())*100));
        System.out.println("% Customers Waited 5 min+ : " + df.format((counter5m/customerArray.size())*100));
        System.out.println("% Customers Waited 10 min+ : " + df.format((counter10m/customerArray.size())*100)+"\n");
        
        //end format
        System.out.println("= ----- End Statistical Analysis ----- =");
        
    }
    
 
   
    private static int getShortestLine(Customer c) { // initialize min to the length of the first register object within the arrayList of registers
        int currLeng, min = registers.get(0).getLineLength(), minIndex = 0;
 
        int endPoint = registers.size();
        if (c.getNumItems() > 50) {
            endPoint = NUMBER_STANDARD_CHECKOUT;
        }
 
        for (int r = 0; r < endPoint; r++) {
            currLeng = registers.get(r).getLineLength();
            if (currLeng < min) {
                min = currLeng;
                minIndex = r;
 
            }
        }
        return minIndex;
    }
 
}