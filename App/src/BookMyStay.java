import java.util.*;

// -------------------- Custom Exception (NEW) --------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// -------------------- Validator Class (NEW) --------------------
class InvalidBookingValidator {

    private static final List<String> VALID_SERVICES =
            Arrays.asList("WiFi", "Food", "Spa", "Laundry");

    // Validate reservation ID
    public void validateReservationId(String id) throws InvalidBookingException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidBookingException("Reservation ID cannot be empty.");
        }
    }

    // Validate service
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

// -------------------- Main Class --------------------
public class BookMyStay {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        AddOnServiceManager manager = new AddOnServiceManager();
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();
        InvalidBookingValidator validator = new InvalidBookingValidator();

        System.out.println("===== Book My Stay - Add-On Services =====");

        try {
            // Reservation ID input
            System.out.print("Enter Reservation ID: ");
            String reservationId = sc.nextLine();

            // ✅ Validate reservation
            validator.validateReservationId(reservationId);

            // Add to history
            history.addReservation(new Reservation(reservationId));

            List<Service> selectedServices = new ArrayList<>();

            // Number of services
            System.out.print("Enter number of add-on services: ");
            int n = sc.nextInt();
            sc.nextLine();

            if (n < 0) {
                throw new InvalidBookingException("Number of services cannot be negative.");
            }

            // Input services
            for (int i = 0; i < n; i++) {
                System.out.println("\nService " + (i + 1));

                System.out.print("Enter service name (WiFi/Food/Spa/Laundry): ");
                String name = sc.nextLine();

                System.out.print("Enter service cost: ");
                double cost = sc.nextDouble();
                sc.nextLine();

                // ✅ Validate service
                validator.validateService(name, cost);

                selectedServices.add(new Service(name, cost));
            }

            // Store services
            manager.addServices(reservationId, selectedServices);

            // Display services
            System.out.println("\n===== Selected Services =====");
            List<Service> services = manager.getServices(reservationId);

            if (services.isEmpty()) {
                System.out.println("No services selected.");
            } else {
                for (Service s : services) {
                    System.out.println("- " + s);
                }
            }

            // Total cost
            double total = manager.calculateTotalCost(reservationId);
            System.out.println("\nTotal Add-On Cost: Rs." + total);

        } catch (InvalidBookingException e) {
            // ✅ Graceful failure
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            // Catch unexpected errors
            System.out.println("Unexpected error occurred.");
        }

        // System continues safely
        reportService.displayAllBookings(history.getAllReservations());
        reportService.generateSummary(history.getAllReservations());

        System.out.println("\n(System remains stable after errors)");

        sc.close();
    }
}