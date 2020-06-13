package Server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import javax.swing.text.Document;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class JoinQueryNonIndexed {
    private String DB;
    private String[] projections;
    private String[] joinConditions;
    private String[] whereConditions;
    private String[] joinedTables;
    private String[] whereOperators;

    private MongoClientURI clientUri;
    private MongoClient client;

    public JoinQueryNonIndexed(String DB, String[] projections, String[] joinConditions,
                               String[] whereConditions, String[] joinedTables, String[] whereOperators){
        this.DB =DB;
        this.projections = projections;
        this.joinConditions = joinConditions;
        this.whereConditions = whereConditions;
        this.joinedTables = joinedTables;
        this.whereOperators = whereOperators;
        String uri = "mongodb+srv://m001-student:m001-mongodb-basics@petercluster-2rjbe.mongodb.net/test";
        this.clientUri = new MongoClientURI(uri);
        this.client = new MongoClient(clientUri);

    }

    public Vector<String> getQueryResult() throws IOException {
        Vector<String> actualResult = new Vector<>();
        Vector<String> finalResult = new Vector<>();
        Vector<String> tmpResult = new Vector<>();
        Boolean isFirst = true;
        MongoDatabase indexDatabase = client.getDatabase("Indexes");
        System.out.println(joinConditions.length);
        // bejarjuk az osszes joint
        for(int i=0;i<joinConditions.length;i++){
            String[] splittedJoinCOndition = joinConditions[i].split("=");
            String[] firstPart = splittedJoinCOndition[0].split("[.]");
            String[] secondPart = splittedJoinCOndition[1].split("[.]");
            String table1 = firstPart[0];
            String atr1 = firstPart[1];
            String table2 = secondPart[0];
            String atr2 = secondPart[1];

            //kikeressuk a tabl1 es table2 valodi primary key-et
            String realAtr1 = getPKForTable(table1);
            String realAtr2 = getPKForTable(table2);
            System.out.println(realAtr1);
            System.out.println(realAtr2);

            //csak az index allomanyokkal dolgozunk
            MongoCollection coll1 = indexDatabase.getCollection(DB + table1 + atr1 + ".ind");
            MongoCollection coll2 = indexDatabase.getCollection(DB + table2 + atr2 + ".ind");

            FindIterable collection2Objects = coll2.find();
            // indexed nested loop
            for(Object obj : collection2Objects) {
                // kikeressuk az elso tabla primary key-jenek az erteket
                String coll2Value = obj.toString().substring(obj.toString().indexOf("value=") + 6, obj.toString().indexOf("}",
                        obj.toString().indexOf("value=") + 7));
                //rakeresunk a masik tabla rekordjai kozott azokra amelyeknek a foreign key-je az elso tabla primery key-je
                FindIterable collection1Result = coll1.find(Filters.eq("key", Integer.parseInt(coll2Value)));
                for (Object obj2 : collection1Result) {
                    String valueString = obj2.toString().substring(obj2.toString().indexOf("value=") + 6,
                            obj2.toString().indexOf("}", obj2.toString().indexOf("value=") + 7));
                    String[] splittedValues = valueString.split("#");
                    //osszeragasszuk tabla1_primary_key#tabla2_primary_key
                    for (String str : splittedValues) {
                        tmpResult.add(realAtr1 + "/" + str + "#" + realAtr2 + "/" + coll2Value);
                    }
                }
            }
            // elmentettuk egy temporalis vekltorba az eredmenyt es a kovetkezo join eredmenyet az elozo temporalis eredmennyel olvasztjuk ossze
            // a joinok megoldasa txt szerint
            for(String doc : actualResult){
                //System.out.println("bent");
                for(String doc2 : tmpResult){
                    String[] docSplit = doc.split("#");
                    String[] doc2Split = doc2.split("#");

                    for(String str1 : docSplit){
                        for(String str2 : doc2Split){
                            if(str1.equals(str2)){
                                List<String> tmpList = new LinkedList<String>(Arrays.asList(doc2Split));//Arrays.asList(doc2Split);
                                tmpList.remove(str2);
                                finalResult.add(String.join("#", docSplit) + "#" + String.join("#",
                                        tmpList));
                                break;
                            }
                        }
                    }
                }
            }
            if(finalResult.isEmpty()) {
                actualResult = (Vector)tmpResult.clone();
            }else{
                actualResult = (Vector)finalResult.clone();
                finalResult.clear();
            }
            tmpResult.clear();

        }
        return actualResult;
    }

    public String getPKForTable(String table) throws  IOException{
        String whileStructure = readFile("Structures/" + this.DB + ".txt", StandardCharsets.UTF_8);
        String tableText = whileStructure.substring(whileStructure.indexOf("tableName=\"" + table),
                whileStructure.indexOf("</Table>", whileStructure.indexOf("tableName=\"" + table) +1));
        String PK = tableText.substring(tableText.indexOf("pkAttribute>") + 12, tableText.indexOf("<",
                tableText.indexOf("pkAttribute>") + 13));

        return PK;
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
