package edu.se.extweb.service;

import edu.se.extweb.model.Item;
import edu.se.extweb.request.ItemCreateRequest;
import edu.se.extweb.request.ItemUpdateRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.BaseMetaData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ItemService {

    private final Map<String, Item> storage = new ConcurrentHashMap<>();

    // ================= TEST HELPER =================
    public void clearAll() {
        storage.clear();
    }

    // ================= CRUD =================

    public List<Item> getAll() {
        return new ArrayList<>(storage.values());
    }

    public Item getById(String id) {
        return storage.get(id);
    }

    public Item create(Item item) {
        String id = UUID.randomUUID().toString();
        item.setId(id);
        item.setCreatedAt(LocalDateTime.now());
        storage.put(id, item);
        return item;
    }

    public Item create(ItemCreateRequest request) {
        Item item = new Item();
        item.setName(request.name());
        item.setCode(request.code());
        item.setDescription(request.description());
        return create(item);
    }

    public Item update(Item item) {
        if (item == null || item.getId() == null) return null;
        if (!storage.containsKey(item.getId())) return null;

        storage.put(item.getId(), item);
        return item;
    }

    public Item update(ItemUpdateRequest request) {
        if (request == null || request.id() == null) return null;
        if (!storage.containsKey(request.id())) return null;

        Item item = storage.get(request.id());
        item.setName(request.name());
        item.setCode(request.code());
        item.setDescription(request.description());

        return item;
    }

    public void delById(String id) {
        storage.remove(id);
    }

    // ================= API RESPONSE =================

    public ApiResponse<BaseMetaData, Item> getByIdAsApiResponse(String id) {

        if (id == null || id.isBlank()) {
            return buildError("Invalid id", 400);
        }

        Item item = storage.get(id);

        if (item == null) {
            return buildError("Not found", 404);
        }

        return buildSuccess(List.of(item));
    }

    public ApiResponse<BaseMetaData, Item> getAllAsApiResponse() {

        List<Item> items = getAll();

        if (items.isEmpty()) {
            return buildError("List is empty", 404);
        }

        return buildSuccess(items);
    }

    public ApiResponse<BaseMetaData, Item> createAsApiResponse(ItemCreateRequest request) {

        if (request == null) {
            return buildError("Request is null", 400);
        }

        Item created = create(request);
        return buildSuccess(List.of(created));
    }

    public ApiResponse<BaseMetaData, Item> updateAsApiResponse(ItemUpdateRequest request) {

        Item updated = update(request);

        if (updated == null) {
            return buildError("Item not found", 404);
        }

        return buildSuccess(List.of(updated));
    }

    public ApiResponse<BaseMetaData, Item> deleteAsApiResponse(String id) {

        if (id == null || id.isBlank()) {
            return buildError("Invalid id", 400);
        }

        Item item = storage.get(id);

        if (item == null) {
            return buildError("Item not found", 404);
        }

        storage.remove(id);

        return buildSuccess(List.of(item));
    }

    // ================= HELPERS =================

    private ApiResponse<BaseMetaData, Item> buildSuccess(List<Item> data) {
        BaseMetaData meta = new BaseMetaData();
        meta.setSuccess(true);
        meta.setCode(200);
        meta.setErrorMessage(null);
        return new ApiResponse<>(meta, data);
    }

    private ApiResponse<BaseMetaData, Item> buildError(String message, int code) {
        BaseMetaData meta = new BaseMetaData();
        meta.setSuccess(false);
        meta.setCode(code);
        meta.setErrorMessage(message);
        return new ApiResponse<>(meta, new ArrayList<>());
    }
}