import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.Color;
import java.sql.*;


class SharedVar{
    private static Connection conn;
    public static synchronized void getConn(Connection c)
    {
        conn = c;
    }
    public static synchronized Connection retConn(){
        return conn;
    }
}

class SQLConnect extends Exception implements Runnable{
    public void run()
    {
        try{
        Class.forName("oracle.jdbc.driver.OracleDriver");

        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","<User-id>","<pass>");
        SharedVar.getConn(conn);
        System.out.println("Connected!");
        
    }
    catch(Exception e)
    {
        System.out.println("COULDN'T CONNECT!");
        System.out.println(e.getMessage());
    }
}
}

public class SwingJDBC {
    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(new SQLConnect());
        t1.start();
        JFrame f = new JFrame();
        f.setSize(300,450);
        f.setLayout(null);
        
        JLabel name = new JLabel("Name");
        name.setBounds(50,100,100,30);
        f.add(name);

        JTextField namefield = new JTextField();
        namefield.setBounds(125,100,100,30);
        f.add(namefield);

        JLabel roll = new JLabel("Roll:");
        roll.setBounds(50,150,100,30);
        f.add(roll);

        JTextField rollfield = new JTextField();
        rollfield.setBounds(125,150,100,30);
        f.add(rollfield);

        JLabel marks = new JLabel("Marks:");
        marks.setBounds(50,200,100,30);
        f.add(marks);

        JTextField marksfield = new JTextField();
        marksfield.setBounds(125,200,100,30);
        f.add(marksfield);

        JButton submit = new JButton("Submit");
        submit.setBounds(85,275,100,30);
        f.add(submit);

        JLabel status = new JLabel();
        status.setBounds(50,325,200,50);
        status.setVisible(false);
        f.add(status);

        f.setVisible(true);
        submit.addActionListener(e->{
            status.setVisible(false);
            if(namefield.getText().equals("") || rollfield.getText().equals("") || marksfield.getText().equals(""))
                {
                status.setVisible(true);
                status.setForeground(Color.RED);
                status.setText("Missing information!");
                }
            else{
                status.setVisible(true);
                status.setForeground(Color.BLACK);
                try{
                t1.join();
                status.setText("Connecting to Database...");
                }
                catch (Exception err)
                {
                    System.out.println(err.getMessage());
                }
                Connection conn = SharedVar.retConn();
                try{
                    PreparedStatement ps = conn.prepareStatement("Insert into TestStudent values (?,?,?)");
                    try{
                    ps.setInt(1, Integer.parseInt(rollfield.getText().trim()));
                    ps.setString(2, namefield.getText().trim());
                    ps.setInt(3, Integer.parseInt((marksfield.getText().trim())));
                    }
                    catch(Exception err)
                    {
                        status.setVisible(true);
                        status.setText("Mismatched inpput data type: "+err.getMessage());
                        status.setForeground(Color.RED);
                    }
                    try{
                        ps.executeUpdate();
                        status.setVisible(true);
                        status.setText("DONE!");
                        status.setForeground(Color.GREEN);

                    }
                    catch(SQLIntegrityConstraintViolationException InsertError){
                        status.setVisible(true);
                        status.setText("ROLL NUMBER ALREADY IN DATABASE!");
                        status.setForeground(Color.RED);
                    }
                    catch(SQLException SQLErr)
                    {
                        status.setVisible(true);
                        status.setText("Some database error occured: "+SQLErr.getErrorCode()+"\nError: "+SQLErr.getMessage());
                        status.setForeground(Color.RED);
                    }
                    catch(Exception err)
                    {
                        status.setVisible(true);
                        status.setText("Some error occured: "+err.getMessage());
                        status.setForeground(Color.RED);
                    }
                }
                catch(Exception err2){
                    System.out.println(err2.getLocalizedMessage());
                }
            }
        });
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
