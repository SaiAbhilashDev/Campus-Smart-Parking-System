import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Vehicle{
    private String registrationNumber;
    private String vehicleType;
    private String category;
    private LocalDateTime entryTime;
    private String slotId;

    public Vehicle(String registrationNumber, String vehicleType, String category, LocalDateTime entryTime, String slotId) {
        this.registrationNumber = registrationNumber;
        this.vehicleType = vehicleType;
        this.category = category;
        this.entryTime = entryTime;
        this.slotId = slotId;
    }

    public String getRegistrationNumber(){
        return registrationNumber;
    }

    public String getVehicleType(){
        return vehicleType;
    }

    public String getCategory(){
        return category;
    }

    public LocalDateTime getEntryTime(){
        return entryTime;
    }

    public String getSlotId(){
        return slotId;
    }
}

class ParkingSlot{
    private String slotId;
    private String slotType;
    private boolean occupied;
    private String registrationNumber;

    public ParkingSlot(String slotId, String slotType) {
    this.slotId = slotId;
    this.slotType = slotType;
    this.occupied = false;
    this.registrationNumber = null;
    }

    public String getSlotId(){
        return slotId;
    }

    public String getSlotType(){
        return slotType;
    }

    public boolean isOccupied(){
        return occupied;
    }

    public String getRegistrationNumber(){
        return registrationNumber;
    }

    public void parkVehicle(String registrationNumber) {
    this.occupied = true;
    this.registrationNumber = registrationNumber;
    }

    public void releaseSlot() {
        this.occupied = false;
        this.registrationNumber = null;
    }
}

class FeeCalculator {
    public static double calculateFee(
            String vehicleType,
            String category,
            LocalDateTime entryTime,
            LocalDateTime exitTime) {

        if (category.equalsIgnoreCase("Faculty")) {
            return 0;
        }

        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        long hours = Math.max(1, (minutes + 59) / 60);

        double rate;

        if (category.equalsIgnoreCase("Visitor")) {
            rate = 40;
        } else if (vehicleType.equalsIgnoreCase("Two-wheeler")) {
            rate = 10;
        } else if (vehicleType.equalsIgnoreCase("Car")) {
            rate = 30;
        } else {
            rate = 20;
        }

        return hours * rate;
    }
}

class ParkingReport {
    private LocalDate reportDate = LocalDate.now();
    private int totalEntries;
    private int totalExits;
    private double totalRevenue;

    private void checkDate() {
        if (!reportDate.equals(LocalDate.now())) {
            reportDate = LocalDate.now();
            totalEntries = 0;
            totalExits = 0;
            totalRevenue = 0;
        }
    }

    public void recordEntry() {
        checkDate();
        totalEntries++;
    }

    public void recordExit(double fee, LocalDate exitDate) {
        checkDate();

        if (exitDate.equals(LocalDate.now())) {
            totalExits++;
            totalRevenue += fee;
        }
    }

    public void displayReport(int currentlyParked) {
        checkDate();

        System.out.println("\n Daily Parking Report");
        System.out.println("Report date: " + reportDate);
        System.out.println("Total entries: " + totalEntries);
        System.out.println("Total exits: " + totalExits);
        System.out.println("Currently parked: " + currentlyParked);
        System.out.printf("Total revenue: Rs. %.2f%n", totalRevenue);
    }
}

class ParkingManager {

    private final List<ParkingSlot> slots = new ArrayList<>();
    private final Map<String, Vehicle> parkedVehicles = new HashMap<>();
    private final ParkingReport report = new ParkingReport();

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public ParkingManager() {
        addSlots("T", "TWO_WHEELER", 5);
        addSlots("C", "CAR", 5);
        addSlots("E", "EV", 3);
        addSlots("F", "FACULTY", 2);
        addSlots("V", "VISITOR", 2);
    }

    private void addSlots(String prefix, String type, int count) {
        for (int i = 1; i <= count; i++) {
            slots.add(new ParkingSlot(prefix + i, type));
        }
    }

    public void run(Scanner sc) {
        int choice;

        do {
            System.out.println(" Campus Smart Parking System");
            System.out.println("1. Vehicle Entry");
            System.out.println("2. Vehicle Exit");
            System.out.println("3. Display Available Slots");
            System.out.println("4. Display Occupied Slots");
            System.out.println("5. Search Vehicle");
            System.out.println("6. Display All Parked Vehicles");
            System.out.println("7. Daily Parking Report");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            choice = readInt(sc);

            switch (choice) {
                case 1:
                    vehicleEntry(sc);
                    break;
                case 2:
                    vehicleExit(sc);
                    break;
                case 3:
                    displaySlots(false);
                    break;
                case 4:
                    displaySlots(true);
                    break;
                case 5:
                    searchVehicle(sc);
                    break;
                case 6:
                    displayAllVehicles();
                    break;
                case 7:
                    report.displayReport(parkedVehicles.size());
                    break;
                case 8:
                    System.out.println("Thank you for using the system!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 8);
    }

    private int readInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid number: ");
            sc.next();
        }
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }

    private void vehicleEntry(Scanner sc) {
        System.out.print("Enter registration number: ");
        String reg = sc.nextLine().trim().toUpperCase();

        if (reg.isEmpty()) {
            System.out.println("Registration number cannot be empty.");
            return;
        }

        if (parkedVehicles.containsKey(reg)) {
            System.out.println("This vehicle is already parked.");
            return;
        }

        System.out.println("\nVehicle type:");
        System.out.println("1. Two-wheeler");
        System.out.println("2. Car");
        System.out.println("3. Electric vehicle");
        System.out.print("Choose type: ");
        int typeChoice = readInt(sc);

        String vehicleType;
        if (typeChoice == 1) {
            vehicleType = "Two-wheeler";
        } else if (typeChoice == 2) {
            vehicleType = "Car";
        } else if (typeChoice == 3) {
            vehicleType = "Electric vehicle";
        } else {
            System.out.println("Invalid vehicle type.");
            return;
        }

        System.out.println("\nVehicle category:");
        System.out.println("1. Student");
        System.out.println("2. Faculty");
        System.out.println("3. Visitor");
        System.out.print("Choose category: ");
        int categoryChoice = readInt(sc);

        String category;
        if (categoryChoice == 1) {
            category = "Student";
        } else if (categoryChoice == 2) {
            category = "Faculty";
        } else if (categoryChoice == 3) {
            category = "Visitor";
        } else {
            System.out.println("Invalid category.");
            return;
        }

        if (!category.equals("Student") && !vehicleType.equals("Car")) {
            System.out.println(
                "Faculty and visitor reserved slots are for cars only.");
            return;
        }

        String requiredSlot;
        if (category.equals("Faculty")) {
            requiredSlot = "FACULTY";
        } else if (category.equals("Visitor")) {
            requiredSlot = "VISITOR";
        } else if (vehicleType.equals("Two-wheeler")) {
            requiredSlot = "TWO_WHEELER";
        } else if (vehicleType.equals("Car")) {
            requiredSlot = "CAR";
        } else {
            requiredSlot = "EV";
        }

        ParkingSlot selected = null;

        for (ParkingSlot slot : slots) {
            if (slot.getSlotType().equals(requiredSlot)
                    && !slot.isOccupied()) {
                selected = slot;
                break;
            }
        }

        if (selected == null) {
            System.out.println("No suitable parking slot available!");
            return;
        }

        LocalDateTime entryTime = LocalDateTime.now();

        Vehicle vehicle = new Vehicle(
                reg, vehicleType, category, entryTime,
                selected.getSlotId());

        selected.parkVehicle(reg);
        parkedVehicles.put(reg, vehicle);
        report.recordEntry();

        System.out.println("\nVehicle parked successfully!");
        System.out.println("Registration : " + reg);
        System.out.println("Slot ID      : " + selected.getSlotId());
        System.out.println("Entry time   : " + entryTime.format(formatter));
    }

    private void vehicleExit(Scanner sc) {
        System.out.print("Enter registration number: ");
        String reg = sc.nextLine().trim().toUpperCase();

        Vehicle vehicle = parkedVehicles.get(reg);

        if (vehicle == null) {
            System.out.println("Vehicle not found in the parking area.");
            return;
        }

        LocalDateTime exitTime = LocalDateTime.now();

        double fee = FeeCalculator.calculateFee(
                vehicle.getVehicleType(),
                vehicle.getCategory(),
                vehicle.getEntryTime(),
                exitTime);

        System.out.println("\n========== PARKING RECEIPT ==========");
        System.out.println("Registration : " + reg);
        System.out.println("Vehicle type : " + vehicle.getVehicleType());
        System.out.println("Category     : " + vehicle.getCategory());
        System.out.println("Slot ID      : " + vehicle.getSlotId());
        System.out.println("Entry time   : "
                + vehicle.getEntryTime().format(formatter));
        System.out.println("Exit time    : " + exitTime.format(formatter));
        System.out.printf("Parking fee  : Rs. %.2f%n", fee);
        System.out.println("=====================================");

        for (ParkingSlot slot : slots) {
            if (slot.getSlotId().equals(vehicle.getSlotId())) {
                slot.releaseSlot();
                break;
            }
        }

        report.recordExit(fee, exitTime.toLocalDate());
        parkedVehicles.remove(reg);

        System.out.println("Vehicle exit completed. Slot released.");
    }

    private void displaySlots(boolean occupied) {
        System.out.println(occupied
                ? "\n========== OCCUPIED SLOTS =========="
                : "\n========== AVAILABLE SLOTS =========");

        boolean found = false;

        for (ParkingSlot slot : slots) {
            if (slot.isOccupied() == occupied) {
                System.out.printf("%-5s | %-12s",
                        slot.getSlotId(), slot.getSlotType());

                if (occupied) {
                    System.out.print(" | " + slot.getRegistrationNumber());
                }

                System.out.println();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No slots found.");
        }
    }

    private void searchVehicle(Scanner sc) {
        System.out.print("Enter registration number: ");
        String reg = sc.nextLine().trim().toUpperCase();

        Vehicle vehicle = parkedVehicles.get(reg);

        if (vehicle == null) {
            System.out.println("Vehicle not currently parked.");
            return;
        }

        System.out.println("\nVehicle found!");
        System.out.println("Registration : "
                + vehicle.getRegistrationNumber());
        System.out.println("Type         : " + vehicle.getVehicleType());
        System.out.println("Category     : " + vehicle.getCategory());
        System.out.println("Slot         : " + vehicle.getSlotId());
        System.out.println("Entry time   : "
                + vehicle.getEntryTime().format(formatter));
    }

    private void displayAllVehicles() {
        System.out.println("\n========== PARKED VEHICLES ==========");

        if (parkedVehicles.isEmpty()) {
            System.out.println("No vehicles are currently parked.");
            return;
        }

        for (Vehicle vehicle : parkedVehicles.values()) {
            System.out.printf(
                "%s | %s | %s | Slot: %s | Entry: %s%n",
                vehicle.getRegistrationNumber(),
                vehicle.getVehicleType(),
                vehicle.getCategory(),
                vehicle.getSlotId(),
                vehicle.getEntryTime().format(formatter));
        }
    }
}
public class CampusSmartParkingSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ParkingManager manager = new ParkingManager();
        manager.run(sc);

        sc.close();
    }
}