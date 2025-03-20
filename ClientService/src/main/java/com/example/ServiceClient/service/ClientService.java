package com.example.ServiceClient.service;

import com.example.ServiceClient.dto.ClientRequest;
import com.example.ServiceClient.dto.ClientResponse;
import com.example.ServiceClient.model.Client;
import com.example.ServiceClient.repository.ClientRepository;
import com.example.ServiceClient.service.exception.ClientNotFoundException;
import com.example.ServiceClient.service.exception.ClientAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientResponse createClient(ClientRequest clientRequest) {
        // Check if the idClient already exists
        if (clientRequest.getIdClient() != null) {
            Optional<Client> existingClient = clientRepository.findById(clientRequest.getIdClient());
            if (existingClient.isPresent()) {
                throw new ClientAlreadyExistsException("Client with id " + clientRequest.getIdClient() + " already exists.");
            }
        }

        // Create a new client with the provided idClient
        Client client = Client.builder()
                .idClient(clientRequest.getIdClient())  // Using the provided idClient
                .nom(clientRequest.getNom())
                .contact(clientRequest.getContact())
                .adresse(clientRequest.getAdresse())
                .commandes(clientRequest.getCommandes())
                .produitsAdditionnelsAchetes(clientRequest.getProduitsAdditionnelsAchetes())
                .build();

        client = clientRepository.save(client);
        log.info("Client {} has been saved.", client.getIdClient());
        return mapToClientResponse(client);
    }

    public ClientResponse updateClient(String id, ClientRequest clientRequest) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Cannot find client with id %s", id)));

        existingClient.setNom(clientRequest.getNom());
        existingClient.setContact(clientRequest.getContact());
        existingClient.setAdresse(clientRequest.getAdresse());
        existingClient.setCommandes(clientRequest.getCommandes());
        existingClient.setProduitsAdditionnelsAchetes(clientRequest.getProduitsAdditionnelsAchetes());

        Client updatedClient = clientRepository.save(existingClient);
        return mapToClientResponse(updatedClient);
    }

    public List<ClientResponse> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream().map(this::mapToClientResponse).toList();
    }

    public ClientResponse getClientByName(String nom) {
        Client client = clientRepository.findByNom(nom)
                .orElseThrow(() -> new RuntimeException(String.format("Cannot find client with name %s", nom)));
        return mapToClientResponse(client);
    }

    public ClientResponse getClientById(String idClient) {
        Optional<Client> client = clientRepository.findById(idClient);

        if (client.isPresent()) {
            Client c = client.get();
            return ClientResponse.builder()
                    .idClient(c.getIdClient())
                    .nom(c.getNom())
                    .contact(c.getContact())
                    .adresse(c.getAdresse())
                    .commandes(c.getCommandes())
                    .produitsAdditionnelsAchetes(c.getProduitsAdditionnelsAchetes())
                    .build();
        } else {
            throw new ClientNotFoundException("Client not found with id: " + idClient);
        }
    }

    public void deleteClient(String id) {
        clientRepository.deleteById(id);
        log.info("Client with id {} has been deleted.", id);
    }

    private ClientResponse mapToClientResponse(Client client) {
        return ClientResponse.builder()
                .idClient(client.getIdClient())
                .nom(client.getNom())
                .contact(client.getContact())
                .adresse(client.getAdresse())
                .commandes(client.getCommandes())
                .produitsAdditionnelsAchetes(client.getProduitsAdditionnelsAchetes())
                .build();
    }
}
