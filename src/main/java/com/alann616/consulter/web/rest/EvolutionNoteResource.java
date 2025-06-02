package com.alann616.consulter.web.rest;

import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.service.EvolutionNoteService;
import com.alann616.consulter.service.PatientService;
import com.alann616.consulter.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evolution-notes")
public class EvolutionNoteResource {
    private final EvolutionNoteService evolutionNoteService;
    private final UserService userService;
    private final PatientService patientService;

    public EvolutionNoteResource(EvolutionNoteService evolutionNoteService, UserService userService, PatientService patientService) {
        this.evolutionNoteService = evolutionNoteService;
        this.userService = userService;
        this.patientService = patientService;
    }

    @GetMapping("/all")
    public List<EvolutionNote> getAllEvolutionNotes() {
        return evolutionNoteService.getAllEvolutionNotes();
    }

    @GetMapping("/by-patient")
    public List<EvolutionNote> getEvolutionNotesByPatient(Long patientId) {
        return evolutionNoteService.getEvolutionNotesByPatientId(patientId);
    }

    @GetMapping("/{id}")
    public EvolutionNote getEvolutionNoteById(@PathVariable Long id) {
        return evolutionNoteService.getEvolutionNoteById(id);
    }

    @PostMapping("/create")
    public EvolutionNote createEvolutionNote(EvolutionNote evolutionNote) {
        return evolutionNoteService.saveEvolutionNote(evolutionNote);
    }

    @PutMapping("/update")
    public EvolutionNote updateEvolutionNote(EvolutionNote evolutionNote) {
        return evolutionNoteService.saveEvolutionNote(evolutionNote);
    }

    @DeleteMapping("/{id}")
    public void deleteEvolutionNoteById(@PathVariable Long id) {
        EvolutionNote note = evolutionNoteService.getEvolutionNoteById(id);
        evolutionNoteService.deleteEvolutionNote(id);
    }
}
