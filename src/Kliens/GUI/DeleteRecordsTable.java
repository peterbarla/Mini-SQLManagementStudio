package Kliens.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Vector;

public class DeleteRecordsTable extends JFrame {
    private JList<String> keyList;
    private DefaultListModel<String> dlm;
    private JTable table;
    private JScrollPane sp;

    private JLabel toDeleteKeyLabel;
    private JTextField toDeleteKeyTextField;
    private JLabel toDeleteValueLabel;
    private JTextField toDeleteValueTextField;

    private JButton deleteButton;

    private String[] keys;

    private static int scrollPaneWidth = 150;
    private static int scrollPaneHeightDecreaser = 35;
    private static int distanceFromTop = 0;
    private static int distanceFromSide = 0;
    private static int distanceFromScroller = 10;
    private static int labelWidth = 200;
    private static int labelHeight = 30;
    private static int textWidth = 100;
    private static int textHeight = 20;
    private static int distanceFromTopKey = 10;
    private static int distanceFromTopValue = textHeight + 10;
    private static int distanceFromBottom = 100;
    private static int deleteWidth = 100;
    private static int deleteHeight = 30;

    private Vector<String> fields;
    private Vector<String> fieldTypes;
    private Vector<String> fieldSpecs;

    public DeleteRecordsTable(String dbName, String tbName, String[] keys, Vector<String> fields, Vector<String> fieldTypes,
                              Vector<String> fieldSpecs){
        setTitle(tbName);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 250);
        this.keys = keys;
        this.fields = fields;
        this.fieldTypes = fieldTypes;
        this.fieldSpecs = fieldSpecs;
        initialize();
        System.out.println(fields);
        System.out.println(fieldTypes);
        System.out.println(fieldSpecs);

        add(sp);
        add(toDeleteKeyLabel);
        add(toDeleteValueLabel);
        add(toDeleteKeyTextField);
        add(toDeleteValueTextField);
        add(deleteButton);

        toDeleteKeyLabel.setBounds(scrollPaneWidth + distanceFromScroller, distanceFromTopKey, labelWidth, labelHeight);
        toDeleteKeyTextField.setBounds(scrollPaneWidth + distanceFromScroller + labelWidth, distanceFromTopKey + 5, textWidth, textHeight);
        toDeleteValueLabel.setBounds(scrollPaneWidth + distanceFromScroller, distanceFromTopValue, labelWidth, labelHeight);
        toDeleteValueTextField.setBounds(scrollPaneWidth + distanceFromScroller + labelWidth, distanceFromTopValue + 5, textWidth, textHeight);
        deleteButton.setBounds(scrollPaneWidth + distanceFromScroller + labelWidth, getHeight() - distanceFromBottom, deleteWidth, deleteHeight);
        sp.setBounds(distanceFromSide,distanceFromTop,scrollPaneWidth,getHeight() - scrollPaneHeightDecreaser);

        deleteButton.addActionListener(e->{
            String key = toDeleteKeyTextField.getText();
            String value = toDeleteValueTextField.getText();

            if(key.equals("") || value.equals("")){
                JOptionPane.showMessageDialog(new JPanel(), "Fill both textFields!", "Warning", JOptionPane.WARNING_MESSAGE);
            }

            String pkOne = "";
            for(int i=0;i<fields.size();i++) {
                if(fieldSpecs.get(i).substring(0, 1).equals("1")){
                    pkOne = fields.get(i);
                    break;
                }
            }
            Boolean canDelete = true;
            System.out.println("check if there is fk");
            for(int i=0;i<fields.size();i++) {
                if(fieldSpecs.get(i).substring(0, 1).equals("1")){
                    System.out.println("fk found!");
                    try {
                        FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                        writer.write(dbName + "\n" + tbName + "\n" + "FKDELETE\n" + key + "\n" + value + "\n" +
                                fields.get(i) + "\n" + pkOne);
                        System.out.println("elkudlve");
                        writer.close();

                    } catch (IOException  ex) {
                        ex.printStackTrace();
                    }

                    File file = new File("Databases/TOCLIENT.txt");
                    while (file.length() == 0) {

                    }

                    try {
                        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOCLIENT.txt"));
                        String fkDeletable = reader.readLine();
                        reader.close();

                        FileWriter writer2 = new FileWriter("Databases/TOCLIENT.txt", false);
                        writer2.write("");
                        writer2.close();

                        if (!fkDeletable.equals("OK")) {
                            canDelete = false;
                        }
                    }catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
                if(!canDelete){
                    JOptionPane.showMessageDialog(new JPanel(), "You can`t delete because foreign key exists in another table!!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                writer.write( dbName + "\n" + tbName + "\nDELETE RECORDS\n" + key + "\n" + value);
                writer.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            File file2 = new File("Databases/TOCLIENT.txt");
            while (file2.length() == 0) {

            }

            try {
                BufferedReader reader = new BufferedReader(new FileReader("Databases/TOCLIENT.txt"));
                String deletionDone = reader.readLine();
                reader.close();

                FileWriter writer2 = new FileWriter("Databases/TOCLIENT.txt", false);
                writer2.write("");
                writer2.close();

                if (!deletionDone.equals("OK")) {
                    canDelete = false;
                }
            }catch (IOException ex) {
                ex.printStackTrace();
            }

            for (int i = 0; i < fields.size(); i++) {
                if (fieldSpecs.get(i).substring(3, 4).equals("1") || fieldSpecs.get(i).substring(1, 2).equals("1")) {
                    try {
                        System.out.println(fields.get(i) + " ezt kell");
                        System.out.println(fieldSpecs.get(i));
                        FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                        writer.write(dbName + "\n" + tbName + "\n" + "BUILDNUI\n" + fields.get(i) + "\n" + pkOne);
                        System.out.println("elkudlve");
                        writer.close();

                        File file = new File("Databases/TOCLIENT.txt");
                        while (file.length() == 0) {

                        }

                        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOCLIENT.txt"));
                        String line = reader.readLine();
                        reader.close();

                        FileWriter writer2 = new FileWriter("Databases/TOCLIENT.txt", false);
                        writer2.write("");
                        writer2.close();


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            for(int i=0;i<fields.size();i++){
                if(fieldSpecs.get(i).substring(2, 3).equals("1") || fieldSpecs.get(i).substring(0, 1).equals("1")){
                    try {
                        System.out.println(fields.get(i) + " ezt kell");
                        System.out.println(fieldSpecs.get(i));
                        FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                        writer.write(dbName + "\n" + tbName + "\n" + "BUILDUI\n" + fields.get(i) + "\n" + pkOne);
                        System.out.println("elkudlve");
                        writer.close();

                        File file = new File("Databases/TOCLIENT.txt");
                        while (file.length() == 0) {

                        }

                        BufferedReader reader = new BufferedReader(new FileReader("Databases/TOCLIENT.txt"));
                        String line = reader.readLine();
                        reader.close();

                        FileWriter writer2 = new FileWriter("Databases/TOCLIENT.txt", false);
                        writer2.write("");
                        writer2.close();


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }


            toDeleteKeyTextField.setText("");
            toDeleteValueTextField.setText("");
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void initialize(){

        dlm = new DefaultListModel<String>();
        keyList = new JList<String>(dlm);
        toDeleteKeyLabel = new JLabel("enter desired key to delete: ");
        toDeleteValueLabel = new JLabel("enter desired value to delete:");
        toDeleteKeyTextField = new JTextField();
        toDeleteValueTextField = new JTextField();
        deleteButton = new JButton("Delete");

        for(String word : fields){
            dlm.addElement(word);
        }
        String[] header = {"Attributes"};
        String[][] data = new String[fields.size()][1];
        for(int i=0;i<fields.size();i++){
            data[i][0] = fields.get(i);
        }
        table = new JTable(data,header);
        table.setRowSelectionAllowed(false);
        sp = new JScrollPane(table);

    }

}
