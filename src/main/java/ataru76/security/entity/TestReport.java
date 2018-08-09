package ataru76.security.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "report_tests")
public class TestReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="result")
    private String result;
    @ManyToOne
    @JoinColumn(name="test_id", nullable=false)
    private Test test;


    @ManyToOne
    @JoinColumn(name="report_id", nullable=false)
    private Report report;



    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public Report getReport() {
        return report;
    }
}
