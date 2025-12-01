import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;


class VehicleRentalTest {

	
	@Test
	public void testLicensePlate() {
		Vehicle v1 = new Car("Toyota", "Corolla", 2020, 5);
		Vehicle v2 = new Car("Ford", "Mustang", 2024, 2);
		Vehicle v3 = new Car("Honda", "Civic", 2020, 4);
		assertTrue(v1.isPlateVaild("AAA100"));
		assertTrue(v2.isPlateVaild("ABC567"));
		assertTrue(v3.isPlateVaild("ZZZ999"));
		Vehicle v4 = new Car("BMW", "X7", 2025, 4);
		
		
		assertThrows(IllegalArgumentException.class, () ->{
			v4.setLicensePlate(null);
		});
		
		assertThrows(IllegalArgumentException.class, () ->{
			v4.setLicensePlate("");
		});
		
		assertThrows(IllegalArgumentException.class, () ->{
			v4.setLicensePlate("AAA1000");
		});
		
		assertThrows(IllegalArgumentException.class, () ->{
			v4.setLicensePlate("ZZZ99");
		});
	}
	
 }

	


