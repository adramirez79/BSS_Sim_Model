/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecobici;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import java.io.FileWriter;

/**
 *
 * Ecobici BSS Simulation Model
 */
public class Ecobici {

    /**
     * @param args the command line arguments
     */
   

    static int NumEsperando,NumEsperando2;
    static Usuario usuario;
    static int llegadas, último;
    static int Espt,Espd;
    final static Scanner ingreso = new Scanner(System.in);
    static int nEst; //number of stations
    static double tnow;
    static double t; //auxiliary
    static int n; //number of events in the calendar
    static ArrayQueue calendario;
    static double fin;
    static int reps;
    static Evento actual;
    static double r;
    static Estación[] estaciones;
    static double[][] arribos;
    static int e1, e2, e3; //params. stations
    static double e4;
    static double[] e5; //interarrival rates
    static int k;
    static int u; //user counter
    static ArrayQueue[] esperando;
    static ArrayQueue servidos = new ArrayQueue();//served users
    static int bicis; //total number of bikes
    static double AV,tiV,tfV,qV,V; //variables to compute empty time
    static double ALl,tiLl,tfLl,qLl,Ll; //variables to compute full time
    static double suet,sued,suetyd,sue,sut,sud,su,sune,//sums
            fuet,fued,fue,fune;//fractions of users that waited
    static double stet,sted,ste,tet,ted,te; //waiting times
    static double SQV,SQV2,QV,sQV,SQLl,SQLl2,QLl,sQLl;//global full/empty
    static double qInef,SInef,SInef2,Inef,sInef; //inef = full + empty
    static double SUT,SUT2,SUD,SUD2,FUET,sUET,FUED,sUED;//global users
    static double SUE,SUE2,FUE,sUE,SUNE,SUNE2,FUNE,sUNE; 
    static double STET,STET2,STED,STED2,TET,sTET,TED,sTED; //global times
    static double STE,STE2,TE,sTE; //global waiting times
    static int periodo; // current period
    static int periodos; //total number of periods
    static double durper; // duration of each period
    static LogNormalDistribution lognormal;
    static double mLogNormal,sdLogNormal,mNormal,sdNormal;
    static String[] resultados;
    static String[] globales;
    static String[][] res; 
    static RngStream aleat = new RngStream();
    static MersenneTwister ale;
    static int[] cumarr;
       
    
    public static void main(String[] args) throws IOException {
        ale = new MersenneTwister();
        ale.setSeed(324789);
             
        int i=0;
        int j=0;
        int l=0;
        BufferedReader br = null;
        try {
            br =new BufferedReader(new FileReader
            ("C:\\ecobici simulación\\Entradas\\DatosEntrada.csv"));
            String line = br.readLine(); //headers
            line = br.readLine();
            String [] fields = line.split(",");
            reps=Integer.parseInt(fields[0]); //number of replications
            fin=Double.parseDouble(fields[1]); //replication length
            periodos=Integer.parseInt(fields[2]); //number of periods
            durper=Double.parseDouble(fields[3]); //period length
            nEst=Integer.parseInt(fields[4]); //number of stations
            mLogNormal=Double.parseDouble(fields[5]);//mean travel time
            sdLogNormal=Double.parseDouble(fields[6]);//std dev travel time
        } catch (Exception e) {
           System.err.println("Error! "+e.getMessage());
        } finally {
           if (null!=br){
              try {
                 br.close();
              } catch (IOException e) {
                 System.err.println("Error closing file !! "+e.getMessage());
              }
           }
        }
        System.out.println("Simulación: "+reps+" repeticiones de "+(int)fin+
                " minutos cada una, con "+periodos+" periodos por jornada, "
                + "cada uno de "+durper+" minutos.");
        System.out.println("Total de estaciones: "+nEst+". Tiempo medio de"
                + " viaje: "+mLogNormal+" minutos, con una desviación estándar "
                + "de "+sdLogNormal+" minutos.");
        mNormal=Math.log(Math.pow(mLogNormal,2)/Math.sqrt(Math.pow(mLogNormal,
                2)+Math.pow(sdLogNormal,2)));
        sdNormal=Math.sqrt(Math.log(1+Math.pow(sdLogNormal,2)/
                Math.pow(mLogNormal,2)));
        lognormal = new LogNormalDistribution(ale, mNormal, sdNormal);
        estaciones = new Estación[nEst];
        esperando = new ArrayQueue[nEst];
        arribos=new double[nEst][periodos];
        cumarr = new int[nEst];
        SQV=0;SQV2=0;QV=0;sQV=0;SQLl=0;SQLl2=0;QLl=0;sQLl=0;
        SInef=0;SInef2=0;Inef=0;sInef=0;
        SUT=0;SUT2=0;SUD=0;SUD2=0;FUET=0;sUET=0;FUED=0;sUED=0;
        STET=0;STET2=0;STED=0;STED2=0;TET=0;sTET=0;TED=0;sTED=0;
        SUE=0;SUE2=0;FUE=0;sUE=0;SUNE=0;SUNE2=0;FUNE=0;sUNE=0;
        STE=0;STE2=0;TE=0;sTE=0;
        String csv= "C:\\ecobici simulación\\Resultados\\Resultados.csv";
        FileWriter writer = null;
        //results for each replication
        resultados=new String[21];
        resultados[0]="Rep:";
        resultados[1]="Bicicletas:";
        resultados[2]="Usuarios servidos:";
        resultados[3]="Usuarios que tomaron bicicleta:";           
        resultados[4]="Usuarios que esperaron tomar bicicleta:";
        resultados[5]="Siguen esperando tomar:";
        resultados[6]="Fracc. de usuarios que esperaron tomar bicicleta:";
        resultados[7]="Usuarios que dejaron bicicleta:";
        resultados[8]="Usuarios que esperaron dejar bicicleta:";
        resultados[9]="Siguen esperando dejar:";
        resultados[10]="Fracc. de usuarios que esperaron dejar bicicleta:";
        resultados[11]= "Usuarios totales que esperaron:";
        resultados[12]="Fracc. de usuarios que esperaron:";
        resultados[13]="No esperaron:";
        resultados[14]="Fracc. de usuarios que no esperaron:";
        resultados[15]="Tiempo promedio de espera para tomar bicicleta:";
        resultados[16]="Tiempo promedio de espera para dejar bicicleta:";
        resultados[17]="Tiempo promedio de espera:";
        resultados[18]="Fracc. promedio de tiempo con estaciones vac?ias:";
        resultados[19]="Fracc. promedio de tiempo con estaciones llenas:";
        resultados[20]="Fracc. promedio de tiempo con estaciones "
                   + "llenas o vac?ias:";
        res=new String[nEst][11];
        for (int m = 0; m < nEst; m++) {
            res[m][0]="Usuarios que tomaron bicicleta: ";
            res[m][1]="Usuarios que esperaron tomar bicicleta: ";
            res[m][2]="Fracc. de usuarios que esperaron tomar bicicleta: ";
            res[m][3]="Tiempo promedio de espera para tomar bicicleta: ";
            res[m][4]="Tiempo que estuvo vacía: ";
            res[m][5]="Usuarios que dejaron bicicleta: ";
            res[m][6]="Usuarios que esperaron dejar bicicleta: ";
            res[m][7]="Fracc. de usuarios que esperaron dejar bicicleta: ";
            res[m][8]="Tiempo promedio de espera para dejar bicicleta: ";
            res[m][9]="Tiempo que estuvo llena: ";
            res[m][10]="Balance: ";
        }
		//loop for each replication
        for(i =0;i<reps;i++){ 
            llegadas=0; último=0;
            System.out.println("\n");
            System.out.println("Repetición "+(i+1)+"\n");
            resultados[0]+=","+(i+1);
            j=0;
            n=0;
            u=0;
            tnow=0;
            AV=0;tiV=0;tfV=0;qV=0;V=0;
            ALl=0;tiLl=0;tfLl=0;qLl=0;Ll=0;
            qInef=0;
            periodo=0;
            suet=0;sued=0;suetyd=0;fuet=0;fued=0;stet=0;sted=0;tet=0;ted=0;
            sut=0;sud=0;sue=0;su=0;sune=0;fue=0;fune=0;ste=0;te=0;
            NumEsperando=0;NumEsperando2=0;Espt=0;Espd=0;
            bicis=0;
        try {
         br =new BufferedReader(new FileReader
        ("C:\\ecobici simulación\\Entradas\\estaciones.csv"));
         String line = br.readLine();
		 //input data for stations
         while (null!=line) {
            String [] fields = line.split(",");
            e1=Integer.parseInt(fields[0]); //id;
            e2=Integer.parseInt(fields[1]); //capacity;
            e3=Integer.parseInt(fields[2]); //bikes;
            e4=Double.parseDouble(fields[3]); //period with rate > 0;
            e5 = new double[periodos];
            for (int m=0; m<periodos; m++)
                e5[m]=Double.valueOf(fields[m+4]); //rates-interarrival time
            estaciones[j] = new Estación(e1,e2,e3,e4,e5);
            esperando[j] = new ArrayQueue();
            bicis=bicis+e3;
            j++;
            line = br.readLine();
         }
         
        } catch (Exception e) {
           System.err.println("Error! "+e.getMessage());
        } finally {
          if (null!=br){
            try {
               br.close();
            } catch (IOException e) {
               System.err.println("Error closing file !! "+e.getMessage());
            }
          }
        }
        if (nEst==j){
            System.out.println("Todas las estaciones creadas, total: "+
                    bicis+" bicicletas y "+j+" estaciones.");
            resultados[1]+=","+bicis;
        }
            
        else
            System.out.println("Error en creación de estaciones.");
        try {
         br =new BufferedReader(new FileReader
        ("C:\\ecobici simulación\\Entradas\\tasasArribos.csv"));
         String line = br.readLine();
         j=0;
          while (null!=line) {
            String [] fields = line.split(",");
            line = br.readLine();
             for (l = 0; l < periodos; l++) {
                 arribos[j][l]=Double.parseDouble(fields[l]);
             }
             j++;
          }
        } catch (Exception e) {
            System.err.println("Error! "+e.getMessage());
        } finally {
           if (null!=br){
              try {
                 br.close();
              } catch (IOException e) {
                 System.err.println("Error closing file !! "+e.getMessage());
              }
           }
        }
        if (nEst==j&&periodos==l)
               System.out.println("Tasas de arribo asignadas correctamente.");
           else
               System.out.println("Error en asignación de tasas de arribo.");

	String departures= "C:\\ecobici simulación\\Resultados\\salidas"+i+".csv";
        FileWriter escribe = null;
        try{
            escribe = new FileWriter(departures);
            escribe.append("Evento , Tiempo , Estacion").append("\n");
        } catch (IOException e) {
           System.err.println("Error! "+e.getMessage());
        }
        
        String arrivals= "C:\\ecobici simulación\\Resultados\\llegadas"+i+".csv";
        FileWriter escribe2 = null;
        try{
            escribe2 = new FileWriter(arrivals);
            escribe2.append("Evento , Tiempo , Estacion").append("\n");
        } catch (IOException e) {
           System.err.println("Error! "+e.getMessage());
        }
        
			//create the event calendar and schedule the first arrival to each station
            calendario=new ArrayQueue();
            for (l = 0; l < nEst; l++) {
               cumarr[l]=0;
               calendario.enqueue(new Evento(estaciones[l],tnow)); 
               n++;
             }
            //schedule the enf of sim
            double endofsim=fin;
            calendario.enqueue(new Evento(tnow, endofsim)); 
            
            //loop for replication length   
            while (tnow<fin){ 
                t= ((Evento) calendario.first()).tiempo;
                for(l=0;l<n;l++){ //find the next event in the calendar
                    calendario.enqueue(calendario.dequeue());
                    if(((Evento)calendario.first()).tiempo<t)
                        t=((Evento)calendario.first()).tiempo;
                }
                while(((Evento)calendario.first()).tiempo!=t) 
                    calendario.enqueue(calendario.dequeue());
                tnow=t;

                // period update
                if (tnow/durper>=periodo+1 && periodo<periodos-1)
                    periodo++;
                actual=(Evento) calendario.dequeue();
                n--;
  
                //User arrival to take a bike:                             
                if(actual.tipo==1){
                     //if no bikes are available, then the user waits
                    if(estaciones[actual.estación-1].Bicis==0){ 
                        u++;
                        ((ArrayQueue) esperando[actual.estación-1]).enqueue
                         (new Usuario(u,tnow,1,actual.estación));
                        estaciones[actual.estación-1].espt++;
                    }
                    //if there are bikes available
                    else{
                        //user takes a bike and begin the trip
                        estaciones[actual.estación-1].ut++;
                        try{
                            String hora = Double.toString(tnow);
                            int esta = estaciones[actual.estación-1].Id;
                            String station = Integer.toString(esta);
                            escribe.append("1").append(",").append(hora).append(",").append(station).append("\n");
                        } catch (IOException e) {
                            System.err.println("Error! "+e.getMessage());
                        }
                                             
                        //if there are no users waiting to return a bike
                         if(esperando[actual.estación-1].isEmpty()){
                             //if station was full, now it has an available space
                             if(estaciones[actual.estación-1].EspDisp==0){
                                 tfLl=tnow;
                                 ALl=ALl+(tfLl-tiLl)*Ll;
                                 Ll--;
                                 tiLl=tnow;
                                 estaciones[actual.estación-1].tf=tnow;
                                 estaciones[actual.estación-1].recalculaLlena();
                             }
                             estaciones[actual.estación-1].Bicis--;
                             estaciones[actual.estación-1].EspDisp++;
                             //if user took the last bike, now the station is empty
                             if(estaciones[actual.estación-1].Bicis==0){
                                 tfV=tnow;
                                 AV=AV+(tfV-tiV)*V;
                                 V++;
                                 tiV=tnow;
                                 estaciones[actual.estación-1].ti=tnow;
                             }
                         }
                         //if there are users waiting to return a bike, the dock is not released
                         else{
                             ((Usuario)esperando
                                     [actual.estación-1].first()).Tf=tnow;
                             estaciones[actual.estación-1].ted=
                                     estaciones[actual.estación-1].ted+
                                     ((Usuario)esperando
                                     [actual.estación-1].first()).tiempo();
                             if(((Usuario)esperando[actual.estación-1].
                                     first()).tipo==3)
                                 estaciones[actual.estación-1].uetyd++;
                             servidos.enqueue(esperando
                                     [actual.estación-1].dequeue());
                             estaciones[actual.estación-1].espd--;
                             estaciones[actual.estación-1].ued++;
                             estaciones[actual.estación-1].ud++;
                         }
                         //schedule end of trip
                         calendario.enqueue(new Evento(tnow));
                         n++;
                     }  
                    //schedule a new user arrival
                    calendario.enqueue(new Evento(
                            estaciones[actual.estación-1],tnow,periodo));
                    cumarr[actual.estación-1]++;
                    n++;
                    r = ale.nextDouble();
                    k=0;
                }
                //User arrival to destination station
                else if (actual.tipo==2){
                    try{
                        String hora = Double.toString(tnow);
                        int esta = estaciones[actual.estación-1].Id;
                        String station = Integer.toString(esta);
                        escribe2.append("2").append(",").append(hora).append(",").append(station).append("\n");
                    } catch (IOException e) {
                        System.err.println("Error! "+e.getMessage());
                     }
                    //if the are not available docks, the user waits
                    if(estaciones[actual.estación-1].EspDisp==0){
                        u++;
                        esperando[actual.estación-1].enqueue(new 
                             Usuario(u,tnow,2,actual.estación));
                        estaciones[actual.estación-1].espd++;
                    }
                    // if there are available docks
                    else{
                        //the user returns the bike and leaves the system
                        estaciones[actual.estación-1].ud++;

                        // if there are no users waiting
                        if(esperando[actual.estación-1].isEmpty()){
                            //if station was empty, now it has bikes available
                            if(estaciones[actual.estación-1].Bicis==0){
                                tfV=tnow;
                                AV=AV+(tfV-tiV)*V;
                                V--;
                                tiV=tnow;
                                estaciones[actual.estación-1].tf=tnow;
                                estaciones[actual.estación-1].recalculaVacía();
                            }
                            estaciones[actual.estación-1].Bicis++;
                            estaciones[actual.estación-1].EspDisp--;
                            //if station became full
                            if(estaciones[actual.estación-1].EspDisp==0){
                                tfLl=tnow;
                                ALl=ALl+(tfLl-tiLl)*Ll;
                                Ll++;
                                tiLl=tnow;
                                estaciones[actual.estación-1].ti=tnow;
                            }
                        }
                        //if there were users waiting, the bike is not available
                        else {
                            ((Usuario)esperando
                                        [actual.estación-1].first()).Tf=tnow;
                            estaciones[actual.estación-1].tet=
                                        estaciones[actual.estación-1].tet+
                                        ((Usuario)esperando
                                        [actual.estación-1].first()).tiempo();
                            
                            calendario.enqueue(new Evento(tnow,((Usuario)
                               esperando[actual.estación-1].first()).id));
                            servidos.enqueue(esperando
                                        [actual.estación-1].dequeue());
                            estaciones[actual.estación-1].espt--;
                            estaciones[actual.estación-1].uet++;
                            estaciones[actual.estación-1].ut++;
                            try{
                            String hora = Double.toString(tnow);
                            int esta = estaciones[actual.estación-1].Id;
                            String station = Integer.toString(esta);
                            escribe.append("1").append(",").append(hora).append(",").append(station).append("\n");
                            } catch (IOException e) {
                            System.err.println("Error! "+e.getMessage());
                            }   
                            
                            n++;
                        }
                    }
                }
				//end of trip of a user that waited to take the bike (not user for the scope of this paper)
                else{
                    try{
                        String hora = Double.toString(tnow);
                        int esta = estaciones[actual.estación-1].Id;
                        String station = Integer.toString(esta);
                        escribe2.append("3").append(",").append(hora).append(",").append(station).append("\n");
                    } catch (IOException e) {
                        System.err.println("Error! "+e.getMessage());
                    }
                    if(estaciones[actual.estación-1].EspDisp==0){
                        esperando[actual.estación-1].enqueue(new 
                             Usuario(actual.u,tnow,3,actual.estación));
                        estaciones[actual.estación-1].espd++;
                    }
                    else{
                        estaciones[actual.estación-1].ud++;
                        if(esperando[actual.estación-1].isEmpty()){
                            if(estaciones[actual.estación-1].Bicis==0){
                                tfV=tnow;
                                AV=AV+(tfV-tiV)*V;
                                V--;
                                tiV=tnow;
                                estaciones[actual.estación-1].tf=tnow;
                                estaciones[actual.estación-1].recalculaVacía();
                            }
                            estaciones[actual.estación-1].Bicis++;
                            estaciones[actual.estación-1].EspDisp--;

                            if(estaciones[actual.estación-1].EspDisp==0){
                                tfLl=tnow;
                                ALl=ALl+(tfLl-tiLl)*Ll;
                                Ll++;
                                tiLl=tnow;
                                estaciones[actual.estación-1].ti=tnow;
                            }
                        }

                        else {
                            ((Usuario)esperando
                                        [actual.estación-1].first()).Tf=tnow;
                            estaciones[actual.estación-1].tet=
                                        estaciones[actual.estación-1].tet+
                                        ((Usuario)esperando
                                        [actual.estación-1].first()).tiempo();
                            calendario.enqueue(new Evento(tnow,((Usuario)
                               esperando[actual.estación-1].first()).id));
                            servidos.enqueue(esperando
                                        [actual.estación-1].dequeue());
                            estaciones[actual.estación-1].espt--;
                            estaciones[actual.estación-1].uet++;
                            estaciones[actual.estación-1].ut++;
                            try{
                            String hora = Double.toString(tnow);
                            int esta = estaciones[actual.estación-1].Id;
                            String station = Integer.toString(esta);
                            escribe.append("1").append(",").append(hora).append(",").append(station).append("\n");
                            } catch (IOException e) {
                            System.err.println("Error! "+e.getMessage());
                            } 
                            
                            n++;
                        }
                    }
                    
                }
            }
            
            String hora = Double.toString(tnow);
            try{
            escribe.append("1").append(",").append(hora).append(",").append("fin").append("\n");
            } catch (IOException e) {
            System.err.println("Error! "+e.getMessage());}
            finally {
               if (null!=escribe){
                  try {
                     escribe.flush();
                  } catch (IOException e) {
                    System.err.println("Error flushing file !!"+e.getMessage());
                  }
                  try {
                     escribe.close();
                  } catch (IOException e) {
                     System.err.println("Error closing file !!"+e.getMessage());
                  }
               }
            }
            
            String hora2 = Double.toString(tnow);
            try{
            escribe2.append("1").append(",").append(hora2).append(",").append("fin").append("\n");
            } catch (IOException e) {
            System.err.println("Error! "+e.getMessage());}
            finally {
               if (null!=escribe2){
                  try {
                     escribe2.flush();
                  } catch (IOException e) {
                    System.err.println("Error flushing file !!"+e.getMessage());
                  }
                  try {
                     escribe2.close();
                  } catch (IOException e) {
                     System.err.println("Error closing file !!"+e.getMessage());
                  }
               }
            }
            
			//replication results
            tfV=tnow;
            AV=AV+(tfV-tiV)*V;
            tfLl=tnow;
            ALl=ALl+(tfLl-tiLl)*Ll;

            System.out.println("\n");
            int cont;
            for(cont=0;cont<nEst;cont++){
                while(!esperando[cont].isEmpty()){
                    usuario = (Usuario) esperando[cont].dequeue();
                    if(usuario.tipo==1)
                        NumEsperando++;
                    else
                        NumEsperando2++;
                    }
            }
            System.out.println("Siguen esperando tomar: "+NumEsperando+", "
                    + "y dejar: "+NumEsperando2);
            NumEsperando=0;NumEsperando2=0;
            System.out.println("\n");
            for (l = 0; l < nEst; l++) {
                  estaciones[l].calculaFinal(tnow);
                  estaciones[l].resultados();
                  sut=sut+estaciones[l].ut;
                  suet=suet+estaciones[l].uet;
                  sud=sud+estaciones[l].ud;
                  sued=sued+estaciones[l].ued;
                  suetyd+=estaciones[l].uetyd;
                  stet=stet+estaciones[l].tet;
                  sted=sted+estaciones[l].ted;
                  Espt+=estaciones[l].espt;
                  Espd+=estaciones[l].espd;
              }
           
          
           System.out.println("\n");
           su=sut+sud;
           System.out.println("Usuarios servidos: "+(int)su);
           resultados[2]+=","+su;
           System.out.println("Usuarios que tomaron bicicleta: "+(int)sut);
           resultados[3]+=","+sut;
           System.out.println("Usuarios que esperaron tomar bicicleta: "
                   +(int)suet);
           resultados[4]+=","+suet;
           System.out.println("Siguen esperando tomar: "+Espt);
           resultados[5]+=","+Espt;
           fuet=(Espt+suet)/(sut+Espt);
           System.out.println("Fracción de usuarios que esperaron tomar "
                   + "bicicleta: "+fuet);
           resultados[6]+=","+fuet;
           System.out.println("Usuarios que dejaron bicicleta: "+(int)sud);
           resultados[7]+=","+sud;
           System.out.println("Usuarios que esperaron dejar bicicleta: "
                   +(int)sued);
           resultados[8]+=","+sued;
           System.out.println("Siguen esperando dejar: "+Espd);
           resultados[9]+=","+Espd;
           fued=(Espd+sued)/(sud+Espd);
           System.out.println("Fracción de usuarios que esperaron dejar "
                   + "bicicleta: "+fued);
           resultados[10]+=","+fued;
           System.out.println("Usuarios que esperaron ambas: "+(int)suetyd);
           sue=suet+sued+Espd+Espt;
           System.out.println("Usuarios totales que esperaron: "+(int)sue);
           resultados[11]+=","+sue;
           fue=sue/(su+Espd+Espt);
           System.out.println("Fracción de usuarios que esperaron: "+fue);
           resultados[12]+=","+fue;
           sune=su-suet-sued;
           System.out.println("No esperaron: "+(int)sune);
           resultados[13]+=","+sune;
           fune=sune/(su+Espt+Espt);
           System.out.println("Fracción de usuarios que no esperaron: "+fune);
           resultados[14]+=","+fune;
           tet=stet/sut;
           System.out.println("Tiempo promedio de espera para tomar "
                   + "bicicleta: "+tet+" minutos");
           resultados[15]+=","+tet;
           ted=sted/sud;
           System.out.println("Tiempo promedio de espera para dejar "
                   + "bicicleta: "+ted+" minutos");
           resultados[16]+=","+ted;
           ste=stet+sted;
           te=ste/su;
           System.out.println("Tiempo promedio de espera: "+te);
           resultados[17]+=","+te;
           qV=AV/(fin*nEst);
           System.out.println("Fracción promedio de tiempo "
                + "con estaciones vacías: "+qV);
           resultados[18]+=","+qV;
           qLl=ALl/(fin*nEst);
           System.out.println("Fracción promedio de tiempo "
                + "con estaciones llenas: "+qLl);
           resultados[19]+=","+qLl;
           qInef=qLl+qV;
           System.out.println("Fracción promedio de tiempo con estaciones "
                   + "llenas o vacías: "+qInef);
           resultados[20]+=","+qInef;
           
        System.out.println("\n");
        System.out.println("Llegadas programadas: "+llegadas);
        System.out.println("Creadas por default al último: "+último);
        System.out.println("Efectivas: "+(llegadas-último));
           SQV=SQV+qV;
           SQV2=SQV2+(qV*qV);
           SQLl=SQLl+qLl;
           SQLl2=SQLl2+(qLl*qLl);
           SInef=SInef+qInef;
           SInef2=SInef2+(qInef*qInef);
           SUT=SUT+fuet;
           SUT2=SUT2+(fuet*fuet);
           SUD=SUD+fued;
           SUD2=SUD2+(fued*fued);
           SUE=SUE+fue;
           SUE2=SUE2+(fue*fue);
           SUNE=SUNE+fune;
           SUNE2=SUNE2+(fune*fune);
           STET=STET+tet;
           STET2=STET2+(tet*tet);
           STED=STED+ted;
           STED2=STED2+(ted*ted);
           STE=STE+te;
           STE2=STE2+(te*te);
            for (int m = 0; m < n; m++) {
                if(((Evento) calendario.dequeue()).tipo==1)
                    NumEsperando++;
                else
                    NumEsperando2++;
            }
            if(calendario.isEmpty())
                System.out.println("Se vació el calendario. Quedaron "+n
                    +" eventos: " +NumEsperando+" llegadas y "+NumEsperando2
                    +" arribos con bicicleta.");
       }
        try{
            writer = new FileWriter(csv);
                for (int m = 0; m < 21; m++)
                    writer.append(resultados[m]).append("\n");
            } catch (Exception e) {
               System.err.println("Error! "+e.getMessage());
            } finally {
               if (null!=writer){
                  try {
                     writer.flush();
                  } catch (IOException e) {
                    System.err.println("Error flushing file !!"+e.getMessage());
                  }
                  try {
                     writer.close();
                  } catch (IOException e) {
                     System.err.println("Error closing file !!"+e.getMessage());
                  }
               }
            }
		
		//compute global results (across replications)
        String csv2= "C:\\ecobici simulación\\Resultados\\Globales.csv";
        FileWriter w = null;
        globales=new String[21];
        System.out.println("\n");
        globales[0]="Estadísticas globales:";
        System.out.println(globales[0]);
        FUET=SUT/reps;
        globales[1]="Fracc. promedio de usuarios que esperaron tomar "
                + "bicicleta: ";
        System.out.println(globales[1]+FUET);
        globales[1]+=","+FUET;
        sUET=SUT2/(reps-1)-(SUT*SUT)/(reps*(reps-1));
        globales[2]="Varianza de fracc. de usuarios que esperaron tomar "
                + "bicicleta: ";
        System.out.println(globales[2]+sUET);
        globales[2]+=","+sUET;
        FUED=SUD/reps;
        globales[3]="Fracc. promedio de usuarios que esperaron dejar "
                + "bicicleta: ";
        System.out.println(globales[3]+FUED);
        globales[3]+=","+FUED;
        sUED=SUD2/(reps-1)-(SUD*SUD)/(reps*(reps-1));
        globales[4]="Varianza de fracc. de usuarios que esperaron dejar "
                + "bicicleta: ";
        System.out.println(globales[4]+sUED);
        globales[4]+=","+sUED;
        FUE=SUE/reps;
        globales[5]="Fracc. promedio de usuarios que esperaron: ";
        System.out.println(globales[5]+FUE);
        globales[5]+=","+FUE;
        sUE=SUE2/(reps-1)-(SUE*SUE)/(reps*(reps-1));
        globales[6]="Varianza de fracc. de usuarios que esperaron: ";
        System.out.println(globales[6]+sUE);
        globales[6]+=","+sUE;
        FUNE=SUNE/reps;
        globales[7]="Fracc. promedio de usuarios que no esperaron: ";
        System.out.println(globales[7]+FUNE);
        globales[7]+=","+FUNE;
        sUNE=SUNE2/(reps-1)-(SUNE*SUNE)/(reps*(reps-1));
        globales[8]="Varianza de fracc. de usuarios que no esperaron: ";
        System.out.println(globales[8]+sUE);
        globales[8]+=","+sUE;
        TET=STET/reps;
        globales[9]="Tiempo de espera promedio para tomar bicicleta: ";
        System.out.println(globales[9]+TET+" minutos");
        globales[9]+=","+TET;
        sTET=STET2/(reps-1)-(STET*STET)/(reps*(reps-1));
        globales[10]="Varianza del tiempo para tomar bicicleta: ";
        System.out.println(globales[10]+sTET);
        globales[10]+=","+sTET;
        TED=STED/reps;
        globales[11]="Tiempo de espera promedio para dejar bicicleta: ";
        System.out.println(globales[11]+TED+" minutos");
        globales[11]+=","+TED;
        sTED=STED2/(reps-1)-(STED*STED)/(reps*(reps-1));
        globales[12]="Varianza del tiempo para dejar bicicleta: ";
        System.out.println(globales[12]+sTED);
        globales[12]+=","+sTED;
        TE=STE/reps;
        globales[13]="Tiempo de espera promedio: ";
        System.out.println(globales[13]+TE+" minutos");
        globales[13]+=","+TE;
        sTE=STE2/(reps-1)-(STE*STE)/(reps*(reps-1));
        globales[14]="Varianza del tiempo de espera: ";
        System.out.println(globales[14]+sTE);
        globales[14]+=","+sTE;
        QV=SQV/reps;
        globales[15]="Fracc. promedio de tiempo con estaciones vacías: ";
        System.out.println(globales[15]+QV);
        globales[15]+=","+QV;
        sQV=SQV2/(reps-1)-(SQV*SQV)/(reps*(reps-1));
        globales[16]="Varianza de la fracc. de tiempo con estaciones vacías: ";
        System.out.println(globales[16]+sQV);
        globales[16]+=","+sQV;
        QLl=SQLl/reps;
        globales[17]="Fracc. promedio de tiempo con estaciones llenas: ";
        System.out.println(globales[17]+QLl);
        globales[17]+=","+QLl;
        sQLl=SQLl2/(reps-1)-(SQLl*SQLl)/(reps*(reps-1));
        globales[18]="Varianza de la fracc. de tiempo con estaciones llenas: ";
        System.out.println(globales[18]+sQLl);
        globales[18]+=","+sQLl;
        Inef=SInef/reps;
        globales[19]="Fracc. prom. de tiempo con estaciones llenas o vacías: ";
        System.out.println(globales[19]+Inef);
        globales[19]+=","+Inef;
        sInef=SInef/(reps-1)-(SInef*SInef)/(reps*(reps-1));
        globales[20]="Varianza de la fracc. de tiempo con estaciones llenas o "
                + "vacías: ";
        System.out.println(globales[20]+sInef);
        globales[20]+=","+sInef;
        
              
        try{
            w = new FileWriter(csv2);
            for (int m = 0; m < 21; m++)
                    w.append(globales[m]).append("\n");
        } catch (Exception e) {
           System.err.println("Error! "+e.getMessage());
        } finally {
           if (null!=w){
              try {
                 w.flush();
              } catch (IOException e) {
                 System.err.println("Error flushing file !! "+e.getMessage());
              }
              try {
                 w.close();
              } catch (IOException e) {
                 System.err.println("Error closing file !! "+e.getMessage());
              }
           }
        }
        for (int m = 0; m < nEst; m++) {
            estaciones[m].imprimeFinal();
        }
    }
    
}
