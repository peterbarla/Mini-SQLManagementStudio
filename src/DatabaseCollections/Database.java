package DatabaseCollections;

import java.util.Vector;

public class Database {
    private Vector<Table> tables;
    private String name;

    public Database(String name){
        this.name = name;
        tables = new Vector<Table>();
    }

    public void deleteTable(String name){
        for(int i=0;i<tables.size();i++){
            if(tables.get(i).getTableName().equals(name)){
                tables.remove(i);
                break;
            }
        }
    }

    public void addTable(Table tb){
        tables.add(tb);
    }

    public Vector<Table> getTables(){
        return tables;
    }

    public String getDBName(){
        return this.name;
    }
}
