package ataru76.security.entity;

public class TestReport {
    private int id;
    private Test test;
    private String result;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
