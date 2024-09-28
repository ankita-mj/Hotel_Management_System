import java.sql.SQLException;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class Hotel {
    private static final String url="jdbc:mysql://127.0.0.1:3306/hotel_db";
    private static final String username="root";
    private static final String password="";

//main function
     public static void main(String[] args) throws ClassNotFoundException,SQLException {
         try {
             Class.forName("com.mysql.cj.jdbc.Driver");
         } catch (ClassNotFoundException e) {
             System.out.println(e.getMessage());
         }
         try {
             Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             while (true) {
                 System.out.println("Hotel Reservation System");
                 Scanner scanner = new Scanner(System.in);
                 scanner = new Scanner(System.in);
                 System.out.println("1. Available Rooms");
                 System.out.println("2. View Reservations");
                 System.out.println("3. Get Room Number");
                 System.out.println("4. Show Recipt");
                 System.out.println("5. Update Reservation");
                 System.out.println("6. Delete Reservation");
                 System.out.println("7. Reserve a Room");
                 System.out.println("0. Exit");
                 System.out.println("Choose an option:");
                 int ch = scanner.nextInt();
                 switch (ch) {
                     case 1:
                         availableRoom(connection, statement);
                         break;
                     case 2:
                         viewReservations(connection,statement);
                         break;
                     case 3:
                         getRoom(connection, scanner, statement);
                         break;
                     case 4:
                         showBill(connection, statement ,scanner);
                         break;
                     case 5:
                         updateReservation(connection, statement, scanner);
                         break;
                     case 6:
                         deleteReservation(connection, scanner, statement);
                         break;
                     case 7:
                         reserveRoom(connection, scanner,statement);
                         break;
                     case 0:
                         exit();
                         scanner.close();
                         return;
                     default:
                         System.out.println("Invalid choice try again....");
                 }

             }


         } catch (SQLException e) {
             System.out.println(e.getMessage());
         }catch(InterruptedException e){
         }



     }
//     reserve room
    private static void reserveRoom(Connection connection, Scanner sc,Statement statement){
        try {
            System.out.println("Enter guest Name:");
            sc.nextLine();
            String guestName = sc.nextLine();
            System.out.println(guestName+"Enter room number:");
            String roomNumber = sc.next();
            sc.nextLine();
            System.out.println("Enter contact number:");
            String contactNumber = sc.next();

            String query = "INSERT INTO reservation(guest_name, room_no, contact_no) VALUES (?,?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, guestName);
                preparedStatement.setString(2, roomNumber);
                preparedStatement.setString(3, contactNumber);
                int rowAffected = preparedStatement.executeUpdate();
                if (rowAffected > 0) {
                    System.out.println("Reservation Successfully");
                    try{
                    String q = String.format("SELECT reservation_id FROM reservation WHERE guest_name = '%s'",guestName);
                    System.out.println(q);

                        ResultSet r1 = statement.executeQuery(q);
                        System.out.println("receive your bill1");
                        while(r1.next()){
                        int reservationId = r1.getInt("reservation_id");
                        System.out.println("receive your bill2");
                        generateBill(connection, reservationId, statement);
                        }
                    }catch(SQLException e){
                        System.out.println(e.getMessage());
                    }

                } else {
                    System.out.println("Reservation Failed!!");

                }
            }
        }catch (SQLException e) {
            System.out.println("room not available..");
//            e.printStackTrace();
        }
    }
//    veiw reservation
    private static void viewReservations(Connection connection,Statement statement){
        String query = "SELECT reservation_id, guest_name, room_no, contact_no, reservation_date FROM reservation;";
        try {
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("Current Reservations");
            System.out.println("+--------------------+-----------------+--------------+-------------------+-----------------------+");
            System.out.println("| Reservation ID     | Guest           | Room Number  | Contact Number    | Reservation Date      |");
            System.out.println("+--------------------+-----------------+--------------+-------------------+-----------------------+");

            while(resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                String roomNo = resultSet.getString("room_no");
                String contact = resultSet.getString("contact_no");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-18d | %-15s | %-12s | %-17s | %-20s |\n",reservationId,guestName,roomNo,contact,reservationDate);
            }
            System.out.println("+--------------------+-----------------+--------------+-------------------+-----------------------+");
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }
//get room
    private static void getRoom(Connection connection, Scanner sc, Statement statement){
         System.out.println("Please Enter Reservation ID:");
         int reservationId = sc.nextInt();
         String query = String.format("SELECT reservation_id,room_no,guest_name FROM reservation WHERE reservation_id = %d",reservationId);
         try {
             ResultSet resultSet = statement.executeQuery(query);
             System.out.println("+--------------------+----------------+-------------+");
             System.out.println("| Reservation ID     | guest Name     | Room Number |");
             System.out.println("+--------------------+----------------+-------------+");
             while(resultSet.next()) {
                 int reservation_id = resultSet.getInt("reservation_id");
                 String guest_name = resultSet.getString("guest_name");
                 String room_no = resultSet.getString("room_no");
//                 String contact_no = resultSet.getString("contact_no");
//                 String reservation_date = resultSet.getTimestamp("reservation_date").toString();


                 System.out.printf("| %-18d | %-14s | %-11s |\n", reservation_id, guest_name, room_no);
//                 System.out.println("| "+reservation_id+" | "+guest_name+" | "+room_no+" |");

             }
             System.out.println("+--------------------+----------------+-------------+");

         }catch(SQLException e){
             System.out.println(e.getMessage());
         }
    }
//    update reservation
    private static void updateReservation(Connection connection, Statement statement,Scanner sc){
         System.out.println("Enter Reservation Id For Update:");
         int reservationId = sc.nextInt();
         sc.nextLine();
         if(!reservationExists(connection, reservationId, statement)){
            System.out.println("Reservation Not Found For The Given ID..");
            return;
         }

         System.out.println("Enter New Guest Name:");
         String guest_name = sc.nextLine();
        System.out.println("Enter New Room Number:");
         String room_no = sc.next();
         sc.nextLine();
         System.out.println("Enter New Contact Number:");
         String contact = sc.next();
         String query = String.format("UPDATE reservation SET guest_name ='%s',room_no = '%s',contact_no = '%s' WHERE reservation_id = %d",guest_name,room_no,contact,reservationId);
         try {
             int affectedRow = statement.executeUpdate(query);
             if(affectedRow>0){
                 System.out.println("Reservation ID "+reservationId+" Reservation is Updated Successfully..");
             }else{
                 System.out.println("Reservation Updation Faild!!");
             }
         }catch(SQLException e){
             System.out.println(e.getMessage());
         }

    }
//delete reservation
    private static void deleteReservation(Connection connection , Scanner sc,Statement statement){
         try{
             System.out.println("Enter ReservationID:");
             int reservationID = sc.nextInt();
             if(!reservationExists(connection,reservationID,statement)){
                 System.out.println("Reservation Not Found For The Given ID..");
                 return;
             }
             String query = "DELETE FROM reservation WHERE reservation_id = ?";
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             preparedStatement.setInt(1,reservationID);
             int rowAffected = preparedStatement.executeUpdate();
             if(rowAffected>0){
                System.out.println("Reservation ID "+reservationID+" Reservation is DELETED ");
             }
         }catch(SQLException e){
             System.out.println(e.getMessage());
         }

    }
//    Generating the bill
    private static void generateBill(Connection connection,int reservation_id, Statement statement){
             String query = String.format("SELECT * FROM reservation INNER JOIN billing ON reservation.room_no = billing.room_id WHERE reservation_id = %d", reservation_id);
             System.out.println(query);
             try(ResultSet resultSet = statement.executeQuery(query)){
                 if(resultSet.next()) {
                     int reservationId = resultSet.getInt("reservation_id");
                     String guestName = resultSet.getString("guest_name");
                     String roomNo = resultSet.getString("room_no");
                     String contact = resultSet.getString("contact_no");
                     int price = resultSet.getInt("rate");
                     String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                     System.out.println("+-----------------------------------------+");
                     System.out.println("|          HOTEL RESERVATION BILL         |");
                     System.out.println("+-----------------------------------------+");
                     System.out.printf("| Reservation Id: %-23d |\n", reservationId);
                     System.out.printf("| Guest Name: %-27s |\n", guestName);
                     System.out.printf("| Contact Number: %-23s |\n", contact);
                     System.out.printf("| Room Number: %-26s |\n", roomNo);
                     System.out.printf("| Price: %-32d |\n", price);
                     System.out.printf("| Reservation Date: %s |\n", reservationDate);
                     System.out.println("+-----------------------------------------+");
                 }else{
                     System.out.println("no bill is generated try again..");
                 }
         }catch(SQLException e){
             System.out.println(e.getMessage());
         }
    }
//    show bill
    private static void showBill(Connection connection,Statement statement,Scanner sc){
        System.out.println("Enter ReservationID:");
        int reservationID = sc.nextInt();
        if(!reservationExists(connection,reservationID,statement)){
            System.out.println("Reservation Not Found For The Given ID..");
            return;
        }
        generateBill(connection,reservationID,statement);


    }
//    reservation exist
//    available rooms
private static void availableRoom(Connection connection, Statement statement){
         String query = "SELECT room_id,rate FROM reservation RIGHT JOIN billing ON reservation.room_no = billing.room_id WHERE reservation.room_no IS NULL";
         try(ResultSet resultSet = statement.executeQuery(query)){
             System.out.println("+-----------------+-----------------+");
             System.out.println("|          AVAILABLE ROOMS          |");
             System.out.println("+-----------------+-----------------+");
             System.out.println("|     Room No     |      Price      |");
             System.out.println("+-----------------+-----------------+");
             while(resultSet.next()){
                 String room_id = resultSet.getString("room_id");
                 double price = resultSet.getDouble("rate");
                 System.out.printf("| %-16s | %-14f |\n",room_id,price);
             }
             System.out.println("+-----------------+-----------------+");

         }catch(SQLException e){

         }
}

    private static Boolean reservationExists(Connection connection, int reservationId,Statement statement){
        int f=0;
        String query = String.format("SELECT * FROM reservation WHERE reservation_id = %d ",reservationId);
        try {
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                f=1;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        if(f == 1){
            return true;
        }else{
            return false;
        }
    }
//    exit the system
    private static void exit() throws InterruptedException {
         System.out.println("Exiting System");
         int i=5;
         while(i!=0){
             System.out.print(".");
             Thread.sleep(450);
             i--;
         }
         System.out.println();
         System.out.println("ThankYou for using Hotel Management System");

    }


}
