package com.example.ServiceClients.service;

import com.example.ServiceClients.dto.ClientRequest;
import com.example.ServiceClients.dto.ClientResponse;
import com.example.ServiceClients.model.Client;
import com.example.ServiceClients.repository.ClientRepository;

import com.example.ServiceClients.service.exception.ClientNotFoundException;
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
        Client client = Client.builder()

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
        // Recherche du client par ID dans la base de données
        Optional<Client> client = clientRepository.findById(idClient);

        // Si le client est trouvé, on retourne la réponse construite
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
            // Si le client n'est pas trouvé, on lance une exception personnalisée
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
