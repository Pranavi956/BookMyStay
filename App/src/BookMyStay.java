import java.util.HashMap;

// Abstract Room class
abstract class Room {

    protected String roomType;
    protected int beds;
    protected int size;
    protected double price;

    public Room(String roomType, int beds, int size, double sizeSqft, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = (int) sizeSqft;
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
        super("Single", 1, 200, 200, 2500);
    }
}


// Double Room
class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double", 2, 350, 350, 4000);
    }
}


// Suite Room
class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite", 3, 600, 600, 8000);
    }
}


// Centralized Inventory
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 0); // Example: no suites available
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " Rooms Available: " + inventory.get(type));
        }
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

            // Defensive check – show only available rooms
            if (availability > 0) {
                room.displayRoomDetails();
                System.out.println("Available Rooms : " + availability);
                System.out.println("--------------------------------");
            }
        }
    }
}


// Main Application
public class BookMyStay {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("Welcome to Book My Stay");
        System.out.println("Hotel Booking Management System");
        System.out.println("Version: 4.0");
        System.out.println("=================================");

        // Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        Room[] rooms = {single, doubleRoom, suite};

        // Centralized inventory
        RoomInventory inventory = new RoomInventory();

        // Search service
        RoomSearchService searchService = new RoomSearchService(inventory);

        // Guest searches available rooms
        searchService.searchAvailableRooms(rooms);

        System.out.println("\nSystem state unchanged. Search completed.");
    }
}