package Server;

import com.mongodb.*;

import java.util.Random;

public class Fill100kPart1 implements Runnable {

    private int start;
    private int limit;
    private String DB;
    private String TB;
    private String[] fields;
    private DBCollection coll;

    public Fill100kPart1(int start, int limit, String[] fields, DBCollection coll){
        this.limit = limit;
        this.start = start;
        this.fields = fields;
        this.coll = coll;

    }

    public void run(){
        Random rand = new Random();
        for(int i=start;i<limit;i++) {
            DBObject obj = new BasicDBObject();
            obj.put("key", i + "");
            String row = "";
            // adatok feltoltese
            for (int j=1;j<fields.length;j++) {
                String[] splitted = fields[j].split("/");
                // ha a field int vagy double
                if(splitted[1].equals("int") || splitted[1].equals("double")) {
                    // ha a field foreign key
                    if (splitted[2].substring(1, 2).equals("1")) {
                        int numb = rand.nextInt(2) + 1;
                        // vagy 521 es csoport vagy 523
                        if (numb == 1) {
                            row += "521#";
                        } else {
                            row += "523#";
                        }
                        // ha a field unique
                    } else if (splitted[2].substring(2, 3).equals("1")) {
                        // i mindig mas
                        row += i + "#";
                        // ha non unique
                    } else if (splitted[2].substring(3, 4).equals("1")) {
                        // general 0 - 10k kozotti szamot
                        int numb = rand.nextInt(10000) + 1;
                        row += numb + "#";
                    } else {
                        int numb = rand.nextInt(10000) + 1;
                        row += numb + "#";
                    }
                }else{
                    // ha  field string es nem unique
                    String[] names = {"peter", "ervin", "adam", "eva", "agota", "paula", "krisztian"};
                    int numb = rand.nextInt(7);
                    row += names[numb] + "#";
                }

            }
            obj.put("value", row);
            coll.insert(obj);
        }
    }
}
