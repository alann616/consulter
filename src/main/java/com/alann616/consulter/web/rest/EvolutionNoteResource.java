package com.alann616.consulter.web.rest;

import com.alann616.consulter.dto.EvolutionNoteDTO; // <-- Import the DTO
import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.service.EvolutionNoteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evolution-notes")
public class EvolutionNoteResource {
    private final EvolutionNoteService evolutionNoteService;

    public EvolutionNoteResource(EvolutionNoteService evolutionNoteService) {
        this.evolutionNoteService = evolutionNoteService;
    }

    // ... (Your GET and DELETE methods remain the same) ...
    @GetMapping("/all")
    public List<EvolutionNote> getAllEvolutionNotes() {
        return evolutionNoteService.getAllEvolutionNotes();
    }

    @GetMapping("/by-patient")
    public List<EvolutionNote> getEvolutionNotesByPatient(@RequestParam Long patientId) {
        return evolutionNoteService.getEvolutionNotesByPatientId(patientId);
    }

    @GetMapping("/{id}")
    public EvolutionNote getEvolutionNoteById(@PathVariable Long id) {
        return evolutionNoteService.getEvolutionNoteById(id);
    }


    // MODIFIED METHOD
    @PostMapping("/create")
    public EvolutionNote createEvolutionNote(@RequestBody EvolutionNoteDTO noteDTO) { // <-- Use the DTO
        return evolutionNoteService.saveEvolutionNoteFromDTO(noteDTO); // <-- Call a new service method
    }

    // It's good practice to have a separate DTO for updates too, but for now this will work.
    @PutMapping("/update")
    public EvolutionNote updateEvolutionNote(@RequestBody EvolutionNoteDTO noteDTO) {
        return evolutionNoteService.saveEvolutionNoteFromDTO(noteDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteEvolutionNoteById(@PathVariable Long id) {
        evolutionNoteService.deleteEvolutionNote(id);
    }
}