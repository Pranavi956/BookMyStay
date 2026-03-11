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


// Inventory Service
class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {

        inventory = new HashMap<>();

        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrementRoom(String roomType) {

        int count = inventory.get(roomType);

        if (count > 0) {
            inventory.put(roomType, count - 1);
        }
    }
}


// Reservation Request
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
}


// Booking Request Queue
class BookingRequestQueue {

    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation r) {
        requestQueue.offer(r);
        System.out.println("Request added: " + r.getGuestName());
    }

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }
}


// Booking Service (Allocation)
class BookingService {

    private RoomInventory inventory;

    // roomType → allocated room IDs
    private HashMap<String, Set<String>> allocatedRooms;

    // global set for uniqueness
    private Set<String> allAllocatedRoomIds;

    public BookingService(RoomInventory inventory) {

        this.inventory = inventory;

        allocatedRooms = new HashMap<>();
        allAllocatedRoomIds = new HashSet<>();
    }

    // generate unique room id
    private String generateRoomId(String roomType) {

        String id;

        do {
            id = roomType + "-" + UUID.randomUUID().toString().substring(0, 4);
        }
        while (allAllocatedRoomIds.contains(id));

        return id;
    }


    public void processBookings(BookingRequestQueue queue) {

        while (!queue.isEmpty()) {

            Reservation request = queue.getNextRequest();

            String roomType = request.getRoomType();

            int available = inventory.getAvailability(roomType);

            if (available > 0) {

                String roomId = generateRoomId(roomType);

                allAllocatedRoomIds.add(roomId);

                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(roomId);

                inventory.decrementRoom(roomType);

                System.out.println(
                        "Booking Confirmed → Guest: "
                                + request.getGuestName()
                                + " | Room: "
                                + roomId
                );

            } else {

                System.out.println(
                        "Booking Failed → No "
                                + roomType
                                + " rooms available for "
                                + request.getGuestName()
                );
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
        System.out.println("Version: 6.0");
        System.out.println("=================================");

        RoomInventory inventory = new RoomInventory();

        BookingRequestQueue queue = new BookingRequestQueue();

        // booking requests
        queue.addRequest(new Reservation("Alice", "Single"));
        queue.addRequest(new Reservation("Bob", "Single"));
        queue.addRequest(new Reservation("Charlie", "Single"));
        queue.addRequest(new Reservation("David", "Suite"));

        BookingService bookingService = new BookingService(inventory);

        bookingService.processBookings(queue);
    }
}