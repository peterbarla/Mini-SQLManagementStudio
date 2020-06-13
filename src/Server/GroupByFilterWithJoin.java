package Server;

import Kliens.GUI.QueryWithJoinWindow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class GroupByFilterWithJoin {

    private Vector<String> rows;
    private String groupBy;
    private String[] havingConditions;
    private String[] sumFunctions;
    private HashMap<String, Integer> temporaryOorderedRows = new HashMap<String, Integer>();
    private HashMap<String, String> finalOrderedRows = new HashMap<String, String>();
    private Vector<Vector<String>> resultVectors;
    private Vector<Vector<String>> finalResultVectors;

    private Vector<Integer> realKeyOrder = new Vector<>();

    private String[] splittingOperators = { "<=", ">=",  "<", ">", "="};

    private String DB, TB;


    public GroupByFilterWithJoin(Vector<String> rows, String groupBy, String[] havingConditions, String[] sumFunctions, String DB, String TB) throws IOException {
        this.rows = rows;
        this.groupBy = groupBy;
        this.havingConditions = havingConditions;
        this.sumFunctions = sumFunctions;
        this.DB = DB;
        this.TB = TB;

        if(!groupBy.trim().equals("")) {
            //System.out.println(groupBy.trim());
            getDistinctGroupByAttributes();
            applySumFunctions();
            filterByHaving();
        }
    }

    public void getDistinctGroupByAttributes() throws IOException {
        int index = -1;
        for(String atr : rows){
            String value = atr.substring(atr.indexOf(groupBy) + groupBy.length() + 1,
                    atr.indexOf(",", atr.indexOf(groupBy) + groupBy.length() + 2));

            if(!temporaryOorderedRows.containsKey(value)){
                index++;
                temporaryOorderedRows.put(value, index);
            }
        }

        Vector<Vector<String>> vects = new Vector<>();
        for(int i=0;i<temporaryOorderedRows.size();i++){
            Vector<String> vec = new Vector<>();
            vects.add(vec);
        }

        for(String atr : rows){
            String value = atr.substring(atr.indexOf(groupBy) + groupBy.length() + 1,
                    atr.indexOf(",", atr.indexOf(groupBy) + groupBy.length() + 2));

            vects.get(temporaryOorderedRows.get(value)).add(atr);
        }

        Iterator it = temporaryOorderedRows.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            finalOrderedRows.put(pair.getKey().toString(), String.join("#", vects.get(Integer.parseInt(pair.getValue().toString()))));

        }
    }

    public void applySumFunctions(){
        Vector<String> onlySumFunc = new Vector<>();
        Vector<String> applyableAttributes = new Vector<>();

        for(String sum : sumFunctions){
            String[] splitted = sum.split("[(]");

            onlySumFunc.add(splitted[0]);
            applyableAttributes.add(splitted[1].substring(0, splitted[1].length() - 1));
        }

        resultVectors = new Vector<>();
        for(int i =0;i<finalOrderedRows.size();i++){
            Vector<String> vec = new Vector<>();
            resultVectors.add(vec);
        }

        Iterator it = finalOrderedRows.entrySet().iterator();
        int index = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            for(int i =0;i< onlySumFunc.size();i++){
                if(onlySumFunc.get(i).equals("count") || onlySumFunc.get(i).equals("COUNT")){
                    resultVectors.get(index).add(sumFunctions[i] + "=" + pair.getValue().toString().split("#").length + ",");
                }else if(onlySumFunc.get(i).equals("min") || onlySumFunc.get(i).equals("MIN")){
                    String[] value = pair.getValue().toString().split("#");
                    int minVal = 9999999;
                    for(String str : value){
                        String val = str.substring(str.indexOf(applyableAttributes.get(i)) + applyableAttributes.get(i).length() + 1,
                                str.indexOf(",", str.indexOf(applyableAttributes.get(i)) + applyableAttributes.get(i).length() + 2));

                        if(!isInt(val)){
                            minVal = -1;
                            break;
                        }
                        else if(Integer.parseInt(val) < minVal){
                            minVal = Integer.parseInt(val);
                        }

                    }

                    resultVectors.get(index).add(sumFunctions[i] + "=" + minVal + ",");
                }else if(onlySumFunc.get(i).equals("max") || onlySumFunc.get(i).equals("MAX")){
                    String[] value = pair.getValue().toString().split("#");
                    int maxVal = -9999999;
                    for(String str : value){
                        String val = str.substring(str.indexOf(applyableAttributes.get(i)) + applyableAttributes.get(i).length() + 1,
                                str.indexOf(",", str.indexOf(applyableAttributes.get(i)) + applyableAttributes.get(i).length() + 2));
                        if(!isInt(val)){
                            maxVal = -1;
                            break;
                        }
                        else if(Integer.parseInt(val) > maxVal){
                            maxVal = Integer.parseInt(val);
                        }

                    }

                    resultVectors.get(index).add(sumFunctions[i] + "=" + maxVal + ",");
                }else if(onlySumFunc.get(i).equals("sum") || onlySumFunc.get(i).equals("SUM")){
                    String[] value = pair.getValue().toString().split("#");
                    int summVal = 0;
                    for(String str : value){
                        String val = str.substring(str.indexOf(applyableAttributes.get(i)) + applyableAttributes.get(i).length() + 1,
                                str.indexOf(",", str.indexOf(applyableAttributes.get(i)) + applyableAttributes.get(i).length() + 2));
                        if(!isInt(val)){
                            summVal = -1;
                            break;
                        }
                        else {
                            summVal += Integer.parseInt(val);
                        }

                    }
                    resultVectors.get(index).add(sumFunctions[i] + "=" + summVal + ",");
                }else if(onlySumFunc.get(i).equals("avg") || onlySumFunc.get(i).equals("AVG")){
                    String[] value = pair.getValue().toString().split("#");
                    int avgVal = 0;
                    for(String str : value){
                        String val = str.substring(str.indexOf(applyableAttributes.get(i)) + applyableAttributes.get(i).length() + 1,
                                str.indexOf(",", str.indexOf(applyableAttributes.get(i)) + applyableAttributes.get(i).length() + 2));
                        if(!isInt(val)){
                            avgVal = -1;
                            break;
                        }
                        else {
                            avgVal += Integer.parseInt(val);
                        }

                    }
                    resultVectors.get(index).add(sumFunctions[i] + "=" + (float)avgVal/value.length + ",");
                }
            }
            index++;

        }

    }

    public void writeResult(String filePath) throws IOException {

        Vector<String> numberOfRows = new Vector<>();
        System.out.println(realKeyOrder);
        for(int i=0;i<realKeyOrder.size();i++){
            Iterator it = finalOrderedRows.entrySet().iterator();
            int index = 0;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(realKeyOrder.get(i) == index){
                    numberOfRows.add(pair.getKey().toString());
                    index++;
                    break;
                }
                index++;
            }
        }
        System.out.println(numberOfRows.size());

        ResultText resultText = new ResultText();
        System.out.println(finalOrderedRows.size());
        // System.out.println(finalResultVectors.size());
        if(!groupBy.trim().equals("")) {
            PrintWriter out = new PrintWriter(new FileWriter(filePath));
            Iterator it = finalOrderedRows.entrySet().iterator();
            //System.out.println(finalResultVectors);
            if (!finalResultVectors.isEmpty()) {
                int index = 0;
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if(realKeyOrder.size() == 0) {
                        out.write(groupBy + "=" + pair.getKey().toString() + ": " +
                                String.join(",", finalResultVectors.get(index)) + ",");
                        out.write("\n");
                        resultText.setText(groupBy + "=" + pair.getKey().toString() + ": " +
                                String.join(",", finalResultVectors.get(index)) + "," + "\n");
                    }else{
                        out.write(groupBy + "=" + numberOfRows.get(index) + ": " +
                                String.join(",", finalResultVectors.get(index)) + ",");
                        out.write("\n");
                        resultText.setText(groupBy + "=" + numberOfRows.get(index) + ": " +
                                String.join(",", finalResultVectors.get(index)) + "," + "\n");
                    }
                    index++;
                    if (index == finalResultVectors.size()) break;

                }
            }
            System.out.println("Done!");
            out.close();
        }else{
            PrintWriter out = new PrintWriter(new FileWriter("OUTPUT/output.txt"));
            for(String row : rows){
                out.write(row + "\n");
                resultText.setText(row + "\n");
            }
            out.close();
        }

    }

    public void filterByHaving(){
        //System.out.println(resultVectors);
        //System.out.println(resultVectors.get(10).get(0));
        if(!(havingConditions.length == 1 && havingConditions[0].equals(""))) {
            System.out.println("bent");
            finalResultVectors = new Vector<Vector<String>>();
            Boolean matches;
            System.out.println(resultVectors.size());
            for (int i = 0; i < resultVectors.size(); i++) {
                matches = true;
                for (int j = 0; j < resultVectors.get(i).size(); j++) {
                    for (int k = 0; k < havingConditions.length; k++) {
                        System.out.println(havingConditions[k]);
                        for (String op : splittingOperators) {
                            Boolean isSplitted = false;
                            if (havingConditions[k].split(op).length == 2) {
                                isSplitted = true;
                                System.out.println("felbontva");
                                String splitted[] = havingConditions[k].split(op);
                                System.out.println(splitted[0]);
                                System.out.println(splitted[1]);
                                if (splitted[0].equals(resultVectors.get(i).get(j).split("=")[0])) {
                                    //System.out.println("egyenlo");
                                    String value = resultVectors.get(i).get(j).substring(resultVectors.get(i).get(j).indexOf(
                                            splitted[0]) + splitted[0].length() + 1, resultVectors.get(i).get(j).indexOf(",",
                                            resultVectors.get(i).get(j).indexOf(splitted[0]) + splitted[0].length() + 1)
                                    );
                                    System.out.println(value);
                                    if (op.equals("=")) {
                                        //System.out.println("value= " + value);
                                        if (!value.equals(splitted[1])) {
                                            matches = false;
                                            break;
                                        }
                                        System.out.println("egyenlo volt!");
                                    } else if (op.equals("<=")) {
                                        if (Integer.parseInt(value) > Integer.parseInt(splitted[1])) {
                                            matches = false;
                                            break;
                                        }
                                    } else if (op.equals(">=")) {
                                        if (Integer.parseInt(value) < Integer.parseInt(splitted[1])) {
                                            System.out.println("nem jo");
                                            matches = false;
                                            break;
                                        }
                                    } else if (op.equals("<")) {
                                        if (Integer.parseInt(value) >= Integer.parseInt(splitted[1])) {
                                            matches = false;
                                            break;
                                        }
                                    } else if (op.equals(">")) {
                                        if (Integer.parseInt(value) <= Integer.parseInt(splitted[1])) {
                                            matches = false;
                                            break;
                                        }
                                    }
                                }

                            }
                            if(isSplitted) break;
                        }
                        if (!matches) break;
                    }
                    if (!matches) break;
                }
                if (matches) {
                    finalResultVectors.add(resultVectors.get(i));
                    realKeyOrder.add(i);
                }
            }
        }else{
            finalResultVectors = resultVectors;
        }
        System.out.println(finalResultVectors);
    }

    public static boolean isInt(String str) {
        try {
            int v = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
        }
        return false;
    }
}
