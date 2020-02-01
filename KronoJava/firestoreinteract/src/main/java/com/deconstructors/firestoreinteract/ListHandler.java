package com.deconstructors.firestoreinteract;

import java.util.List;
import java.util.Map;

public interface ListHandler {
    void handle(List<Map<String,Object>> list);
}
