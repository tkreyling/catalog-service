package catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(Application.class)
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void getProducts() throws Exception {
        mvc.perform(get("/products"))
                .andExpect(status().is2xxSuccessful());
    }
}