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
        for (Service s : getServices(reservationId)) {
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

    public synchronized void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    public synchronized List<Reservation> getAllReservations() {
        return new ArrayList<>(history);
    }

    public synchronized boolean removeReservation(String reservationId) {
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

// -------------------- Cancellation Service --------------------
class CancellationService {

    private Stack<String> rollbackStack = new Stack<>();
    private Map<String, Integer> inventory = new HashMap<>();

    public CancellationService() {
        inventory.put("Standard", 5);
    }

    public synchronized void cancelBooking(String reservationId, BookingHistory history)
            throws InvalidBookingException {

        boolean exists = history.getAllReservations()
                .stream()
                .anyMatch(r -> r.getReservationId().equals(reservationId));

        if (!exists) {
            throw new InvalidBookingException("Reservation does not exist.");
        }

        rollbackStack.push(reservationId);
        inventory.put("Standard", inventory.get("Standard") + 1);
        history.removeReservation(reservationId);

        System.out.println("Cancelled: " + reservationId);
    }
}

// -------------------- Concurrent Booking Processor (NEW) --------------------
class ConcurrentBookingProcessor {

    private Queue<String> bookingQueue = new LinkedList<>();
    private int availableRooms = 3; // shared resource

    // Add booking request
    public synchronized void addBookingRequest(String reservationId) {
        bookingQueue.add(reservationId);
    }

    // Process booking safely
    public void processBookings(BookingHistory history) {

        while (true) {
            String request;

            synchronized (this) {
                if (bookingQueue.isEmpty()) break;

                request = bookingQueue.poll();

                if (availableRooms <= 0) {
                    System.out.println("No rooms available for " + request);
                    continue;
                }

                // CRITICAL SECTION
                availableRooms--;
                System.out.println(Thread.currentThread().getName() +
                        " allocated room to " + request +
                        " | Remaining: " + availableRooms);
            }

            // outside synchronized → add to history
            history.addReservation(new Reservation(request));
        }
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

        ConcurrentBookingProcessor processor = new ConcurrentBookingProcessor();

        System.out.println("===== Concurrent Booking Simulation =====");

        try {
            System.out.print("Enter number of concurrent requests: ");
            int n = sc.nextInt();
            sc.nextLine();

            // Add requests
            for (int i = 0; i < n; i++) {
                System.out.print("Enter Reservation ID: ");
                String id = sc.nextLine();
                validator.validateReservationId(id);

                processor.addBookingRequest(id);
            }

            // Create threads
            Thread t1 = new Thread(() -> processor.processBookings(history), "Thread-1");
            Thread t2 = new Thread(() -> processor.processBookings(history), "Thread-2");

            t1.start();
            t2.start();

            t1.join();
            t2.join();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Final report
        reportService.displayAllBookings(history.getAllReservations());
        reportService.generateSummary(history.getAllReservations());

        System.out.println("\n(System remains consistent under concurrency)");

        sc.close();
    }
}