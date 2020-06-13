package DatabaseCollections;

public class Attribute {
    private String name;
    private Boolean isPK;
    private Boolean isFK;

    public Attribute(String name, Boolean isPK, Boolean isFK){
        this.name = name;
        this.isPK = isPK;
        this.isFK = isFK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPK() {
        return isPK;
    }

    public void setPK(Boolean PK) {
        isPK = PK;
    }

    public Boolean getFK() {
        return isFK;
    }

    public void setFK(Boolean FK) {
        isFK = FK;
    }
}
