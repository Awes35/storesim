package storesimulation;
 
/**
 *
 * @author Kollen Gruizenga && Travis Wahl
 * 
 * class to create object type Event, for tracking Event times of varying
 * types for given Customer within store (times for data collection)
 */
 
enum EventType {ARRIVAL, END_SHOPPING, END_CHECKOUT}
 
class Event implements Comparable{
 
    private Customer customer;
    private double eventTime, startWaitTime, waitTime;
    private String registerType;
    private EventType eventType;
 
    Event(Customer customer, double eventTime, EventType eventType){
        setCustomer(customer);
        setEventTime(eventTime);
        setEventType(eventType);
    }
   
    /**
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }
 
    /**
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
 
    /**
     * @return the eventTime
     */
    public double getEventTime() {
        return eventTime;
    }
 
    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTime(double eventTime) {
        this.eventTime = eventTime;
    }
 
    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }
 
    /**
     * @param eventType the eventType to set
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
   
    public void setStartWaitTime(double i){
        this.startWaitTime = i;
    }
   
    public void setWaitTime(double i){
        this.waitTime = i;
    }
   
    public void setRegType(String r){
        this.registerType = r;
    }
   
    public double getStartWaitTime(){
        return this.startWaitTime;
    }
   
    public double getWaitTime(){
        return this.waitTime;
    }
   
    public String getRegType(){
        return this.registerType;
    }
 
    @Override
    public int compareTo(Object event) {
        if( !(event instanceof Event)){
            throw new UnsupportedOperationException("Object should be an Event");
        }
        Event otherEvent = (Event) event;
        Double time = Double.valueOf(this.eventTime);
       
        return time.compareTo(otherEvent.eventTime);
        // < 0 if time < eventTime     (obj compareTo secondObj)
        // > 0 if time > eventTime
    }
 
   
}