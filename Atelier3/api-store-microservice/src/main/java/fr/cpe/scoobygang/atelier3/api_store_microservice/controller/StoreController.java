package fr.cpe.scoobygang.atelier3.api_store_microservice.controller;

import fr.cpe.scoobygang.common.dto.mapper.CardMapper;
import fr.cpe.scoobygang.common.dto.mapper.StoreMapper;
import fr.cpe.scoobygang.common.dto.request.StoreOrderRequest;
import fr.cpe.scoobygang.atelier3.api_store_microservice.service.CardService;
import fr.cpe.scoobygang.atelier3.api_store_microservice.service.StoreService;
import fr.cpe.scoobygang.common.dto.response.CardResponse;
import fr.cpe.scoobygang.common.dto.response.StoreResponse;
import fr.cpe.scoobygang.common.jwt.JWT;
import fr.cpe.scoobygang.common.jwt.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class StoreController {
    private final CardService cardService;
    private final StoreService storeService;
    private final JWTService jwtService;

    public StoreController(JWTService jwtService, CardService cardService, StoreService storeService) {
        this.jwtService = jwtService;
        this.cardService = cardService;
        this.storeService = storeService;
    }

    @GetMapping(value = {"/stores"})
    public ResponseEntity<List<StoreResponse>> getStores() {
        return ResponseEntity.ok(StoreMapper.INSTANCE.storesToStoreResponses(storeService.getStores()));
    }

    @PostMapping(value = {"/store/buy"})
    public ResponseEntity<HttpStatus> buyCard(@RequestHeader(value = "Authorization") String authorization, @RequestBody StoreOrderRequest storeOrderRequest) {
        Optional<JWT> jwt = jwtService.fromAuthorization(authorization);

        if (jwt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int userID = jwt.get().getJwtInformation().getUserID();

        if (!storeService.buyCard(storeOrderRequest.getCardId(), userID, storeOrderRequest.getStoreId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(value = {"/store/{id}/cards"})
    public ResponseEntity<List<CardResponse>> sellCard(@PathVariable int id) {
        return ResponseEntity.ok(CardMapper.INSTANCE.cardsToCardResponses(storeService.getCardsById(id)));
    }

    @PostMapping(value = {"/store/sell"})
    public ResponseEntity<ResponseStatus> sellCard(@RequestHeader(value = "Authorization") String authorization, @RequestBody StoreOrderRequest storeOrderRequest) {
        Optional<JWT> jwt = jwtService.fromAuthorization(authorization);
        if (jwt.isPresent()) {
            int userID = jwt.get().getJwtInformation().getUserID();
            boolean test1 = cardService.getAllUserCard(userID).stream().anyMatch(c -> c.getId() == storeOrderRequest.getCardId());
            boolean test = storeService.sellUserCard(storeOrderRequest.getCardId(), storeOrderRequest.getStoreId());
            if (cardService.getAllUserCard(userID).stream().anyMatch(c -> c.getId() == storeOrderRequest.getCardId()) && storeService.sellUserCard(storeOrderRequest.getCardId(), storeOrderRequest.getStoreId())) {
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping(value = {"/store/sell/cancel"})
    public ResponseEntity<ResponseStatus> cancelSellCard(@RequestHeader(value = "Authorization") String authorization, @RequestBody StoreOrderRequest storeOrderRequest) {
        Optional<JWT> jwt = jwtService.fromAuthorization(authorization);
        if (jwt.isPresent()) {
            int userID = jwt.get().getJwtInformation().getUserID();
            if (cardService.getAllUserCard(userID).stream().anyMatch(c -> c.getId() == storeOrderRequest.getCardId()) && storeService.cancelSellCard(storeOrderRequest.getCardId(), storeOrderRequest.getStoreId())) {
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}