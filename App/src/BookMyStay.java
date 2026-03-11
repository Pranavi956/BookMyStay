// Abstract class representing a generic Room
abstract class Room {

    protected String roomType;
    protected int beds;
    protected int size;
    protected double price;

    // Constructor
    public Room(String roomType, int beds, int size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    // Method to display room details
    public void displayRoomDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq ft");
        System.out.println("Price     : ₹" + price + " per night");
    }
}


// Single Room Class
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 200, 2500);
    }
}


// Double Room Class
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 350, 4000);
    }
}


// Suite Room Class
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 600, 8000);
    }
}


// Main Application Class
public class BookMyStay {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("Welcome to Book My Stay");
        System.out.println("Hotel Booking Management System");
        System.out.println("Version: 2.1");
        System.out.println("=================================\n");

        // Create room objects using polymorphism
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability variables
        int singleRoomAvailability = 5;
        int doubleRoomAvailability = 3;
        int suiteRoomAvailability = 2;

        // Display Single Room details
        System.out.println("----- Single Room -----");
        single.displayRoomDetails();
        System.out.println("Available Rooms : " + singleRoomAvailability);
        System.out.println();

        // Display Double Room details
        System.out.println("----- Double Room -----");
        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms : " + doubleRoomAvailability);
        System.out.println();

        // Display Suite Room details
        System.out.println("----- Suite Room -----");
        suite.displayRoomDetails();
        System.out.println("Available Rooms : " + suiteRoomAvailability);
        System.out.println();

        System.out.println("Application Terminated.");
    }
}