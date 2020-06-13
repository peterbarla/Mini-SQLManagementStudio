package Server;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.Vector;

public class UIBuilderNotPK implements Runnable {

    private int start;
    private int limit;
    private int offset;
    private int position;

    private Vector<String> attributes;
    private Vector<String> values;
    private Vector<String> pkList;

    private DBCollection coll;
    private DBCollection coll2;

    public UIBuilderNotPK(int start, int limit, int offset, int position, Vector<String> attributes, Vector<String> values,Vector<String> pkList, DBCollection coll,
                          DBCollection coll2){
        this.start = start;
        this.limit = limit;
        this.offset = offset;
        this.position = position;
        this.attributes = attributes;
        this. values = values;
        this.pkList = pkList;
        this.coll = coll;
        this.coll2 = coll2;
    }
    public void run(){
        for (int i = start; i < limit; i++) {
            DBObject obj2 = new BasicDBObject();
            if(isInt(values.get(i)))
                obj2.put("key", Integer.parseInt(values.get(i)));
            else
                obj2.put("key", values.get(i));
            obj2.put("value", pkList.get(i));
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
