package com.example.ServiceClient.controller;

import com.example.ServiceClient.dto.ClientLocation;
import com.example.ServiceClient.dto.ClientRequest;
import com.example.ServiceClient.dto.ClientResponse;
import com.example.ServiceClient.service.ClientService;
import com.example.ServiceClient.service.GeocodingService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor

public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientResponse> createClient(@RequestBody ClientRequest clientRequest) {
        ClientResponse clientResponse = clientService.createClient(clientRequest);
        return new ResponseEntity<>(clientResponse, HttpStatus.CREATED);
    }
    @GetMapping("/{id}/location")
    public ResponseEntity<ClientLocation> getClientLocation(@PathVariable String id) {
        try {
            ClientLocation dto = clientService.getClientLocation(id);
            return ResponseEntity.ok(dto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{idClient}/addCommande/{idCommande}")
    public ResponseEntity<Void> addCommandeToClient(@PathVariable String idClient, @PathVariable String idCommande)

    {
        clientService.addCommande(idCommande, idClient);
        return ResponseEntity.ok().build(); // Or use noContent() if no response body is needed
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable String id, @RequestBody ClientRequest clientRequest)
    {
        ClientResponse clientResponse = clientService.updateClient(id, clientRequest);
        return ResponseEntity.ok(clientResponse);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientResponse> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/id/{idClient}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable String idClient)
    {
        ClientResponse clientResponse = clientService.getClientById(idClient);
        return ResponseEntity.ok(clientResponse);
    }

    @GetMapping("/{nom}")
    public ResponseEntity<ClientResponse> getClientByName(@PathVariable String nom) {
        ClientResponse clientResponse = clientService.getClientByName(nom);
        return ResponseEntity.ok(clientResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable String id)
    {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
