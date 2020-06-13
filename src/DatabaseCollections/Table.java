package DatabaseCollections;

import java.util.Vector;

public class Table {
    private Vector<Attribute> atributes;
    private Vector<Vector<String>> recordRows;
    private String name;

    public Table(String name){
        atributes = new Vector<Attribute>();
        recordRows = new Vector<Vector<String>>();
        this.name = name;
    }

    public void addAtr(Attribute atr){
        atributes.add(atr);
    }

    public void addRecordRow(Vector<String> row){
        recordRows.add(row);
    }

    public void deleteRecordRow(String id){
        for(Vector<String> row : recordRows){
            if(row.get(0).equals(id)){
                recordRows.remove(row);
                break;
            }
        }
    }


    public String getTableName(){
        return this.name;
    }
}
