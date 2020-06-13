package Server;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.Vector;

public class UIBuilderPK implements Runnable {
    private int start;
    private int limit;

    private Vector<String> values;

    private DBCollection coll2;

    public UIBuilderPK(int start, int limit, Vector<String> values, DBCollection coll2){
        this.start = start;
        this.limit = limit;
        this.values = values;
        this.coll2 = coll2;
    }

    public void run(){
        for(int i=start;i<limit;i++){
            DBObject obj2 = new BasicDBObject();
            if(isInt(values.get(i)))
                obj2.put("key", Integer.parseInt(values.get(i)));
            else
                obj2.put("key", values.get(i));
            obj2.put("value", values.get(i));
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

