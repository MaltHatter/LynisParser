package ataru76.security.entity;

public class Report {
    private int id;
    private int description;
    private int rank;
    private TestReport[] tests;


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

    public TestReport[] getTests() {
        return tests;
    }

    public void setTests(TestReport[] tests) {
        this.tests = tests;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
