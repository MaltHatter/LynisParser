package ataru76.security.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reports")
public class Report  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name = "name")
    private String name;

    @Column(name = "auditor")
    private String auditor;
    @Column(name = "rank")
    private int rank;

   /* @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "report_id")*/

    @OneToMany(mappedBy = "report")
    private Set<TestReport> tests;

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


    public Set<TestReport> getTests() {
        return tests;
    }

    public void setTests(Set<TestReport> tests) {
        this.tests = tests;
    }

    public Report() {
        tests= new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
