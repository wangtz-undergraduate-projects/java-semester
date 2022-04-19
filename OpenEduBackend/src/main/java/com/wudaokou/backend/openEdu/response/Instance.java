package com.wudaokou.backend.openEdu.response;

import lombok.Data;

@Data
public class Instance {
    String label;
    String category;
    String uri;
    boolean hasStar;
    int id;
}
