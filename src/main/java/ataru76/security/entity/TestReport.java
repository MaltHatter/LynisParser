package ataru76.security.entity;

import ataru76.security.Severities;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "report_tests")
public class TestReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "result")
    private String result;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "severity")
    private Severities severity;


    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;


    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
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


    public Report getReport() {
        return report;
    }

    public Severities getSeverity() {
        return severity;
    }

    public void setSeverity(Severities severity) {
        this.severity = severity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
