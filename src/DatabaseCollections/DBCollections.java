package DatabaseCollections;

import javax.xml.crypto.Data;
import java.util.Vector;

public class DBCollections {
    private Vector<Database> databases;
    public DBCollections(){
        databases = new Vector<Database>();
    }

    public void deleteDatabase(String name){
        for(int i =0;i<databases.size();i++){
            if(databases.get(i).getDBName().equals(name)){
                databases.remove(i);
                break;
            }
        }
    }

    public void addDatabase(Database db){
        databases.add(db);
    }

    public Database getDatabase(String name){
        for(int i =0;i<databases.size();i++){
            if(databases.get(i).getDBName().equals(name)){
                return databases.get(i);
            }
        }
        return null;
    }



    public Vector<Database> getDatabases(){
        return databases;
    }


}
