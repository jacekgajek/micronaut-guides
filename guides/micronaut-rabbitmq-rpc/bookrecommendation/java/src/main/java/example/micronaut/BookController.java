package example.micronaut;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

@Controller("/books") // <1>
public class BookController {

    private final CatalogueClient catalogueClient; // <2>
    private final InventoryClient inventoryClient; // <2>

    public BookController(CatalogueClient catalogueClient, InventoryClient inventoryClient) { // <2>
        this.catalogueClient = catalogueClient;
        this.inventoryClient = inventoryClient;
    }

    @Get // <3>
    public Publisher<BookRecommendation> index() {
        return Flux.from(catalogueClient.findAll(null))
                .flatMap(Flux::fromIterable)
                .flatMap(book -> Flux.from(inventoryClient.stock(book.getIsbn()))
                        .filter(Boolean::booleanValue)
                        .map(response -> book))
                .map(book -> new BookRecommendation(book.getName()));
    }
}
