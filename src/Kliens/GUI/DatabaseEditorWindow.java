package Kliens.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseEditorWindow extends JFrame {
    private JPanel panel;
    private JButton createTable;
    private TextField insertedTableName;

    private JButton dropThisDatabase;
    private JButton dropThisTable;
    private JButton openThisTable;
    private JButton buildTable;
    private JButton query;

    private final int distanceBetweenButtons = 150;
    private final int offset = 50;

    public DatabaseEditorWindow(String database, List<String> tables) throws IOException {
        setTitle(database);
        setLayout(new GridLayout(1,1));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 250);

        initialize();




        JPanel panelScroll = new JPanel(new BorderLayout());
        JList<String> list = new JList<String>(tables.toArray(new String[tables.size()]));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        panelScroll.add(scrollPane);
        panelScroll.setBounds(20,75,150,100);

        panel.add(panelScroll);




        panel.setLayout(null);
        panel.add(createTable);
        panel.add(insertedTableName);
        panel.add(dropThisDatabase);
        panel.add(dropThisTable);
        panel.add(openThisTable);
        panel.add(buildTable);
        panel.add(query);

        insertedTableName.setBounds(20 , 20,150, 20);
        createTable.setBounds(offset + insertedTableName.getWidth() ,15, 125,30);
        dropThisDatabase.setBounds(insertedTableName.getWidth() + offset + distanceBetweenButtons, 15, 125, 30);
        dropThisTable.setBounds(offset + insertedTableName.getWidth()  , 75, 125,30);
        openThisTable.setBounds(offset + insertedTableName.getWidth()  , 120, 125,30);
        buildTable.setBounds(offset + insertedTableName.getWidth(), 165, 125, 30);
        query.setBounds(insertedTableName.getWidth() + offset + distanceBetweenButtons, 75, 125, 30);

        add(panel);

        createTable.addActionListener(e-> {
                  String tableName = insertedTableName.getText();
                  if (!tableName.equals("")) {
                      try {

                          FileWriter writer0 = new FileWriter("Databases/TOSERVER.txt", false);
                          String[] dataBaseName = database.split("\\.",0);
                          writer0.write(dataBaseName[0] + "\n" + tableName + "\n" + "CREATE TABLE");
                          //System.out.println(database);
                          writer0.close();
                      } catch (IOException ex) {
                          ex.printStackTrace();
                      }

                      String newTableName = insertedTableName.getText();
                      tables.add(newTableName);
                      String [] strings = tables.toArray(new String[0]);
                      list.setListData(strings);
                      scrollPane.setViewportView(list);
                      insertedTableName.setText("");
                  }


              });
        dropThisDatabase.addActionListener(e->{
            try {

                FileWriter writer2 = new FileWriter("Databases/TOSERVER.txt", false);
                writer2.write( database + "\n" + "DROP DATABASE");
                writer2.close();
                this.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        dropThisTable.addActionListener(e->{
            String selectedTable = list.getSelectedValue();
            System.out.println(selectedTable);

            try {
                FileWriter writer = new FileWriter("Databases/TOSERVER.txt");
                writer.write(database + "\n" + selectedTable + "\n" +  "DROP TABLE");
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


          //  int index = list.getSelectedIndex();
            tables.remove(selectedTable);
            System.out.println(tables);
           // JList<String> list2 = new JList<String>(tables.toArray(new String[tables.size()]));
            String [] strings = tables.toArray(new String[0]);
            list.setListData(strings);
            scrollPane.setViewportView(list);

        });

        openThisTable.addActionListener(e->{
            String chosenTable = list.getSelectedValue();
            Vector<String> atrs = new Vector<String>();

            try {
                String content = readFile("Structures/" + database + ".txt", StandardCharsets.UTF_8);
                int startIndex = content.indexOf("tableName=\"" + chosenTable);
                int endIndex = content.indexOf("/Table>", startIndex + 13) ;
                content = content.substring(startIndex + 11, endIndex);
                Vector<Integer> indexes1 = new Vector<Integer>();
                Vector<Integer> indexes2 = new Vector<Integer>();
                Vector<Integer> indexes3 = new Vector<Integer>();
                int index = 0;
                while(index != -1){
                    index = content.indexOf("attributeName", index);
                    if(index != -1){
                        indexes1.add(index);
                        index++;
                    }
                }
                index = 0;
                while(index != -1){
                    index = content.indexOf("type", index);
                    if(index != -1){
                        indexes2.add(index);
                        index++;
                    }
                }
                index = 0;
                while(index != -1){
                    index = content.indexOf("specs", index);
                    if(index != -1){
                        indexes3.add(index);
                        index++;
                    }
                }
                for(int i=0;i<indexes1.size();i++){
                    atrs.add(content.substring(indexes1.get(i) + 15,content.indexOf("\"",indexes1.get(i) + 16))
                    + "/" + content.substring(indexes2.get(i) + 6,content.indexOf("\"",indexes2.get(i) + 8))
                    + "/" + content.substring(indexes3.get(i) + 7,content.indexOf("\"",indexes3.get(i) + 8)));
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if(!list.isSelectionEmpty()) {
                //this.dispose();
                new TableEditorWindow(chosenTable, database, atrs);
            }
            else{
                JOptionPane.showMessageDialog(new JPanel(), "Choose a table!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        buildTable.addActionListener(e->{
            String chosenTable = list.getSelectedValue();
            if(!list.isSelectionEmpty()) {
                //this.dispose();
                try {
                    new TableBuilderWIndow(database,chosenTable);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else{
                JOptionPane.showMessageDialog(new JPanel(), "Choose a table!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });


        query.addActionListener(e->{
            if(!list.isSelectionEmpty()) {
                //this.dispose();
                new QueryChooserWIndow(database, list.getSelectedValue(), list);
            }else{
                JOptionPane.showMessageDialog(new JPanel(), "Choose a table!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void  initialize(){
        panel = new JPanel();
        createTable = new JButton("Create table");
        insertedTableName = new TextField();
        dropThisDatabase = new JButton("Drop database");
        dropThisTable = new JButton("Drop table");
        openThisTable = new JButton("Open table");
        buildTable = new JButton("Build table");
        query = new JButton("Query");
    }

    public String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
