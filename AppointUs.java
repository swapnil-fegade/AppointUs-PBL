import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// User class (Base class)
class User {
    protected String name;
    protected String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
    }
}

// ServiceProvider class (Inherits from User)
class ServiceProvider extends User {
    private List<Appointment> appointments;
    private List<Service> servicesOffered;

    public ServiceProvider(String name, String email) {
        super(name, email);
        this.appointments = new ArrayList<>();
        this.servicesOffered = new ArrayList<>();
    }

    public void addService(Service service) {
        servicesOffered.add(service);
    }

    public List<Service> getServicesOffered() {
        return servicesOffered;
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public void listAppointments() {
        System.out.println("Appointments for " + name + ":");
        for (Appointment appt : appointments) {
            System.out.println("Date: " + appt.getDate() + ", Service: " + appt.getService().getServiceName());
        }
    }
}

// Customer class (Inherits from User)
class Customer extends User {
    private List<Appointment> bookedAppointments;

    public Customer(String name, String email) {
        super(name, email);
        this.bookedAppointments = new ArrayList<>();
    }

    public void bookAppointment(Appointment appointment) {
        bookedAppointments.add(appointment);
        System.out.println(name + " booked an appointment for " + appointment.getService().getServiceName());
    }

    public void listBookedAppointments() {
        System.out.println("Booked appointments for " + name + ":");
        for (Appointment appt : bookedAppointments) {
            System.out.println("Date: " + appt.getDate() + ", Service: " + appt.getService().getServiceName());
        }
    }
}

// Service class
class Service {
    private String serviceName;
    private double price;

    public Service(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }
}

// Appointment class
class Appointment {
    private String date;
    private Service service;
    private ServiceProvider provider;

    public Appointment(String date, Service service, ServiceProvider provider) {
        this.date = date;
        this.service = service;
        this.provider = provider;
    }

    public String getDate() {
        return date;
    }

    public Service getService() {
        return service;
    }

    public ServiceProvider getProvider() {
        return provider;
    }
}
public class AppointUs {
    public static void main(String[] args) {
        List<Customer> customers = new ArrayList<>();
        List<ServiceProvider> providers = new ArrayList<>();

        // Load customer and provider data
        loadDataFromFile("data.txt", customers, providers);

        // Run test cases
        runTestCases("testcase.txt", customers, providers);
    }

    // Method to load data from file
    public static void loadDataFromFile(String filename, List<Customer> customers, List<ServiceProvider> providers) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("Customer")) {
                    String[] parts = line.split(",", 3);
                    if (parts.length == 3) {
                        customers.add(new Customer(parts[1].trim(), parts[2].trim()));
                    }
                } else if (line.startsWith("Provider")) {
                    String[] parts = line.split(",", 3);
                    if (parts.length == 3) {
                        ServiceProvider provider = new ServiceProvider(parts[1].trim(), parts[2].trim());
                        line = br.readLine();
                        if (line != null && line.startsWith("Services:")) {
                            String[] services = line.substring(9).split(";");
                            for (String service : services) {
                                String[] serviceParts = service.split("@");
                                if (serviceParts.length == 2) {
                                    provider.addService(new Service(serviceParts[0].trim(), Double.parseDouble(serviceParts[1].trim())));
                                }
                            }
                        }
                        providers.add(provider);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading data file: " + e.getMessage());
        }
    }

    // Method to run test cases
    public static void runTestCases(String filename, List<Customer> customers, List<ServiceProvider> providers) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 5); // Split into test case components
                if (parts.length >= 4) {
                    String testType = parts[0].trim();
                    String customerName = parts[1].trim();
                    String emailOrService = parts[2].trim();
                    String providerOrExpected = parts[3].trim();

                    System.out.println("\nRunning test case: " + testType);

                    switch (testType) {
                        case "LoginTest" -> {
                            Customer customer = customers.stream()
                                    .filter(c -> c.getEmail().equals(emailOrService))
                                    .findFirst()
                                    .orElse(null);
                            boolean result = customer != null;
                            System.out.println("Checking login for email: " + emailOrService);
                            System.out.println("Expected: " + providerOrExpected);
                            System.out.println("Result: " + (result ? "Pass" : "Fail"));
                        }
                        case "BookAppointmentTest" -> {
                            Customer customer = customers.stream()
                                    .filter(c -> c.name.equals(customerName))
                                    .findFirst()
                                    .orElse(null);
                            ServiceProvider provider = providers.stream()
                                    .filter(p -> p.name.equals(providerOrExpected))
                                    .findFirst()
                                    .orElse(null);
                            if (customer != null && provider != null) {
                                Service service = provider.getServicesOffered().stream()
                                        .filter(s -> s.getServiceName().equals(emailOrService))
                                        .findFirst()
                                        .orElse(null);
                                if (service != null) {
                                    Appointment appointment = new Appointment("2024-11-09", service, provider);
                                    customer.bookAppointment(appointment);
                                    provider.addAppointment(appointment);
                                    System.out.println("Booking appointment for " + customerName + " with " + providerOrExpected);
                                    System.out.println("Expected: " + providerOrExpected);
                                    System.out.println("Result: Pass");
                                } else {
                                    System.out.println("Service not found: " + emailOrService);
                                    System.out.println("Result: Fail");
                                }
                            } else {
                                System.out.println("Customer or Provider not found.");
                                System.out.println("Result: Fail");
                            }
                        }
                        default -> System.out.println("Unknown test case type: " + testType);
                    }
                } else {
                    System.err.println("Invalid test case format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading test cases file: " + e.getMessage());
        }
    }
}