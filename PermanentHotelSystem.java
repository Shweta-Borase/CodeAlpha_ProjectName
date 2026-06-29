import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class Room {
    int roomNumber;
    String category;
    double pricePerNight;
    boolean isAvailable;

    public Room(int roomNumber, String category, double pricePerNight, boolean isAvailable) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.isAvailable = isAvailable;
    }

    // Convert object data to a clean line of text with commas
    public String toFileString() {
        return roomNumber + "," + category + "," + pricePerNight + "," + isAvailable;
    }
}

class Reservation {
    String bookingId;
    String guestName;
    int roomNumber;
    boolean isActive;

    public Reservation(String bookingId, String guestName, int roomNumber, boolean isActive) {
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.isActive = isActive;
    }

    // Convert object data to a clean line of text with commas
    public String toFileString() {
        return bookingId + "," + guestName + "," + roomNumber + "," + isActive;
    }
}

public class PermanentHotelSystem {
    private static ArrayList<Room> rooms = new ArrayList<>();
    private static ArrayList<Reservation> reservations = new ArrayList<>();

    // Names of our simple text database files
    private static final String ROOMS_FILE = "rooms_data.txt";
    private static final String BOOKINGS_FILE = "bookings_data.txt";

    public static void main(String[] args) {
        // Load data from hard drive first!
        loadDataFromFiles();

        // If file was empty, initialize default rooms
        if (rooms.isEmpty()) {
            rooms.add(new Room(101, "Standard", 1500.0, true));
            rooms.add(new Room(102, "Standard", 1500.0, true));
            rooms.add(new Room(201, "Deluxe", 3000.0, true));
            rooms.add(new Room(301, "Suite", 6000.0, true));
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== HOTEL SYSTEM (PERMANENT STORAGE) ===");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View All Bookings");
            System.out.println("5. Save & Exit");
            System.out.print("Choose option (1-5): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.println("\n--- AVAILABLE ROOMS ---");
                for (Room r : rooms) {
                    if (r.isAvailable) {
                        System.out.println("Room " + r.roomNumber + " [" + r.category + "] - Rs." + r.pricePerNight);
                    }
                }
            } else if (choice.equals("2")) {
                System.out.print("Enter Guest Name: ");
                String name = scanner.nextLine().trim();
                System.out.print("Enter Room Number to book: ");
                int roomNum = Integer.parseInt(scanner.nextLine());

                Room selectedRoom = null;
                for (Room r : rooms) {
                    if (r.roomNumber == roomNum && r.isAvailable) {
                        selectedRoom = r;
                        break;
                    }
                }

                if (selectedRoom != null) {
                    selectedRoom.isAvailable = false;
                    String bId = "BKID" + (reservations.size() + 1001);
                    reservations.add(new Reservation(bId, name, roomNum, true));

                    saveDataToFiles(); // Auto-save changes to file!
                    System.out.println("Success! Booking ID: " + bId);
                } else {
                    System.out.println("Room is unavailable or doesn't exist.");
                }
            } else if (choice.equals("3")) {
                System.out.print("Enter Booking ID to cancel: ");
                String id = scanner.nextLine().trim();

                for (Reservation res : reservations) {
                    if (res.bookingId.equalsIgnoreCase(id) && res.isActive) {
                        res.isActive = false;
                        for (Room r : rooms) {
                            if (r.roomNumber == res.roomNumber)
                                r.isAvailable = true;
                        }
                        saveDataToFiles(); // Auto-save changes to file!
                        System.out.println("Booking cancelled successfully.");
                        break;
                    }
                }
            } else if (choice.equals("4")) {
                System.out.println("\n--- ALL RESERVATIONS ---");
                for (Reservation res : reservations) {
                    String status = res.isActive ? "CONFIRMED" : "CANCELLED";
                    System.out.println("ID: " + res.bookingId + " | Guest: " + res.guestName + " | Room: "
                            + res.roomNumber + " | [" + status + "]");
                }
            } else if (choice.equals("5")) {
                saveDataToFiles(); // Final backup save before exiting
                System.out.println("All data saved to files. Goodbye!");
                break;
            }
        }
        scanner.close();
    }

    // --- WRITE TO FILE ---
    private static void saveDataToFiles() {
        try {
            // Save Rooms List
            BufferedWriter roomWriter = new BufferedWriter(new FileWriter(ROOMS_FILE));
            for (Room r : rooms) {
                roomWriter.write(r.toFileString());
                roomWriter.newLine();
            }
            roomWriter.close();

            // Save Bookings List
            BufferedWriter bookingWriter = new BufferedWriter(new FileWriter(BOOKINGS_FILE));
            for (Reservation res : reservations) {
                bookingWriter.write(res.toFileString());
                bookingWriter.newLine();
            }
            bookingWriter.close();
        } catch (IOException e) {
            System.out.println("Error saving to files!");
        }
    }

    // --- READ FROM FILE ---
    private static void loadDataFromFiles() {
        try {
            // Load Rooms File
            File rFile = new File(ROOMS_FILE);
            if (rFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(rFile));
                String line;
                rooms.clear();
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    int num = Integer.parseInt(parts[0]);
                    String cat = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    boolean avail = Boolean.parseBoolean(parts[3]);
                    rooms.add(new Room(num, cat, price, avail));
                }
                reader.close();
            }

            // Load Bookings File
            File bFile = new File(BOOKINGS_FILE);
            if (bFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(bFile));
                String line;
                reservations.clear();
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String id = parts[0];
                    String name = parts[1];
                    int rNum = Integer.parseInt(parts[2]);
                    boolean active = Boolean.parseBoolean(parts[3]);
                    reservations.add(new Reservation(id, name, rNum, active));
                }
                reader.close();
            }
        } catch (Exception e) {
            System.out.println("Error loading data files!");
        }
    }
}