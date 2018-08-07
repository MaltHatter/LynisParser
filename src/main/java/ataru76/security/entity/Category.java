package ataru76.security.entity;

public class Category {
    private int id;
    private int description;
    private Test[] tests;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public Test[] getTests() {
        return tests;
    }

    public void setTests(Test[] tests) {
        this.tests = tests;
    }
}
