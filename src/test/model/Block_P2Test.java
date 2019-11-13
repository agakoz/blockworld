package test.model;

import static org.junit.Assert.*;

import model.*;
import org.junit.Test;

import model.exceptions.StackSizeException;
import model.exceptions.WrongMaterialException;

public class Block_P2Test {

	Material bedrock=Material.BEDROCK;
	Material chest=Material.CHEST;
	Material grass=Material.GRASS;
	Material obsidian=Material.OBSIDIAN;
	
	Material water=Material.WATER_BUCKET;
	
	Material shovel=Material.IRON_SHOVEL;
	
	Material wsword=Material.WOOD_SWORD;
	
	
	@Test
	public final void testConstructorGetters() {
		
		Block bl;
		try {
			bl = new SolidBlock(grass);
			SolidBlock b = (SolidBlock)bl;
			assertTrue(b.getType()==grass);
			assertTrue(b.getDrops()==null);

			b = new SolidBlock(bedrock);
			assertTrue(b.getType()==bedrock);

			b = new SolidBlock(obsidian);
			assertTrue(b.getType()==obsidian);

			/*Block bb = new Block(b);
			assertTrue(bb.getType()==obsidian);
			assertNull(bb.getDrops());*/
		} catch (WrongMaterialException e) {
			fail("WrongMaterialException incorrectamente lanzada");
		} 
	}
	
	@Test
	public final void testConstructorExceptions() {
		Block b=null;
		try {
			b = new SolidBlock(water);
			fail("WrongMaterialException no se ha lanzado");
		} catch (WrongMaterialException e) {
			assertNull(b);
		} 
		
		try {
			b = new SolidBlock(shovel);
			fail("WrongMaterialException no se ha lanzado");
		} catch (WrongMaterialException e) {
			assertNull(b);
		} 
		
		try {
			b = new SolidBlock(wsword);
			fail("WrongMaterialException no se ha lanzado");
		} catch (WrongMaterialException e) {
			assertNull(b);
		} 
	}


	/*@Test
	public void testConstructorCopia() {
		Block block;
		try {
			block = new SolidBlock(bedrock);
			block.setDrops(bedrock, 1);
		    Block auxblock = new Block(block);
		    assertEquals(bedrock,auxblock.getType());
		    assertEquals(block.getDrops(),auxblock.getDrops());
		} catch (Exception e) {
			fail("Error: excepción "+e.getClass().toString()+" inesperada");
		}
		
	}*/

	@Test
	public final void testSetDrops() {
		SolidBlock b;
		
		try {
			b = new SolidBlock(grass);
			
			try {
				// amount != 1 para no CHEST (debe lanzar StackSizeException)
				b.setDrops(water,5);
				fail("StackSizeException no se ha lanzado");
			} catch (StackSizeException e) {
				
				assertNull(b.getDrops());  // drops debe seguir siendo null
				
				try {
					b = new SolidBlock(chest);
					b.setDrops(water,4);  // correcto
					
					ItemStack it=b.getDrops();
					assertTrue(it.getType()==water);
					assertTrue(it.getAmount()==4);
					
					
					
				} catch (StackSizeException e1) {
					fail("StackSizeException incorrectamente lanzada");
				}
				
				try {
					b.setDrops(chest,1200000); // incorrecto
					fail("StackSizeException no se ha lanzado");
				} catch (StackSizeException e1) {
					
					// OK, debe lanzar StackSizeException porque amount > 64
				}  

			}
			
		} catch (WrongMaterialException e) {
			fail("WrongMaterialException incorrectamente lanzada");
		}
	}

	//Prueba toString() para algunos bloques
	@Test
	public final void testToString() {
		Block b;
		try {
			b = new SolidBlock(grass);
			assertEquals("toString","[GRASS]", b.toString());

			b = new SolidBlock(bedrock);
			assertEquals("toString","[BEDROCK]", b.toString());

			b = new SolidBlock(obsidian);
			assertEquals("toString","[OBSIDIAN]", b.toString());
		} catch (WrongMaterialException e) {
			fail("WrongMaterialException incorrectamente lanzada");
		} 	
	}

	// Test para equals probando con cada uno de los atributos que deben intervenir
	@Test
	public void testEqualsObject() {
		SolidBlock b1;
		try {
			b1 = new SolidBlock(grass);
			assertFalse(b1.equals(null));
			assertTrue(b1.equals(b1));
			assertFalse(b1.equals(obsidian));
			
			//Distintos type
			SolidBlock b2=new SolidBlock (bedrock);
			b2.setDrops(shovel, 1);
			assertFalse(b1.equals(b2));
		} catch (Exception e) {
			fail("Error: excepción "+e.getClass().toString()+" inesperada");;
		}
	}
	
	//Test para hasCode() probando los atributos que deben intervenir
	@Test
	public void testHashCode() {
	  try {
		SolidBlock b1 = new SolidBlock(bedrock);
		SolidBlock b2 = new SolidBlock(bedrock);
		assertEquals(b1.hashCode(),b2.hashCode());
		
		b1= new SolidBlock(obsidian);
		assertNotEquals(b1.hashCode(), b2.hashCode());
	  } catch (Exception e) {
			fail("Error: excepción "+e.getClass().toString()+" inesperada");;
	  }
	}

}
