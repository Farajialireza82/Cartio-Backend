package com.cromulent.cartio.controller;


import com.cromulent.cartio.model.MinimalShopItem;
import com.cromulent.cartio.model.ShopItem;
import com.cromulent.cartio.repository.ShopItemRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shopItems")
public class ShopItemController {

    ShopItemRepository shopItemRepository;

    public ShopItemController(ShopItemRepository shopItemRepository) {
        this.shopItemRepository = shopItemRepository;
    }

    @PostMapping
    private ResponseEntity<Void> createShopItem(
            @RequestBody MinimalShopItem newShopItem,
            UriComponentsBuilder ucb
    ) {
           ShopItem shopItem = new ShopItem(null, newShopItem.name(), newShopItem.amount(), false);
        ShopItem savedShopItem = shopItemRepository.save(shopItem);
        URI locationOfNewShopItem = ucb
                .path("/api/shopItems/{id}")
                .buildAndExpand(savedShopItem.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewShopItem).build();
    }

    @GetMapping
    private ResponseEntity<List<ShopItem>> getAllShopItems() {
        List<ShopItem> items = new ArrayList<>();

        for (ShopItem shopItem : shopItemRepository.findAll()) {
            items.add(shopItem);
        }
        return ResponseEntity.ok(items);
    }

    @PutMapping
    private ResponseEntity<ShopItem> editShopItem(@RequestBody ShopItem newShopItem) {
        if (shopItemRepository.existsById(newShopItem.getId())) {
            shopItemRepository.save(newShopItem);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{requestedId}")
    private ResponseEntity<ShopItem> deleteShopItem(@PathVariable("requestedId") Long id) {
        if (shopItemRepository.existsById(id)) {
            shopItemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteShopItems(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            shopItemRepository.deleteAllById(ids);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/markBought")
    public ResponseEntity<Void> markShopItemsAsBought(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<ShopItem> shopItems = new ArrayList<>();
        for (ShopItem shopItem : shopItemRepository.findAllById(ids)) {
            shopItems.add(shopItem);
        }

        if (shopItems.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        shopItems.forEach(shopItem -> shopItem.setBought(true));
        shopItemRepository.saveAll(shopItems);

        return ResponseEntity.ok().build();
    }


}
