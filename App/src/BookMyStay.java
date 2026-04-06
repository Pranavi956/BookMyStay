import java.util.*;
import java.io.*;

// -------------------- Custom Exception --------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// -------------------- Validator --------------------
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

// -------------------- Service --------------------
class Service implements Serializable {
    private String serviceName;
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public String toString() {
        return serviceName + " (Rs." + cost + ")";
    }
}

// -------------------- Reservation --------------------
class Reservation implements Serializable {
    private String reservationId;

    public Reservation(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String toString() {
        return "Reservation ID: " + reservationId;
    }
}

// -------------------- Booking History --------------------
class BookingHistory implements Serializable {

    private List<Reservation> history = new ArrayList<>();

    public synchronized void addReservation(Reservation r) {
        history.add(r);
    }

    public synchronized List<Reservation> getAllReservations() {
        return new ArrayList<>(history);
    }

    public synchronized boolean removeReservation(String id) {
        return history.removeIf(r -> r.getReservationId().equals(id));
    }
}

// -------------------- Report Service --------------------
class BookingReportService {
    public void displayAllBookings(List<Reservation> list) {
        System.out.println("\n===== Booking History =====");
        if (list.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        for (Reservation r : list) {
            System.out.println(r);
        }
    }

    public void generateSummary(List<Reservation> list) {
        System.out.println("\nTotal Bookings: " + list.size());
    }
}

// -------------------- Cancellation Service --------------------
class CancellationService implements Serializable {

    private Stack<String> rollbackStack = new Stack<>();
    private Map<String, Integer> inventory = new HashMap<>();

    public CancellationService() {
        inventory.put("Standard", 5);
    }

    public synchronized void cancelBooking(String id, BookingHistory history)
            throws InvalidBookingException {

        boolean exists = history.getAllReservations()
                .stream().anyMatch(r -> r.getReservationId().equals(id));

        if (!exists) throw new InvalidBookingException("Reservation not found.");

        rollbackStack.push(id);
        inventory.put("Standard", inventory.get("Standard") + 1);
        history.removeReservation(id);

        System.out.println("Cancelled: " + id);
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(Map<String, Integer> inv) {
        this.inventory = inv;
    }
}

// -------------------- Concurrent Booking --------------------
class ConcurrentBookingProcessor {

    private Queue<String> queue = new LinkedList<>();
    private int availableRooms = 3;

    public synchronized void addBookingRequest(String id) {
        queue.add(id);
    }

    public void processBookings(BookingHistory history) {
        while (true) {
            String req;

            synchronized (this) {
                if (queue.isEmpty()) break;

                req = queue.poll();

                if (availableRooms <= 0) {
                    System.out.println("No rooms for " + req);
                    continue;
                }

                availableRooms--;
                System.out.println(Thread.currentThread().getName() +
                        " booked " + req + " | Remaining: " + availableRooms);
            }

            history.addReservation(new Reservation(req));
        }
    }
}

// -------------------- Persistence Service (NEW) --------------------
class PersistenceService {

    private static final String FILE_NAME = "booking_data.ser";

    // Save state
    public void save(BookingHistory history, Map<String, Integer> inventory) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(history);
            oos.writeObject(inventory);

            System.out.println("\nData saved successfully.");

        } catch (Exception e) {
            System.out.println("Error saving data.");
        }
    }

    // Load state
    public Object[] load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            BookingHistory history = (BookingHistory) ois.readObject();
            Map<String, Integer> inventory =
                    (Map<String, Integer>) ois.readObject();

            System.out.println("Data loaded successfully.");

            return new Object[]{history, inventory};

        } catch (Exception e) {
            System.out.println("No previous data found. Starting fresh.");
            return null;
        }
    }
}

// -------------------- MAIN CLASS --------------------
public class BookMyStay {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BookingHistory history = new BookingHistory();
        BookingReportService report = new BookingReportService();
        CancellationService cancelService = new CancellationService();
        PersistenceService persistence = new PersistenceService();
        ConcurrentBookingProcessor processor = new ConcurrentBookingProcessor();

        // -------- LOAD PREVIOUS STATE --------
        Object[] data = persistence.load();
        if (data != null) {
            history = (BookingHistory) data[0];
            cancelService.setInventory((Map<String, Integer>) data[1]);
        }

        try {
            System.out.print("Enter number of booking requests: ");
            int n = sc.nextInt();
            sc.nextLine();

            for (int i = 0; i < n; i++) {
                System.out.print("Enter Reservation ID: ");
                processor.addBookingRequest(sc.nextLine());
            }

            Thread t1 = new Thread(() -> processor.processBookings(history));
            Thread t2 = new Thread(() -> processor.processBookings(history));

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            // -------- CANCEL OPTION --------
            System.out.print("\nCancel any booking? (yes/no): ");
            if (sc.nextLine().equalsIgnoreCase("yes")) {
                System.out.print("Enter ID to cancel: ");
                cancelService.cancelBooking(sc.nextLine(), history);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // -------- DISPLAY --------
        report.displayAllBookings(history.getAllReservations());
        report.generateSummary(history.getAllReservations());

        // -------- SAVE STATE --------
        persistence.save(history, cancelService.getInventory());

        System.out.println("\n(System recovered & persisted successfully)");

        sc.close();
    }
}