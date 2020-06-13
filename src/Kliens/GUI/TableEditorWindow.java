package Kliens.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TableEditorWindow extends JFrame {
    private JList<String> keyList;
    private DefaultListModel<String> dlm;
    private JTable table;
    private JScrollPane sp;

    private JLabel key;
    private JLabel value;
    private JTextField keyField;
    private JTextField valueField;
    private JButton commit;
    private JButton addKeyValue;
    private JButton deleteRecord;
    private JButton fillTable;

    private final int textFieldWidth = 150;
    private final int textFieldHeight = 20;
    private final int labelWidth = 100;
    private final int labelHeight = 20;
    private final int widthOffset = 50;
    private final int heightOffset = 20;
    private final int distanceFromFrameLeft = 30;
    private final int distanceFromFrameTop = 30;
    private final int buttonWidth = 100;
    private final int buttonHeight = 30;
    private final int scrollWidth = 100;
    private final int scrollHeight = 75;
    private final int panelFromTop = 75;

    private Vector<String> keys;
    private Vector<String> values;
    private List<String> types;

    private Vector<String> fields;
    private Vector<String> fieldTypes;
    private Vector<String> fieldSpecs;

    public TableEditorWindow(String tbName,String database, Vector<String> atrs){
        setTitle(tbName);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 250);
        initialize(atrs);
        System.out.println(atrs);
        for(String atr : atrs) {
            String[] splittedThree = atr.split("/");
            fields.add(splittedThree[0]);
            fieldTypes.add(splittedThree[1]);
            fieldSpecs.add(splittedThree[2]);
        }

        JPanel panelScroll = new JPanel(new BorderLayout());
        JList<String> list = new JList<String>(types.toArray(new String[types.size()]));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        panelScroll.add(scrollPane);
        panelScroll.setBounds(distanceFromFrameLeft,panelFromTop,scrollWidth,scrollHeight);


        add(sp);
        add(key);
        add(value);
        add(keyField);
        add(valueField);
        add(commit);
        add(addKeyValue);
        add(deleteRecord);
        add(panelScroll);
        add(fillTable);

        key.setBounds(distanceFromFrameLeft, distanceFromFrameTop, labelWidth, labelHeight);
        keyField.setBounds(distanceFromFrameLeft + widthOffset, distanceFromFrameTop, textFieldWidth, textFieldHeight);
        value.setBounds(distanceFromFrameLeft, distanceFromFrameTop + heightOffset,labelWidth,labelHeight);
        valueField.setBounds(distanceFromFrameLeft + widthOffset, distanceFromFrameTop + heightOffset,textFieldWidth,textFieldHeight);
        commit.setBounds(getWidth()/2 + 2 * widthOffset,distanceFromFrameTop,buttonWidth, buttonHeight );
        addKeyValue.setBounds(getWidth()/2 + 2 * widthOffset,distanceFromFrameTop + 45, buttonWidth,buttonHeight);
        deleteRecord.setBounds(getWidth()/2 + 2 * widthOffset, distanceFromFrameTop + 60 + buttonHeight, buttonWidth, buttonHeight);
        fillTable.setBounds(getWidth()/2 + 2 * widthOffset, distanceFromFrameTop + 75 + 2*buttonHeight, buttonWidth, buttonHeight);
        sp.setBounds(getWidth()/2 - 2*widthOffset, distanceFromFrameTop + 45, 140, 100);

        commit.addActionListener(e->{
                try {
                    FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                    String allKeys = "";
                    for(String key : keys){
                        allKeys += key + " ";
                    }
                    String allValues = "";
                    for(String value : values){
                        allValues += value + " ";
                    }
                    writer.write(database + "\n" + tbName + "\n" +"INSERT" + "\n" + allKeys + "\n" + allValues );
                    writer.close();
                    keyField.setText("");
                    valueField.setText("");
                    keys.clear();
                    values.clear();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.out.println("masodik");
            String pkOne = "";
                for(int i=0;i<fields.size();i++) {
                    if(fieldSpecs.get(i).substring(0, 1).equals("1")){
                        pkOne = fields.get(i);
                        break;
                    }
                }

                for (int i = 0; i < fields.size(); i++) {
                    if (fieldSpecs.get(i).substring(3, 4).equals("1") || fieldSpecs.get(i).substring(1, 2).equals("1")) {
                        try {
                            System.out.println(fields.get(i) + " ezt kell");
                            System.out.println(fieldSpecs.get(i));
                            FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                            writer.write(database + "\n" + tbName + "\n" + "BUILDNUI\n" + fields.get(i) + "\n" + pkOne);
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
                        writer.write(database + "\n" + tbName + "\n" + "BUILDUI\n" + fields.get(i) + "\n" + pkOne);
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
        });

        addKeyValue.addActionListener(e->{
            Boolean isValid = false;
            int indexForMatchingAtr = 0;

            if(keyField.getText().equals("") || valueField.getText().equals("")){
                JOptionPane.showMessageDialog(new JPanel(), "One of the fields is empty!", "Warning", JOptionPane.WARNING_MESSAGE);
            }else{
                int ind = 0;
                for(String f : fields) {
                    if (keyField.getText().equals(f)){
                        isValid = true;
                        indexForMatchingAtr = ind;
                        break;
                    }
                    ind++;
                }
                if(isValid) {
                    if (list.isSelectionEmpty()) {
                        JOptionPane.showMessageDialog(new JPanel(), "Select a type for your value!", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        Boolean isPKOK = true;
                        for(int i=0;i<fields.size();i++){
                            if(keyField.getText().equals(fields.get(i)) && (fieldSpecs.get(i).substring(0, 1).equals("1")
                             || fieldSpecs.get(i).substring(2, 3).equals("1"))){
                                try {
                                    FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                                    writer.write(database + "\n" + tbName + "\nCHECKPK\n" + fields.get(i) + "\n" + valueField.getText());
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

                                    if(line.equals("NOOK")){
                                        isPKOK = false;
                                    }


                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                break;

                            }
                        }
                        if(isPKOK) {

                            String fkExists = "";
                            Boolean isFKOK = true;

                            if (fieldSpecs.get(indexForMatchingAtr).substring(1, 2).equals("1")) {
                                isFKOK = false;
                                String content = "";
                                try {
                                    content = readFile("Structures/" + database + ".txt", StandardCharsets.UTF_8);
                                    int startIndex = content.indexOf("tableName=\"" + tbName) + 11;
                                    int endIndex = content.indexOf("/Table", startIndex + 1);
                                    content = content.substring(startIndex, endIndex);

                                    System.out.println("Ezt az atr t keresem: " + fields.get(indexForMatchingAtr));
                                    int index = content.indexOf("refAttribute>" + fields.get(indexForMatchingAtr)) + 13;
                                    String refAtr = content.substring(index, content.indexOf("<", index + 1));
                                    System.out.println("ezt kaptam: " + refAtr);
                                    index = content.lastIndexOf("<refTable>", index) + 10;
                                    String refTable = content.substring(index, content.indexOf("<", index - 1));
                                    System.out.println("ennek a tabaja: " + refTable);

                                    FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                                    writer.write(database + "\n" + refTable + "\n" + "CHECKFK\n" + refAtr + "\n" + valueField.getText());
                                    writer.close();

                                    File file = new File("Databases/TOCLIENT.txt");
                                    while (file.length() == 0) {

                                    }

                                    BufferedReader reader = new BufferedReader(new FileReader("Databases/TOCLIENT.txt"));
                                    fkExists = reader.readLine();
                                    reader.close();

                                    FileWriter writer2 = new FileWriter("Databases/TOCLIENT.txt", false);
                                    writer2.write("");
                                    writer2.close();

                                    if (fkExists.equals("OK")) {
                                        isFKOK = true;
                                    }

                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            if (isFKOK) {
                                String type = list.getSelectedValue();
                                String extension = type.substring(0, 1);
                                String key = keyField.getText();
                                String value = extension + valueField.getText();
                                keys.add(key);
                                values.add(value);
                                keyField.setText("");
                                valueField.setText("");

                            } else {
                                JOptionPane.showMessageDialog(new JPanel(), "The FK value does not exist!", "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                        }else{
                            JOptionPane.showMessageDialog(new JPanel(), "Unique field already in use!", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(new JPanel(), "You entered a non-existent field name!", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        deleteRecord.addActionListener(e->{
            try {
                FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                writer.write( database + "\n" + tbName + "\nGET RECORDS");
                writer.close();
                //TODO send to server
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            File file = new File("Databases/TOCLIENT.txt");

            long t= System.currentTimeMillis();
            long end = t+15000;

            while(file.length() == 0 && System.currentTimeMillis() < end){
            }

            if(file.length() == 0){
                JOptionPane.showMessageDialog(new JPanel(), "There are no records in collection!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
            if(file.length() != 0) {
                String line = "";

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader("Databases/TOCLIENT.txt"));
                    line = reader.readLine();
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                String[] result;
                result = line.split(" ");
                for (String word : result) {
                    System.out.println(word);
                }

                FileWriter writer2 = null;
                try {
                    writer2 = new FileWriter("Databases/TOCLIENT.txt", false);
                    writer2.write("");
                    writer2.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                new DeleteRecordsTable(database, tbName, result, fields, fieldTypes, fieldSpecs);
                this.dispose();
            }
        });

        fillTable.addActionListener(e->{
            try {
                String line = "";
                for(int i=0;i<fields.size();i++){
                    String tmp = fields.get(i) + "/" + fieldTypes.get(i) + "/" + fieldSpecs.get(i);
                    line += tmp + " ";
                }

                FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                writer.write(database + "\n" + tbName + "\nFILL\n" + line);
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void initialize(Vector<String> atrs) {
        dlm = new DefaultListModel<String>();
        keyList = new JList<String>(dlm);

        Object[] objects = atrs.toArray();
        String[] words = Arrays.copyOf(objects,
                objects.length,
                String[].class);

        for(String word : words){
            dlm.addElement(word);
        }
        String[] header = {"Attributes"};
        String[][] data = new String[words.length][1];
        for(int i=0;i<words.length;i++){
            data[i][0] = words[i];
        }
        table = new JTable(data,header);
        table.setRowSelectionAllowed(false);
        sp = new JScrollPane(table);

        key = new JLabel("KEY: ");
        value = new JLabel("VALUE: ");
        keyField = new JTextField();
        valueField = new JTextField();
        commit = new JButton("Commit");
        addKeyValue = new JButton("Add");
        deleteRecord = new JButton("Delete rec.");
        fillTable = new JButton("Fill");
        keys = new Vector<String>();
        values = new Vector<String>();
        types = new ArrayList<String>();
        types.add("int");
        types.add("string");
        types.add("double");
        types.add("boolean");

        fields = new Vector<String>();
        fieldTypes = new Vector<String>();
        fieldSpecs = new Vector<String>();

    }

    public String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
