package Kliens.GUI;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CreateOrOpenWindow extends JPanel {
    //creating the new and open buttons and setting their position and size
    private JButton newDatabase;
    private JButton openDatabase;
    private final int buttonWidth = 75;
    private final int buttonHeight = 50;
    private final int distanceFromLeft = 20;
    private final int distanceBetweenButtons = 50;
    private final int distanceFromTop = 30;

    public CreateOrOpenWindow(Window window){
        initialize();
        setLayout(null);
        add(newDatabase);
        add(openDatabase);
        //setting buttons positions and size
        newDatabase.setBounds(distanceFromLeft,distanceFromTop,buttonWidth,buttonHeight);
        openDatabase.setBounds(buttonWidth + distanceFromLeft + distanceBetweenButtons ,distanceFromTop,buttonWidth,buttonHeight);
        //if new button pressed then it opens another window which allows us to enter new database name
        newDatabase.addActionListener(e->{
            new NewDatabaseWindow(window);
        });
        //if open button pressed then it shows the currently existing databases(it will get another functionality later on)
        openDatabase.addActionListener(e->{


            try {
                FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                writer.write( "GET DATABASES");
                writer.close();
                //TODO send to server
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            List<String> databases = new ArrayList<String>();

            File file = new File("Databases/TOCLIENT.txt");
            while(file.length() == 0){

            }
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader("Databases/TOCLIENT.txt"));
                String line = reader.readLine();
                while(line != null){
                    databases.add(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(databases);
            try {
                new DatabaseSelectorWindow(databases);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


    }
    public void initialize(){
        newDatabase = new JButton("New");
        openDatabase = new JButton("Open");
    }
}
