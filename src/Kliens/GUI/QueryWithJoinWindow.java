package Kliens.GUI;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class QueryWithJoinWindow extends JFrame {

    private final int frameWidth = 1000;
    private final int frameHeight = 600;
    private final int scrollWidth = 125;
    private final int scrollHeight = 125;
    private final int textWidth = 750;
    private final int textHeight = 40;
    private final int distanceFromLeft = textWidth + 100;
    private final int distanceFromTop = 20;
    private final int buttonWidth = 100;
    private final int buttonHeight = 30;

    private String DB;

    private JTextField text;
    private JButton query;

    private Vector<JTable> graphicalTables;

    private Vector<String> tables;
    private Vector<Vector<String>> attributesPerTable;

    private String wholeText;

    private JLabel groupByLabel;
    private JTextField groupByText;
    private final int groupByLabelWidth = 75;
    private final int groupByLabelHeight = 50;
    private final int groupByTextWidth = 150;
    private final int groupByTextHeight = 35;
    private final int groupByOffset = 25;

    private JLabel havingLabel;
    private JTextField havingText;
    private final int havingLabelWidth = 75;
    private final int havingLabelHeight = 50;
    private final int havingTextWidth = 400;
    private final int havingTextHeight = 35;
    private final int havingOffset = 25;

    private JLabel summFunctionsLabel;
    private JTextField summFunctionText;
    private final int summLabelWidth = 100;
    private final int summLabelHeight = 50;
    private final int summTextWidth = 400;
    private final int summTextHeight = 35;
    private final int summOffset = 25;

    private static QueryWithJoinWindow single_instance = null;
    private String DBName, TBName;

    private QueryWithJoinWindow(String DB, String TB){

        setTitle(DB);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(frameWidth, frameHeight);
        this.DB = DB;
        initialize();
        getDBTables(DB);
        drawTables();

        add(text);
        add(query);
        add(groupByLabel);
        add(groupByText);
        add(havingLabel);
        add(havingText);
        add(summFunctionsLabel);
        add(summFunctionText);

        DBName = this.DB;
        TBName = TB;

        text.setBounds(0, distanceFromTop, textWidth, textHeight);
        query.setBounds(distanceFromLeft, distanceFromTop, buttonWidth, buttonHeight);
        groupByLabel.setBounds(150, 220, groupByLabelWidth, groupByLabelHeight);
        groupByText.setBounds(150 + groupByLabelWidth + groupByOffset, 220, groupByTextWidth, groupByTextHeight);
        havingLabel.setBounds(150 , 240 + groupByTextHeight, havingLabelWidth, havingLabelHeight);
        havingText.setBounds(150 + havingLabelWidth + havingOffset, 240 + groupByTextHeight, havingTextWidth, havingTextHeight);
        summFunctionsLabel.setBounds(150, 260 + groupByTextHeight + havingLabelHeight, summLabelWidth, summLabelHeight);
        summFunctionText.setBounds(150 + summLabelWidth + summOffset, 260 + groupByTextHeight + havingLabelHeight, summTextWidth, summTextHeight);
        //resultArea.setBounds(100, 280 + + groupByTextHeight + havingLabelHeight + summLabelHeight, resultAreaWidth, resultAreaHeight);
        //resultArea.setText("vegvegeg");

        query.addActionListener(e->{
            String enteredText = text.getText();
            Boolean isZerothValid = true;
            String groupTextEntered = groupByText.getText();
            String havingTextEntered = havingText.getText();
            String summTextEntered = summFunctionText.getText();

            GroupByValidator groupByValidator = new GroupByValidator(groupTextEntered, havingTextEntered, summTextEntered, attributesPerTable, tables, "tmp", "JOIN");

            if(!groupByValidator.isValid()){
                Vector<String> errors;
                errors = groupByValidator.getErrorMessage();
                JOptionPane.showMessageDialog(new JPanel(), errors, "Warning", JOptionPane.WARNING_MESSAGE);
                isZerothValid = false;
            }
            if(isZerothValid) {
                if (enteredText.equals("")) {
                    JOptionPane.showMessageDialog(new JPanel(), "You haven`t entered a query!", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    JoinQueryValidator validator = new JoinQueryValidator(enteredText, DB, tables, attributesPerTable);
                    try {
                        if (validator.validate()) {
                            Vector<Vector<String>> resultInformation = validator.getResultInformation();
                            String projections = String.join("#", resultInformation.get(0));
                            String joinConditions = String.join("#", resultInformation.get(1));
                            String whereConditions = String.join("#", resultInformation.get(2));
                            String whereOperator = String.join("#", resultInformation.get(3));
                            String joinedTables = String.join("#", resultInformation.get(4));
                            String allAttributesPerTable = "";
                            for (int i = 0; i < attributesPerTable.size(); i++) {
                                String attributesForOneTable = tables.get(i) + "/";
                                attributesForOneTable += String.join("/", attributesPerTable.get(i));
                                allAttributesPerTable += attributesForOneTable + "#";
                            }
                            try {
                                if(!groupTextEntered.equals("")) projections="*";
                                FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                                writer.write(DB + "\n" + TB + "\nJOINQUERY\n" + projections + "\n" + joinConditions +
                                        "\n" + whereConditions + "\n" + whereOperator + "\n" + joinedTables + "\n" + allAttributesPerTable
                                + "\n" + groupTextEntered + "\n" + havingTextEntered + "\n" + summTextEntered + "\n" + "...");
                                writer.close();
                            } catch (IOException ex) {
                                System.out.println(ex);
                            }
                        } else {
                            String errMSG = validator.getErrorMessage();
                            JOptionPane.showMessageDialog(new JPanel(), errMSG, "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static synchronized QueryWithJoinWindow getInstance(String DB, String TB)
    {
        System.out.println(DB);
        System.out.println(TB);
        if (single_instance == null) {
            System.out.println("meg nincs meg");
            single_instance = new QueryWithJoinWindow(DB, TB);
            System.out.println(single_instance);
        }else{
            System.out.println("mar megvan");
        }


        //System.out.println("mar megvan");
        return single_instance;
    }

    private void initialize(){
        text = new JTextField();
        query = new JButton("QUERY");
        tables = new Vector<>();
        attributesPerTable = new Vector<Vector<String>>();
        graphicalTables = new Vector<>();

        groupByLabel = new JLabel("Group By: ");
        groupByText = new JTextField();

        havingLabel = new JLabel("Having: ");
        havingText = new JTextField();

        summFunctionsLabel = new JLabel("Projection: ");
        summFunctionText = new JTextField();

        try {
            wholeText = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public void getDBTables(String DB){
        int indStart = 0;
        int indEnd = 0;
        while(indStart >=0){
            indStart = wholeText.indexOf("tableName=\"", indEnd);
            if(indStart == -1)
                break;
            indStart += 11;
            indEnd = wholeText.indexOf("\"", indStart);
            tables.add(wholeText.substring(indStart ,indEnd));
        }
        for(int i =0;i<tables.size();i++){
            getAttrsForTable(tables.get(i), i);
        }

    }

    public void getAttrsForTable(String table, int index){
        String tableText = wholeText.substring(wholeText.indexOf("tableName=\"" + table), wholeText.indexOf(
                "<primaryKey>", wholeText.indexOf("tableName=\"" + table)));
        int indStart = 0;
        int indEnd = 0;
        Vector<String> attributes = new Vector<>();
        while(indStart >=0){
            indStart = tableText.indexOf("attributeName=\"", indEnd);
            if(indStart == -1)
                break;
            indStart += 15;
            indEnd = tableText.indexOf("\"", indStart);
            attributes.add(tableText.substring(indStart ,indEnd));
        }
        attributesPerTable.add(attributes);
    }

    public void drawTables(){
        for(int i=0;i<tables.size();i++){
            Vector<Vector<String>> columns = new Vector<>();
            Vector<String> titles = new Vector<>();
            for(int j=0;j<attributesPerTable.get(i).size();j++){
                Vector<String> tmp = new Vector<>();
                tmp.add((attributesPerTable.get(i)).get(j));
                columns.add(tmp);
            }
            titles.add(tables.get(i));
            JTable tab = new JTable(columns, titles);
            JScrollPane sp = new JScrollPane(tab);
            add(sp);
            sp.setBounds(i*100, 100, 100, 100);
        }
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public String getDB(){
        return DBName;
    }

    public String getTB(){
        return TBName;
    }
}
