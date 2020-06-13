package Kliens.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSelectorWindow extends JFrame{
    private JPanel myPanel;
    private JButton selectButton;

    public DatabaseSelectorWindow(List<String> databases) throws IOException {
        setTitle("Databases");
        setLayout(new GridLayout(1,2));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 250);


        initialize();


        JPanel panel = new JPanel(new BorderLayout());
        List<String> myList = new ArrayList<>(10);
        for(String database : databases){
            myList.add(database);
        }
        final JList<String> list = new JList<String>(myList.toArray(new String[myList.size()]));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        panel.add(scrollPane);

        FileWriter writer = new FileWriter("Databases/TOCLIENT.txt", false);
        writer.write("");
        writer.close();



        myPanel.setLayout(null);
        selectButton.setBounds(myPanel.getWidth()/2 + 50,50,100,50);
        myPanel.add(selectButton);

        selectButton.addActionListener(e->{
            if(list.isSelectionEmpty()){
                JOptionPane.showMessageDialog(new JPanel(), "Choose a database!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            else {
                try {
                    FileWriter writer2 = new FileWriter("Databases/TOSERVER.txt", false);
                    writer2.write(list.getSelectedValue() + "\n" + "GET TABLES");
                    writer2.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                this.dispose();
                List<String> tables = new ArrayList<>();

                File file = new File("Databases/TOCLIENT.txt");
                while (file.length() == 0) {

                }

                try {
                    BufferedReader reader = new BufferedReader(new FileReader("Databases/TOCLIENT.txt"));
                    String line = reader.readLine();
                    while (line != null) {
                        tables.add(line);
                        line = reader.readLine();
                    }
                    reader.close();
                    FileWriter writer2 = new FileWriter("Databases/TOCLIENT.txt", false);
                    writer2.write("");
                    writer2.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (list.getSelectedIndex() != -1) {
                    try {
                        new DatabaseEditorWindow(list.getSelectedValue(), tables);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }


        });

        add(panel);
        add(myPanel);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public void initialize(){
        myPanel = new JPanel();
        selectButton = new JButton("Select");
    }
}
