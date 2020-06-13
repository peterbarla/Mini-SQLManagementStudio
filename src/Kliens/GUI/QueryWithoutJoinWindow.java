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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class QueryWithoutJoinWindow extends JFrame {
    private final int frameWidth = 800;
    private final int frameHeight = 450;
    private final int scrollWidth = 125;
    private final int scrollHeight = 125;
    private final int textWidth = 500;
    private final int textHeight = 40;
    private final int distanceFromLeft = textWidth + 100;
    private final int distanceFromTop = 20;
    private final int buttonWidth = 100;
    private final int buttonHeight = 30;

    Vector<String> atrs;
    private Vector<String> fields;
    private Vector<String> fieldTypes;
    private Vector<String> fieldSpecs;
    private JList<String> keyList;
    private DefaultListModel<String> dlm;
    private JTable table;
    private JScrollPane sp;

    private JTextField text;
    private JButton query;

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

    public QueryWithoutJoinWindow(String DB, String TB){
        setTitle(TB);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(frameWidth, frameHeight);

        initialize(DB, TB);

        for(String atr : atrs) {
            String[] splittedThree = atr.split("/");
            fields.add(splittedThree[0]);
            fieldTypes.add(splittedThree[1]);
            fieldSpecs.add(splittedThree[2]);
        }

        add(sp);
        add(text);
        add(query);
        add(groupByLabel);
        add(groupByText);
        add(havingLabel);
        add(havingText);
        add(summFunctionsLabel);
        add(summFunctionText);

        sp.setBounds(0,100,scrollWidth,scrollHeight);
        text.setBounds(0, 30, textWidth, textHeight);
        query.setBounds(distanceFromLeft, distanceFromTop, buttonWidth, buttonHeight);
        groupByLabel.setBounds(150, 100, groupByLabelWidth, groupByLabelHeight);
        groupByText.setBounds(150 + groupByLabelWidth + groupByOffset, 100, groupByTextWidth, groupByTextHeight);
        havingLabel.setBounds(150 , 120 + groupByTextHeight, havingLabelWidth, havingLabelHeight);
        havingText.setBounds(150 + havingLabelWidth + havingOffset, 120 + groupByTextHeight, havingTextWidth, havingTextHeight);
        summFunctionsLabel.setBounds(150, 140 + groupByTextHeight + havingLabelHeight, summLabelWidth, summLabelHeight);
        summFunctionText.setBounds(150 + summLabelWidth + summOffset, 140 + groupByTextHeight + havingLabelHeight, summTextWidth, summTextHeight);

        query.addActionListener(e->{
            String enteredText = text.getText();
            String[] splitted = enteredText.split(" ");
            Boolean isZerothValid = true;
            Boolean isFirstValid = true;
            Boolean isSecondValid = true;
            Boolean isThirdValid = true;
            Boolean isFourthValid = true;
            Boolean isFifthValid = true;
            Boolean isSixthValid = true;
            Vector<String> operators = new Vector<String>();

            String groupTextEntered = groupByText.getText();
            String havingTextEntered = havingText.getText();
            String summTextEntered = summFunctionText.getText();
            Vector<Vector<String>> allAttrs = new Vector<Vector<String>>();
            allAttrs.add(fields);
            Vector<String> tables = new Vector<>();
            GroupByValidator groupByValidator = new GroupByValidator(groupTextEntered, havingTextEntered, summTextEntered, allAttrs, tables, TB,  "SIMPLE");

            if(!groupByValidator.isValid()){
                Vector<String> errors;
                errors = groupByValidator.getErrorMessage();
                JOptionPane.showMessageDialog(new JPanel(), errors, "Warning", JOptionPane.WARNING_MESSAGE);
                isZerothValid = false;
            }

            if(isZerothValid) {
                if (splitted.length < 4) {
                    JOptionPane.showMessageDialog(new JPanel(), "Incorrect syntax!", "Warning", JOptionPane.WARNING_MESSAGE);
                    isFirstValid = false;
                } else if (splitted.length == 5) {
                    JOptionPane.showMessageDialog(new JPanel(), "Incorrect syntax!", "Warning", JOptionPane.WARNING_MESSAGE);
                    isFirstValid = false;
                } else if (((!splitted[0].equals("SELECT")) && (!splitted[0].equals("select"))) || ((!splitted[2].equals("FROM"))
                        && (!splitted[2].equals("from")))) {
                    JOptionPane.showMessageDialog(new JPanel(), "Incorrect syntax!", "Warning", JOptionPane.WARNING_MESSAGE);
                    isFirstValid = false;
                } else if (splitted.length > 4 && splitted.length <= 6) {
                    if (!splitted[4].equals("WHERE") && !splitted[4].equals("where")) {
                        JOptionPane.showMessageDialog(new JPanel(), "Incorrect syntax!", "Warning", JOptionPane.WARNING_MESSAGE);
                        isFirstValid = false;
                    }
                } else if (splitted.length > 6 && splitted.length % 2 == 1) {
                    JOptionPane.showMessageDialog(new JPanel(), "Incorrect syntax!", "Warning", JOptionPane.WARNING_MESSAGE);
                    isFirstValid = false;
                } else if (splitted.length > 6 && splitted.length % 2 == 0) {
                    for (int i = 6; i < splitted.length; i += 2) {
                        if (!splitted[i].equals("AND") && !splitted[i].equals("and")) {
                            JOptionPane.showMessageDialog(new JPanel(), "Incorrect syntax!", "Warning", JOptionPane.WARNING_MESSAGE);
                            isFirstValid = false;
                        }
                    }
                } else if (splitted[1].substring(0, 1).equals("*") && splitted[1].length() > 1) {
                    JOptionPane.showMessageDialog(new JPanel(), "Incorrect syntax!", "Warning", JOptionPane.WARNING_MESSAGE);
                    isFirstValid = false;
                }
                if (isFirstValid) {
                    String[] splitted2 = splitted[1].split(",");
                    System.out.println(splitted2.length);
                    for (String tmp : splitted2) {
                        System.out.println(tmp);
                    }

                    Boolean isMatch = false;
                    if (splitted2.length == 1) {
                        for (String tmp : fields) {
                            if (tmp.equals(splitted2[0])) {
                                isMatch = true;
                            }
                        }
                        if (!isMatch) {
                            if (!splitted2[0].equals("*")) {
                                JOptionPane.showMessageDialog(new JPanel(), "Incorrect projection fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                                isSecondValid = false;
                            }
                        }
                    } else {
                        for (String tmp : splitted2) {
                            Boolean isOK = false;
                            for (String tmp2 : fields) {
                                if (tmp.equals(tmp2)) {
                                    isOK = true;
                                }
                            }
                            if (!isOK) {
                                JOptionPane.showMessageDialog(new JPanel(), "Incorrect projection fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                                isSecondValid = false;
                                break;
                            }
                        }
                    }
                }
                if (isSecondValid) {
                    if (!splitted[3].equals(TB)) {
                        isThirdValid = false;
                        JOptionPane.showMessageDialog(new JPanel(), "Incorrect tablename!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
                String operator = "";
                if (isThirdValid) {
                    for (int i = 5; i < splitted.length; i += 2) {
                        System.out.println("bent");
                        if (splitted[i].split("=").length == 2 && !splitted[i].contains("<") && !splitted[i].contains(">")) {
                            operator = "=";
                            operators.add(operator);
                        } else if (splitted[i].split("<=").length == 2) {
                            operator = "<=";
                            operators.add(operator);
                        } else if (splitted[i].split(">=").length == 2) {
                            operator = ">=";
                            operators.add(operator);
                        } else if (splitted[i].split("<").length == 2 && !splitted[i].contains("=")) {
                            operator = "<";
                            operators.add(operator);
                        } else if (splitted[i].split(">").length == 2 && !splitted[i].contains("=")) {
                            operator = ">";
                            operators.add(operator);
                        } else {
                            JOptionPane.showMessageDialog(new JPanel(), "Wrong where conditions!", "Warning", JOptionPane.WARNING_MESSAGE);
                            isFourthValid = false;
                        }

                        if (isFourthValid) {
                            Boolean isOK = false;
                            System.out.println(operator);
                            isOK = false;
                            for (String tmp : fields) {
                                if (splitted[i].split(operator)[0].equals(tmp)) {
                                    isOK = true;
                                    break;
                                }
                            }
                            if (!isOK) {
                                JOptionPane.showMessageDialog(new JPanel(), "Wrong attribute given in where condition!", "Warning", JOptionPane.WARNING_MESSAGE);
                                isFifthValid = false;
                                break;
                            }
                        }

                        if (isFifthValid) {
                            if (operator.equals("<=") || operator.equals(">=") || operator.equals("<") || operator.equals(">")) {
                                if (!isInt(splitted[i].split(operator)[1])) {
                                    JOptionPane.showMessageDialog(new JPanel(), "Attribute non int but value is int!", "Warning", JOptionPane.WARNING_MESSAGE);
                                    isSixthValid = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (isFirstValid && isSecondValid && isThirdValid && isFourthValid && isSixthValid && isFifthValid) {
                    try {
                        String conditions = "";
                        for (int i = 5; i < splitted.length; i += 2) {
                            conditions += splitted[i] + " ";
                        }

                        FileWriter writer = new FileWriter("Databases/TOSERVER.txt", false);
                        if(!groupTextEntered.equals("")) splitted[1] = "*";
                        writer.write(DB + "\n" + TB + "\n" + "SIMPLEQUERY\n" + conditions + "\n" + String.join("#", operators)
                                + "\n" + splitted[1] + "\n" + groupTextEntered + "\n" + havingTextEntered + "\n" + summTextEntered +
                                "\n" + "...");
                        writer.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void initialize(String DB, String TB){

        dlm = new DefaultListModel<String>();
        keyList = new JList<String>(dlm);
        atrs = new Vector<String>();

        try {
            String content = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
            int startIndex = content.indexOf("tableName=\"" + TB);
            int endIndex = content.indexOf("/Table>", startIndex + 13);
            content = content.substring(startIndex + 11, endIndex);
            Vector<Integer> indexes1 = new Vector<Integer>();
            Vector<Integer> indexes2 = new Vector<Integer>();
            Vector<Integer> indexes3 = new Vector<Integer>();
            int index = 0;
            while (index != -1) {
                index = content.indexOf("attributeName", index);
                if (index != -1) {
                    indexes1.add(index);
                    index++;
                }
            }
            index = 0;
            while (index != -1) {
                index = content.indexOf("type", index);
                if (index != -1) {
                    indexes2.add(index);
                    index++;
                }
            }
            index = 0;
            while (index != -1) {
                index = content.indexOf("specs", index);
                if (index != -1) {
                    indexes3.add(index);
                    index++;
                }
            }
            for (int i = 0; i < indexes1.size(); i++) {
                atrs.add(content.substring(indexes1.get(i) + 15, content.indexOf("\"", indexes1.get(i) + 16))
                        + "/" + content.substring(indexes2.get(i) + 6, content.indexOf("\"", indexes2.get(i) + 8))
                        + "/" + content.substring(indexes3.get(i) + 7, content.indexOf("\"", indexes3.get(i) + 8)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

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

        fields = new Vector<String>();
        fieldTypes = new Vector<String>();
        fieldSpecs = new Vector<String>();

        text = new JTextField();
        query = new JButton("Query");

        groupByLabel = new JLabel("Group By: ");
        groupByText = new JTextField();

        havingLabel = new JLabel("Having: ");
        havingText = new JTextField();

        summFunctionsLabel = new JLabel("Projection: ");
        summFunctionText = new JTextField();
    }

    public String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public boolean isInt(String str) {
        try {
            int v = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
        }
        return false;
    }


}
