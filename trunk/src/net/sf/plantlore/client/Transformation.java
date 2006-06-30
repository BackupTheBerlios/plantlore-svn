/*
 * Transformation.java
 *
 * Created on 4. červen 2006, 0:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

import org.apache.log4j.Logger;

/**
 *
 * Zdroj: 
 * Converting between grid eastings and northings and ellipsoidal latititude and longitude. http://www.gps.gov.uk/guidec.asp.
 * Hrdina, Z.: Přepočet z S-JTSK do WGS-84. 2002. http://gpsweb.cz/JTSK-WGS.htm.
 * Hrdina, Z.: Transformace souřadnic ze systému WGS-84 do systému S-JTSK. Praha: ČVUT, 1997. http://www.geospeleos.com/Mapovani/WGS84toSJTSK/WGS_JTSK.pdf. 
 * Transformace souradnic WGS84<-->-JTSK<-->S42 http://astro.mff.cuni.cz/mira/sh/sh.php  
 *
 * @author Lada Oberreiterova
 * 
 * 
 * U WGS-84 se bude zadavat a zobrazovat geodeticke souradnice:
 *    latitude: 50.4576°
 * 	  longitude: 14.3986°
 * 	  altitude: 289.15 m
 *
 * U S42 a S-JTSK se budou zadavat a zobrazovat souradnice v ortogonálním systému: 
 * Pro S-JSTK se rozlisuji kvadranty - CR spada do 3.kvadrantu (X-ova souradnice bude vychazet zaporne)
 * Pro S-42 se rozlisuji pasy - CR spada do 3.pasu a část Karpat na Moravě do 4.pasu
 * 		Y= 1002007 m 
 * 		X= 738791 m.
 * 		Z=  244 m
 */
public class Transformation {
    
    private Logger logger;    
    
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double latitudeRAD;
    private Double longitudeRAD;
    private Double x;
    private Double y;
    private Double z;
    private String coordinateSystem;
    
    public static final String WGS84 = "WGS84";
    public static final String SJTSK = "S-JTSK";
    public static final String S42 = "S-42";
    
    //A = velka poloosa zplosteneho elipsoidu
    //F = zplosteni elipsoidu
    public static final Double ELIPSOID_WGS84_A = 6378137.0; //6377397,155
    public static final Double ELIPSOID_WGS84_F = 298.257223563;
    public static final Double ELIPSOID_BESSEL_A = 6377397.15508;
    public static final Double ELIPSOID_BESSEL_F = 299.152812853;    
    public static final Double ELIPSOID_KRAJOVSKIJ_A = 6378245.0;
    public static final Double ELIPSOID_KRAJOVSKIJ_F = 298.3;    
    
    /**
     * 
     * Creates a new instance of Transformation 
     * 
     * 
     * @param latitude
     * @param longitude
     * @param altitude
     * @param coordinateSystem
     */
    public Transformation() {
        
        logger = Logger.getLogger(this.getClass().getPackage().getName());	                
        
    }
    
    
     public static void main(String[] args) {
            Transformation tr = new Transformation();
            System.out.println("********************WGS84 --> S-JTSK**************************");
            Double[] coordinate_SJTSK = tr.transform_WGS84_to_SJTSK(50.4576163694, 14.3986, 289.155);
            System.out.println("******************** S-JTSK --> WGS84 **************************");
            Double[] coordinate_WGS84_SJTSK = tr.transform_SJTSK_to_WGS84(coordinate_SJTSK[0], coordinate_SJTSK[1], coordinate_SJTSK[2]);
            System.out.println("********************WGS84 --> S-42**************************");
            Double[] coordinate_S42 = tr.transform_WGS84_to_S42(50.4576163694, 14.3986, 289.155);
            System.out.println("******************** S-42 --> WGS84 **************************");                       
            Double[] coordinate_WGS84_S42 = tr.transform_S42_to_WGS84(coordinate_S42[0], coordinate_S42[1], coordinate_S42[2]);            
     }
         
    //**********************************************************************************************//
    //**********************************  WGS-84  <---> S-JTSK ************************************//
    //*********************************************************************************************//
    /**
     * Helmertova transformace (podmínka "min max") 
     * 
     *  WGS-84 používá elipsoid WGS-84, kdežto S-JTSK používá elipsoid Basseův (Elipsoidy se s různou přesností přibližují 
     * zobecněnému fyzikálnímu tvaru Země - geoidu). 
     *
     *  Transformace z WGS84 do S-JTSK má dvě části:
     *  1. transformace elipsoidu WGS84 na Besselův elipsoid
     *  2. transformace z Besselova elipsoidu do kartézského souř. systému S-JTSK 
     *
     *  vraci JTSK souřadnice pro zadanou severní šířku a východní délku - změřenou GPS v souřadném systému elipsoidu WGS84
     * 
     */
    
    public Double[] transform_WGS84_to_SJTSK(Double latitude, Double longitude, Double altitude) {
        
        //Transformace elipsoidu WGS84 na Besselův elipsoid.
        //WGS84 - transform latitude and longitude from degree to radiant format
        Double[] geodeticCoordinateRAD_WGS = degreeToRadiant(latitude, longitude);   
        System.out.println("from " + latitude + " toRadian: " + geodeticCoordinateRAD_WGS[0]);
        System.out.println("from " + longitude + " toRadian: " + geodeticCoordinateRAD_WGS[1] + "\n"); 
        //WGS84 - vypocet pravouhlych souradnic z geodetickych souradnic
        Double[] cartesianCoordinate_WGS = WGS84_BLH_xyz(geodeticCoordinateRAD_WGS[0], geodeticCoordinateRAD_WGS[1], altitude); 
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS[0]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS[1]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS[2] + "\n");
        //transformace prostorových pravoúhlých souřadnic do systému S-JTSK
        Double[] cartesianCoordinate_SJTSK = transformation_WGS84_SJTSK_xyz_xyz(cartesianCoordinate_WGS[0], cartesianCoordinate_WGS[1], cartesianCoordinate_WGS[2]);
        System.out.println("SJTSK - provouhle souradnice: " + cartesianCoordinate_SJTSK[0]);
        System.out.println("SJTSK - provouhle souradnice: " + cartesianCoordinate_SJTSK[1]);
        System.out.println("SJTSK - provouhle souradnice: " + cartesianCoordinate_SJTSK[2] + "\n");
        //S-JTSK - vypocet geodetickych souradnic z pravouhlych souradnic
        Double[] geodeticCoordinateRAD_SJTSK = bessel_xyz_BLH(cartesianCoordinate_SJTSK[0], cartesianCoordinate_SJTSK[1], cartesianCoordinate_SJTSK[2]);      
        System.out.println("SJTSK - geodeticke souradnice RAD: " + geodeticCoordinateRAD_SJTSK[0]);
        System.out.println("SJTSK - geodeticke souradnice RAD: " + geodeticCoordinateRAD_SJTSK[1]);
        System.out.println("SJTSK - geodeticke souradnice altitude: " + geodeticCoordinateRAD_SJTSK[2] + "\n");        
        //S-JTSK - transform latitude and longitude from radiant to degree format
        Double[] latLong = radiantToDegree(geodeticCoordinateRAD_SJTSK[0], geodeticCoordinateRAD_SJTSK[1]);
        System.out.println("SJTSK - geodeticke souradnice: " + latLong[0]);
        System.out.println("SJTSK - geodeticke souradnice: " + latLong[1]);
        System.out.println("SJTSK - geodeticke souradnice altitude: " + geodeticCoordinateRAD_SJTSK[2] + "\n");        
         
        Double[] planarCoordinate = krovak_BLH_xy(latLong[0], latLong[1], geodeticCoordinateRAD_SJTSK[2]);       
        System.out.println("SJTSK - planar souradnice (X): " + planarCoordinate[0]);
        System.out.println("SJTSK - planar souradnice (Y): " + planarCoordinate[1]);
        System.out.println("SJTSK (Z): " + planarCoordinate[2] + "\n");
        
        return planarCoordinate;
    }
    
    public Double[] transform_SJTSK_to_WGS84(Double x, Double y, Double z) {
                 
        //Prepocet z S-JTSK  ... uz dostanu hodnoty v radianech
        Double[] geodeticCoordinate = kovak_xy_BLH(x, y, z);
        System.out.println("SJTSK - geodeticke souradnice: " + geodeticCoordinate[0]);
        System.out.println("SJTSK - geodeticke souradnice: " + geodeticCoordinate[1]);
        System.out.println("SJTSK - geodeticke souradnice altitude: " + geodeticCoordinate[2] + "\n");                      
        //Vypocet pravouhlych souradnic z geodetickych souradnic pro elipsoid Bessel
        Double[] cartesianCoordinate = bessel_BLH_xyz(geodeticCoordinate[0], geodeticCoordinate[1], geodeticCoordinate[2]);
        System.out.println("SJTSK - provouhle souradnice: " + cartesianCoordinate[0]);
        System.out.println("SJTSK - provouhle souradnice: " + cartesianCoordinate[1]);
        System.out.println("SJTSK - provouhle souradnice: " + cartesianCoordinate[2] + "\n");
        //Transformace pravouhlych souradnic z S-JTSK do WGS84
        Double[] cartesianCoordinate_WGS84 = transformation_SJTSK_WGS84_xyz_xyz(cartesianCoordinate[0], cartesianCoordinate[1], cartesianCoordinate[2]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS84[0]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS84[1]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS84[2] + "\n");
        //Vypocet geodetickych souradnic z pravouhlych souradnic pro elipsoid WGS84
        Double[] geodeticCoordinateRAD_WGS84 = WGS84_xyz_BLH(cartesianCoordinate_WGS84[0], cartesianCoordinate_WGS84[1], cartesianCoordinate_WGS84[2]);
        System.out.println("WGS84 - geodeticke souradnice: " + geodeticCoordinateRAD_WGS84[0]);
        System.out.println("WGS84 - geodeticke souradnice: " + geodeticCoordinateRAD_WGS84[1]);
        System.out.println("WGS84 - geodeticke souradnice: " + geodeticCoordinateRAD_WGS84[2] + "\n");
        //Transformation latitude and longitude from radiant to degree 
        Double[] latLong = radiantToDegree(geodeticCoordinateRAD_WGS84[0], geodeticCoordinateRAD_WGS84[1]);
        Double[] geodeticCoordinate_WGS84 = new Double[3];
        geodeticCoordinate_WGS84[0] = latLong[0];
        geodeticCoordinate_WGS84[1] = latLong[1];
        geodeticCoordinate_WGS84[2] = geodeticCoordinateRAD_WGS84[2];
        
        System.out.println("WGS84 latitude: "+ geodeticCoordinate_WGS84[0]);
        System.out.println("WGS84 longitude: " + geodeticCoordinate_WGS84[1]);
        System.out.println("WGS84 altitude: " + geodeticCoordinate_WGS84[2] + "\n");
        
        return geodeticCoordinate_WGS84;
    }      
    
    //**********************************************************************************************//
    //**********************************  WGS-84  <---> S-42 ************************************//
    //*********************************************************************************************//
    
    public Double[] transform_WGS84_to_S42(Double latitude, Double longitude, Double altitude) {
                
        //WGS84 - transform latitude and longitude from degree to radiant format
        Double[] geodeticCoordinateRAD_WGS = degreeToRadiant(latitude, longitude);   
        System.out.println("from " + latitude + " toRadian: " + geodeticCoordinateRAD_WGS[0]);
        System.out.println("from " + longitude + " toRadian: " + geodeticCoordinateRAD_WGS[1] + "\n"); 
        //WGS84 - vypocet pravouhlych souradnic z geodetickych souradnic
        Double[] cartesianCoordinate_WGS = WGS84_BLH_xyz(geodeticCoordinateRAD_WGS[0], geodeticCoordinateRAD_WGS[1], altitude); 
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS[0]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS[1]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS[2] + "\n");
        //transformace prostorových pravoúhlých souřadnic do systému S-42
        Double[] cartesianCoordinate_S42 = this.transformation_WGS84_S42_xyz_xyz(cartesianCoordinate_WGS[0], cartesianCoordinate_WGS[1], cartesianCoordinate_WGS[2]);
        System.out.println("S42 - provouhle souradnice: " + cartesianCoordinate_S42[0]);
        System.out.println("S42 - provouhle souradnice: " + cartesianCoordinate_S42[1]);
        System.out.println("S42 - provouhle souradnice: " + cartesianCoordinate_S42[2] + "\n");
        //S-42 - vypocet geodetickych souradnic z pravouhlych souradnic
        Double[] geodeticCoordinateRAD_S42 = this.krajovskij_xyz_BLH(cartesianCoordinate_S42[0], cartesianCoordinate_S42[1], cartesianCoordinate_S42[2]);      
        System.out.println("S42 - geodeticke souradnice RAD: " + geodeticCoordinateRAD_S42[0]);
        System.out.println("S42 - geodeticke souradnice RAD: " + geodeticCoordinateRAD_S42[1]);
        System.out.println("S42 - geodeticke souradnice altitude: " + geodeticCoordinateRAD_S42[2] + "\n");        
        //S-42 - transform latitude and longitude from radiant to degree format
        Double[] latLong = radiantToDegree(geodeticCoordinateRAD_S42[0], geodeticCoordinateRAD_S42[1]);
        System.out.println("S42 - geodeticke souradnice: " + latLong[0]);
        System.out.println("S42 - geodeticke souradnice: " + latLong[1]);
        System.out.println("S42 - geodeticke souradnice altitude: " + geodeticCoordinateRAD_S42[2] + "\n");        
                 
        Double[] coordinate = transformatin_S42_BLH_xy(geodeticCoordinateRAD_S42[0], geodeticCoordinateRAD_S42[1]);               
        System.out.println("S42 - planar souradnice (X): " + coordinate[0]);
        System.out.println("S42 - planar souradnice (Y): " + coordinate[1]);
        System.out.println("S42 (Z): " + geodeticCoordinateRAD_S42[2] + "\n");
        
        Double[] planarCoordinate = new Double[3];
        planarCoordinate[0] = coordinate[0];
        planarCoordinate[1] = coordinate[1];
        planarCoordinate[2] = geodeticCoordinateRAD_S42[2];
               
        return planarCoordinate;
    }
      
    
     public Double[] transform_S42_to_WGS84(Double x, Double y, Double z) {
         
         //Prepocet z S-42 do WGS-84 ... uz dostanu hodnoty v radianech
         //!!!!!FIXME - v teto funkci je chyba
        //Double[] geodeticCoordinate = transformation_S42_xy_BLH(x, y, z);
    	 Double[] geodeticCoordinate = S42_xy_BLH(x, y, z);
        System.out.println("S42 - geodeticke souradnice: " + geodeticCoordinate[0]);
        System.out.println("S42 - geodeticke souradnice: " + geodeticCoordinate[1]);
        System.out.println("S42 - geodeticke souradnice altitude: " + geodeticCoordinate[2] + "\n");                      
        //Vypocet pravouhlych souradnic z geodetickych souradnic pro elipsoid Bessel
        Double[] cartesianCoordinate = krajovskij_BLH_xyz(geodeticCoordinate[0], geodeticCoordinate[1], geodeticCoordinate[2]);
        //Double[] cartesianCoordinate = krajovskij_BLH_xyz(0.8806574639397869, 0.25133270679766656, geodeticCoordinate[2]);
        System.out.println("S42 - provouhle souradnice: " + cartesianCoordinate[0]);
        System.out.println("S42 - provouhle souradnice: " + cartesianCoordinate[1]);
        System.out.println("S42 - provouhle souradnice: " + cartesianCoordinate[2] + "\n");
        //Transformace pravouhlych souradnic z S-42 do WGS84
        Double[] cartesianCoordinate_WGS84 = transformation_S42_WGS84_xyz_xyz(cartesianCoordinate[0], cartesianCoordinate[1], cartesianCoordinate[2]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS84[0]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS84[1]);
        System.out.println("WGS84 - pravouhle souradnice: " + cartesianCoordinate_WGS84[2] + "\n");
        //Vypocet geodetickych souradnic z pravouhlych souradnic pro elipsoid WGS84
        Double[] geodeticCoordinateRAD_WGS84 = WGS84_xyz_BLH(cartesianCoordinate_WGS84[0], cartesianCoordinate_WGS84[1], cartesianCoordinate_WGS84[2]);
        System.out.println("WGS84 - geodeticke souradnice: " + geodeticCoordinateRAD_WGS84[0]);
        System.out.println("WGS84 - geodeticke souradnice: " + geodeticCoordinateRAD_WGS84[1]);
        System.out.println("WGS84 - geodeticke souradnice: " + geodeticCoordinateRAD_WGS84[2] + "\n");
        //Transformation latitude and longitude from radiant to degree 
        Double[] latLong = radiantToDegree(geodeticCoordinateRAD_WGS84[0], geodeticCoordinateRAD_WGS84[1]);
        Double[] geodeticCoordinate_WGS84 = new Double[3];
        geodeticCoordinate_WGS84[0] = latLong[0];
        geodeticCoordinate_WGS84[1] = latLong[1];
        geodeticCoordinate_WGS84[2] = geodeticCoordinateRAD_WGS84[2];
        
        System.out.println("WGS84 latitude: "+ geodeticCoordinate_WGS84[0]);
        System.out.println("WGS84 longitude: " + geodeticCoordinate_WGS84[1]);
        System.out.println("WGS84 altitude: " + geodeticCoordinate_WGS84[2] + "\n");
        
        return geodeticCoordinate_WGS84;
     }
        
    //*******************************************************************************************//
    //*********************** Planar coordinates ***********************************************//
    //******************************************************************************************//    
    
    /**    
     *   Prepocet z S-JTSk do WGS-84
     *   (Krovak projection (used in the S-JTSK coordinate system))
     */    
    public Double[] kovak_xy_BLH(Double x, Double y, Double altitude) {
          
        //konstanty prouzite pro transformaci
        double a     = 6377397.15508;   //polomer Gausseovy koule (6380065.5402 metrů)
        double e     = 0.081696831215303;
        double n     = 0.97992470462083;
        double rho_0 = 12310230.12797036;
        double sinUQ = 0.863499969506341; //kulova sirka odpovidajici zemepisne sirce 49°30`
        double cosUQ = 0.504348889819882;
        double sinVQ = 0.420215144586493;
        double cosVQ = 0.907424504992097;
        double alfa  = 1.000597498371542; //koustanta pro vypocet sfericke sirky a delky
        double k_2   = 1.003419163966575;  //koustanta pro vypocet sfericke sirky a delky             
        
        double ro = Math.sqrt(Math.pow(x,2)+ Math.pow(y,2));
        double epsilon = 2.* Math.atan(y/(ro+x));
        double d = epsilon/n; 
        double s = 2.* Math.atan(Math.exp(1./ n* Math.log(rho_0 / ro)))- Math.PI/2.;
        double sinS = Math.sin(s); 
        double cosS = Math.cos(s);
        double sinU = sinUQ * sinS - cosUQ * cosS * Math.cos(d); 
        double cosU = Math.sqrt(1.- Math.pow(sinU, 2));
        double sinDV = Math.sin(d) * cosS/cosU; 
        double cosDV = Math.sqrt(1.-Math.pow(sinDV,2));
        double sinV = sinVQ * cosDV - cosVQ * sinDV; 
        double cosV = cosVQ * cosDV + sinVQ * sinDV;
        double longitude = 2.* Math.atan(sinV / (1.+ cosV))/alfa;
        double t = Math.exp(2./alfa * Math.log((1.+ sinU)/cosU/k_2));
        double pom = (t-1.)/(t+1.);
        double sinB;
        do {
           sinB = pom;
           pom = t * Math.exp(e * Math.log((1.+ e * sinB)/(1. - e * sinB))); 
           pom = (pom-1)/(pom+1);
      } while (Math.abs(pom - sinB) > 1.e-15);
       double latitude = Math.atan(pom/Math.sqrt(1.- Math.pow(pom,2)));
  
      // this is an appropriate heigth correction                  

       Double[] geologicCoordinate = new Double[3];
       geologicCoordinate[0] = latitude;
       geologicCoordinate[1] = longitude;
       geologicCoordinate[2] = altitude;
       
       return geologicCoordinate;       
        
    }
    
    /**
     * Transformace souradnic ze systemu WGS-84 do systemu S-JTSK.
     *
     * vraci JTSK souřadnice pro zadanou Severní šířku a východní délku  
     * Zobrazeni zemepisnych souradnic na pravouhle:
     *  - zobrazeni Besselova elipsoidu  na Gausseovu kouli
     *  - vypocet kartografickych souradnic na Gaussove kouli s posunutym polem Q o souradnicich 48°15`, 42°30`.
     *  - konformni kuzelove zobrazeni s tecnym kuzelem na zakladni rovnobezce
     *  - dopocitani pravouhlych souradnic
     */
    public Double[] krovak_BLH_xy(Double latitude, Double longitude, Double altitude) {      
       
        //konstanty prouzite pro transformaci
        double a     = 6377397.15508;   //polomer Gausseovy koule (6380065.5402 metrů)
        double e     = 0.081696831215303;
        double n     = 0.97992470462083;
        double rho_0 = 12310230.12797036; //1298039,0046
        double sinUQ = 0.863499969506341; //kulova sirka odpovidajici zemepisne sirce 49°30`
        double cosUQ = 0.504348889819882;
        double sinVQ = 0.420215144586493;
        double cosVQ = 0.907424504992097;
        double alfa  = 1.000597498371542; //koustanta pro vypocet sfericke sirky a delky
        double k_2   = 1.00685001861538;  //koustanta pro vypocet sfericke sirky a delky
           
       Double[] coordinateRAD = degreeToRadiant(latitude, longitude);
       latitude = coordinateRAD[0]; 
       longitude = coordinateRAD[1];
       
       double sinB = Math.sin(latitude);
       double t = (1-e*sinB)/(1+e*sinB);
              t = Math.pow(1+sinB,2)/(1- Math.pow(sinB,2)) * Math.exp(e*Math.log(t));
              t = k_2 * Math.exp(alfa * Math.log(t));

       double sinU  = (t-1)/(t+1);
       double cosU  = Math.sqrt(1-Math.pow(sinU,2));
       double V = alfa * longitude;
       double sinV  = Math.sin(V);
       double cosV  = Math.cos(V);
       double cosDV = cosVQ * cosV + sinVQ * sinV;
       double sinDV = sinVQ * cosV - cosVQ * sinV;
       double sinS  = sinUQ*sinU + cosUQ*cosU*cosDV;
       double cosS  = Math.sqrt(1-sinS*sinS);
       double sinD  = sinDV * cosU/cosS;
       double cosD  = Math.sqrt(1-sinD*sinD);
       
       double eps = n * Math.atan(sinD/cosD);
       double rho = rho_0 * Math.exp(-n * Math.log((1+sinS)/cosS));

       Double[] planarCoordinate = new Double[3];
       planarCoordinate[0] = rho * Math.cos(eps); //x
       planarCoordinate[1] = rho * Math.sin(eps); //y
       planarCoordinate[2] = altitude;
       
       return planarCoordinate;       
    }        
 
    //*******************************************************************************************//
    //***************** S-42  Orthogonal coordinates --> geodetic coordinate*********************//
    //******************************************************************************************//   
    
    public Double[] S42_xy_BLH(Double x, Double y, Double z) {
    	//VYPOCET S-42 ortogonalni system --> S-42 geodeticky system

    	double A2 = x;
    	double B2 = y;
    	double C2 = 6378245;
    	double D2 = 0.0818133340169312;
    	double E2 = 500000+Math.rint(A2/1000000)*1000000;
    	double F2 = 0;
    	double G2 = 21+6*(Math.rint(A2/1000000)-4);
    	double H2 = G2*Math.PI/180;
    	double I2 = 0;
    	double J2 = I2*Math.PI/180;
    	double K2 = 1;
    	double L2 = A2-E2;
    	double M2 = B2-F2;
    	double N2 = (1-Math.sqrt(1-D2*D2))/(1+ Math.sqrt(1-D2*D2));
    	double O2 = C2*(J2*(1-D2*D2/4-3*Math.pow(D2,4)/64-5*Math.pow(D2,6)/256)-Math.sin(2*J2)*(3*D2*D2/8+3*Math.pow(D2,4)/32+45*Math.pow(D2,6)/1024)+Math.sin(4*J2)*(15*Math.pow(D2,4)/256+45*Math.pow(D2,6)/1024)-Math.sin(6*J2)*35*Math.pow(D2,6)/3072);
    	double P2 = O2+M2/K2;
    	double Q2 = P2/(C2*(1-D2*D2/4-3*Math.pow(D2,4)/64-5*Math.pow(D2,6)/256));
    	double R2 = Q2+Math.sin(2*Q2)*(3*N2/2-27*Math.pow(N2,3)/32)+Math.sin(4*Q2)*(21*N2*N2/16-55*Math.pow(N2,4)/32)+Math.sin(6*Q2)*151*Math.pow(N2,3)/96+Math.sin(8*Q2)*1097*Math.pow(N2,4)/512;
    	double S2 = D2*D2/(1-D2*D2);
    	double T2 = S2*Math.cos(R2)*Math.cos(R2);
    	double U2 = Math.tan(R2)*Math.tan(R2);
    	double V2 = C2/Math.sqrt(1-D2*D2*Math.sin(R2)*Math.sin(R2));
    	double W2 = C2*(1-D2*D2)/Math.sqrt(1-D2*D2*Math.sin(R2)*Math.sin(R2));
    	double X2 = L2/(V2*K2);
    	double Z2 = H2+(X2-(1+2*U2+T2)*Math.pow(X2,3)/6+(5-2*T2+28*U2-3*T2*T2+8*S2+24*U2*U2)*Math.pow(X2,5)/120)/Math.cos(R2);
    	double Y2 = R2-(V2*Math.tan(R2)/W2)*(X2*X2/2-(5+3*U2+10*T2-4*T2*T2-9*S2)*Math.pow(X2,4)/24+(61+90*U2+298*T2+45*U2*U2-252*S2-3*T2*T2)*Math.pow(X2,6)/720);
    	//latitude
    	double lambda = Z2*180/Math.PI;
    	//longitude
    	double fi = Y2 * 180/Math.PI; //prevod na stupne z radianu    	
    	
    	Double[] coordinate = new Double[3];
    	coordinate[0] = Math.toRadians(fi);
    	coordinate[1] = Math.toRadians(lambda);
    	coordinate[2] = z;
    	
    	return coordinate;
    }
    
    //*******************************************************************************************//
    //***************** S-42  Planar coordinates ***********************************************//
    //******************************************************************************************//    
 
    /**
     *  Mercator projection for the S42 coordinate system
     *
     */ 
    public Double[] transformatin_S42_BLH_xy(Double phi, Double lambda) {               
       
        // parameters of the Krasovskij ellipsoid
        double elipsoid_a = ELIPSOID_KRAJOVSKIJ_A; 
        double elipsoid_f = ELIPSOID_KRAJOVSKIJ_F;

        // parameters of the S-42 coordinate system (3rd belt)
       double no = 0;
       double eo = 3500000;
       double fo = 1;
       double phi0 = Math.toRadians(0);
       double lambda0 = Math.toRadians(15);
       double b = elipsoid_a - elipsoid_a/elipsoid_f;

       Double[] listEN = transverseMercator_BLH_XY(phi, lambda, no, eo, fo, phi0, lambda0, elipsoid_a, b);
      
       return listEN;
    }
    
    /**
     * Mercator projection for the S42 coordinate system
     * (the 3rd belt suitable for the Czech Republic)
     *
     */
    public Double[] transformation_S42_xy_BLH(Double e, Double n, Double altitude) {
             
        // parameters of the Krasovskij ellipsoid 
        double elipsoid_a = ELIPSOID_KRAJOVSKIJ_A; 
        double elipsoid_f = ELIPSOID_KRAJOVSKIJ_F;


        // parameters of the S-42 coordinate system (3rd belt)
       double no = 0;
       double eo = 3500000;
       double fo = 1;
       double phi0 = Math.toRadians(0);
       double lambda0 = Math.toRadians(15);
       double b = elipsoid_a - (elipsoid_a/elipsoid_f);
          
       Double[] listPhiLambda = mercator_XY_BLH(e, n, no, eo, fo, phi0, lambda0, elipsoid_a, b);      
       Double[] value = new Double[3];
       value[0] = listPhiLambda[0];
       value[1] = listPhiLambda[1];
       value[2] = altitude;          
       return value;
    }    
    
    /**
     *
     *
     */
    public Double[] transverseMercator_BLH_XY(double phi, double lambda, double no, double eo, double fo, double phi0, double lambda0, double elipsoid_a, double b) {
                
        double e2 = (Math.pow(elipsoid_a,2) - Math.pow(b, 2)) / Math.pow(elipsoid_a,2);
        double n = (elipsoid_a - b) / (elipsoid_a + b);
        double sinphi = Math.sin(phi);       
        double e2sinphi2 = 1 - e2 * Math.pow(sinphi, 2);
        double nu = elipsoid_a * fo / Math.sqrt(e2sinphi2);
        double rho = elipsoid_a * fo * (1 - e2) / Math.sqrt( Math.pow(e2sinphi2, 3));
        double eta2 = nu / rho - 1;
        
        double n2 = Math.pow(n,2);
        double n3 = Math.pow(n, 3);
        
        double m = b * fo *(
              (1 + n + 5/4*n2 + 5/4*n3) * (phi - phi0)
              - (3*n + 3*n2 + 21/8*n3) * Math.sin(phi - phi0) * Math.cos(phi + phi0)
              + (15/8*n2 + 15/8*n3) * Math.sin(2*(phi - phi0)) * Math.cos(2*(phi + phi0))
              - 35/24*n3 * Math.sin(3*(phi - phi0)) * Math.cos(3*(phi-phi0))
              );
        
        double cosphi = Math.cos(phi);
        double tanphi = Math.tan(phi);
     
        double i = m + no;
        double ii = nu/2 * sinphi * cosphi;
        double iii = nu/24 * sinphi* Math.pow(cosphi,3) * (5 - Math.pow(tanphi,2) + 9 * eta2);
        double iiia = nu/720 * sinphi * Math.pow(cosphi,5) * (61 - 58 * Math.pow(tanphi,2) + Math.pow(tanphi,4));
        double iv = nu * cosphi;
        double v = nu/6 * Math.pow(cosphi,3) * (nu/rho - Math.pow(tanphi,2));
        double vi = nu/120 * Math.pow(cosphi,5) * (5 - 18* Math.pow(tanphi,2) + Math.pow(tanphi,4) + 14 * eta2 - 58 * Math.pow(tanphi,2) * eta2);

        double llo = lambda - lambda0;
        n = i + ii * Math.pow(llo,2) + iii * Math.pow(llo,4) + iiia * Math.pow(llo,6);
        double e = eo + iv * llo + v * Math.pow(llo,3) + vi * Math.pow(llo,5);
        
        Double[] value = new Double[2];
        value[0] = e;
        value[1] = n;
        
        return value;
    }
    
    /**
     *
     *
     */
    public Double[] mercator_XY_BLH(double e, double n, double no, double eo, double fo, double phi0, double lambda0, double elipsoid_a, double b){
        
        double phid = (n-no) / (elipsoid_a * fo) + phi0;

        n = (elipsoid_a - b) / (elipsoid_a + b);
        double n2 = Math.pow(n,2);
        double n3 = Math.pow(n,3);
        double m = 0;
        
        int i1st = 0;
        do {
            if (i1st > 0) 
                phid = (n - no - m) / (elipsoid_a * fo) + phid;
            i1st++;

            m = b * fo *(
              (1 + n + 5/4*n2 + 5/4*n3) * (phid - phi0)
              - (3*n + 3*n2 + 21/8*n3) * Math.sin(phid - phi0) * Math.cos(phid + phi0)
              + (15/8*n2 + 15/8*n3) * Math.sin(2*(phid - phi0)) * Math.cos(2*(phid + phi0))
              - 35/24*n3 * Math.sin(3*(phid - phi0)) * Math.cos(3*(phid-phi0))
              );

          } while (Math.abs(n-no-m) > 1.e-4);

          double e2 = (Math.pow(elipsoid_a,2) - Math.pow(b, 2)) / Math.pow(elipsoid_a,2);
          double sinphi = Math.sin(phid);	// tady je zrejme phi        
          double e2sinphi2 = 1 - e2 * Math.pow(sinphi, 2);
          double nu = elipsoid_a * fo / Math.sqrt(e2sinphi2);
          double rho = elipsoid_a * fo * (1 - e2) / Math.sqrt( Math.pow(e2sinphi2, 3));
          double eta2 = nu / rho - 1;
          
          double secphid = 1./ Math.cos(phid);
          double tanphid = Math.tan(phid);         

          double vii = tanphid/(2 * rho * nu);
          double viii = tanphid/(24 * rho * Math.pow(nu,3)) * (5 + 3* Math.pow(tanphid,2) + eta2 - 9 * Math.pow(tanphid,2) * eta2);
          double ix = tanphid/(720 * rho * Math.pow(nu,5)) * (61 + 90 * Math.pow(tanphid,2) + 45 * Math.pow(tanphid,4));
          double x = secphid / nu;
          double xi = secphid/(6 * Math.pow(nu,3)) * (nu/rho + 2 * Math.pow(tanphid,2));
          double xii = secphid/(120 * Math.pow(nu,5)) * (5 + 28 * Math.pow(tanphid,2) + 24 * Math.pow(tanphid,4));
          double xiia = secphid/(5040* Math.pow(nu,7)) * (61 + 662 * Math.pow(tanphid,2) + 1320 * Math.pow(tanphid,4) + 720 * Math.pow(tanphid,6));

          double eeo = e - eo;
          double phi = phid - vii * Math.pow(eeo,2) + viii * Math.pow(eeo,4) - ix * Math.pow(eeo,6);
          double lambda = lambda0 + x * eeo - xi * Math.pow(eeo,3) + xii * Math.pow(eeo,5) - xiia * Math.pow(eeo,7);

          Double[] value = new Double[2];
          value[0] = phi;
          value[1] = lambda;                   
          
          return value;
    }
    
    //****************************************************************************************//
    //******** Convergetion between geological coordinate and cartesian coordinate ***********//
    //***************************************************************************************//
    
    /**
     *  Vypocet geodetickych souradnic z pravouhlych souradnic pro urcity elipsoid
     */
    public Double[] ellipsoid_xyz_BLH(Double x, Double y, Double z, Double elipsoid_a, Double elipsoid_f) {

          double a_b = elipsoid_f/(elipsoid_f - 1);      
          double p = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));      
          double e2 = 1 - Math.pow((1 - 1 /elipsoid_f), 2);      
          double th = Math.atan(z * a_b / p); 
          double sinth = Math.sin(th);
          double costh = Math.cos(th);          
          double t = (z + e2 * a_b * elipsoid_a * Math.pow(sinth,3)) / (p - e2 * elipsoid_a * Math.pow(costh,3));  
        
          Double[] geodeticCoordinate = new Double[3];
          //latitude
          geodeticCoordinate[0] = Math.atan(t);
          //longitude
          geodeticCoordinate[1] = 2 * Math.atan(y / (p + x));
          //altitude
          geodeticCoordinate[2] =  Math.sqrt(1 + Math.pow(t, 2)) * (p - elipsoid_a / Math.sqrt(1 + (1 - e2) * Math.pow(t, 2)));            
          
          return geodeticCoordinate;       
      }
    
    /**
     * Vypocet pravouhlych souradnic z geodetickych souradnic pro dany elipsoid
     * (phi = B, lambda = L )
     *
     * @param  zeměpisná geodetická šířka φ je úhel, který svírá rovina rovníku s normálou k ploše elipsoidu (kladná na sever)
     * @param zeměpisná geodetická délka λ je úhel, který svírá rovina místního poledníku s rovinou základního poledníku (kladná na východ),
     * @param elipsoidická výška H je vzdálenost od elipsoidu, měřená po normále (kladná vně elipsoidu)
     *
     */
    public Double[] ellipsoid_BLH_xyz(Double latitude, Double longitude, Double altitude, Double elipsoid_a, Double elipsoid_f) {
       
        //e = excentricita elipsoidu
        Double e2 = 1 - Math.pow(1 - 1 / elipsoid_f, 2);
        //rho = pricny polomer krivosti
        Double rho = elipsoid_a/Math.sqrt(1 - e2 * Math.pow(Math.sin(latitude),2));          
        //pravouhle souradnice
        Double[] cartesianCoordinate = new Double[3];
        cartesianCoordinate[0] = (rho + altitude) * Math.cos(latitude) * Math.cos(longitude); //x
        cartesianCoordinate[1] = (rho + altitude) * Math.cos(latitude) * Math.sin(longitude); //y
        cartesianCoordinate[2] = ((1-e2)*rho + altitude) * Math.sin(latitude);  //z
                
        return cartesianCoordinate;
    }
    
    /**
     * Vypocet pravouhlych souradnic z geodetickych souradnic pro elipsoid WGS84
     *     
     */
    public Double[] WGS84_BLH_xyz(Double latitude, Double longitude, Double altitude) {                
       
        Double[] cartesianCoordinate = ellipsoid_BLH_xyz(latitude, longitude, altitude, ELIPSOID_WGS84_A, ELIPSOID_WGS84_F);        
        return cartesianCoordinate;
    }
    
    /**
     * Vypocet geodetickych souradnic z pravouhlych souradnic pro elipsoid WGS84
     *
     */
    public Double[] WGS84_xyz_BLH(Double x, Double y, Double z) {        
     
        Double[] geodeticCoordinate = ellipsoid_xyz_BLH(x,y, z, ELIPSOID_WGS84_A, ELIPSOID_WGS84_F);
        return geodeticCoordinate;
      }        
    
    /**
     * Vypocet pravouhlych souradnic z geodetickych souradnic pro elipsoid Bessel
     *     
     */
    public Double[] bessel_BLH_xyz(Double latitude, Double longitude, Double altitude) {                
       
        Double[] cartesianCoordinate = ellipsoid_BLH_xyz(latitude, longitude, altitude, ELIPSOID_BESSEL_A, ELIPSOID_BESSEL_F);        
        return cartesianCoordinate;
    }
    
    /**
     * Vypocet geodetickych souradnic z pravouhlych souradnic pro elipsoid Bessel
     *
     */
    public Double[] bessel_xyz_BLH(Double x, Double y, Double z) {        
     
        Double[] geodeticCoordinate = ellipsoid_xyz_BLH(x,y, z, ELIPSOID_BESSEL_A, ELIPSOID_BESSEL_F);
        return geodeticCoordinate;
      }        
    
    /**
     * Vypocet pravouhlych souradnic z geodetickych souradnic pro elipsoid Krajovskij
     *     
     */
    public Double[] krajovskij_BLH_xyz(Double latitude, Double longitude, Double altitude) {                
       
        Double[] cartesianCoordinate = ellipsoid_BLH_xyz(latitude, longitude, altitude, ELIPSOID_KRAJOVSKIJ_A, ELIPSOID_KRAJOVSKIJ_F);        
        return cartesianCoordinate;
    }
    
    /**
     * Vypocet geodetickych souradnic z pravouhlych souradnic pro elipsoid Krajovskij
     *
     */
    public Double[] krajovskij_xyz_BLH(Double x, Double y, Double z) {        
     
        Double[] geodeticCoordinate = ellipsoid_xyz_BLH(x,y, z, ELIPSOID_KRAJOVSKIJ_A, ELIPSOID_KRAJOVSKIJ_F);
        return geodeticCoordinate;
      }        
    
   
    //*****************************************************************************************//
    //***************** Transaction of the cartesian coordinates ******************************//
    //****************************************************************************************//
    
    /**
     * Hrdina (1997)
     *
     * Transformace pravouhlych souradnic z WGS84 do S-JTSK
     */
    public Double[] transformation_WGS84_SJTSK_xyz_xyz(Double xs, Double ys, Double zs) {
        // koeficienty transformace ze systemu WGS-84 do systemu S-JTSK
        //posunuti
       double dx = -570.69; 
       double dy = -85.69; 
       double dz = -462.84; 
       //rotace
       double wx = 4.99821/3600 * Math.PI/180; 
       double wy = 1.58676/3600 * Math.PI/180; 
       double wz = 5.2611/3600 * Math.PI/180;
       //meritko
       double m  = -3.543e-6; 
       
       //Heltmert transform
        double xn = dx + (1+m)*(+   xs + wz*ys - wy*zs);
        double yn = dy + (1+m)*(-wz*xs +    ys + wx*zs);
        double zn = dz + (1+m)*(+wy*xs - wx*ys +    zs);
        
        //save
        Double[] cartesianCoordinateNew = new Double[3];
        cartesianCoordinateNew[0] = xn;
        cartesianCoordinateNew[1] = yn;
        cartesianCoordinateNew[2] = zn;
                
        return cartesianCoordinateNew;
    }
    
    /**
     * Hrdina (2002)
     *
     * Transformace pravouhlych souradnic z S-JTSK do WGS84 
     */
    public Double[] transformation_SJTSK_WGS84_xyz_xyz(Double xs, Double ys, Double zs) {
        // koeficienty transformace ze systemu S-JTSK do systemu WGS-84
        //posunuti
       double dx = 570.69; 
       double dy = 85.69; 
       double dz = 462.84; 
       //rotace
       double wx = -4.99821/3600 * Math.PI/180; 
       double wy = -1.58676/3600 * Math.PI/180; 
       double wz = -5.2611/3600 * Math.PI/180;
       //meritko
       double m  = 3.543e-6; 
       
       //Heltmert transform
        double xn = dx + (1+m)*(+   xs + wz*ys - wy*zs);
        double yn = dy + (1+m)*(-wz*xs +    ys + wx*zs);
        double zn = dz + (1+m)*(+wy*xs - wx*ys +    zs);
        
        //save
        Double[] cartesianCoordinateNew = new Double[3];
        cartesianCoordinateNew[0] = xn;
        cartesianCoordinateNew[1] = yn;
        cartesianCoordinateNew[2] = zn;
                
        return cartesianCoordinateNew;
    }
    
    /**
     *
     * Transformace pravouhlych souradnic z WGS84 do S42
     */
    public Double[] transformation_WGS84_S42_xyz_xyz(Double xs, Double ys, Double zs) {
        // koeficienty transformace ze systemu WGS84 do systemu S42
        //posunuti
       double dx = -23; 
       double dy = 124; 
       double dz = 84; 
       //rotace
       double wx = -0.13/3600 * Math.PI/180; 
       double wy = -0.25/3600 * Math.PI/180; 
       double wz = 0.02/3600 * Math.PI/180;
       //meritko
       double m  = -1.1e-6; 
       
       //Heltmert transform
        double xn = dx + (1+m)*(+   xs + wz*ys - wy*zs);
        double yn = dy + (1+m)*(-wz*xs +    ys + wx*zs);
        double zn = dz + (1+m)*(+wy*xs - wx*ys +    zs);
        
        //save
        Double[] cartesianCoordinateNew = new Double[3];
        cartesianCoordinateNew[0] = xn;
        cartesianCoordinateNew[1] = yn;
        cartesianCoordinateNew[2] = zn;
                
        return cartesianCoordinateNew;
    }
    
    /**     
     *
     * Transformace pravouhlych souradnic z S42 do WGS84 
     */
    public Double[] transformation_S42_WGS84_xyz_xyz(Double xs, Double ys, Double zs) {
        // koeficienty transformace ze systemu S42 do systemu WGS-84
        //posunuti
       double dx = 23; 
       double dy = -124; 
       double dz = -84; 
       //rotace
       double wx = 0.13/3600 * Math.PI/180; 
       double wy = 0.25/3600 * Math.PI/180; 
       double wz = -0.02/3600 * Math.PI/180;
       //meritko
       double m  = 1.1e-6; 
       
       //Heltmert transform
        double xn = dx + (1+m)*(+   xs + wz*ys - wy*zs);
        double yn = dy + (1+m)*(-wz*xs +    ys + wx*zs);
        double zn = dz + (1+m)*(+wy*xs - wx*ys +    zs);
        
        //save
        Double[] cartesianCoordinateNew = new Double[3];
        cartesianCoordinateNew[0] = xn;
        cartesianCoordinateNew[1] = yn;
        cartesianCoordinateNew[2] = zn;
                
        return cartesianCoordinateNew;
    }
    
    //**********************************************************************//
    //******************* Radiant vs. Degree *******************************//
    //**********************************************************************//
    
   /**
   * transform latitude and longitude from degree to radiant format
   * 
   * @param latitude
   * @param longitude
   */
  protected Double[] degreeToRadiant(double latitude, double longitude)
  {
      Double[] geodeticCoordinateRAD = new Double[2];
      geodeticCoordinateRAD[0] = Math.toRadians(latitude);
      geodeticCoordinateRAD[1] = Math.toRadians(longitude);
      
      //geodeticCoordinateRAD[0] = latitude;
      //geodeticCoordinateRAD[1] = longitude;
      
      return geodeticCoordinateRAD;
  }

  /**
   * transform latitude and longitude from radiant to degree format
   * 
   * @param latitude
   * @param longitude
   */
  protected Double[] radiantToDegree(double latitudeRAD, double longitudeRAD)
  {
    Double[] geodeticCoordinate = new Double[2];  
    geodeticCoordinate[0] = Math.toDegrees(latitudeRAD);
    geodeticCoordinate[1] = Math.toDegrees(longitudeRAD);    
    
    //geodeticCoordinate[0] = latitudeRAD;
    //geodeticCoordinate[1] = longitudeRAD;    
    
    return geodeticCoordinate;
  }
  
}