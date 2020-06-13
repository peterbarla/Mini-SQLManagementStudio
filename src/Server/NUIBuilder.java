package Server;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.Vector;

public class NUIBuilder implements Runnable {

    private int start;
    private int limit;
    private int offset;
    private int position;

    private Vector<String> attributes;
    private HashMap<Integer, String> map;

    private DBCollection coll;
    private DBCollection coll2;

    public NUIBuilder(int start, int limit, int offset, int position, Vector<String> attributes, HashMap<Integer, String> map, DBCollection coll,
                      DBCollection coll2){
        this.start = start;
        this.limit = limit;
        this.offset = offset;
        this.position = position;
        this.attributes = attributes;
        this. map = map;
        this.coll = coll;
        this.coll2 = coll2;
    }

    public void run(){
        for(int i=start;i<limit;i++) {
            DBObject obj2 = new BasicDBObject();
            String[] splitted = map.get(i).split("@");
            if(isInt(splitted[0]))
                obj2.put("key", Integer.parseInt(splitted[0]));
            else
                obj2.put("key", splitted[0]);
            obj2.put("value", splitted[1]);
            coll2.insert(obj2);
        }
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
