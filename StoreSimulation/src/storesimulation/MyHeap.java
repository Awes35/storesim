package storesimulation;
 
/**
 *
 * @author Kollen Gruizenga & Travis Wahl
 * 
 * class to create a heap of Events of generic type
 */
class MyHeap<T> {
   
    private Event[] Heap;
    private int size;
   
    private static final int FRONT = 0;
   
    public MyHeap(){
        this.size = 0;
        Heap = new Event[10000];
    }
   
    int getSize() {
        return size;
    }
   
    //returns the position of the PARENT for the event
    private int getParent(int pos){
        return pos / 2;
    }
   
    //returns the position of the LEFT child for the event
    private int getLeftChild(int pos){
        return (2 * pos);
    }
   
    //returns the position of the RIGHT child for the event
    private int getRightChild(int pos){
        return (2 * pos) + 1;
    }
   
    //returns whether event is a leaf
    private boolean isLeaf(int pos){
        if (pos >= (size / 2) && pos <= size) {
            return true;
        }
        return false;
    }
   
    //function to swap two events
    private void swap(int fpos, int spos){
        Event tmp;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }
   
    //function to heapify the event
    private void minHeapify(int pos){
   
        // If the event is not a leaf and greater than any of its children
        if (!isLeaf(pos)){
            if (Heap[pos].getEventTime() > Heap[getLeftChild(pos)].getEventTime() ||
                    Heap[pos].getEventTime() > Heap[getRightChild(pos)].getEventTime()){
   
                // Swap with the LEFT child
                if (Heap[getLeftChild(pos)].getEventTime() < Heap[getRightChild(pos)].getEventTime() ) {
                    swap(pos, getLeftChild(pos));
                    minHeapify(getLeftChild(pos));
                }
   
                // Swap with the RIGHT child
                else {
                    swap(pos, getRightChild(pos));
                    minHeapify(getRightChild(pos));
                }
            }
        }
    }
   
    //start from first open leaf, then compare/swap with parents
    public void insert(Event element){
        if (size == 0){
            size++;
            Heap[0] = element;
            return;
        }
       
        Heap[size++] = element;
        int current = size-1;
       
        while (current > 0 && Heap[current].compareTo(Heap[getParent(current)]) < 0) {
            swap(current, getParent(current));
            current = getParent(current);
        }
    }
   
    // find the min (top) and remove
    public Event remove(){
        Event popped = Heap[FRONT];
        Heap[FRONT] = Heap[--size];
        minHeapify(FRONT);
        return popped;
    }
 
   
}