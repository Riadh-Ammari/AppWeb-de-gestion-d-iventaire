
package com.example.ProduitService.service;

import com.example.ProduitService.dto.ProduitRequest;
import com.example.ProduitService.dto.ProduitResponse;
import com.example.ProduitService.model.Produit;
import com.example.ProduitService.repository.ProduitRepository;
import com.example.ProduitService.service.execption.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j


public class ProduitService {

    private final ProduitRepository produitRepository;
    private final WebClient.Builder webClientBuilder;

    public ProduitResponse createProduit(ProduitRequest produitRequest) {
        Produit produit = Produit.builder()
                .idProduit(produitRequest.getIdProduit())
                .idStock(produitRequest.getIdStock())
                .nom(produitRequest.getNom())
                .dateAjout(produitRequest.getDateAjout())
                .prixUnitaire(produitRequest.getPrixUnitaire())
                .seuilCritique(produitRequest.getSeuilCritique())
                .build();

        produit = produitRepository.save(produit);
        log.info("Produit {} has been saved.", produit.getIdProduit());

        // Call StockService to add the product to the stock
        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://localhost:8081/api/stock/{stockId}/add-product/{productId}",
                            produit.getIdStock(), produit.getIdProduit())  // Pass both stockId and productId here
                    .retrieve()
                    .bodyToMono(Void.class)  // No response expected
                    .block();  // Synchronous call
            log.info("Produit {} added to Stock {}", produit.getIdProduit(), produit.getIdStock());
        } catch (Exception e) {
            log.error("Failed to add produit {} to StockService: {}", produit.getIdProduit(), e.getMessage());
        }

        return mapToProduitResponse(produit);
    }



    public ProduitResponse decreaseQuantiteToProduit(Long id, int quantiteToRemove) {
        // Find the product
        Produit produit = produitRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException(String.format("Cannot find produit with id %s", id)));

        // Get the stock ID associated with the product
        String stockId = produit.getIdStock();
        if (stockId == null) {
            throw new RuntimeException("Stock ID is missing for this product");
        }

        // Call Stock Service to decrease quantity
        try {
            webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8081/api/stock/{stockId}/decrease-quantity/{productId}/{quantity}",
                            stockId, id, quantiteToRemove)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            log.info("Decreased {} from stock {}, updated product {}", quantiteToRemove, stockId, id);
        } catch (Exception e) {
            log.error("Failed to update stock for produit {}: {}", id, e.getMessage());
        }

        return mapToProduitResponse(produit);
    }


    public ProduitResponse addQuantiteToProduit(Long id, int quantiteToAdd) {
        // Find the product in the product database
        Produit produit = produitRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException(String.format("Cannot find produit with id %s", id)));

        // Get the stock ID associated with the product
        String stockId = produit.getIdStock();
        if (stockId == null) {
            throw new RuntimeException("Stock ID is missing for this product");
        }

        // Call Stock Service to update the quantity for this product inside the stock
        try {
            webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8081/api/stock/{stockId}/add-quantity/{productId}/{quantity}",
                            stockId, id, quantiteToAdd)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block(); // Synchronous call to ensure stock is updated before proceeding

            log.info("Successfully added {} to stock {} for product {}", quantiteToAdd, stockId, id);
        } catch (Exception e) {
            log.error("Failed to update stock for produit {}: {}", id, e.getMessage());
            throw new RuntimeException("Stock update failed", e);
        }

        return mapToProduitResponse(produit);
    }





    public ProduitResponse updateProduit(Long id, ProduitRequest produitRequest) {
        Produit existingProduit = produitRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException(String.format("Cannot find produit with id %s", id)));

        // Update only the other product details, not the quantity
        existingProduit.setNom(produitRequest.getNom());
        existingProduit.setDateAjout(produitRequest.getDateAjout());
        existingProduit.setPrixUnitaire(produitRequest.getPrixUnitaire());
        existingProduit.setSeuilCritique(produitRequest.getSeuilCritique());
        Produit updatedProduit = produitRepository.save(existingProduit);

        // Return the updated product response
        return mapToProduitResponse(updatedProduit);
    }




    public List<ProduitResponse> getAllProduits() {
        List<Produit> produits = produitRepository.findAll();
        return produits.stream().map(this::mapToProduitResponse).toList();
    }

    public ProduitResponse getProduitByName(String nom) {
        Produit produit = produitRepository.findByNom(nom)
                .orElseThrow(() -> new RuntimeException(String.format("Cannot find produit with name %s", nom)));
        return mapToProduitResponse(produit);
    }

    public ProduitResponse getProduitById(String idProduit) {
        Optional<Produit> produit = produitRepository.findByIdProduit(idProduit);

        if (produit.isPresent()) {
            Produit p = produit.get();
            return ProduitResponse.builder()
                    .idProduit(p.getIdProduit())
                    .nom(p.getNom())
                    .prixUnitaire(p.getPrixUnitaire())
                    .seuilCritique(p.getSeuilCritique())
                    .dateAjout(p.getDateAjout())
                    .idStock(p.getIdStock())
                    .build();
        } else {
            throw new ProductNotFoundException("Produit not found with id: " + idProduit);
        }
    }

    public void deleteProduit(String id) {
        // Find the product in the database
        Produit produit = produitRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException(String.format("Cannot find produit with id %s", id)));

        String stockId = produit.getIdStock(); // Stock ID associated with the product

        // Call StockService to remove the product entry from stock
        try {
            webClientBuilder.build()
                    .delete()
                    .uri("http://localhost:8081/api/stock/{stockId}/remove-product/{productId}", stockId, id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Produit {} removed from Stock {}", id, stockId);
        } catch (Exception e) {
            log.error("Failed to remove produit {} from stock {}: {}", id, stockId, e.getMessage());
            throw new RuntimeException("Failed to remove produit from stock.");
        }

        // Now delete the product from the product database
        produitRepository.deleteById(id);
        log.info("Produit with id {} has been deleted from the database.", id);
    }



    private ProduitResponse mapToProduitResponse(Produit produit) {
        return ProduitResponse.builder()
                .idProduit(produit.getIdProduit())
                .nom(produit.getNom())
                .dateAjout(produit.getDateAjout())
                .prixUnitaire(produit.getPrixUnitaire())
                .seuilCritique(produit.getSeuilCritique())
                .idStock(produit.getIdStock())
                .build();
    }
}
