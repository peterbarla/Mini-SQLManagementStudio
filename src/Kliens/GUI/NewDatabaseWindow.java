package Kliens.GUI;

import DatabaseCollections.Database;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class NewDatabaseWindow extends JFrame {
    //creating a textarea where we write the databases name and a label which tells where to write the name
    private TextField insertNameText;
    private JLabel insertNameLabel;
    private static int distanceBetweenRows = 20;
    private static int distanceFromLeft = 30;
    private static int distanceFromTop = 30;
    private static int distanceBetweenCols = 20;
    private static int labelWidth = 150;
    private static int labelHeight = 20;
    private static int textWidth = 150;
    private static int textHeight = 20;
    //creating the create and cancel buttons
    private JButton create;
    private JButton cancel;

    private static int buttonWidth = 100;
    private static int buttonHeight = 40;

    private String dataBaseName;
    //creating references to another window and to the Database class
    private String dbName;

    public NewDatabaseWindow(Window window){
        setBounds(100,100,400,200);
        setTitle("Create Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialize();
        setLayout(null);
        //adding gui element
        add(insertNameText);
        add(insertNameLabel);
        add(create);
        add(cancel);
        //positioning elements
        insertNameLabel.setBounds(distanceFromLeft, distanceFromTop,labelWidth, labelHeight );
        insertNameText.setBounds(distanceFromLeft + insertNameLabel.getWidth() + distanceBetweenCols, distanceFromTop,textWidth,textHeight );

        create.setBounds(distanceFromLeft, distanceFromTop + insertNameLabel.getHeight() + distanceBetweenRows,buttonWidth,buttonHeight);
        cancel.setBounds(distanceFromLeft + insertNameLabel.getWidth() + distanceBetweenCols, distanceFromTop + insertNameLabel.getHeight() + distanceBetweenRows,buttonWidth,buttonHeight);
        //if cancel button pressed then it returns us to the window before
        cancel.addActionListener(e->{
            this.dispose();
        });
        //if create pressed then it saves our new database in the DatabasCollection static class and creates a new Database object
        create.addActionListener(e->{
            dbName = insertNameText.getText();
            if (!dbName.equals("")) {
                    writeIntoTOSERVER(dbName);

                } else {
                    JOptionPane.showMessageDialog(new JPanel(), "Enter a database name!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
                this.dispose();



        });

        setVisible(true);
    }

    public void initialize(){
        insertNameText = new TextField();
        insertNameLabel = new JLabel("database name: ");
        create = new JButton("Create");
        cancel = new JButton("Cancel");
    }

    public void writeIntoTOSERVER(String dbName){
        String currentContent = "";
        try {
            File txt =
                    new File("Databases/TOSERVER.txt");
            Scanner sc = new Scanner(txt);

            while (sc.hasNextLine())
                currentContent = sc.nextLine();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        if (currentContent != dbName + ".txt") {
            try {
                FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                writer.write(dbName + "\nCREATE DATABASE");
                //System.out.println(database);
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
