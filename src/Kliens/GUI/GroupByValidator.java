package Kliens.GUI;

import java.util.Vector;

public class GroupByValidator {

    private String groupByText;
    private String havingText;
    private String sumText;

    private Vector<Vector<String>> fields;
    private Vector<String> tables;

    private String[] splittingOperators = { "<", ">", "<=", ">=", "="};
    private String[] sumFunctions = { "min", "MIN", "max", "MAX", "sum", "SUM", "count", "COUNT", "avg", "AVG"};

    private String errorMessage = "ok";

    private String groupErrorMessage = "ok";
    private String havingErrorMessage = "ok";
    private String sumErrorMessage = "ok";

    private String type;

    private String simpleTable;

    public GroupByValidator(String groupByText, String havingText, String sumText, Vector<Vector<String>> fields, Vector<String> tables,String simpleTable,  String type){
        this.groupByText = groupByText;
        this.havingText = havingText;
        this.sumText = sumText;

        this.fields = fields;
        this.tables = tables;
        this.simpleTable = simpleTable;

        this.type = type;
    }

    public Boolean checkGroupBy(){
        Boolean isOK = false;
        String splittedGroup[] = groupByText.split("[.]");
        if(splittedGroup.length != 2){
            groupErrorMessage = "Syntax error in group by field!";
            return isOK;
        }

        if(type.equals("SIMPLE")) {
            System.out.println("SIMPLE");
            for (String str : fields.get(0)) {
                if (str.equals(splittedGroup[1]) && splittedGroup[0].equals(simpleTable)) {
                    isOK = true;
                    break;
                }
            }

        }else{
            for(int i =0;i< fields.size();i++){
                for(int j=0;j<fields.get(i).size();j++){
                    if(splittedGroup[1].equals(fields.get(i).get(j))){
                        if(splittedGroup[0].equals(tables.get(i))){
                            isOK = true;
                            break;
                        }
                    }
                }
                if(isOK) break;
            }
        }
        if(!isOK){
            groupErrorMessage = "Invalid atr name given in the group by fiweld!";
        }
        return isOK;
    }

    public Boolean checkHaving(){
        if(havingText.equals("")) return true;

        Boolean isOK = false;
        String splittedHavings[] = havingText.split("and");
        if(splittedHavings.length == 1){
            for(String operator : splittingOperators) {
                String splittedHavingPart[] = splittedHavings[0].split(operator);
                if(splittedHavingPart.length == 2){
                    String splittedWithHaving[] = splittedHavingPart[0].split("[(]");
                    if(splittedWithHaving.length == 2){
                        for(String op : sumFunctions){
                            if(op.equals(splittedWithHaving[0])){
                                String splittedPart[] = splittedWithHaving[1].split("[.]");
                                if(splittedPart.length == 2){
                                    splittedPart[1] = splittedPart[1].substring(0, splittedPart[1].length() - 1);
                                    if(type.equals("SIMPLE")){
                                        for(String atr : fields.get(0)){
                                            if(splittedPart[1].equals(atr)){
                                                if(splittedPart[0].equals(simpleTable)) {
                                                    isOK = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }else{
                                        for(int i=0;i<fields.size();i++){
                                            for(int j=0;j<fields.get(i).size();j++) {
                                                if (splittedPart[1].equals(fields.get(i).get(j))) {
                                                    if (splittedPart[0].equals(tables.get(i))) {
                                                        isOK = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(isOK) break;
                }
            }
        }else if(splittedHavings.length == 2){

            splittedHavings[0] = splittedHavings[0].substring(0, splittedHavings[0].length() - 1);
            splittedHavings[1] = splittedHavings[1].substring(1);
            for(String splitPart : splittedHavings){
                isOK = false;
                for(String operator : splittingOperators){
                    String splittedHavingPart[] = splitPart.split(operator);
                    if(splittedHavingPart.length == 2) {
                        String splittedWithHaving[] = splittedHavingPart[0].split("[(]");
                        if (splittedWithHaving.length == 2) {
                            for (String op : sumFunctions) {
                                if (op.equals(splittedWithHaving[0])) {
                                    String splittedPart[] = splittedWithHaving[1].split("[.]");
                                    if (splittedPart.length == 2) {
                                        splittedPart[1] = splittedPart[1].substring(0, splittedPart[1].length() - 1);
                                        if (type.equals("SIMPLE")) {
                                            for (String atr : fields.get(0)) {
                                                if (splittedPart[1].equals(atr)) {
                                                    if (splittedPart[0].equals(simpleTable)) {
                                                        isOK = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            for (int i = 0; i < fields.size(); i++) {
                                                for (int j = 0; j < fields.get(i).size(); j++) {
                                                    if (splittedPart[1].equals(fields.get(i).get(j))) {
                                                        if (splittedPart[0].equals(tables.get(i))) {
                                                            isOK = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(isOK) break;
                }
            }
        }else{
            splittedHavings[0] = splittedHavings[0].substring(0, splittedHavings[0].length() - 1);
            splittedHavings[splittedHavings.length - 1] = splittedHavings[splittedHavings.length - 1].substring(1);
            for(int i = 0;i<splittedHavings.length;i++){
                isOK = false;
                if(i > 0 && i < splittedHavings.length - 1) {
                    splittedHavings[i] = splittedHavings[i].substring(1, splittedHavings[i].length() - 1);
                }
                System.out.println(splittedHavings[i]);
                for(String operator : splittingOperators){
                    String splittedHavingPart[] = splittedHavings[i].split(operator);
                    if(splittedHavingPart.length == 2){
                        String splittedWithHaving[] = splittedHavingPart[0].split("[(]");
                        if (splittedWithHaving.length == 2) {
                            for (String op : sumFunctions) {
                                if (op.equals(splittedWithHaving[0])) {
                                    String splittedPart[] = splittedWithHaving[1].split("[.]");
                                    if (splittedPart.length == 2) {
                                        splittedPart[1] = splittedPart[1].substring(0, splittedPart[1].length() - 1);
                                        if (type.equals("SIMPLE")) {
                                            for (String atr : fields.get(0)) {
                                                if (splittedPart[1].equals(atr)) {
                                                    if (splittedPart[0].equals(simpleTable)) {
                                                        isOK = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            for (int k = 0; k < fields.size(); k++) {
                                                for (int j = 0; j < fields.get(k).size(); j++) {
                                                    if (splittedPart[1].equals(fields.get(k).get(j))) {
                                                        if (splittedPart[0].equals(tables.get(k))) {
                                                            isOK = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(isOK) break;
                }
            }
        }
        if(!isOK){
            havingErrorMessage = "Invalid atr name given in the having field!";
        }
        return isOK;
    }

    public Boolean checkSum(){
        Boolean isSumOK = false;
        Boolean isAtrOK = false;
        String splittedSums[] = sumText.split(",");
        for(String part : splittedSums) {
            isSumOK = false;
            isAtrOK = false;
            String splittedSumPart[] = part.split("[(]");
            if (splittedSumPart.length == 2) {
                for (String func : sumFunctions) {
                    if (func.equals(splittedSumPart[0])) {
                        isSumOK = true;
                        break;
                    }
                }
                splittedSumPart[1] = splittedSumPart[1].substring(0, splittedSumPart[1].length() - 1);
                String splittedPart[] = splittedSumPart[1].split("[.]");
                if(type.equals("SIMPLE")){
                    for (String atr : fields.get(0)) {
                        if (atr.equals(splittedPart[1])) {
                            if(splittedPart[0].equals(simpleTable)) {
                                isAtrOK = true;
                                break;
                            }
                        }
                    }
                }else{
                    for(int k=0;k<fields.size();k++){
                        for(int j=0;j<fields.get(k).size();j++) {
                            if (splittedPart[1].equals(fields.get(k).get(j))) {
                                if (splittedPart[0].equals(tables.get(k))) {
                                    isAtrOK = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(isAtrOK && isSumOK) return true;
        else { sumErrorMessage = "invalid sum or atr name given in the sum field!"; return false;}
    }

    public  Vector<String> getErrorMessage(){
        Vector<String> errors = new Vector<>();
        errors.add(groupErrorMessage);
        errors.add(havingErrorMessage);
        errors.add(sumErrorMessage);
        errors.add(errorMessage);
        return errors;
    }

    public Boolean isValid(){
        if((!groupByText.equals("") && sumText.equals("")) || (groupByText.equals("") && !sumText.equals(""))
        || (!havingText.equals("") && (groupByText.equals("") || sumText.equals("")))){
            errorMessage = "You filled group by fields incorrectly!";
            return false;
        }else if(groupByText.equals("") && sumText.equals("") && havingText.equals("")) return true;
        if(checkGroupBy() && checkHaving() && checkSum())
            return true;
        return false;
    }
}
