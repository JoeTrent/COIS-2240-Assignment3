import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class RentalSystem {
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    private static RentalSystem instance;
    File vehicleFile = new File("vehicles.txt");
    File customerFile = new File("customers.txt");
    File recordFile = new File("RentalRecords.txt");
    
    
    private RentalSystem() {
    	loadData();
    	
    }
    
    public static RentalSystem getInstance() {
    	if(instance == null) {
    		instance = new RentalSystem();
    	}
    	return instance;
    }

    public boolean addVehicle(Vehicle vehicle) {
    	String plateCheck = vehicle.getLicensePlate();
    	Vehicle v = findVehicleByPlate(plateCheck);
    	if(v != null && v.getLicensePlate().equals(vehicle.getLicensePlate()) ) {
    		System.out.print("Dupe found, did not add");
    		return false;
    	}
    	else {
    		vehicles.add(vehicle);
            saveVehicle(vehicle);
            System.out.print("Successfully added");
            return true;
    	}
    }

    public boolean addCustomer(Customer customer) {
    	int idCheck = customer.getCustomerId();
    	Customer c = findCustomerById(idCheck);
    	if(c!= null && c.getCustomerId() == customer.getCustomerId()) {
    		System.out.print("Dupe found, did not add");
    		return false;
    	}
    	else {
    		customers.add(customer);
    		saveCustomer(customer);
    		System.out.print("Successfully added");
    		return true;
    	}
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
    		file.append("\n");
    		String info = vehicle.getInfo();
    		String [] str = info.split("\\|");
    		if(vehicle instanceof Car) {
        		String numSeats = str[6].trim();
    			file.append("Car" + "," + vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + numSeats+ "," + vehicle.getStatus() + "\n");
    		}
    		
    		if(vehicle instanceof Minibus) {
    			String acc = str[6];
    			acc = acc.replace("Accessible: ", "").trim();
    			file.append("Minibus" + "," + vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + acc+ "," + vehicle.getStatus() + "\n");
    		}
    		
    		if(vehicle instanceof PickupTruck) {
    			String cargo = str[6];
    			cargo = cargo.replace("Cargo Size: ", "").trim();
    			String trailer = str[7];
    			trailer = trailer.replace("Has Trailer: ", "").trim();
    			file.append("PickupTruck" + "," + vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + cargo+ "," + vehicle.getStatus() + "," + trailer + "\n");
    			
    		}
    		
    		if(vehicle instanceof SportCar) {
    			String seats = str[6];
    			seats = seats.replace("Seats: ", "").trim();
    			String horsePower = str[7].trim();
    			horsePower = horsePower.replace("Horsepower: ", "").trim();
    			String turbo = str[8].trim();
    			turbo = turbo.replace("Turbo: ", "").trim();
    			file.append("SportCar" + "," + vehicle.getLicensePlate() + "," + vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear() + "," + seats+ "," + vehicle.getStatus() + "," + horsePower + "," + turbo + "\n");
    			
    		}
    			
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	}
    
    public void saveCustomer(Customer customer) {
    	try(FileWriter file = new FileWriter(customerFile, true)){
    		file.append("\n");
    		file.append(customer.getCustomerId()+ "," + customer.getCustomerName() + "\n");
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public void saveRecord(RentalRecord record) { // make so it saves the vehicles license plate and customerID
    	
    	try(FileWriter file = new FileWriter(recordFile, true)){
    		//file.append("\n");
    		Vehicle vehicle = record.getVehicle();
    		
    		Customer customer = record.getCustomer();
    		
    		
    		file.append(vehicle.getLicensePlate() + "," + customer.getCustomerId() + "," + record.getRecordType() + "," + record.getRecordDate() + "," + record.getTotalAmount() +"\n");
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    //go back to save task and make it so 
    
    private void loadData() {
    	try(Scanner scanner = new Scanner(vehicleFile)){
    		
    		while(scanner.hasNextLine()) {
    			String plate;
    			String make;
    			String model;
    			String status;
    			int year;
    			int numSeats;
    			boolean acc;
    			double cargoSize;
    			boolean hasTrailer;
    			int horsePower;
    			boolean hasTurbo;
    			String row = scanner.nextLine();
    			if(row.isEmpty())
    			continue;
    			String [] col = row.split("[,]");
    			try {
    				int i =0;
    				Vehicle vehicle = null;
    			String type = col[i++].trim();
    			if(type.equals("Car")) {
    				plate = col[i++];
    				make = col[i++];
    				model = col[i++];
    				year = Integer.parseInt(col[i++]);
    				numSeats = Integer.parseInt(col[i++]);
    				status = col[i++];
    				
    				vehicle = new Car(make,model,year,numSeats);
    				vehicle.setLicensePlate(plate);
    				Vehicle.VehicleStatus stat = Vehicle.VehicleStatus.valueOf(status);
    				vehicle.setStatus(stat);
    				
    			}
    			if(type.equals("Minibus")) {
    				plate = col[i++];
    				make = col[i++];
    				model = col[i++];
    				year = Integer.parseInt(col[i++]);
    				acc = Boolean.parseBoolean(col[i++]);
    				status = col[i++];
    				
    				vehicle = new Minibus(make,model,year,acc);
    				vehicle.setLicensePlate(plate);
    				Vehicle.VehicleStatus stat = Vehicle.VehicleStatus.valueOf(status);
    				vehicle.setStatus(stat);
    			}
    			
    			if(type.equals("PickupTruck")) {
    				plate = col[i++];
    				make = col[i++];
    				model = col[i++];
    				year = Integer.parseInt(col[i++]);
    				cargoSize = Double.parseDouble(col[i++]);
    				status = col[i++];
    				hasTrailer = Boolean.parseBoolean(col[i++]);
    				vehicle = new PickupTruck(make,model,year,cargoSize,hasTrailer);
    				vehicle.setLicensePlate(plate);
    				Vehicle.VehicleStatus stat = Vehicle.VehicleStatus.valueOf(status);
    				vehicle.setStatus(stat);
    				
    			}
    			
    			if(type.equals("SportCar")) {
    				plate = col[i++];
    				make = col[i++];
    				model = col[i++];
    				year = Integer.parseInt(col[i++]);
    				numSeats = Integer.parseInt(col[i++]);
    				status = col[i++];
    				horsePower = Integer.parseInt(col[i++]);
    				hasTurbo = Boolean.parseBoolean(col[i++]);
    				vehicle = new SportCar(make,model,year,numSeats,horsePower,hasTurbo);
    				vehicle.setLicensePlate(plate);
    				Vehicle.VehicleStatus stat = Vehicle.VehicleStatus.valueOf(status);
    				vehicle.setStatus(stat);
    				
    			}
    			if(vehicle != null)
    			vehicles.add(vehicle);	
    			
    			}catch(Exception e) {
    				System.out.print("Bab vehicle row found: " + row);
    			}
    			
    			}
    	//	}
    			
    		}catch (IOException e) {
    			e.printStackTrace();
    		}
    	
    	if(customerFile.exists()) {
    	try(Scanner scanner = new Scanner(customerFile)){
    		while(scanner.hasNextLine()) {
    			int i = 0,iD;
    			String row = scanner.nextLine();
    			String [] col = row.split(",");
    			String name;
    			try {
    			iD = Integer.parseInt(col[i++]);
    			name = col[i++];
    			Customer customer = new Customer(iD,name);
    			if(customer != null)
    				customers.add(customer);
    			} catch(Exception e) {
    				System.out.print("Bad vehicle row found: " + row);
    			}
    			
    			
    			
    		}
    		
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
    	}
    	
    	
    	if(recordFile.exists()) {
    	try(Scanner scanner = new Scanner(recordFile)){
    		while(scanner.hasNextLine()) {
    			int i = 0;
    			String row = scanner.nextLine();
    			String [] col = row.split(",");
    			try {
    			String vecSearch = col[i++];
    			Vehicle vehicle = findVehicleByPlate(vecSearch);
    			int custSearch = Integer.parseInt(col[i++]);
    			Customer customer = findCustomerById(custSearch);
    			String recType = col[i++];
    			String recDate = col[i++];
    			double totalAmount = Double.parseDouble(col[i++]);
    			LocalDate date = LocalDate.parse(recDate);
    			
    			RentalRecord record = new RentalRecord(vehicle,customer,date,totalAmount,recType);
    			rentalHistory.addRecord(record);
    			
    			}catch(Exception e) {
    				System.out.print("Bad record found: " + row);
    			}
    			
    			
    			
    			
    		}
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	
    	}
    
    } 
  

    }
