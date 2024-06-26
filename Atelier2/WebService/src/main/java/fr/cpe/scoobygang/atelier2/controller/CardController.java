package fr.cpe.scoobygang.atelier2.controller;

import fr.cpe.scoobygang.atelier2.dao.mapper.CardMapper;
import fr.cpe.scoobygang.atelier2.dao.request.CardRequest;
import fr.cpe.scoobygang.atelier2.dao.response.CardResponse;
import fr.cpe.scoobygang.atelier2.model.Card;
import fr.cpe.scoobygang.atelier2.security.JWT;
import fr.cpe.scoobygang.atelier2.security.JWTService;
import fr.cpe.scoobygang.atelier2.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class CardController {
    private final JWTService jwtService;
    private final CardService cardService;

    public CardController(JWTService jwtService, CardService cardService) {
        this.jwtService = jwtService;
        this.cardService = cardService;
    }

    @DeleteMapping(value = {"/card/{id}"})
    public ResponseEntity<Void> deleteCard(@RequestHeader(value = "Authorization") String authorization, @PathVariable("id") int id) {
        Optional<Card> card = cardService.deleteCard(id);
        if (card.isPresent()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @PostMapping(value = {"/card"})
    public ResponseEntity<Card> createCard(@RequestHeader(value = "Authorization") String authorization, @RequestBody CardRequest cardRequest) {
        Card createdCard = cardService.saveCard(cardRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCard.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdCard);
    }

    @PutMapping(value = {"/card/{id}"})
    public ResponseEntity<Card> updateCard(@RequestHeader(value = "Authorization") String authorization, @PathVariable("id") int id, @RequestBody CardRequest cardRequest) {
        Optional<Card> updatedCard = cardService.updateCard(id, cardRequest);
        return updatedCard.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = {"/card/{id}"})
    public ResponseEntity<Card> getCard(@RequestHeader(value = "Authorization") String authorization, @PathVariable("id") int id) {
        return cardService.getCard(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = {"/cards"})
    public ResponseEntity<List<CardResponse>> getCards(@RequestHeader(value = "Authorization") String authorization) {
        return ResponseEntity.ok(CardMapper.INSTANCE.cardsToCardResponses(cardService.getCards()));
    }

    @GetMapping(value = {"/cards/user"})
    public ResponseEntity<List<CardResponse>> getCurrentUserCards(@RequestHeader(value = "Authorization") String authorization) {
        Optional<JWT> jwt = jwtService.fromAuthorization(authorization);
        if (jwt.isPresent()) {
            int userID = jwt.get().getJwtInformation().getUserID();
            return ResponseEntity.ok(CardMapper.INSTANCE.cardsToCardResponses(cardService.getAllUserCard(userID)));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}
