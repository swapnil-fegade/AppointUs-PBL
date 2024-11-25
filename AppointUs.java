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
                if (line.isEmpty())
                    continue;

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
                                    provider.addService(new Service(serviceParts[0].trim(),
                                            Double.parseDouble(serviceParts[1].trim())));
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

    // Parsing test cases and executing them
    public static void runTestCases(String filename, List<Customer> customers, List<ServiceProvider> providers) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    // Skip comments or empty lines
                    continue;
                }

                System.out.println("Running test case: " + line);
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    System.out.println("Invalid test case format: " + line);
                    continue;
                }

                String testCaseType = parts[0].trim();
                switch (testCaseType) {
                    case "LoginTest" -> handleLoginTest(parts, customers);
                    case "BookAppointmentTest" -> handleBookAppointmentTest(parts, customers, providers);
                    case "CheckServiceAvailabilityTest" -> handleCheckServiceAvailabilityTest(parts, providers);
                    case "ListCustomerAppointmentsTest" -> handleListCustomerAppointmentsTest(parts, customers);
                    case "ListProviderAppointmentsTest" -> handleListProviderAppointmentsTest(parts, providers);
                    case "ValidateCustomerDataTest" -> handleValidateCustomerDataTest(parts, customers);
                    case "ValidateProviderDataTest" -> handleValidateProviderDataTest(parts, providers);
                    default -> System.out.println("Test case type not supported: " + testCaseType);
                }
                // Added space line
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Error reading test cases file: " + e.getMessage());
        }
    }

    public static void handleCheckServiceAvailabilityTest(String[] parts, List<ServiceProvider> providers) {
        String serviceName = parts[1].trim();
        boolean isAvailable = false;

        System.out.println("Checking service availability: " + serviceName);
        for (ServiceProvider provider : providers) {
            for (Service service : provider.getServicesOffered()) {
                if (service.getServiceName().equalsIgnoreCase(serviceName)) {
                    System.out.println("Service " + serviceName + " is available with " + provider.name + " (INR "
                            + service.getPrice() + ")");
                    isAvailable = true;
                }
            }
        }

        if (!isAvailable) {
            System.out.println("Service " + serviceName + " is not available with any provider.");
        }
        System.out.println("CheckServiceAvailabilityTest " + (isAvailable ? "Passed" : "Failed"));
    }

    public static void handleListCustomerAppointmentsTest(String[] parts, List<Customer> customers) {
        String customerName = parts[1].trim();
        boolean found = false;

        System.out.println("Listing appointments for customer: " + customerName);
        for (Customer customer : customers) {
            if (customer.name.equalsIgnoreCase(customerName)) {
                customer.listBookedAppointments();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No appointments found for customer " + customerName);
        }
        System.out.println("ListCustomerAppointmentsTest " + (found ? "Passed" : "Failed"));
    }

    public static void handleListProviderAppointmentsTest(String[] parts, List<ServiceProvider> providers) {
        String providerName = parts[1].trim();
        boolean found = false;

        System.out.println("Listing appointments for provider: " + providerName);
        for (ServiceProvider provider : providers) {
            if (provider.name.equalsIgnoreCase(providerName)) {
                provider.listAppointments();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No appointments found for provider " + providerName);
        }
        System.out.println("ListProviderAppointmentsTest " + (found ? "Passed" : "Failed"));
    }

    public static void handleValidateCustomerDataTest(String[] parts, List<Customer> customers) {
        String customerName = parts[1].trim();
        boolean found = false;

        System.out.println("Validating customer data for: " + customerName);
        for (Customer customer : customers) {
            if (customer.name.equalsIgnoreCase(customerName)) {
                customer.displayInfo();
                found = true;
            }
        }

        if (!found) {
            System.out.println("Customer " + customerName + " does not exist in the system.");
        }
        System.out.println("ValidateCustomerDataTest " + (found ? "Passed" : "Failed"));
    }

    public static void handleLoginTest(String[] parts, List<Customer> customers) {
        String expectedName = parts[1].trim();
        String email = parts[2].trim();
        boolean loggedIn = false;

        System.out.println("Attempting login for email: " + email);
        for (Customer customer : customers) {
            // Now compare the email to find the customer
            if (customer.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Checking customer: " + customer.getEmail());
                if (customer.name.equalsIgnoreCase(expectedName)) {
                    System.out.println("Login successful for customer: " + customer.name);
                    loggedIn = true;
                    break;
                }
            }
        }

        if (!loggedIn) {
            System.out.println("Login failed for email: " + email + ". Customer not found.");
        }
        System.out.println("LoginTest " + (loggedIn ? "Passed" : "Failed") + "\n");
    }

    public static void handleBookAppointmentTest(String[] parts, List<Customer> customers,
            List<ServiceProvider> providers) {
        String customerName = parts[1].trim();
        String serviceName = parts[2].trim();
        String providerName = parts[3].trim();
        String date = parts.length > 4 ? parts[4].trim() : "2024-11-10"; // Default date if not provided

        System.out.println("Attempting to book appointment:");
        System.out.println("Customer: " + customerName + ", Service: " + serviceName + ", Provider: " + providerName
                + ", Date: " + date);

        Customer bookingCustomer = null;
        ServiceProvider bookingProvider = null;
        Service bookingService = null;

        // Find the customer
        for (Customer customer : customers) {
            if (customer.name.equalsIgnoreCase(customerName)) {
                bookingCustomer = customer;
                break;
            }
        }

        // Find the provider and validate the service
        for (ServiceProvider provider : providers) {
            if (provider.name.equalsIgnoreCase(providerName)) {
                bookingProvider = provider;
                for (Service service : provider.getServicesOffered()) {
                    if (service.getServiceName().equalsIgnoreCase(serviceName)) {
                        bookingService = service;
                        break;
                    }
                }
            }
        }

        if (bookingCustomer != null && bookingProvider != null && bookingService != null) {
            Appointment appointment = new Appointment(date, bookingService, bookingProvider);
            bookingCustomer.bookAppointment(appointment);
            bookingProvider.addAppointment(appointment);
            System.out.println("Appointment booked successfully!");
            System.out.println("BookAppointmentTest Passed");
        } else {
            if (bookingCustomer == null) {
                System.out.println("Customer not found: " + customerName);
            }
            if (bookingProvider == null) {
                System.out.println("Provider not found: " + providerName);
            } else if (bookingService == null) {
                System.out.println("Service not found or not offered by provider: " + serviceName);
            }
            System.out.println("BookAppointmentTest Failed");
        }
    }

    public static void handleValidateProviderDataTest(String[] parts, List<ServiceProvider> providers) {
        String providerName = parts[1].trim();
        boolean found = false;

        System.out.println("Validating provider data for: " + providerName);
        for (ServiceProvider provider : providers) {
            if (provider.name.equalsIgnoreCase(providerName)) {
                provider.displayInfo();
                System.out.println("Services offered by " + provider.name + ":");
                for (Service service : provider.getServicesOffered()) {
                    System.out.println("- " + service.getServiceName() + " (INR " + service.getPrice() + ")");
                }
                found = true;
            }
        }

        if (!found) {
            System.out.println("Provider " + providerName + " does not exist in the system.");
        }
        System.out.println("ValidateProviderDataTest " + (found ? "Passed" : "Failed"));
    }

} // final