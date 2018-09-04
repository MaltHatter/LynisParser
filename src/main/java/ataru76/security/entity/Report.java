package ataru76.security.entity;

import ataru76.security.Severities;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;



    @Column(name = "name")
    private String name;

    @Column(name = "auditor")
    private String auditor;
    @Column(name = "rank")
    private int rank;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "severity")
    private Severities severity;

    @OneToMany(mappedBy = "report",cascade = CascadeType.ALL)
    private List<TestReport> tests;

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }


    public List<TestReport> getTests() {
        return tests;
    }

    public void setTests(List<TestReport> tests) {
        this.tests = tests;
    }

    public Report() {
        tests = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
