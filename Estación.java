/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.sqrt;

/**
 *
 * @author
 */
public class Estación{

    final double Capacidad; //total number of docks
    final int Id;
    int EspDisp; // available docks
    double Bicis; // available bikes
    double FirstArr;
    final double[] tasas; //user arrival rate per minute
    double ut,ud,uet,ued,uetyd; // users that take and return bikes
    double espt,espd; //users that waited to take and return bikes
    double tet,ted; // waiting time to take and return bikes
    double tLlen,tVac; //time that a station was full/empty
    double fuet, fued; //fraction of users that waited
    double tpet,tped; //avg user waiting time to take and return
    double ti,tf; //auxiliary
    int w;
    FileWriter writ;
    
    public Estación(int id, int c, int b, double f, double[] tiempo){
        Capacidad= c;
        Id=id;
        EspDisp= c-b;
        Bicis = b;
        tasas=tiempo;
        FirstArr=f;
        ut=0;
        ud=0;
        uet=0;
        ued=0;
        uetyd=0;
        espt=0;espd=0;
        tet=0;
        ted=0;
        fuet=0; fued=0;tpet=0;tped=0;
        ti=0;
        w=0;
        writ=null;
    }

    public void recalculaVacía(){
        double aux = tf-ti;
        tVac=tVac+aux;
    }
    public void recalculaLlena(){
        double aux = tf-ti;
        tLlen=tLlen+aux;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Estación: ").append(Id).append("\n");
        sb.append("Bicicletas: ").append(Bicis).append("\n");
        sb.append("Espacios: ").append(EspDisp).append("\n");
        return sb.toString();
    }
    
    public void calculaFinal(double t){
        if(EspDisp==0){
            tf=t;
            recalculaLlena();
        }
        if(Bicis==0){
            tf=t;
            recalculaVacía();
        }
        if(ut!=0){
            fuet=uet/ut;
            tpet=tet/ut;
        }
        else{
            fuet=0;
            tpet=0;
        }
        if(ud!=0){
            fued=ued/ud;
            tped=ted/ud;
        }
        else{
            fued=0;
            tped=0;
        }
    }
    
    public String toStringFinal(){
        double u=ut+ud;
        StringBuilder sb = new StringBuilder();
        sb.append("Estación: ").append(Id).append("\n");
        sb.append("Bicicletas: ").append(Bicis).append("\n");
        sb.append("Espacios: ").append(EspDisp).append("\n");
        sb.append("Usuarios servidos: ").append(u).append("\n");
        if(ut!=0)
            sb.append("Esperaron tomar: ").append(Math.round(uet)).append(" = ")
                .append(fuet).append("\n");
        else
            sb.append("Esperaron tomar: 0").append("\n");
        if(ud!=0)
            sb.append("Esperaron dejar: ").append(Math.round(ued)).append(" = ")
                .append(fued).append("\n");
        else
            sb.append("Esperaron dejar: 0").append("\n");
        if(u!=0)
            sb.append("No esperaron: ").append(Math.round(u-ued-uet))
              .append(" = ").append((u-ued-uet)*100/u).append("%").append("\n");
        else
            sb.append("No esperaron: 0").append("\n");
        sb.append("Tiempo que estuvo vacía: ").append(tVac)
                .append(" minutos").append("\n");
        sb.append("Tiempo que estuvo llena: ").append(tLlen)
                .append(" minutos").append("\n");
        sb.append("Tiempo promedio de espera global: ")
                .append((tet+ted)/u).append(" minutos").append("\n");
        sb.append("Tiempo promedio de espera para tomar bici: ");
        sb.append(tpet).append(" minutos").append("\n");
        sb.append("Tiempo promedio de espera para dejar bici: ");
        sb.append(tped).append(" minutos").append("\n");
        sb.append("Tiempo promedio de quienes esperaron tomar bici: ");
        if(uet!=0)
            sb.append(tet/uet).append(" minutos").append("\n");
        else
            sb.append("0 minutos").append("\n");
        sb.append("Tiempo promedio de quienes esperaron dejar bici: ");
        if(ued!=0)
            sb.append(ted/ued).append(" minutos").append("\n");
        else
            sb.append("0 minutos").append("\n");
        
        return sb.toString();
    }
    public void resultados(){
        Ecobici.res[Id-1][0]+=","+ut;
        Ecobici.res[Id-1][1]+=","+uet;
        Ecobici.res[Id-1][2]+=","+fuet;
        Ecobici.res[Id-1][3]+=","+tpet;
        Ecobici.res[Id-1][4]+=","+tVac;
        Ecobici.res[Id-1][5]+=","+ud;
        Ecobici.res[Id-1][6]+=","+ued;
        Ecobici.res[Id-1][7]+=","+fued;
        Ecobici.res[Id-1][8]+=","+tped;
        Ecobici.res[Id-1][9]+=","+tLlen;
        Ecobici.res[Id-1][10]+=","+(ud-ut);
        if((uet+ued)!=uet&&(uet+ued)!=ued)
            System.out.println("Estación "+Id+" tiene usuarios que esperan tanto esperar como dejar...");
    }
    public void imprimeFinal(){
        String s= "C:\\ecobici simulación\\Resultados\\porEst\\Est"+Id+".csv";
        try{
            writ = new FileWriter(s);
            for (int i = 0; i < 11; i++)
                    writ.append(Ecobici.res[Id-1][i]).append("\n");
        } catch (Exception e) {
           System.err.println("Error! "+e.getMessage());
        } finally {
           if (null!=writ){
              try {
                 writ.flush();
              } catch (IOException e) {
                 System.err.println("Error flushing file !! "+e.getMessage());
              }
              try {
                 writ.close();
              } catch (IOException e) {
                 System.err.println("Error closing file !! "+e.getMessage());
              }
           }
        }
    }
}
