package edu.se.extweb;

import edu.se.extweb.model.Item;
import edu.se.extweb.request.ItemCreateRequest;
import edu.se.extweb.request.ItemUpdateRequest;
import edu.se.extweb.response.ApiResponse;
import edu.se.extweb.response.BaseMetaData;
import edu.se.extweb.service.ItemService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceApiResponseTest {

    @Autowired
    private ItemService underTest;

    private Item mockItem;

    // -------------------- SETUP --------------------

    @BeforeEach
    void setUp() {
        // створюємо mock item (для стабільності тестів)
        mockItem = new Item();
        mockItem.setName("Test Item");
        mockItem.setCode("T-001");
        mockItem.setDescription("Test description");

        underTest.create(mockItem);
    }

    // ============================================================
    // ===================== GET BY ID =============================
    // ============================================================

    @Test
    void whenItemExists_thenReturnValidApiResponse() {
        // given
        String id = mockItem.getId();

        // when
        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);

        // then
        // перевіряємо, що response створений
        assertNotNull(response);

        // перевірка, що дані не пусті
        assertFalse(response.getData().isEmpty());

        // перевірка success
        assertTrue(response.getMeta().isSuccess());

        // код 200
        assertEquals(200, response.getMeta().getCode());
    }

    @Test
    void whenItemExists_thenDataMatchesOriginalItem() {
        String id = mockItem.getId();

        Item item = underTest.getById(id);
        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);

        // перевіряємо, що item у response == item з сервісу
        assertEquals(item, response.getData().get(0));
    }

    @Test
    void whenItemExists_thenErrorMessageIsNull() {
        String id = mockItem.getId();

        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);

        // при успіху errorMessage має бути null
        assertNull(response.getMeta().getErrorMessage());
    }

    @Test
    void whenItemNotFound_thenReturnEmptyDataList() {
        String id = "wrong-id";

        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);

        // якщо item нема — список порожній
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void whenItemNotFound_thenSuccessFalse() {
        String id = "wrong-id";

        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);

        // success має бути false
        assertFalse(response.getMeta().isSuccess());
    }

    @Test
    void whenItemNotFound_thenReturn404Code() {
        String id = "wrong-id";

        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);

        assertEquals(404, response.getMeta().getCode());
    }

    @Test
    void whenItemNotFound_thenErrorMessageNotNull() {
        String id = "wrong-id";

        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);

        assertNotNull(response.getMeta().getErrorMessage());
    }

    @Test
    void whenItemNotFound_thenErrorMessageEqualsNotFound() {
        String id = "wrong-id";

        ApiResponse<BaseMetaData, Item> response = underTest.getByIdAsApiResponse(id);

        assertEquals("Not found", response.getMeta().getErrorMessage());
    }

    // ============================================================
    // ===================== GET ALL ===============================
    // ============================================================

    @Test
    void whenGetAll_thenReturnList() {
        List<Item> items = underTest.getAll();

        // список не null
        assertNotNull(items);
    }

    @Test
    void whenGetAll_thenReturnApiResponseNotNull() {
        ApiResponse<BaseMetaData, Item> response = underTest.getAllAsApiResponse();

        assertNotNull(response);
    }

    @Test
    void whenGetAllNotEmpty_thenSuccessTrue() {
        ApiResponse<BaseMetaData, Item> response = underTest.getAllAsApiResponse();

        assertTrue(response.getMeta().isSuccess());
    }

    @Test
    void whenGetAllEmpty_thenReturnError() {
        // тут залежить від реалізації (можна очистити БД)

        ApiResponse<BaseMetaData, Item> response = underTest.getAllAsApiResponse();

        if (response.getData().isEmpty()) {
            assertFalse(response.getMeta().isSuccess());
        }
    }

    // ============================================================
    // ===================== CREATE ================================
    // ============================================================

    @Test
    void whenCreateItem_thenReturnSavedItem() {
        ItemCreateRequest request =
                new ItemCreateRequest("New", "C-001", "desc");

        Item created = underTest.create(request);

        assertNotNull(created.getId());
    }

    @Test
    void whenCreateItem_thenApiResponseSuccess() {
        ItemCreateRequest request =
                new ItemCreateRequest("New", "C-002", "desc");

        ApiResponse<BaseMetaData, Item> response =
                underTest.createAsApiResponse(request);

        assertTrue(response.getMeta().isSuccess());
    }

    // ============================================================
    // ===================== UPDATE ================================
    // ============================================================

    @Test
    void whenUpdateExistingItem_thenSuccess() {
        ItemUpdateRequest request =
                new ItemUpdateRequest(
                        mockItem.getId(),
                        "Updated",
                        "U-001",
                        "updated desc"
                );

        ApiResponse<BaseMetaData, Item> response =
                underTest.updateAsApiResponse(request);

        assertTrue(response.getMeta().isSuccess());
    }

    @Test
    void whenUpdateNonExistingItem_thenReturnError() {
        ItemUpdateRequest request =
                new ItemUpdateRequest(
                        "wrong-id",
                        "Updated",
                        "U-002",
                        "desc"
                );

        ApiResponse<BaseMetaData, Item> response =
                underTest.updateAsApiResponse(request);

        assertFalse(response.getMeta().isSuccess());
    }

}
