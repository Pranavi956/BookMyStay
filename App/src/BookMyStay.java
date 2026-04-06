import java.util.*;

// -------------------- Custom Exception --------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// -------------------- Validator Class --------------------
class InvalidBookingValidator {

    private static final List<String> VALID_SERVICES =
            Arrays.asList("WiFi", "Food", "Spa", "Laundry");

    public void validateReservationId(String id) throws InvalidBookingException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidBookingException("Reservation ID cannot be empty.");
        }
    }

    public void validateService(String name, double cost) throws InvalidBookingException {

        if (!VALID_SERVICES.contains(name)) {
            throw new InvalidBookingException("Invalid service: " + name);
        }

        if (cost <= 0) {
            throw new InvalidBookingException("Service cost must be positive.");
        }
    }
}

// -------------------- Service Class --------------------
class Service {
    private String serviceName;
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (Rs." + cost + ")";
    }
}

// -------------------- Add-On Service Manager --------------------
class AddOnServiceManager {

    private Map<String, List<Service>> serviceMap = new HashMap<>();

    public void addServices(String reservationId, List<Service> services) {
        serviceMap.put(reservationId, services);
    }

    public List<Service> getServices(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    public double calculateTotalCost(String reservationId) {
        double total = 0;
        List<Service> services = getServices(reservationId);

        for (Service s : services) {
            total += s.getCost();
        }
        return total;
    }
}

// -------------------- Reservation Class --------------------
class Reservation {
    private String reservationId;

    public Reservation(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationId() {
        return reservationId;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId;
    }
}

// -------------------- Booking History --------------------
class BookingHistory {

    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return history;
    }

    // Remove on cancellation
    public boolean removeReservation(String reservationId) {
        return history.removeIf(r -> r.getReservationId().equals(reservationId));
    }
}

// -------------------- Booking Report Service --------------------
class BookingReportService {

    public void displayAllBookings(List<Reservation> reservations) {
        System.out.println("\n===== Booking History =====");

        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    public void generateSummary(List<Reservation> reservations) {
        System.out.println("\n===== Booking Summary =====");
        System.out.println("Total Bookings: " + reservations.size());
    }
}

// -------------------- Cancellation Service (NEW) --------------------
class CancellationService {

    private Stack<String> rollbackStack = new Stack<>();

    // Simulated inventory
    private Map<String, Integer> inventory = new HashMap<>();

    public CancellationService() {
        inventory.put("Standard", 5);
        inventory.put("Deluxe", 3);
    }

    public void cancelBooking(String reservationId, BookingHistory history)
            throws InvalidBookingException {

        // Validate existence
        boolean exists = history.getAllReservations()
                .stream()
                .anyMatch(r -> r.getReservationId().equals(reservationId));

        if (!exists) {
            throw new InvalidBookingException("Reservation does not exist.");
        }

        // Push to stack (rollback tracking)
        rollbackStack.push(reservationId);

        // Restore inventory (simulated)
        inventory.put("Standard", inventory.get("Standard") + 1);

        // Remove from history
        history.removeReservation(reservationId);

        System.out.println("Cancellation successful for: " + reservationId);
    }

    public void showRollbackStack() {
        System.out.println("Rollback Stack: " + rollbackStack);
    }

    public void showInventory() {
        System.out.println("Updated Inventory: " + inventory);
    }
}

// -------------------- Main Class --------------------
public class BookMyStay {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        AddOnServiceManager manager = new AddOnServiceManager();
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();
        InvalidBookingValidator validator = new InvalidBookingValidator();
        CancellationService cancellationService = new CancellationService();

        System.out.println("===== Book My Stay - Add-On Services =====");

        try {
            // Reservation ID
            System.out.print("Enter Reservation ID: ");
            String reservationId = sc.nextLine();

            validator.validateReservationId(reservationId);

            history.addReservation(new Reservation(reservationId));

            List<Service> selectedServices = new ArrayList<>();

            System.out.print("Enter number of add-on services: ");
            int n = sc.nextInt();
            sc.nextLine();

            if (n < 0) {
                throw new InvalidBookingException("Invalid number of services.");
            }

            for (int i = 0; i < n; i++) {
                System.out.print("Enter service name (WiFi/Food/Spa/Laundry): ");
                String name = sc.nextLine();

                System.out.print("Enter service cost: ");
                double cost = sc.nextDouble();
                sc.nextLine();

                validator.validateService(name, cost);

                selectedServices.add(new Service(name, cost));
            }

            manager.addServices(reservationId, selectedServices);

            System.out.println("\n===== Selected Services =====");
            for (Service s : manager.getServices(reservationId)) {
                System.out.println("- " + s);
            }

            double total = manager.calculateTotalCost(reservationId);
            System.out.println("\nTotal Add-On Cost: Rs." + total);

            // ---------------- CANCEL FLOW ----------------
            System.out.print("\nDo you want to cancel this booking? (yes/no): ");
            String choice = sc.nextLine();

            if (choice.equalsIgnoreCase("yes")) {
                cancellationService.cancelBooking(reservationId, history);
                cancellationService.showRollbackStack();
                cancellationService.showInventory();
            }

        } catch (InvalidBookingException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error occurred.");
        }

        // Reports
        reportService.displayAllBookings(history.getAllReservations());
        reportService.generateSummary(history.getAllReservations());

        System.out.println("\n(System remains stable after cancellation)");

        sc.close();
    }
}