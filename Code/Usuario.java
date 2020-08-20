/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

/**
 *
 * @author 
 */
public class Usuario {
    final int id;
    private final double Ti; // time that started waiting
    double Tf; // time that finished waiting
    final int tipo; // if user waits to take or return a bike, or both
    final int est; //  waiting station
    
    public Usuario(int n, double ti, int t, int e){
        id = n;
        Ti = ti;
        Tf = 0;
        tipo=t; // 1= waits to take a bike, 2= waits to return a bike
        est=e;
    }
    
    public Usuario(int n, double ti, int t, int e, int b){
        if (b==5){
            Tf=ti;
        }
        id = n;
        Ti = ti;
        tipo=t; // 1= waits to take a bike, 2= waits to return a bike
        est=e;
    }
    
    public double tiempo(){
        double t=Tf-Ti;
        return t;
    }
            
    public String toString(){
        String st;
        double t=tiempo();
        if(tipo==1)
            if(Ti<=Tf)
                st="usuario "+id+" esperó tomar bici por "+t+
                    " minutos en la estación "+est;
            else
                st="usuario "+id+" sigue esperando tomar bici "
                        + "en la estación "+est;
        else if(tipo==2)
            if(Ti<=Tf)
                st="usuario "+id+" esperó dejar bici por "+t+
                    " minutos en la estación "+est;
            else
                st="usuario "+id+" sigue esperando dejar bici "
                        + "en la estación "+est;
        else
            if(Ti<=Tf)
                st="usuario "+id+" esperó tomar bici y "+t+" minutos para"
                        + " dejarla en la estación "+est;
            else
                st="usuario "+id+" esperó tomar bici por y sigue esperando "
                        + "dejar bici en la estación "+est;
        return st;
    }
}
