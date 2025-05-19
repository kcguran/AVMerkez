package com.avmerkez.mallservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "malls")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String district;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "geometry(Point,4326)") // SRID 4326 for WGS 84
    private Point location;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(length = 100)
    private String workingHours;

    @Column
    private String website;

    @Column(length = 20)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String services;

    @Column(columnDefinition = "TEXT")
    private String floorPlans;

    @Column
    private Integer popularityScore;

    @OneToMany(mappedBy = "mall", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Facility> facilities = new ArrayList<>();

    @PostLoad
    public void postLoad() {
        if (location != null) {
            this.latitude = location.getY();
            this.longitude = location.getX();
        }
    }
    
    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate() {
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
        
        if (latitude != null && longitude != null) {
            location = gf.createPoint(new Coordinate(longitude, latitude));
        } 
        else if (location != null && (latitude == null || longitude == null)) {
            latitude = location.getY();
            longitude = location.getX();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Mall mall = (Mall) o;
        return getId() != null && Objects.equals(getId(), mall.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}