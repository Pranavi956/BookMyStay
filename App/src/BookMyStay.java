import java.util.*;

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

// -------------------- Reservation Class (NEW) --------------------
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

// -------------------- Booking History (NEW) --------------------
class BookingHistory {

    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return history;
    }
}

// -------------------- Booking Report Service (NEW) --------------------
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

        System.out.println("===== Book My Stay - Add-On Services =====");

        // Reservation ID input
        System.out.print("Enter Reservation ID: ");
        String reservationId = sc.nextLine();

        // ✅ Add to booking history (NEW)
        history.addReservation(new Reservation(reservationId));

        List<Service> selectedServices = new ArrayList<>();

        // Number of services
        System.out.print("Enter number of add-on services: ");
        int n = sc.nextInt();
        sc.nextLine();

        // Input services
        for (int i = 0; i < n; i++) {
            System.out.println("\nService " + (i + 1));

            System.out.print("Enter service name: ");
            String name = sc.nextLine();

            System.out.print("Enter service cost: ");
            double cost = sc.nextDouble();
            sc.nextLine();

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

        // ✅ Show booking history (NEW)
        reportService.displayAllBookings(history.getAllReservations());

        // ✅ Summary report (NEW)
        reportService.generateSummary(history.getAllReservations());

        System.out.println("\n(Booking and room allocation remain unchanged)");

        sc.close();
    }
}