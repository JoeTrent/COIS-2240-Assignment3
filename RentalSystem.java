import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class RentalSystem {
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    private static RentalSystem instance;
    File vehicleFile = new File("vehicles.csv");
    File customerFile = new File("customers.csv");
    File recordFile = new File("rental_records.csv");
    
    private RentalSystem() {
    	
    }
    
    public static RentalSystem getInstance() {
    	if(instance == null) {
    		instance = new RentalSystem();
    	}
    	return instance;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
    
    public void saveVehicle(Vehicle vehicle) {
    	try(FileWriter file = new FileWriter(vehicleFile, true)){
    		//file.append("\n");
    		String info = vehicle.getInfo();
    		String [] str = info.split("\\|");
    		if(vehicle instanceof Car) {
        		String numSeats = str[6].trim();
    			file.append("Car" + "," + vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + numSeats+ "," + vehicle.getStatus());
    		}
    		
    		if(vehicle instanceof Minibus) {
    			String acc = str[6];
    			acc = acc.replace("Accessible: ", "").trim();
    			file.append("Minibus" + "," + vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + acc+ "," + vehicle.getStatus());
    		}
    		
    		if(vehicle instanceof PickupTruck) {
    			String cargo = str[6];
    			cargo = cargo.replace("Cargo Size: ", "").trim();
    			String trailer = str[7];
    			trailer = trailer.replace("Has Trailer: ", "").trim();
    			file.append("PickupTruck" + "," + vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + cargo+ "," + vehicle.getStatus() + "," + trailer);
    			
    		}
    		
    		if(vehicle instanceof SportCar) {
    			String seats = str[6];
    			seats = seats.replace("Seats: ", "").trim();
    			String horsePower = str[7].trim();
    			horsePower = horsePower.replace("Horsepower: ", "").trim();
    			String turbo = str[8].trim();
    			turbo = turbo.replace("Turbo: ", "").trim();
    			file.append("SportCar" + "," + vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + seats+ "," + vehicle.getStatus() + "," + horsePower + "," + turbo);
    			
    		}
    			
    	//file.append("Car" + "," + vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + Car.getNumSeats() + "," + vehicle.getStatus());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	}
    
    public void saveCustomer(Customer customer) {
    	try(FileWriter file = new FileWriter(vehicleFile, true)){
    		//file.append("\n");
    		file.append(customer.getCustomerName()+ "," + customer.getCustomerId());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public void saveRecord(RentalRecord record) {
    	
    	try(FileWriter file = new FileWriter(vehicleFile, true)){
    		//file.append("\n");
    		
    		file.append(record.getVehicle() + "," + record.getRecordDate() + "," + record.getRecordType() + "," + record.getCustomer() + "," + record.getTotalAmount());
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    private void loadData() {
    	try(Scanner scanner = new Scanner(vehicleFile)){
    		while(scanner.hasNextLine()) {
    			
    			String plate = scanner.nextLine();
    			String make = scanner.next();
    			String model = scanner.next();
    			int year = scanner.nextInt();
    			
    			String status = scanner.next();
    			Vehicle vehicle;
    			
    			
    			}
    			
    		}catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
