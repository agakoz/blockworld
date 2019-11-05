package test.model;

import model.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;


import model.exceptions.StackSizeException;
import model.exceptions.WrongMaterialException;

public class Block_P2Test {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);
	
	Material bedrock=Material.BEDROCK;
	Material chest=Material.CHEST;
	Material grass=Material.GRASS;
	Material obsidian=Material.OBSIDIAN;
	
	Material water=Material.WATER_BUCKET;
	
	Material shovel=Material.IRON_SHOVEL;
	
	Material wsword=Material.WOOD_SWORD;
	
	
	@Test
	public final void testConstructorGetters() {
		
		Block b;
		try {
			b = new Block(grass);
			assertTrue(b.getType()==grass);
			assertTrue(b.getDrops()==null);

			b = new Block(bedrock);
			assertTrue(b.getType()==bedrock);

			b = new Block(obsidian);
			assertTrue(b.getType()==obsidian);

		} catch (WrongMaterialException e) {
			fail("WrongMaterialException incorrectamente lanzada");
		} 
	}
	
	@Test
	public final void testCopyConstructor() {
		
		Block b;
		try {
			b = new Block(obsidian);
			Block bb = new Block(b);
			assertTrue(bb.getType()==obsidian);
			assertNull(bb.getDrops());
		} catch (WrongMaterialException e) {
			fail("WrongMaterialException incorrectamente lanzada");
		} 
	}

	
	@Test
	public final void testConstructorExceptions() {
		Block b=null;
		try {
			b = new Block(water);
			fail("WrongMaterialException no se ha lanzado");
		} catch (WrongMaterialException e) {
			assertNull(b);
		} 
		
		try {
			b = new Block(shovel);
			fail("WrongMaterialException no se ha lanzado");
		} catch (WrongMaterialException e) {
			assertNull(b);
		} 
		
		try {
			b = new Block(wsword);
			fail("WrongMaterialException no se ha lanzado");
		} catch (WrongMaterialException e) {
			assertNull(b);
		} 
	}


	@Test
	public void testConstructorCopia() {
		Block block;
		try {
			block = new Block(bedrock);
			block.setDrops(bedrock, 1);
		    Block auxblock = new Block(block);
		    assertEquals(bedrock,auxblock.getType());
		    assertEquals(block.getDrops(),auxblock.getDrops());
		} catch (Exception e) {
			fail("Error: excepción "+e.getClass().toString()+" inesperada");
		}
		
	}

	@Test
	public final void testSetDropsBadAmount() {
		try {			
			Block b = new Block(grass);
			try {
				// amount != 1 para no CHEST (debe lanzar StackSizeException)
				b.setDrops(water,5);
				fail("StackSizeException no se ha lanzado");

			} catch (StackSizeException e) {
				assertNull(b.getDrops());  // drops debe seguir siendo null
			}
		}
		catch (WrongMaterialException e) {
			fail("WrongMaterialException incorrectamente lanzada");
		}
	}

	@Test
	public final void testSetDropsCorrectAmount() {
		Block b;
		
		try {

			b = new Block(chest);
			b.setDrops(water,4);  // correcto

			ItemStack it=b.getDrops();
			assertTrue(it.getType()==water);
			assertTrue(it.getAmount()==4);

		} catch (StackSizeException e1) {
			fail("StackSizeException incorrectamente lanzada");
		}
		catch (WrongMaterialException e) {
			fail("WrongMaterialException incorrectamente lanzada");
		}
	}

	@Test
	public final void testSetDropsTooMuchItems() {
		Block b;
		
		try {
			b = new Block(grass);

			try {
				b.setDrops(water,1200000); // incorrecto
				fail("StackSizeException no se ha lanzado");
			} catch (StackSizeException e1) {

				// OK, debe lanzar StackSizeException porque amount > 64
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
			b = new Block(grass);
			assertEquals("toString","[GRASS]", b.toString());

			b = new Block(bedrock);
			assertEquals("toString","[BEDROCK]", b.toString());

			b = new Block(obsidian);
			assertEquals("toString","[OBSIDIAN]", b.toString());
		} catch (WrongMaterialException e) {
			fail("WrongMaterialException incorrectamente lanzada");
		} 	
	}

	// Test para equals probando con cada uno de los atributos que deben intervenir
	@Test
	public void testEqualsObject() {
		Block b1;
		try {
			b1 = new Block(grass);
			assertFalse(b1.equals(null));
			assertTrue(b1.equals(b1));
			assertFalse(b1.equals(obsidian));
						
			//Distintos type
			Block b2=new Block (bedrock);
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
		Block b1 = new Block(grass);
		Block b2 = new Block(b1);
		b1.setDrops(bedrock, 1);
		assertEquals(b1.hashCode(),b2.hashCode());
		
		b1= new Block(obsidian);
		assertNotEquals(b1.hashCode(), b2.hashCode());
	  } catch (Exception e) {
			fail("Error: excepción "+e.getClass().toString()+" inesperada");;
	  }
	}

}
