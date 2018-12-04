/**
 * The max-heap priority queue
 * Name: 
 */

public class PriorityQ
{
    private int size;  
    
    private String[] data;
    private int[] key;
    
    /**
     * constructs an empty priority queue.
     */
    public PriorityQ()
    {
        size = 0;
        data = new String[10];
        key = new int[10];
    }
    
    /**
     * Adds a string s with priority p to the priority queue.
     * @param s the string
     * @param p priority value
     */
    public void add(String s, int p)
    {
        if (size == data.length)
        {
            String[] tmp = new String[size * 2];
            int[] tmpk = new int[size * 2];
            for (int i = 0; i < size; i++)
            {
                tmp[i] = data[i];
                tmpk[i] = key[i];
            }
            data = tmp;
            key = tmpk;            
        }
        data[size] = s;
        key[size] = p;
        size++;
        moveUp(size - 1);
    }
    
    /**
     * Swap two items and priorities
     * @param i  index
     * @param j  index
     */
    private void swap(int i, int j)
    {
        String tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
        
        int tmpk = key[i];
        key[i] = key[j];
        key[j] = tmpk;
    }
    
    /**
     * Move item up
     * @param i index
     */
    private void moveUp(int i)
    {
        while (i > 0)
        {
            int parent = (i-1) / 2;
            
            if (key[parent] >= key[i])
                break;
            
            swap(i, parent);

            i = parent;            
        }
    }
    
    /**
     * Move item down
     * @param i index
     */
    private void moveDown(int i)
    {
        int child = i*2+1;
        while (child < size)
        {
            // find largest child
            if (child + 1 < size && key[child+1] > key[child])
                child = child + 1;
            
            if (key[i] >= key[child])
                break;
            
            swap(i, child);
            
            i = child;
            child = i * 2 + 1;
        }
    }
    
    /**
     * returns a string whose priority is maximum.
     * @return the string
     */
    public String returnMax()
    {
        return data[0];
    }
    
    /**
     * returns a string whose priority is maximum and removes it from 
     * the priority queue.
     * @return the string
     */
    public String extractMax()
    {
        String v = data[0];
        remove(0);
        return v;
    }
    
    /**
     * removes the element from the priority queue whose array index is i.
     * @param i index
     */
    public void remove(int i)
    {
        data[i] = data[size-1];
        key[i] = key[size-1];
        size--;
        moveDown(i);
    }
    
    /**
     * Decrements the priority of the ith element by k.
     * @param i index
     * @param k decrement value
     */
    public void decrementPriority(int i, int k)
    {
        key[i] -= k;
        if (k > 0)
            moveDown(i);
        else
            moveUp(i);
    }
    
    /**
     * Return the priority array
     * @return int array
     */
    public int[] priorityArray()
    {
        int[] pr = new int[size];
        for (int i = 0; i < size; i++)
            pr[i] = key[i];
        return pr;
    }
    
    /**
     * Returns key(A[i]), where A is the array used to represent 
     *  the priority queue
     * @param i index
     * @return priority
     */
    public int getKey(int i)
    {
        return key[i];
    }
    
    /**
     * Returns value(A[i]), where A is the array used to represent the 
     * priority queue
     * @param i index
     * @return string value
     */
    public String getValue(int i)
    {
        return data[i];
    }
    
    /**
     * Return true if and only if the queue is empty.
     * @return true/false
     */
    public boolean isEmpty()
    {
        return size == 0;
    }
}
