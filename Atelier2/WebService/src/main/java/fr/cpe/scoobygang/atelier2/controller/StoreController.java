package fr.cpe.scoobygang.atelier2.controller;

import fr.cpe.scoobygang.atelier2.initializer.StoreApplicationRunner;
import fr.cpe.scoobygang.atelier2.mapper.CardMapper;
import fr.cpe.scoobygang.atelier2.mapper.StoreMapper;
import fr.cpe.scoobygang.atelier2.model.Transaction;
import fr.cpe.scoobygang.atelier2.request.CardResponse;
import fr.cpe.scoobygang.atelier2.request.StoreOrderRequest;
import fr.cpe.scoobygang.atelier2.request.StoreResponse;
import fr.cpe.scoobygang.atelier2.service.CardService;
import fr.cpe.scoobygang.atelier2.service.StoreService;
import fr.cpe.scoobygang.atelier2.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class StoreController {
    private final CardService cardService;
    private final StoreService storeService;


    private final StoreApplicationRunner storeApplicationRunner;

    public StoreController(CardService cardService, StoreService storeService, StoreApplicationRunner storeApplicationRunner, TransactionService transactionService) {
        this.cardService = cardService;
        this.storeService = storeService;
        this.storeApplicationRunner = storeApplicationRunner;
    }

    @GetMapping(value = {"/stores"})
    public ResponseEntity<List<StoreResponse>> getStores(@RequestHeader(value = "Authorization") String authorization) {
        return ResponseEntity.ok(StoreMapper.INSTANCE.storesToStoreResponses(storeService.getStores()));
    }

    @PostMapping(value = {"/store/buy"})
    public ResponseEntity buyCard(@RequestHeader(value = "Authorization") String authorization, @RequestBody StoreOrderRequest storeOrderRequest) {
        if (storeService.buyCard(storeOrderRequest.getCardId(), storeOrderRequest.getUserId(), storeOrderRequest.getStoreId())) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping(value = {"/store/{id}/cards"})
    public ResponseEntity<List<CardResponse>> sellCard(@RequestHeader(value = "Authorization") String authorization, @PathVariable int id) {
        return ResponseEntity.ok(CardMapper.INSTANCE.cardsToCardResponses(storeService.getCardsById(id)));
    }

    @PostMapping(value = {"/store/sell"})
    public ResponseEntity sellCard(@RequestHeader(value = "Authorization") String authorization, @RequestBody StoreOrderRequest storeOrderRequest) {
        if (storeService.sellUserCard(storeOrderRequest.getCardId(), storeOrderRequest.getStoreId())) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping(value = {"/store/sell/cancel"})
    public ResponseEntity cancelSellCard(@RequestBody StoreOrderRequest storeOrderRequest){
        if (storeService.cancelSellCard(storeOrderRequest.getCardId(), storeOrderRequest.getStoreId(), storeOrderRequest.getUserId())) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
