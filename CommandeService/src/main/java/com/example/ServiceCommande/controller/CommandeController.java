package com.example.ServiceCommande.controller;

import com.example.ServiceCommande.dto.CommandeRequest;
import com.example.ServiceCommande.dto.CommandeResponse;
import com.example.ServiceCommande.service.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commande")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommandeResponse> createCommande(@RequestBody CommandeRequest commandeRequest) {
        CommandeResponse commandeResponse = commandeService.createCommande(commandeRequest);
        return new ResponseEntity<>(commandeResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandeResponse> updateCommande(@PathVariable String id, @RequestBody CommandeRequest commandeRequest) {
        CommandeResponse commandeResponse = commandeService.updateCommande(id, commandeRequest);
        return ResponseEntity.ok(commandeResponse);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommandeResponse> getAllCommandes() {
        return commandeService.getAllCommandes();
    }

    @GetMapping("/id/{idCommande}")
    public ResponseEntity<CommandeResponse> getCommandeById(@PathVariable String idCommande) {
        CommandeResponse commandeResponse = commandeService.getCommandeById(idCommande);
        return ResponseEntity.ok(commandeResponse);
    }

    @GetMapping("/etat/{etat}")
    public ResponseEntity<List<CommandeResponse>> getCommandesByEtat(@PathVariable String etat) {
        return ResponseEntity.ok(commandeService.getCommandesByEtat(etat));
    }

    @GetMapping("/fournisseur/{fournisseur}")
    public ResponseEntity<List<CommandeResponse>> getCommandesByFournisseur(@PathVariable String fournisseur) {
        return ResponseEntity.ok(commandeService.getCommandesByFournisseur(fournisseur));
    }

    @GetMapping("/destination/{destination}")
    public ResponseEntity<List<CommandeResponse>> getCommandesByDestination(@PathVariable String destination) {
        return ResponseEntity.ok(commandeService.getCommandesByDestination(destination));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable String id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }
}
