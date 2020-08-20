


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 //Array to work with the event calendar and the user queues
package ecobici;
/**
 *
 * @author 
 */
public class ArrayQueue <T> implements QueueADT<T>{
    private final int DEFAULT_CAPACITY=100000;
    private T[] cola;
    private int ultimo;
    private int primero;
    
    public ArrayQueue(){
        cola=(T[])(new Object[DEFAULT_CAPACITY]);
        primero=0;
        ultimo=0;
    }
    public ArrayQueue(int cap){
        cola=(T[])(new Object[cap]);
        primero=0;
        ultimo=0;
    }
    public void enqueue(T dato){
        if(primero!=ultimo){
            cola[ultimo]=dato;
            ultimo++;
            if(ultimo==cola.length)
                ultimo=0;
        }
        else if(primero==ultimo&&cola[0]==null){
            primero=0;
            ultimo=1;
            cola[0]=dato;
        }
        else{
            expandCapacity();
            enqueue(dato);
        }
    }
    private void expandCapacity(){
        ArrayQueue<T> nuevo=new ArrayQueue(cola.length*2);
        int i;
        for(i=0;i<cola.length;i++){
            nuevo.enqueue(dequeue());
        }
        cola=nuevo.cola;
    }
    public T dequeue(){
        T res;
        if(!isEmpty()){
            res=cola[primero];
            cola[primero]=null;
            primero++;
            if(primero==cola.length)
                primero=0;
        }
        else
            throw new EmptyCollectionException();
        return res;
    }
    public T first(){
        if(!isEmpty())
            return cola[primero];
        else
            throw new EmptyCollectionException();
    }
    public boolean isEmpty(){
        return primero==ultimo&&cola[0]==null;
    }
}
