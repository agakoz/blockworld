package test.model;

import model.*;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;


import model.exceptions.BadLocationException;

/**
 * 
 * @author paco
 *
 */

public class Location_P2Test {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

	
    static World mars, mercury;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
        mars = new World(2, 100, "Mars");
        mercury = new World(3,2,"Mercury"); 
	}

 
    
    /* 
     * Comprueba que para check, las localizaciones en los límites del mundo son válidas
     */
    @Test
    public final void testCheckLimits() throws RuntimeException
    {
    	World world = new World(3,7,"Mercury");
    	
    	Location loc1 = new Location(world,3.0,Location.UPPER_Y_VALUE, 3.0);
    	Location loc2 = new Location(world,-3.0,Location.UPPER_Y_VALUE,3.0);
    	Location loc3 = new Location(world, 3.0,Location.UPPER_Y_VALUE,-3.0);
    	Location loc4 = new Location(world,-3.0,Location.UPPER_Y_VALUE,-3.0);
    	assertTrue(Location.check(loc1));
    	assertTrue(Location.check(loc2.getWorld(),loc2.getX(),loc2.getY(),loc2.getZ()));
    	assertTrue(Location.check(loc3));
    	assertTrue(Location.check(loc4.getWorld(),loc4.getX(),loc4.getY(),loc4.getZ()));
    }
    
    /* 
     * Comprueba que para check, las localizaciones fuera de los límites del mundo no son válidas
     */
    @Test
    public final void testCheckOutOfLimits() throws RuntimeException
    {
    	mercury = new World(3,7,"Mercury");
    	
    	Location loc1 = new Location(mercury,3.0001,Location.UPPER_Y_VALUE, 3.0);
    	Location loc2 = new Location(mercury,-3.0,Location.SEA_LEVEL,3.00001);
    	Location loc3 = new Location(mercury, 3.0,Location.UPPER_Y_VALUE,-3.00001);
    	Location loc4 = new Location(mercury,-3.00001,Location.SEA_LEVEL,-3.0);
    	assertFalse(Location.check(loc1));
       	assertFalse(Location.check(loc2.getWorld(),loc2.getX(),loc2.getY(),loc2.getZ()));
    	assertFalse(Location.check(loc3));
       	assertFalse(Location.check(loc4.getWorld(),loc4.getX(),loc4.getY(),loc4.getZ()));	
    }
    
    /* 
     * Comprueba que isFree es false, cuando las posiciones no están asociadas a un mundo.
     */
    @Test
    public final void testNullIsNotFree() {
    	Location loc; 
    	for (int x=-10; x<10; x++)
    		for (int z=-10; z<10; z++) {
    			loc = new Location(null,x,Location.UPPER_Y_VALUE,z);
    			assertFalse(loc.isFree());
    		}			
    }
    
    /*
     * Comprueba isFree en un mundo. Todas las posiciones con bloques  debe
     * devolver false.
     */   
    @Test
    public final void testIsFreeFalse() {
    	
    	
    	Location loc= null; 
    	/* mercury: Hay bloques en
    	 *  X      Y     Z
    	 * ---    ---   ---
    	 *  0   [0..70]	 0
    	 *  0   [0..70]	 1
    	 *  1   [0..70]	 0
    	 *  1   [0..69]	 1
    	 *  Player en (0,71,0)
    	 */
    	//System.out.println(mercury.getPlayer().getLocation().toString());
    	for (int x=0; x<2; x++)
    		for (int z=0; z<2; z++) 
    			for (int y=0; y<=70; y++) {
    				loc = new Location(mercury,x,y,z);
    					if ( (x!=1) || (y!=70 ) || (z!=1) ) //No hay bloque
    						assertFalse("No Libre "+loc.toString(), loc.isFree());     			    				
    			}			
    }
 
    /*
     * Comprueba isFree en un mundo. Donde está player debe
     * devolver false.
     */   
    @Test
    public final void testIsFreeFalsePlayer() {
    	
    	
    	Location loc= null; 
    	/* mercury: Hay bloques en
    	 *  X      Y     Z
    	 * ---    ---   ---
    	 *  0   [0..70]	 0
    	 *  0   [0..70]	 1
    	 *  1   [0..70]	 0
    	 *  1   [0..69]	 1
    	 *  Player en (0,71,0)
    	 */
    	loc = new Location(mercury,0,71,0);
    	assertFalse("No Libre "+loc.toString(), loc.isFree());     			    				
    }
    
    /*
     * Comprueba isFree en un mundo. Todas las posiciones vacías deben devolver true.
     */   
    @Test
    public final void testIsFreeTrue() {
    	
    	Location loc= null; 
    	/* mercury: Hay bloques en
    	 *  X      Y     Z
    	 * ---    ---   ---
    	 *  0   [0..70]	 0
    	 *  0   [0..70]	 1
    	 *  1   [0..70]	 0
    	 *  1   [0..69]	 1
    	 *  Player en (0,71,0)
    	 */
    	//System.out.println(mercury.getPlayer().getLocation().toString());
    	for (int x=0; x<2; x++)
    		for (int z=0; z<2; z++) 
    			for (int y=71; y<=Location.UPPER_Y_VALUE; y++) {
    				loc = new Location(mercury,x,y,z);
    					if ( (x!=0) || (y!=71) || (z!=0) ) // No está Player
    						assertTrue("Libre "+loc.toString(),loc.isFree());    			    				
    			}			
    	loc = new Location(mercury,1,70,1);
		assertTrue("Libre "+loc.toString(),loc.isFree());    			    				
    }
 
    /*
     * Prueba método below() para y [1..UPPER_VALUE] con mundo. 
     * Para y=0 si no hay mundo
     */
    @Test
    public final void testBelow() {
    	World world;
    	Location loc,locBelow;
    	for (int x=0; x<2; x++)
    		for (int z=0; z<2; z++) 
    			for (int y=0; y<=Location.UPPER_Y_VALUE; y++) {
    				world = (y!=0 ? mercury : null);
    				loc = new Location(world,x,y,z);
    				locBelow = new Location(world,x,y-1,z);				
    				try {
						assertEquals("below = "+locBelow.toString(), locBelow, loc.below());
					} catch (BadLocationException e) {
						fail("La excepción "+e.toString()+" no debió producirse");
					}  				
    			}			
    }
    
    /*
     * Prueba método below() con lanzamiento de excepción BadLocationException 
     * para todos x,z e y=0 con mundo
     */
    @Test 
    public final void testBelowBadLocationException() {
    	Location loc,locBelow, locResult=null;
    	for (int x=0; x<2; x++)
    		for (int z=0; z<2; z++) {
    				loc = new Location(mercury,x,0,z);
    				locBelow = new Location(mercury,x,-1,z);
    				//System.out.println(locBelow.toString());
    				try {
    					locResult = loc.below();
						fail ("Error aquí nunca debió llegar");
					} catch (BadLocationException e) {
						assertEquals("below = "+locBelow.toString(), null, locResult);
					} catch (Exception f) {
						fail ("Error, la excepción "+f.toString()+" nunca tuvo que ocurrir");
					}
    		}			
    }
    
    /*
     * Prueba método above() para y [0..UPPER_Y_VALUE-1] con mundo. 
     * Para y=UPPER_Y_VALUE sin mundo
     */
    @Test
    public final void testAbove() {
    	World world;
    	Location loc,locAbove;
    	for (int x=0; x<2; x++)
    		for (int z=0; z<2; z++) 
    			for (int y=0; y<=Location.UPPER_Y_VALUE; y++) {
    				world = (y!=Location.UPPER_Y_VALUE ? mercury : null);
    				loc = new Location(world,x,y,z);
    				locAbove = new Location(world,x,y+1,z);				
    				try {
						assertEquals("above = "+locAbove.toString(), locAbove, loc.above());
					} catch (BadLocationException e) {
						fail("La excepción "+e.toString()+" no debió producirse");
					}  				
    			}			
    }
    
    /*
     * Prueba método above() con lanzamiento de excepción BadLocationException 
     * para todos x,z e y=UPPER_Y_VALUE con mundo
     */
    @Test 
    public final void testAboveBadLocationException() {
    	Location loc,locAbove, locResult=null;
    	for (int x=0; x<2; x++)
    		for (int z=0; z<2; z++) {
    				loc = new Location(mercury,x,Location.UPPER_Y_VALUE,z);
    				locAbove = new Location(mercury,x,Location.UPPER_Y_VALUE+1,z);
    				//System.out.println(locAbove.toString());
    				try {
    					locResult = loc.above();
						fail ("Error aquí nunca debió llegar");
					} catch (BadLocationException e) {
						assertEquals("below = "+locAbove.toString(), null, locResult);
					} catch (Exception f) {
						fail ("Error, la excepción "+f.toString()+" nunca tuvo que ocurrir");
					}
    		}			
    }
    
    /*
     * Prueba de las 26 posiciones adyacentes totales posibles.
     */
    @Test
    public final void testGetNeighborhoodIn() {
    	
    	Location center = new Location(mars, 20.0,150.0, -20.0);
    	Location vecino;
    	Set<Location> adyacentes = center.getNeighborhood(); 
    	
    	for (int x=19; x<=21; x++) 
    		for (int y=149; y<=151; y++)
    			for (int z=-21; z<=-19; z++) {
    				vecino = new Location (mars,x,y,z);
    				if (!vecino.equals(center))
    					assertTrue ("Adyacente "+vecino.toString(), adyacentes.contains(vecino));
    			}
    				
    }
    
    /*
     * Prueba que las posiciones no vecinas no estén incluídas en el vecindario.
     */
    @Test
    public final void testGetNeighborhoodOut() {
    	
    	Location center = new Location(mars, 20.0,150.0, -20.0);
    	Location vecino;
    	Set<Location> adyacentes = center.getNeighborhood(); 
    	
    	for (int x=-24; x<=25; x++) 
    		for (int y=0; y<=Location.UPPER_Y_VALUE; y++)
    			for (int z=-24; z<=25; z++) {
    				vecino = new Location (mars,x,y,z);
    				if ((x<19) || (x>21) || (y<149) || (y>151) || (z<-21) || (z>-19))
    					assertFalse ("No adyacente "+vecino.toString(), adyacentes.contains(vecino));
    			}
    				
    }
    
    /*
     * Prueba de las posiciones adyacentes en un mundo de size 2 con posiciones adyacentes fuera
     * del mundo (Mercury).
     */
    @Test
    public final void testGetNeighborhoodInMercury() {
    	
    	Location center = new Location(mercury, 0.0, Location.UPPER_Y_VALUE, 0.0);
    	Location vecino;
    	Set<Location> adyacentes = center.getNeighborhood(); 
    	
    	for (int x=0; x<=1; x++) 
    		for (int y=(int)Location.UPPER_Y_VALUE-1; y<=Location.UPPER_Y_VALUE; y++)
    			for (int z=0; z<=1; z++) {
    				vecino = new Location (mercury,x,y,z);
    				if (!vecino.equals(center))
    					assertTrue ("Adyacente en mercury"+vecino.toString(), adyacentes.contains(vecino));
    			}
    				
    }
    
    /*
     * Prueba de las posiciones NO adyacentes en un mundo de size 2 con posiciones adyacentes fuera
     * del mundo (Mercury).
     */
    @Test
    public final void testGetNeighborhoodOutMercury() {
    	
    	Location center = new Location(mercury, 0.0, Location.UPPER_Y_VALUE, 0.0);
    	Location vecino;
    	Set<Location> adyacentes = center.getNeighborhood(); 
    	
    	for (int x=-1; x<=1; x++) 
    		for (int y=0; y<=Location.UPPER_Y_VALUE; y++)
    			for (int z=-1; z<=1; z++) {
    				vecino = new Location (mercury,x,y,z);
    				if ((x<0) || (y<Location.UPPER_Y_VALUE-1) || (z<0)) 
    					assertFalse ("No adyacente en mercury"+vecino.toString(), adyacentes.contains(vecino));
    			}
    				
    }
    
    /*
     * Prueba de las posiciones adyacentes en posiciones que no pertenecen a un mundo.
     */
    @Test
    public final void testGetNeighborhoodInNullWorld() {
    	
    	Location center = new Location(null, 0.0, Location.UPPER_Y_VALUE, 0.0);
    	Location vecino;
    	Set<Location> adyacentes = center.getNeighborhood(); 
    	
    	for (int x=-1; x<=1; x++) 
    		for (int y=(int)Location.UPPER_Y_VALUE-1; y<=Location.UPPER_Y_VALUE+1; y++)
    			for (int z=-1; z<=1; z++) {
    				vecino = new Location (null,x,y,z);
    				if (!vecino.equals(center)) 
    					assertTrue ("Adyacente mundo null"+vecino.toString(), adyacentes.contains(vecino));
    			}
    				
    }

    /*
     * Prueba de las posiciones NO adyacentes en posiciones que no pertenecen a un mundo.
     */
    @Test
    public final void testGetNeighborhoodOutNullWorld() {
    	
    	Location center = new Location(null, 0.0, Location.UPPER_Y_VALUE, 0.0);
    	Location vecino;
    	Set<Location> adyacentes = center.getNeighborhood(); 
    	
    	for (int x=-2; x<=2; x++) 
    		for (int y=0; y<=Location.UPPER_Y_VALUE; y++)
    			for (int z=-2; z<=2; z++) {
    				vecino = new Location (null,x,y,z);
    				if ((x<-1) || (x>1) || (y<Location.UPPER_Y_VALUE-1) || (y>Location.UPPER_Y_VALUE+1) || (z<-1) || (z>1) )
    					assertFalse ("No adyacente mundo null"+vecino.toString(), adyacentes.contains(vecino));
    			}
    				
    }

}
