package Kliens.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;

public class TableBuilderWIndow extends JFrame {

    private java.util.List<String> tables;

    private JLabel atrLabel;
    private JTextField atrField;

    private JLabel typeLabel;
    private JTextField typeField;

    private JLabel isUI;
    private JCheckBox UI;

    private JLabel isNUI;
    private JCheckBox NUI;

    private JLabel isPK;
    private JCheckBox PK;

    private JLabel isFK;
    private JCheckBox FK;

    private JButton addBTN;
    private JButton buildBTN;

    private static int scrollPaneWidth = 120;
    private static int scrollPaneHeight = 150;
    private final int distanceFromLeft = 30;
    private final int distanceFromTop = 30;
    private final int heightOffset = 15;
    private final int widthOffset = 20;
    private final int labelWidth = 70;
    private final int smallLabelWidth = 40;
    private final int labelHeight = 20;
    private final int boxWidth = 15;
    private final int bosHeight = 15;
    private final int textWidth = 80;
    private final int textHeight = 20;
    private final int buttonWidth = 75;
    private final int buttonHeight = 35;
    private final int buttonWidthOffset = 50;
    private final int buttonHeightOffset = 50;

    private Vector<String> fields;
    private String DB;

    public TableBuilderWIndow(String dbName, String table) throws IOException {
        this.DB = dbName;
        setTitle(table);
        setLayout(null);
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

        add(panelScroll);
        add(atrLabel);
        add(atrField);
        add(typeLabel);
        add(typeField);
        add(isUI);
        add(UI);
        add(isNUI);
        add(NUI);
        add(isPK);
        add(PK);
        add(isFK);
        add(FK);
        add(addBTN);
        add(buildBTN);

        panelScroll.setBounds(distanceFromLeft + 2*buttonWidthOffset + 2*buttonWidth, distanceFromTop + 2*labelHeight + buttonHeightOffset, scrollPaneWidth,scrollPaneHeight);
        atrLabel.setBounds(distanceFromLeft, distanceFromTop, labelWidth, labelHeight);
        atrField.setBounds(distanceFromLeft + labelWidth + widthOffset, distanceFromTop, textWidth, textHeight);
        typeLabel.setBounds(distanceFromLeft + labelWidth + 2*widthOffset + textWidth, distanceFromTop , labelWidth, labelHeight);
        typeField.setBounds(distanceFromLeft + 2*labelWidth + 3*widthOffset + textWidth, distanceFromTop, textWidth, textHeight);
        isPK.setBounds(distanceFromLeft, distanceFromTop + labelHeight + heightOffset, smallLabelWidth, labelHeight);
        PK.setBounds(distanceFromLeft + smallLabelWidth + widthOffset, distanceFromTop + labelHeight + heightOffset, boxWidth, bosHeight);
        isFK.setBounds(distanceFromLeft + smallLabelWidth + boxWidth + 2*widthOffset, distanceFromTop + labelHeight + heightOffset, smallLabelWidth,labelHeight);
        FK.setBounds(distanceFromLeft + 2*smallLabelWidth + boxWidth + 3*widthOffset, distanceFromTop + labelHeight + heightOffset, boxWidth, bosHeight);
        isUI.setBounds(distanceFromLeft + 2*smallLabelWidth + 2*boxWidth + 4*widthOffset, distanceFromTop + labelHeight + heightOffset, smallLabelWidth, labelHeight);
        UI.setBounds(distanceFromLeft + 3*smallLabelWidth + 2*boxWidth + 5*widthOffset, distanceFromTop + labelHeight + heightOffset, boxWidth, bosHeight);
        isNUI.setBounds(distanceFromLeft + 3*smallLabelWidth + 3*boxWidth + 6*widthOffset, distanceFromTop + labelHeight + heightOffset, smallLabelWidth, labelHeight);
        NUI.setBounds(distanceFromLeft + 4*smallLabelWidth + 3*boxWidth + 7*widthOffset, distanceFromTop + labelHeight + heightOffset, boxWidth ,bosHeight);
        addBTN.setBounds(distanceFromLeft, distanceFromTop + 2*labelHeight + buttonHeightOffset, buttonWidth, buttonHeight);
        buildBTN.setBounds(distanceFromLeft + buttonWidthOffset + buttonWidth, distanceFromTop + 2*labelHeight + buttonHeightOffset, buttonWidth,buttonHeight);


        addBTN.addActionListener(e->{
            String field = atrField.getText();
            String type = typeField.getText();
            int pk = PK.isSelected() ? 1 : 0;
            int fk = FK.isSelected() ? 1 : 0;
            int ui = UI.isSelected() ? 1 : 0;
            int nui = NUI.isSelected() ? 1 : 0;
            String selectedForFK = list.getSelectedValue();


            if(field.equals("") || type.equals("")){
                JOptionPane.showMessageDialog(new JPanel(), "Fill both fields!", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if(fk == 1 && selectedForFK == null){
                JOptionPane.showMessageDialog(new JPanel(), "You must select a database to reference with FK!", "Warning", JOptionPane.WARNING_MESSAGE);
            }else if(pk == 1 && ui == 1){
                JOptionPane.showMessageDialog(new JPanel(), "You cannot select PK and Unique in the same time!", "Warning", JOptionPane.WARNING_MESSAGE);
            }else{
                String refAtr = "";
                if(fk == 1){
                    try {
                        String base = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
                        String content = base.substring(base.indexOf("Table tableName=\"" + selectedForFK) + 17,
                                base.indexOf("/Structure", base.indexOf("Table tableName=\"" + selectedForFK) + 18));
                        int index = content.indexOf("pkAttribute>");

                        refAtr = content.substring(index + 12, content.indexOf("<", index + 13));


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                System.out.println(refAtr);
                //System.out.println(fk);
                if(fk == 1){
                    fields.add(field + "#" + type + "#" + pk + fk + "(" + selectedForFK + "." + refAtr + ")" + ui + nui);
                }else {
                    fields.add(field + "#" + type + "#" + pk + fk + ui + nui);
                }
                atrField.setText("");
                typeField.setText("");
                PK.setSelected(false);
                FK.setSelected(false);
                UI.setSelected(false);
                NUI.setSelected(false);
            }
        });

        buildBTN.addActionListener(e->{
            try {
                String line = "";
                for(String word : fields){
                    line +=word + " ";
                }
                FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                writer.write( dbName + "\n" + table + "\nGENERATETABLE\n" + line);
                writer.close();
                fields.clear();
                this.dispose();
                //TODO send to server
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void initialize() throws IOException {

        String content = readFile("Structures/" + DB + ".txt",StandardCharsets.UTF_8);

        Vector<Integer> indexes = new Vector<Integer>();
        int index = 0;
        while(index != -1){
            index = content.indexOf("tableName", index);
            if(index != -1){
                indexes.add(index);
                index++;
            }
        }

        tables = new ArrayList<>();
        for(int ind : indexes){
            tables.add(content.substring(ind + 11,content.indexOf("\"",ind + 13)));
        }

        atrLabel = new JLabel("Atr name: ");
        atrField = new JTextField();
        typeLabel = new JLabel("type: ");
        typeField = new JTextField();
        isUI = new JLabel("is UI");
        UI = new JCheckBox();
        isNUI = new JLabel("is NUI");
        NUI = new JCheckBox();
        isPK = new JLabel("is PK");
        PK = new JCheckBox();
        isFK = new JLabel("is FK");
        FK = new JCheckBox();
        addBTN = new JButton("Add");
        buildBTN = new JButton("Build");
        fields = new Vector<String>();
    }

    public String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
