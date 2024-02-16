package subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    // Test1 : Test 의존적인 생성자 추가
    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void update(final String name, final String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

//    새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다.
//    이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.
    public void addSection(Station upStation, Station downStation, int distance) {
        if (sections.size() == 0) {
            sections.add(new Section(this, upStation, downStation, distance));
            return;
        }
        if (!sections.get(sections.size() - 1).getDownStationId().getId().equals(upStation.getId())) {
            throw new RuntimeException("상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다.");
        }
        if (sections.stream().anyMatch(section -> section.getUpStationId().getId().equals(upStation.getId()) || section.getDownStationId().getId().equals(downStation.getId()))) {
            throw new RuntimeException("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다.");
        }
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public void deleteSection(Long stationId) {
        if (sections.size() == 0) {
            throw new RuntimeException("노선에는 구간이 존재해야 합니다.");
        }
        if (sections.size() == 1) {
            throw new RuntimeException("상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없습니다.");
        }
        if (!sections.get(sections.size() - 1).getDownStationId().getId().equals(stationId)) {
            throw new RuntimeException("하행 종점역을 찾을 수 없거나 마지막 종점역만 제거할 수 있습니다.");
        }
        sections.remove(sections.size() - 1);
    }
}
