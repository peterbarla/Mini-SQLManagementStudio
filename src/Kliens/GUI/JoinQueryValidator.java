package Kliens.GUI;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class JoinQueryValidator {
    private String text;
    private String DB;
    private String errMessage;

    private Vector<String> tables;
    private Vector<Vector<String>> attributesPerTable;
    private Vector<Vector<String>> resultInformation;
    private Vector<String> projections;
    private Vector<String> joinConditions;
    private Vector<String> whereConditions;
    private Vector<String> whereOperators;
    private Vector<String> joinedTables;
    public JoinQueryValidator(String text, String DB, Vector<String> tables, Vector<Vector<String>> attributesPerTable){
        this.text = text;
        this.DB = DB;
        this.tables = tables;
        this.attributesPerTable = attributesPerTable;
        resultInformation = new Vector<Vector<String>>();
        projections = new Vector<String>();
        joinConditions = new Vector<String>();
        whereConditions = new Vector<String>();
        whereOperators = new Vector<>();
        joinedTables = new Vector<>();
        String[] proj = text.split(" ");
        for(String pr : proj[1].split(",")){
            projections.add(pr);
        }

    }

    public Boolean validate() throws IOException {
        String[] splittedText = text.split(" ");
        int length = splittedText.length;
        if(length < 8){
            errMessage = "Syntax error!";
            return false;
        }else{
            String[] projections = splittedText[1].split(",");
            if(!projections[0].equals("*") && projections.length != 1) {
                for (String proj : projections) {
                    String[] projectionFragment = proj.split("[.]");
                    if (!validateTable(projectionFragment[0])) {
                        errMessage = "Invalid table in projection!";
                        return false;
                    }
                    if (!projectionFragment[1].equals("*")) {
                        if (!validateAttributeInTable(projectionFragment[0], projectionFragment[1])) {
                            errMessage = "Invalid attribute for table in projection!";
                            return false;
                        }
                    }
                }
            }else if(!projections[0].equals("*") && projections.length == 1){
                for (String proj : projections) {
                    String[] projectionFragment = proj.split("[.]");
                    if (!validateTable(projectionFragment[0])) {
                        errMessage = "Invalid table in projection!";
                        return false;
                    }
                    if (!projectionFragment[1].equals("*")) {
                        if (!validateAttributeInTable(projectionFragment[0], projectionFragment[1])) {
                            errMessage = "Invalid attribute for table in projection!";
                            return false;
                        }
                    }
                }
            }else if(projections.length != 1){
                errMessage = "Invalid projections field!";
                return false;
            }



            if(!validateTable(splittedText[3])){
                errMessage = "Non-existent table name!";
                return false;
            }
            joinedTables.add(splittedText[3]);
            int afterJoinsIndex = 0;
            List<String> list = Arrays.asList(splittedText);
            if(!list.contains("WHERE") && !list.contains("where") && list.size()%4 == 0) {
                for (int i = 4; i < length; i += 4) {

                    if (!splittedText[i].equals("JOIN") && !splittedText[i].equals("join")) {
                        errMessage = "Syntax error!";
                        return false;
                    }
                    if(!validateTable(splittedText[i + 1])){
                        errMessage = "Non-existent table name!";
                        return false;
                    }
                    joinedTables. add(splittedText[i + 1]);

                    if (!splittedText[i + 2].equals("ON") && !splittedText[i + 2].equals("on")) {
                        errMessage = "Syntax error!";
                        return false;
                    }

                    String[] splittedJoinCondition = splittedText[i + 3].split("=");
                    if(splittedJoinCondition.length != 2){
                        errMessage = "Invalid Syntax in join condition!";
                        return false;
                    }
                    String[] firstJoinCondition = splittedJoinCondition[0].split("[.]");
                    if(!validateTable(firstJoinCondition[0])){
                        errMessage = "Non-existent table name in join condition!";
                        return false;
                    }
                    if(!validateAttributeInTable(firstJoinCondition[0], firstJoinCondition[1])){
                        errMessage = "Non-existent attribute name for table in join condition!";
                        return false;
                    }

                    if(!validateJoinAttributeFK(firstJoinCondition[0], firstJoinCondition[1])){
                        errMessage = "Invalid join conditions!";
                        return false;
                    }

                    String[] secondJoinCondition = splittedJoinCondition[1].split("[.]");
                    if(!validateTable(secondJoinCondition[0])){
                        errMessage = "Non-existent table name in join condition!";
                        return false;
                    }
                    if(!validateAttributeInTable(secondJoinCondition[0], secondJoinCondition[1])){
                        errMessage = "Non-existent attribute name for table in join condition!";
                        return false;
                    }

                    if(!validateJoinAttributePK(secondJoinCondition[0], secondJoinCondition[1])){
                        errMessage = "Invalid join conditions!";
                        return false;
                    }

                    if(!firstJoinCondition[1].equals(secondJoinCondition[1])){
                        errMessage = "Foreign and primary key don`t match in join condition!";
                        return false;
                    }
                    joinConditions.add(splittedText[i + 3]);
                }
            }else{
                for(int i=0;i<length;i++){
                    if(splittedText[i].equals("ON") || splittedText[i].equals("on")){
                        afterJoinsIndex = i;
                    }
                }
                for(int i=4;i<=afterJoinsIndex + 1;i += 4){
                    if (!splittedText[i].equals("JOIN") && !splittedText[i].equals("join")) {
                        errMessage = "Syntax error!";
                        return false;
                    }
                    if(!validateTable(splittedText[i + 1])){
                        errMessage = "Non-existent table name!";
                        return false;
                    }
                    joinedTables.add(splittedText[i + 1]);

                    if (!splittedText[i + 2].equals("ON") && !splittedText[i + 2].equals("on")) {
                        errMessage = "Syntax error!";
                        return false;
                    }

                    String[] splittedJoinCondition = splittedText[i + 3].split("=");
                    if(splittedJoinCondition.length != 2){
                        errMessage = "Invalid Syntax in join condition!";
                        return false;
                    }
                    String[] firstJoinCondition = splittedJoinCondition[0].split("[.]");
                    if(!validateTable(firstJoinCondition[0])){
                        errMessage = "Non-existent table name in join condition!";
                        return false;
                    }
                    if(!validateAttributeInTable(firstJoinCondition[0], firstJoinCondition[1])){
                        errMessage = "Non-existent attribute name for table in join condition!";
                        return false;
                    }

                    String[] secondJoinCondition = splittedJoinCondition[1].split("[.]");
                    if(!validateTable(secondJoinCondition[0])){
                        errMessage = "Non-existent table name in join condition!";
                        return false;
                    }
                    if(!validateAttributeInTable(secondJoinCondition[0], secondJoinCondition[1])){
                        errMessage = "Non-existent attribute name for table in join condition!";
                        return false;
                    }

                    if(!firstJoinCondition[1].equals(secondJoinCondition[1])){
                        errMessage = "Foreign and primary key don`t match in join condition!";
                        return false;
                    }
                    joinConditions.add(splittedText[i + 3]);
                }
                afterJoinsIndex++;
                System.out.println(afterJoinsIndex);
                if(Collections.frequency(list, "WHERE") == 1 || Collections.frequency(list, "where") == 1){
                    System.out.println("van where");
                    if(Collections.frequency(list, "AND") == 0 && Collections.frequency(list, "and") == 0){
                        if(length != afterJoinsIndex + 3){
                            errMessage = "Syntax error!";
                            return false;
                        }
                        System.out.println("and nelkuli hossz ok");
                    }
                    System.out.println("van and");
                    if(!splittedText[afterJoinsIndex + 1].equals("WHERE") && !splittedText[afterJoinsIndex + 1].equals("where")){
                        errMessage = "Syntax error!";
                        return false;
                    }

                    if(splittedText[afterJoinsIndex + 2].split("<=").length == 2){
                        String[] splittedWhereCondition = splittedText[afterJoinsIndex + 2].split("<=");
                        String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                        if(!validateTable(splittedFirstPart[0])){
                            errMessage = "Invalid table name in where condition!";
                            return false;
                        }
                        if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                            errMessage = "Invalid attribute name  for table in where condition!";
                            return false;
                        }
                        whereOperators.add("<=");
                    }else if(splittedText[afterJoinsIndex + 2].split(">=").length == 2){
                        String[] splittedWhereCondition = splittedText[afterJoinsIndex + 2].split(">=");
                        String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                        if(!validateTable(splittedFirstPart[0])){
                            errMessage = "Invalid table name in where condition!";
                            return false;
                        }
                        System.out.println(splittedFirstPart[0]);
                        System.out.println(splittedFirstPart[1]);
                        if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                            errMessage = "Invalid attribute name  for table in where condition!";
                            return false;
                        }
                        whereOperators.add(">=");
                    }
                    else if(splittedText[afterJoinsIndex + 2].split("=").length == 2){
                        String[] splittedWhereCondition = splittedText[afterJoinsIndex + 2].split("=");
                        String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                        if(!validateTable(splittedFirstPart[0])){
                            errMessage = "Invalid table name in where condition!";
                            return false;
                        }
                        if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                            errMessage = "Invalid attribute name  for table in where condition!";
                            return false;
                        }
                        whereOperators.add("=");
                    }
                    else if(splittedText[afterJoinsIndex + 2].split("<").length == 2){
                        String[] splittedWhereCondition = splittedText[afterJoinsIndex + 2].split("<");
                        String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                        if(!validateTable(splittedFirstPart[0])){
                            errMessage = "Invalid table name in where condition!";
                            return false;
                        }
                        if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                            errMessage = "Invalid attribute name  for table in where condition!";
                            return false;
                        }
                        whereOperators.add("<");
                    }
                    else if(splittedText[afterJoinsIndex + 2].split(">").length == 2){
                        String[] splittedWhereCondition = splittedText[afterJoinsIndex + 2].split(">");
                        String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                        if(!validateTable(splittedFirstPart[0])){
                            errMessage = "Invalid table name in where condition!";
                            return false;
                        }
                        if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                            errMessage = "Invalid attribute name  for table in where condition!";
                            return false;
                        }
                        whereOperators.add(">");
                    }else{
                        errMessage = "Invalid where condition!";
                        return false;
                    }

                    whereConditions.add(splittedText[afterJoinsIndex + 2]);

                    int afterWhereAndFirstCond = afterJoinsIndex + 3;
                    System.out.println(length);
                    System.out.println(afterWhereAndFirstCond);
                    System.out.println((length - (afterWhereAndFirstCond)) % 2);
                    if((length - (afterWhereAndFirstCond)) % 2 != 0){
                        errMessage = "Syntax error!";
                        return false;
                    }
                    System.out.println("hossz ok");
                    for(int i=afterJoinsIndex + 3;i<length;i += 2){
                        System.out.println(i);
                        if(!splittedText[i].equals("AND") && !splittedText[i].equals("and")){
                            System.out.println("nem ok");
                            errMessage = "Syntax error!";
                            return false;
                        }
                        if(splittedText[i + 1].split("<=").length == 2){
                            String[] splittedWhereCondition = splittedText[i + 1].split("<=");
                            String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                            if(!validateTable(splittedFirstPart[0])){
                                errMessage = "Invalid table name in where condition!";
                                return false;
                            }
                            if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                                errMessage = "Invalid attribute name  for table in where condition!";
                                return false;
                            }
                            whereOperators.add("<=");
                        }else if(splittedText[i + 1].split(">=").length == 2){
                            String[] splittedWhereCondition = splittedText[i + 1].split(">=");
                            String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                            if(!validateTable(splittedFirstPart[0])){
                                errMessage = "Invalid table name in where condition!";
                                return false;
                            }
                            if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                                errMessage = "Invalid attribute name  for table in where condition!";
                                return false;
                            }
                            whereOperators.add(">=");
                        }
                        else if(splittedText[i + 1].split("=").length == 2){
                            String[] splittedWhereCondition = splittedText[i + 1].split("=");
                            String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                            if(!validateTable(splittedFirstPart[0])){
                                errMessage = "Invalid table name in where condition!";
                                return false;
                            }
                            if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                                errMessage = "Invalid attribute name  for table in where condition!";
                                return false;
                            }
                            whereOperators.add("=");
                        }
                        else if(splittedText[i + 1].split("<").length == 2){
                            String[] splittedWhereCondition = splittedText[i + 1].split("<");
                            String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                            if(!validateTable(splittedFirstPart[0])){
                                errMessage = "Invalid table name in where condition!";
                                return false;
                            }
                            if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                                errMessage = "Invalid attribute name  for table in where condition!";
                                return false;
                            }
                            whereOperators.add("<");
                        }
                        else if(splittedText[i + 1].split(">").length == 2){
                            String[] splittedWhereCondition = splittedText[i + 1].split(">");
                            String[] splittedFirstPart = splittedWhereCondition[0].split("[.]");
                            if(!validateTable(splittedFirstPart[0])){
                                errMessage = "Invalid table name in where condition!";
                                return false;
                            }
                            if(!validateAttributeInTable(splittedFirstPart[0], splittedFirstPart[1])){
                                errMessage = "Invalid attribute name  for table in where condition!";
                                return false;
                            }
                            whereOperators.add(">");
                        }else{
                            errMessage = "Invalid where condition!";
                            return false;
                        }
                        whereConditions.add(splittedText[i + 1]);
                    }
                }else if(Collections.frequency(list, "WHERE") == 0){
                    errMessage = "Syntax error!";
                    return false;
                }
            }
        }
        return true;
    }

    public String getErrorMessage(){
        return this.errMessage;
    }

    public Boolean validateTable(String table){
        Boolean tableOK = false;
        for(String tabla : tables){
            if(tabla.equals(table)){
                tableOK = true;
                break;
            }
        }
        if(!tableOK){
            return false;
        }
        return true;
    }

    public Boolean validateAttributeInTable(String table, String attribute){
        int index = 0;
        for(int i=0;i<tables.size();i++){
            if(tables.get(i).equals(table)){
                index = i;
                break;
            }
        }
        Boolean attributeOK = false;
        Vector<String> row= attributesPerTable.get(index);
        for(int i=0;i<row.size();i++){
            if(row.get(i).equals(attribute)){
                attributeOK = true;
                break;
            }
        }
        if(!attributeOK){
            return false;
        }
        return true;
    }

    public Boolean validateJoinAttributeFK(String table, String attribute) throws IOException{
        String wholeText = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
        String tableText = wholeText.substring(wholeText.indexOf("tableName=\"" + table) + 11,
                wholeText.indexOf("</Table>", wholeText.indexOf("tableName=\"" + table) + 12));
        if(!tableText.contains("fkAttribute>" + attribute)){
            return false;
        }
        return true;
    }

    public Boolean validateJoinAttributePK(String table, String attribute) throws IOException{
        String wholeText = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
        String tableText = wholeText.substring(wholeText.indexOf("tableName=\"" + table) + 11,
                wholeText.indexOf("</Table>", wholeText.indexOf("tableName=\"" + table) + 12));
        if(!tableText.contains("pkAttribute>" + attribute)){
            return false;
        }
        return true;
    }

    public Vector<Vector<String>> getResultInformation(){
        resultInformation.add(projections);
        resultInformation.add(joinConditions);
        resultInformation.add(whereConditions);
        resultInformation.add(whereOperators);
        resultInformation.add(joinedTables);
        return resultInformation;
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
