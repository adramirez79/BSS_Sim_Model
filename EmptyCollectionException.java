/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;
/**
 *
 * @author
 */
public class EmptyCollectionException extends RuntimeException{
    public EmptyCollectionException(){
        super("Cola vacía");
    }
}
