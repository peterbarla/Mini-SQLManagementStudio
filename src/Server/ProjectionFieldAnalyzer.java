package Server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjectionFieldAnalyzer {
    private String[] projections;
    private String[] joinedTables;
    private String[] allAttributesPerTable;

    private Boolean allFieldsIndexed;

    private String wholeDBText;
    private String DB;
    public ProjectionFieldAnalyzer(String[] projections, String[] joinedTables, String DB, String[] allAttributesPerTable) throws IOException{
        this.projections = projections;
        this.joinedTables = joinedTables;
        this.allFieldsIndexed = true;
        this.DB = DB;
        this.wholeDBText = readFile("Structures/" + DB + ".txt", StandardCharsets.UTF_8);
        this.allAttributesPerTable = allAttributesPerTable;
        analyze();
    }

    public void analyze(){
        if(projections.length == 1 && projections[0].equals("*")){
            for(int i=0;i<joinedTables.length;i++){
                for(String table : allAttributesPerTable){
                    String[] splittedAttributes = table.split("/");
                    if(joinedTables[i].equals(splittedAttributes[0])){
                        for(int j=1;j<splittedAttributes.length;j++){
                            if(!(new File("IndexFiles/" + DB + table + splittedAttributes[j] + ".ind").exists())){
                                allFieldsIndexed = false;
                                return;
                            }
                        }
                    }
                }
            }
        }else{
            for(String proj : projections){
                String[] tableAndAttributeProjection = proj.split("[.]");
                if(!(new File("IndexFiles/" + DB + tableAndAttributeProjection[0] +
                        tableAndAttributeProjection[1] + ".ind").exists())){
                    allFieldsIndexed = false;
                    return;
                }
            }
        }
    }

    public Boolean areAllFieldsIndexed(){
        return allFieldsIndexed;
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
