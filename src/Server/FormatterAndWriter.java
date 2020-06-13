package Server;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class FormatterAndWriter implements Runnable {

    private int start;
    private int limit;

    private String[] projections;

    private Vector<String> result;
    private Vector<Integer> projectionIndexesToShow;
    private Vector<String> attributes;

    private MongoCollection coll;

    public FormatterAndWriter(int start, int limit, Vector<String> result, Vector<Integer> projectionIndexesToShow,
                              Vector<String> attributes, String[] projections, MongoCollection coll){
        this.start = start;
        this.limit = limit;
        this.result = result;
        this.projections = projections;
        this.projectionIndexesToShow = projectionIndexesToShow;
        this.attributes = attributes;
        this.coll = coll;
    }

    public void run(){
        FileWriter writer2 = null;
        try {
            writer2 = new FileWriter("OUTPUT/output.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FindIterable findIterable;
        String results = "";
        for(int i=start;i<limit;i++) {
            results = "";
            if(projections.length == 1 && projections[0].equals("*")) {
                findIterable = coll.find();//Filters.eq("key", result.get(i)));
                for(Object doc : findIterable){
                    //System.out.println(doc.toString());
                    String values = doc.toString().substring(doc.toString().indexOf("value=") + 6,
                            doc.toString().indexOf("}", doc.toString().indexOf("value=") + 7));
                    String pk = doc.toString().substring(doc.toString().indexOf("key=") + 4,
                            doc.toString().indexOf(",", doc.toString().indexOf("key=") + 5));
                    String[] splittedValues = values.split("#");
                    results += "{ " + attributes.get(0) + ":" + pk + ", ";
                    for(int j=1;j<attributes.size();j++){
                        results +=attributes.get(j) + ":" + splittedValues[j - 1] + ", ";
                    }

                    results +="}\n";
                    try {
                        writer2.write(results);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }else{
                findIterable = coll.find(Filters.eq("key", result.get(i)));
                for(Object doc : findIterable){
                    String values = doc.toString().substring(doc.toString().indexOf("value=") + 6,
                            doc.toString().indexOf("}", doc.toString().indexOf("value=") + 7));
                    String pk = doc.toString().substring(doc.toString().indexOf("key=") + 4,
                            doc.toString().indexOf(",", doc.toString().indexOf("key=") + 5));
                    String[] splittedValues = values.split("#");
                    for(int j=0;j<attributes.size();j++){
                        if(projectionIndexesToShow.contains(j)){
                            if(j == 0){
                                results += attributes.get(j) + ":" + pk + ", ";
                            }else{
                                results += attributes.get(j) + ":" + splittedValues[j - 1] + ", ";
                            }
                        }
                    }
                    results +="}\n";
                    try {
                        writer2.write(results);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        try {
            //writer2.write(results);
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
