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

    // Map<ReservationID, List of Services>
    private Map<String, List<Service>> serviceMap = new HashMap<>();

    // Add services to reservation
    public void addServices(String reservationId, List<Service> services) {
        serviceMap.put(reservationId, services);
    }

    // Get services for reservation
    public List<Service> getServices(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total cost
    public double calculateTotalCost(String reservationId) {
        double total = 0;
        List<Service> services = getServices(reservationId);

        for (Service s : services) {
            total += s.getCost();
        }
        return total;
    }
}

// -------------------- Main Class --------------------
public class BookMyStay {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AddOnServiceManager manager = new AddOnServiceManager();

        System.out.println("===== Book My Stay - Add-On Services =====");

        // Reservation ID input
        System.out.print("Enter Reservation ID: ");
        String reservationId = sc.nextLine();

        List<Service> selectedServices = new ArrayList<>();

        // Number of services
        System.out.print("Enter number of add-on services: ");
        int n = sc.nextInt();
        sc.nextLine(); // consume newline

        // Input services
        for (int i = 0; i < n; i++) {
            System.out.println("\nService " + (i + 1));

            System.out.print("Enter service name: ");
            String name = sc.nextLine();

            System.out.print("Enter service cost: ");
            double cost = sc.nextDouble();
            sc.nextLine(); // consume newline

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

        System.out.println("\n(Booking and room allocation remain unchanged)");

        sc.close();
    }
}