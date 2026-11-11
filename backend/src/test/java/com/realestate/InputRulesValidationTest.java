package com.realestate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.realestate.common.InputRules;
import com.realestate.common.ModuleException;
import static org.junit.jupiter.api.Assertions.*;

class InputRulesValidationTest {
    // Same vectors as the frontend; this test never opens a database.
    @SuppressWarnings("unchecked")
    @Test void frontendAndBackendAgreeOnValidation() throws Exception {
        tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
        Map<String, Map<String, List<String>>> cases = mapper.readValue(
            Files.readString(Path.of("../frontend/src/shared/validation-cases.json")), Map.class);
        for (String field : cases.keySet()) {
            for (String value : cases.get(field).get("valid")) assertEquals(value, validate(field, value), field);
            for (String value : cases.get(field).get("invalid")) {
                assertThrows(ModuleException.class, new org.junit.jupiter.api.function.Executable() {
                    public void execute() { validate(field, value); }
                }, field);
            }
        }
    }
    private String validate(String field, String value) {
        if (field.equals("phone")) return InputRules.phone(value);
        if (field.equals("email")) return InputRules.email(value);
        return InputRules.password(value);
    }
}
