package Kliens.GUI;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    //adding panel which contains new and open buttons
    private CreateOrOpenWindow createOrOpenWindow;
    public Window(){
        setTitle("Database Manager");
        initialize();
        setBounds(300,400,300,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1,1));
        add(createOrOpenWindow);



        setVisible(true);
    }

    public void initialize(){
        createOrOpenWindow = new CreateOrOpenWindow(this);
    }
}
