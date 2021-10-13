package storesimulation;
 
import java.util.ArrayList;
 
/**
 *
 * @author Kollen Gruizenga & Travis Wahl
 * 
 * class to create a queue of people in line for a specific register
 * queue holds objects data type Customer
 */
 
class Register {
    private double itemScanTime, payTime;
    private int totalPassedThru, maxL;
    private String regType;
   
   
    private ArrayList<Customer> que;//each registers' length
 
    Register(double d, double d0, String t){ //D=min/item D0=min to pay
        this.itemScanTime = d;
        this.payTime = d0;
        maxL = 0;
        totalPassedThru = 0;
        que = new ArrayList<Customer>();//given queue for any one register
        this.regType = t;
    }
   
    String getRegisterType(){
        return this.regType;
    }
 
    int getLineLength() {
        return this.que.size();
    }
 
    void enqueue(Customer customer) {
        this.que.add(customer);
        this.totalPassedThru++;//keep track of how many passed thru each register
        checkNewMaxL();
    }
 
    Customer dequeue() {//pop off the next up in line
        return this.que.remove(0);
    }
 
    boolean isEmpty() {
        return this.que.isEmpty();
    }
 
    Customer peek() {//*view* the next up in line
        return this.que.get(0);
    }
   
    void checkNewMaxL(){
        if (this.que.size() > this.maxL) this.maxL = this.que.size();
    }
   
    double getScanTime() {
        return this.itemScanTime;
    }
 
    double getPayTime() {
        return this.payTime;
    }
   
    int getMaxLength(){
        return this.maxL;
    }
   
    int getTotalPassThru(){
        return this.totalPassedThru;
    }
}