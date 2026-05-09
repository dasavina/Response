package edu.se.extweb;

import edu.se.extweb.model.Item;
import edu.se.extweb.request.ItemCreateRequest;
import edu.se.extweb.request.ItemUpdateRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.BaseMetaData;
import edu.se.extweb.service.ItemService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemServiceLoggingTest {

    @Autowired
    private ItemService underTest;

    private String createdId;

    @BeforeEach
    void setup() {
        underTest.clearAll();

        Item item = new Item();
        item.setName("Iggy");
        item.setCode("001");
        item.setDescription("test");

        Item saved = underTest.create(item);
        createdId = saved.getId();
    }

    // ---------- BEFORE (5) ----------

    @Test
    void testBeforeGetById(CapturedOutput output) {
        underTest.getById(createdId);

        assertTrue(output.toString().contains("Entering method"));
        assertTrue(output.toString().contains("ItemService.getById"));
    }

    @Test
    void testBeforeGetByIdContainsId(CapturedOutput output) {
        underTest.getById(createdId);

        assertTrue(output.toString().contains(createdId));
    }

    @Test
    void testBeforeGetAll(CapturedOutput output) {
        underTest.getAll();

        assertTrue(output.toString().contains("ItemService.getAll"));
    }

    @Test
    void testBeforeCreate(CapturedOutput output) {
        Item item = new Item();
        item.setName("New");

        underTest.create(item);

        assertTrue(output.toString().contains("ItemService.create"));
    }

    @Test
    void testBeforeDelete(CapturedOutput output) {
        underTest.delById(createdId);

        assertTrue(output.toString().contains("ItemService.delById"));
    }

    // ---------- AFTER (5) ----------

    @Test
    void testAfterGetById(CapturedOutput output) {
        Item item = underTest.getById(createdId);

        assertNotNull(item);
        assertTrue(output.toString().contains("completed successfully"));
    }

    @Test
    void testAfterGetByIdContainsName(CapturedOutput output) {
        underTest.getById(createdId);

        assertTrue(output.toString().contains("Iggy"));
    }

    @Test
    void testAfterGetAll(CapturedOutput output) {
        List<Item> list = underTest.getAll();

        assertFalse(list.isEmpty());
        assertTrue(output.toString().contains("completed successfully"));
    }

    @Test
    void testAfterCreate(CapturedOutput output) {
        Item item = new Item();
        item.setName("Test");

        Item created = underTest.create(item);

        assertNotNull(created);
        assertTrue(output.toString().contains("completed successfully"));
    }

    @Test
    void testAfterDelete(CapturedOutput output) {
        underTest.delById(createdId);

        assertTrue(output.toString().contains("completed successfully"));
    }

    // ---------- API RESPONSE (5) ----------

    @Test
    void testApiGetByIdSuccess(CapturedOutput output) {
        ApiResponse<BaseMetaData, Item> response =
                underTest.getByIdAsApiResponse(createdId);

        assertTrue(response.getMeta().isSuccess());
        assertTrue(output.toString().contains("getByIdAsApiResponse"));
    }

    @Test
    void testApiGetByIdError(CapturedOutput output) {
        ApiResponse<BaseMetaData, Item> response =
                underTest.getByIdAsApiResponse("wrong");

        assertFalse(response.getMeta().isSuccess());
        assertTrue(output.toString().contains("completed successfully"));
    }

    @Test
    void testApiCreate(CapturedOutput output) {
        ItemCreateRequest req =
                new ItemCreateRequest("Name","Code","Desc");

        ApiResponse<BaseMetaData, Item> response =
                underTest.createAsApiResponse(req);

        assertTrue(response.getMeta().isSuccess());
        assertTrue(output.toString().contains("createAsApiResponse"));
    }

    @Test
    void testApiUpdate(CapturedOutput output) {
        ItemUpdateRequest req =
                new ItemUpdateRequest(createdId,"New","002","upd");

        ApiResponse<BaseMetaData, Item> response =
                underTest.updateAsApiResponse(req);

        assertTrue(response.getMeta().isSuccess());
        assertTrue(output.toString().contains("updateAsApiResponse"));
    }

    @Test
    void testApiDelete(CapturedOutput output) {
        ApiResponse<BaseMetaData, Item> response =
                underTest.deleteAsApiResponse(createdId);

        assertTrue(response.getMeta().isSuccess());
        assertTrue(output.toString().contains("deleteAsApiResponse"));
    }
}
