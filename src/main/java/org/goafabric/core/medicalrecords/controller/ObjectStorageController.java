package org.goafabric.core.medicalrecords.controller;

import org.goafabric.core.medicalrecords.controller.dto.ObjectEntry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping(value = "/objects", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class ObjectStorageController {
    private final ObjectStorageLogicNative logic;

    private final List<ObjectEntry> objectEntriesInMem = new ArrayList<>();

    public ObjectStorageController(ObjectStorageLogicNative logic) {
        this.logic = logic;
    }

    @GetMapping("getById/{name}")
    public ObjectEntry getByName(@PathVariable("name") String name) {
        return logic.getById(name);
    }

    @GetMapping("search")
    public List<ObjectEntry> search(@RequestParam("search") String search) {
        return logic.search(search);
    }

    @PostMapping(value = "save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestBody ObjectEntry objectEntry) {
        logic.save(objectEntry);
    }
}
