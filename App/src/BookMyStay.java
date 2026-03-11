import java.util.*;

// Abstract Room class
abstract class Room {

    protected String roomType;
    protected int beds;
    protected int size;
    protected double price;

    public Room(String roomType, int beds, int size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq ft");
        System.out.println("Price     : ₹" + price + " per night");
    }

    public String getRoomType() {
        return roomType;
    }
}


// Single Room
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single", 1, 200, 2500);
    }
}


// Double Room
class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double", 2, 350, 4000);
    }
}


// Suite Room
class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite", 3, 600, 8000);
    }
}


// Centralized Inventory
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 0);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}


// Search Service (Read-Only)
class RoomSearchService {

    private RoomInventory inventory;

    public RoomSearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void searchAvailableRooms(Room[] rooms) {

        System.out.println("\nAvailable Rooms:\n");

        for (Room room : rooms) {

            int availability = inventory.getAvailability(room.getRoomType());

            if (availability > 0) {
                room.displayRoomDetails();
                System.out.println("Available Rooms : " + availability);
                System.out.println("--------------------------------");
            }
        }
    }
}


// Reservation represents a guest booking request
class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + " | Requested Room: " + roomType);
    }
}


// Booking Request Queue (FIFO)
class BookingRequestQueue {

    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
        System.out.println("Request added for " + reservation.getGuestName());
    }

    public void displayQueue() {

        System.out.println("\nCurrent Booking Request Queue:");

        for (Reservation r : requestQueue) {
            r.displayReservation();
        }
    }
}


// Main Application
public class BookMyStay {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("Welcome to Book My Stay");
        System.out.println("Hotel Booking Management System");
        System.out.println("Version: 5.0");
        System.out.println("=================================");

        // Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        Room[] rooms = {single, doubleRoom, suite};

        // Inventory
        RoomInventory inventory = new RoomInventory();

        // Search
        RoomSearchService searchService = new RoomSearchService(inventory);
        searchService.searchAvailableRooms(rooms);

        // Booking Queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        Reservation r1 = new Reservation("Alice", "Single");
        Reservation r2 = new Reservation("Bob", "Double");
        Reservation r3 = new Reservation("Charlie", "Suite");

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        bookingQueue.displayQueue();

        System.out.println("\nRequests stored in arrival order.");
    }
}