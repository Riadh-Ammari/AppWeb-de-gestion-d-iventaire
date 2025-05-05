package primitives;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.Map;
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderEvent {
    private Commande ordre;
    private String message;



    // Add no-arg constructor for deserialization (needed by Jackson)

}
