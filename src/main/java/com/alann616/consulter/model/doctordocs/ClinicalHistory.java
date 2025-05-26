package com.alann616.consulter.model.doctordocs;

import com.alann616.consulter.model.doctordocs.historytables.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
public class ClinicalHistory extends DoctorDocument{
    @Column(name = "type", columnDefinition = "TINYINT(1)")
    private Boolean type;

    @Column(name = "body_mass_index")
    private Double bodyMassIndex;

    @Column(name = "capillary_glycemia")
    private Double capillaryGlycemia;

    @Column(name = "cephalic_perimeter")
    private Double cephalicPerimeter;

    @Column(name = "abdominal_perimeter")
    private Double abdominalPerimeter;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "hereditary_id")
    private Hereditary hereditary;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "non_pathological_id")
    private NonPathological nonPathological;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pathological_id")
    private Pathological pathological;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gynecological_id")
    private Gynecological gynecological;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "patient_interview_id")
    private PatientInterview patientInterview;
}
