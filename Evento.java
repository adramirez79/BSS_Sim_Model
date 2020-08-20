/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

import static ecobici.Ecobici.arribos;
import java.io.FileWriter;
import java.io.IOException;


/**
 *
 * @author
 */
public class Evento {
    
    final int tipo; //1=user arrival to take bike, 2=end of trip, 3=end of trip if user waited to take (not used in this paper)
    final int estación;
    final String nombre;
    double tiempo;
    double primlleg;
    int u=-1;
    final double duración=Ecobici.durper;
    final int cantper=Ecobici.periodos;
    RngStream aleatorio = new RngStream();
    
	//first user arrival to take a bike	
    public Evento(Estación est, double t){
       estación=est.Id;
       nombre="Toma bici en la estación E"+estación;
       Ecobici.llegadas++;
       tipo=1;
       tiempo = encuentraPrimerTiempo(t, est);
    }
    
	//find the first period such that rate > 0
    private double encuentraPrimerTiempo(double t, Estación est){
        primlleg = est.FirstArr;
        double tasaept;
        if (primlleg <= cantper){
            tasaept=est.tasas[(int)primlleg];
        } else{
            tasaept= cantper;
        }
        double rand = aleatorio.randU01();
        double taux=(duración * primlleg)-(Math.log(1-rand)/tasaept);
        return taux;
    }
    
	//user arrivals to take a bike
    public Evento(Estación est, double t, int p){ 
        tipo=1;
        estación=est.Id;
        nombre="Toma bici en la estación E"+estación;
        Ecobici.llegadas++;
        tiempo=encuentraTiempo(t,est,p);
    }
    
   //schedule the next user arrival considering periods with rate=0 and drastic changes in rates between two consecutive periods
    private double encuentraTiempo(double t, Estación est, int p){
        double tasa=est.tasas[p];
        if (tasa == 0){
            int per = p;
            int indic=0;
            double tasax = tasa;
            while (tasax == 0 && per < cantper-1){
                per++;
                tasax=est.tasas[per];
            }
            if(t==(cantper-1)*duración){
                per=cantper+1;
                indic=1;
            }
            if (indic==0){
                double randnum = aleatorio.randU01();
                double taux=(duración*per)-(Math.log(1-randnum)/tasax);
                return taux;
            } else if(indic==1){
                double taux=(cantper*duración)+2;
                return taux;
            }
        }
        else if(tasa > 0 && p == cantper-1){
            double rand3 = aleatorio.randU01();
            double taux3=t-Math.log(1-rand3)/tasa;
            return taux3;
        }
        else if(tasa > 0 && p < cantper-1 && est.tasas[p+1] > 0){
            double rand = aleatorio.randU01();
            double taux=t-Math.log(1-rand)/tasa;
            double taux2=t+((taux-t)*(tasa/est.tasas[p+1]));
            if (p>=32&&p<=52){
                if (taux <= taux2){
                    return (3*taux/4)+(1*taux2/4);
                } else {
                    return (1*taux/4)+(3*taux2/4);
                }
            }                    
            if (p>=155&&p<=176){
                if (taux < taux2){
                    return (4*taux/5)+(1*taux2/5);
                } else {
                    return (4*taux2/5)+(1*taux/5);
                }
            }
            if (p>=85&&p<=144){
                if (taux < taux2){
                    return (1*taux/2)+(1*taux2/2);
                } else {
                    return (1*taux2/2)+(1*taux/2);
                }
            }
            if ((p>=0&&p<=30) || (p>=60&&p<=84)|| (p>=182)){
                if (taux < taux2){
                    return taux2;
                } else {
                    return taux;
                }
            }
            
            else{
                if (taux < taux2){
                    return taux2;
                } else {
                    return taux;
                }
            }
        }
        else if(tasa > 0 && p < cantper-1 && est.tasas[p+1]== 0){
            double rand = aleatorio.randU01();
            double taux=t-Math.log(1-rand)/tasa;
            double per = taux/duración;
            int peri = (int)per;
            int indic=0;
            int indica=0;
            double tasax = 0;
            if (per <= cantper-1){
                tasax = est.tasas[peri];
            } else{
                indica=1;
            }
            if (indica ==1){
                return (cantper*duración)+2;            
            }
            
            if (tasax == 0 && indica==0){
                if ((p>=0&&p<=30) || (p>=60&&p<=102) || (p>=182)){ 
                    while (tasax == 0 && peri < cantper-1){
                        peri++;
                        tasax=est.tasas[peri];
                    }
                    if(t==(cantper-1)*duración){
                        peri = cantper+1;
                        indic=1;
                    }
                    if (indic==0){
                        double randnum = aleatorio.randU01();
                        double taux5=(duración*Double.valueOf(peri))-(Math.log(1-randnum)/tasax);
                        return taux5;
                    } else if(indic==1){
                        double taux5=((cantper-1)*duración)+2;
                        return taux5;
                    }
                }
                else{
                    return taux;
                }
            } else{
                return taux;
            }
        }
        return 98.5; //auxiliary constant not used
    }
    
	//end of trip
    public Evento(double t){
        tipo=2;

        tiempo = t+Ecobici.lognormal.sample();
        if (tiempo <= cantper*duración){
            int columna = (int) Math.floor(tiempo/Ecobici.durper);
            if (columna >= cantper){
                columna = cantper-1;
            }
            int k=0;
            double r=aleatorio.randU01();
            while(arribos[k][columna]<r){
                k++;
            }
            estación=k+1;
            nombre="Deja bici en la estación E"+estación;
        } else {
            estación=1;
            nombre="Deja bici en la estación E"+estación;
        }
    }
    
	//end of sim
    public Evento(double t, double tfinal){
        estación=1;
        tipo=4;
        tiempo=tfinal;
        nombre="Fin de Sim";
    }
    
	//event out of the scope of this paper
    public Evento(double t, int m){
        tipo=2;
        u=m;

        tiempo = t+Ecobici.lognormal.sample();
        if (tiempo <= cantper*duración){
            int columna = (int) Math.floor(tiempo/Ecobici.durper);
            if (columna >= cantper){
                columna = cantper-1;
            }
            int k=0;
            double r=aleatorio.randU01();
            while(arribos[k][columna]<r){
                k++;
            }
            estación=k+1;
            nombre="Deja bici en la estación E"+estación;
        } else {
            estación=1;
            nombre="Deja bici en la estación E"+estación;
        }
    }
}