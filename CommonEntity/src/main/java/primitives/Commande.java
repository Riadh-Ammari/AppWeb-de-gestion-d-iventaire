package primitives;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document(collection = "Commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {
    @Id
    private String idCommande;
    private Date dateCommande;
    private Double montantTotal;
    private String idClient;
    private String etat; // (en cours, livrée, annulée)
    private Map<String,Integer> produits;//ids des produits
    private String idFournisseur;
}
