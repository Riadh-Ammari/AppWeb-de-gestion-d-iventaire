package com.example.ProduitService.service;

import com.example.ProduitService.dto.ProduitRequest;
import com.example.ProduitService.dto.ProduitResponse;
import com.example.ProduitService.model.Produit;
import com.example.ProduitService.repository.ProduitRepository;
import com.example.ProduitService.service.execption.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProduitService {

    private final ProduitRepository produitRepository;

    public ProduitResponse createProduit(ProduitRequest produitRequest) {
        Produit produit = Produit.builder()
                .IdStock(produitRequest.getIdStock())
                .nom(produitRequest.getNom())
                .dateAjout(produitRequest.getDateAjout())
                .prixUnitaire(produitRequest.getPrixUnitaire())
                .seuilCritique(produitRequest.getSeuilCritique())
                .build();

        produit = produitRepository.save(produit);
        log.info("Produit {} has been saved.", produit.getIdProduit());
        return mapToProduitResponse(produit);
    }

    public ProduitResponse updateProduit(Long id, ProduitRequest produitRequest) {
        Produit existingProduit = produitRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new RuntimeException(String.format("Cannot find produit with id %s", id)));

        existingProduit.setNom(produitRequest.getNom());
        existingProduit.setDateAjout(produitRequest.getDateAjout());
        existingProduit.setPrixUnitaire(produitRequest.getPrixUnitaire());
        existingProduit.setSeuilCritique(produitRequest.getSeuilCritique());

        Produit updatedProduit = produitRepository.save(existingProduit);
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
                    .IdStock(p.getIdStock())
                    .build();
        } else {
            throw new ProductNotFoundException("Produit not found with id: " + idProduit);
        }
    }

    public void deleteProduit(String id) {
        produitRepository.deleteById(String.valueOf(id));
        log.info("Produit with id {} has been deleted.", id);
    }

    private ProduitResponse mapToProduitResponse(Produit produit) {
        return ProduitResponse.builder()
                .idProduit(produit.getIdProduit())
                .nom(produit.getNom())
                .dateAjout(produit.getDateAjout())
                .prixUnitaire(produit.getPrixUnitaire())
                .seuilCritique(produit.getSeuilCritique())
                .IdStock(produit.getIdStock())
                .build();
    }
}
