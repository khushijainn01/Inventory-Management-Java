import java.sql.*;
import java.util.*;

public class InventoryApp {

    static final String DB_URL = "jdbc:sqlite:inventory.db";

    public static void main(String[] args) {
        initializeDatabase();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Inventory Management System =====");
            System.out.println("1. Add Item");
            System.out.println("2. Update Item");
            System.out.println("3. Delete Item");
            System.out.println("4. View Inventory");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addItem(scanner);
                    break;
                case 2:
                    updateItem(scanner);
                    break;
                case 3:
                    deleteItem(scanner);
                    break;
                case 4:
                    viewInventory();
                    break;
                case 5:
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 5);

        scanner.close();
    }

    // === Database Setup ===
    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            return null;
        }
    }

    public static void initializeDatabase() {
        String createTable = "CREATE TABLE IF NOT EXISTS inventory ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "price REAL NOT NULL"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
        } catch (SQLException e) {
            System.out.println("Failed to create table: " + e.getMessage());
        }
    }

    // === Core Features ===

    public static void addItem(Scanner scanner) {
        System.out.print("Item Name: ");
        String name = scanner.next();
        System.out.print("Quantity: ");
        int qty = scanner.nextInt();
        System.out.print("Price: ");
        double price = scanner.nextDouble();

        String sql = "INSERT INTO inventory(name, quantity, price) VALUES(?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, qty);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
            System.out.println("Item added successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to add item: " + e.getMessage());
        }
    }

    public static void updateItem(Scanner scanner) {
        System.out.print("Item ID to update: ");
        int id = scanner.nextInt();
        System.out.print("New Quantity: ");
        int qty = scanner.nextInt();
        System.out.print("New Price: ");
        double price = scanner.nextDouble();

        String sql = "UPDATE inventory SET quantity = ?, price = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, qty);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, id);
            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Item updated." : "Item not found.");
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    public static void deleteItem(Scanner scanner) {
        System.out.print("Item ID to delete: ");
        int id = scanner.nextInt();

        String sql = "DELETE FROM inventory WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "Item deleted." : "Item not found.");
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    public static void viewInventory() {
        String sql = "SELECT * FROM inventory";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nID\tName\tQty\tPrice");
            System.out.println("-----------------------------------");
            while (rs.next()) {
                System.out.printf("%d\t%s\t%d\t%.2f\n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("View failed: " + e.getMessage());
        }
    }
}
