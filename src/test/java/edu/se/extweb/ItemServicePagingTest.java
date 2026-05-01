package edu.se.extweb;

import edu.se.extweb.model.Item;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.BaseMetaData;
import edu.se.extweb.service.ItemService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceAdvancedTest {

    @Autowired
    private ItemService underTest;

    private Item createMockItem() {
        Item item = new Item();
        item.setName("Test");
        item.setCode("CODE");
        item.setDescription("DESC");
        return item;
    }

    private String existingId;

    @BeforeEach
    void setUp() {
        underTest.clearAll();

        Item created = underTest.create(createMockItem());
        existingId = created.getId();
    }

    // ---------- GET ALL ----------

    @Test
    void whenGetAll_thenNotEmpty() {
        assertFalse(underTest.getAll().isEmpty());
    }

    @Test
    void whenGetAllApiResponse_thenSuccess() {
        assertTrue(underTest.getAllAsApiResponse().getMeta().isSuccess());
    }

    // ---------- GET BY ID ----------

    @Test
    void whenItemExists_thenSuccess() {
        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(existingId);
        assertTrue(response.getMeta().isSuccess());
    }

    @Test
    void whenItemExists_thenDataNotEmpty() {
        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(existingId);
        assertFalse(response.getData().isEmpty());
    }

    @Test
    void whenItemNotExists_then404() {
        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse("wrong");
        assertEquals(404, response.getMeta().getCode());
    }

    // ---------- CREATE ----------

    @Test
    void whenCreate_thenStored() {
        Item created = underTest.create(createMockItem());
        assertNotNull(created.getId());
    }

    // ---------- UPDATE ----------

    @Test
    void whenUpdate_thenSuccess() {
        Item item = underTest.getById(existingId);
        item.setName("UPDATED");

        Item updated = underTest.update(item);

        assertEquals("UPDATED", updated.getName());
    }

    @Test
    void whenUpdateNotExists_thenNull() {
        Item item = createMockItem();
        item.setId("wrong");

        assertNull(underTest.update(item));
    }

    // ---------- DELETE ----------

    @Test
    void whenDelete_thenRemoved() {
        underTest.delById(existingId);
        assertNull(underTest.getById(existingId));
    }

    @Test
    void whenDeleteApi_thenSuccess() {
        ApiResponse<BaseMetaData, Item> response = underTest.deleteAsApiResponse(existingId);
        assertTrue(response.getMeta().isSuccess());
    }

    @Test
    void whenDeleteNotExists_then404() {
        ApiResponse<BaseMetaData, Item> response = underTest.deleteAsApiResponse("wrong");
        assertEquals(404, response.getMeta().getCode());
    }
}