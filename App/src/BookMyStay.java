import java.util.*;

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

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean isEmpty() {
        return requestQueue.isEmpty();
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


// Booking Service (Room Allocation)
class BookingService {

    private RoomInventory inventory;

    private HashMap<String, Set<String>> allocatedRooms;
    private Set<String> allAllocatedRoomIds;

    public BookingService(RoomInventory inventory) {

        this.inventory = inventory;

        allocatedRooms = new HashMap<>();
        allAllocatedRoomIds = new HashSet<>();
    }

    private String generateRoomId(String roomType) {

        String id;

        do {
            id = roomType + "-" + UUID.randomUUID().toString().substring(0,4);
        } while (allAllocatedRoomIds.contains(id));

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
                        + " | Room ID: "
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

        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Alice", "Single"));
        queue.addRequest(new Reservation("Bob", "Double"));
        queue.addRequest(new Reservation("Charlie", "Single"));
        queue.addRequest(new Reservation("David", "Suite"));

        RoomInventory inventory = new RoomInventory();

        BookingService bookingService = new BookingService(inventory);

        bookingService.processBookings(queue);
    }
}