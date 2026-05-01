package edu.se.extweb.service;

import edu.se.extweb.model.Item;
import edu.se.extweb.request.ItemCreateRequest;
import edu.se.extweb.request.ItemUpdateRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.BaseMetaData;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ItemService {

    // імітація БД
    private final Map<String, Item> storage = new ConcurrentHashMap<>();

    // ============================================================
    // ======================= BASIC CRUD =========================
    // ============================================================

    public List<Item> getAll() {
        return new ArrayList<>(storage.values());
    }

    public Item getById(String id) {
        return storage.get(id);
    }

    public Item create(Item item) {
        String id = UUID.randomUUID().toString();
        item.setId(id);
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
        if (!storage.containsKey(item.getId())) {
            return null;
        }
        storage.put(item.getId(), item);
        return item;
    }

    public Item update(ItemUpdateRequest request) {
        if (!storage.containsKey(request.id())) {
            return null;
        }

        Item item = storage.get(request.id());
        item.setName(request.name());
        item.setCode(request.code());
        item.setDescription(request.description());

        return item;
    }

    public void delById(String id) {
        storage.remove(id);
    }

    // ============================================================
    // =================== API RESPONSE LOGIC ======================
    // ============================================================

    public ApiResponse<BaseMetaData, Item> getByIdAsApiResponse(String id) {

        Item item = storage.get(id);

        if (item == null) {
            return buildErrorResponse("Not found", 404);
        }

        return buildSuccessResponse(List.of(item));
    }

    public ApiResponse<BaseMetaData, Item> getAllAsApiResponse() {

        List<Item> items = getAll();

        if (items.isEmpty()) {
            return buildErrorResponse("List is empty", 404);
        }

        return buildSuccessResponse(items);
    }

    public ApiResponse<BaseMetaData, Item> createAsApiResponse(ItemCreateRequest request) {

        Item created = create(request);

        return buildSuccessResponse(List.of(created));
    }

    public ApiResponse<BaseMetaData, Item> updateAsApiResponse(ItemUpdateRequest request) {

        Item updated = update(request);

        if (updated == null) {
            return buildErrorResponse("Item not found for update", 404);
        }

        return buildSuccessResponse(List.of(updated));
    }

    // ============================================================
    // =================== HELPERS (TDD FRIENDLY) ==================
    // ============================================================

    private ApiResponse<BaseMetaData, Item> buildSuccessResponse(List<Item> data) {

        BaseMetaData meta = new BaseMetaData();
        meta.setSuccess(true);
        meta.setCode(200);
        meta.setErrorMessage(null);

        return new ApiResponse<>(meta, data);
    }

    private ApiResponse<BaseMetaData, Item> buildErrorResponse(String message, int code) {

        BaseMetaData meta = new BaseMetaData();
        meta.setSuccess(false);
        meta.setCode(code);
        meta.setErrorMessage(message);

        return new ApiResponse<>(meta, new ArrayList<>());
    }
}