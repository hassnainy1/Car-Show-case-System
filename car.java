import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

 class CarShowcaseApp extends JFrame {

    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private Inventory inventory;

    public CarShowcaseApp() {
        inventory = new Inventory();
        inventory.addVehicle(new ElectricCar("Model S", "Tesla", 79999, "Electric sedan", "In Stock"));
        inventory.addVehicle(new SportCar("Mustang", "Ford", 55999, "Sport coupe", "In Stock"));
        inventory.addVehicle(new Car("Civic", "Honda", 20999, "Compact car", "In Stock"));

        setTitle("Car Showcase App");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Table for Vehicle List
        tableModel = new DefaultTableModel(new Object[]{"Model", "Make", "Price", "Description", "Status"}, 0);
        vehicleTable = new JTable(tableModel);
        updateVehicleTable(inventory.getAllVehicles());
        JScrollPane tableScrollPane = new JScrollPane(vehicleTable);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchVehicles());
        JButton sellButton = new JButton("Sell Vehicle");
        sellButton.addActionListener(e -> sellVehicle());
        JButton purchaseButton = new JButton("Purchase Vehicle");
        purchaseButton.addActionListener(e -> purchaseVehicle());
        buttonPanel.add(searchButton);
        buttonPanel.add(sellButton);
        buttonPanel.add(purchaseButton);

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void updateVehicleTable(List<Vehicle> vehicles) {
        tableModel.setRowCount(0); // Clear the table
        for (Vehicle vehicle : vehicles) {
            tableModel.addRow(new Object[]{vehicle.getModel(), vehicle.getMake(), vehicle.getPrice(), vehicle.getDescription(), vehicle.getStatus()});
        }
    }

    private void searchVehicles() {
        String keyword = JOptionPane.showInputDialog(this, "Enter search keyword:");
        if (keyword != null && !keyword.isEmpty()) {
            List<Vehicle> results = inventory.searchVehicles(keyword);
            updateVehicleTable(results);
        }
    }

    private void sellVehicle() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow != -1) {
            String status = (String) tableModel.getValueAt(selectedRow, 4);
            if ("In Stock".equals(status)) {
                String model = (String) tableModel.getValueAt(selectedRow, 0);
                String make = (String) tableModel.getValueAt(selectedRow, 1);
                Vehicle vehicle = inventory.getVehicle(model, make);
                String buyer = JOptionPane.showInputDialog(this, "Enter buyer's name:");
                if (buyer != null && !buyer.isEmpty()) {
                    vehicle.setStatus("Sold Out");
                    updateVehicleTable(inventory.getAllVehicles());
                    JOptionPane.showMessageDialog(this, "Vehicle sold to " + buyer);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selected vehicle is already sold out.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No vehicle selected.");
        }
    }

    private void purchaseVehicle() {
        JTextField modelField = new JTextField();
        JTextField makeField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField sellerField = new JTextField();
        Object[] message = {
                "Model:", modelField,
                "Make:", makeField,
                "Price:", priceField,
                "Description:", descriptionField,
                "Seller:", sellerField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Purchase Vehicle", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String model = modelField.getText();
            String make = makeField.getText();
            double price = Double.parseDouble(priceField.getText());
            String description = descriptionField.getText();
            String seller = sellerField.getText();

            Vehicle vehicle = new Car(model, make, price, description, "In Stock");
            inventory.addVehicle(vehicle);
            updateVehicleTable(inventory.getAllVehicles());
            JOptionPane.showMessageDialog(this, "Vehicle purchased from " + seller);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarShowcaseApp app = new CarShowcaseApp();
            app.setVisible(true);
        });
    }

    // Abstract class for Vehicle
    public abstract class Vehicle {
        private String model;
        private String make;
        private double price;
        private String description;
        private String status; // "In Stock" or "Sold Out"

        public Vehicle(String model, String make, double price, String description, String status) {
            this.model = model;
            this.make = make;
            this.price = price;
            this.description = description;
            this.status = status;
        }

        public String getModel() {
            return model;
        }

        public String getMake() {
            return make;
        }

        public double getPrice() {
            return price;
        }

        public String getDescription() {
            return description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return model + " " + make + " - $" + price + " - " + description + " - " + status;
        }

        public abstract String getVehicleType();
    }

    // Car class
    public class Car extends Vehicle {
        public Car(String model, String make, double price, String description, String status) {
            super(model, make, price, description, status);
        }

        @Override
        public String getVehicleType() {
            return "Car";
        }
    }

    // ElectricCar class
    public class ElectricCar extends Car {
        public ElectricCar(String model, String make, double price, String description, String status) {
            super(model, make, price, description, status);
        }

        @Override
        public String getVehicleType() {
            return "Electric Car";
        }
    }

    // SportCar class
    public class SportCar extends Car {
        public SportCar(String model, String make, double price, String description, String status) {
            super(model, make, price, description, status);
        }

        @Override
        public String getVehicleType() {
            return "Sport Car";
        }
    }

    // Inventory class
    public class Inventory {
        private List<Vehicle> vehicles;

        public Inventory() {
            vehicles = new ArrayList<>();
        }

        public void addVehicle(Vehicle vehicle) {
            vehicles.add(vehicle);
        }

        public List<Vehicle> searchVehicles(String keyword) {
            return vehicles.stream()
                    .filter(vehicle -> vehicle.getModel().toLowerCase().contains(keyword.toLowerCase()) ||
                            vehicle.getMake().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        public List<Vehicle> getAllVehicles() {
            return new ArrayList<>(vehicles);
        }

        public Vehicle getVehicle(String model, String make) {
            return vehicles.stream()
                    .filter(vehicle -> vehicle.getModel().equals(model) && vehicle.getMake().equals(make))
                    .findFirst()
                    .orElse(null);
        }
    }
}
