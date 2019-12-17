package QuestionGraph;

enum QGItemType{
    entity,label,attr,rel,value,other,question
}

public class QGItem {

    private int ID;

    private QGItemType type;

    private String value;

    public QGItem(int ID, String type, String value) {
        this.ID = ID;
        this.type = QGItemType.valueOf(type);
        this.value = value;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public QGItemType getType() {
        return type;
    }

    public void setType(QGItemType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)",this.value, this.type.name());
    }
}
