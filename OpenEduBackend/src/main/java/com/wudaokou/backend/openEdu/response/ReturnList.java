package com.wudaokou.backend.openEdu.response;

import lombok.Data;

import java.util.List;

@Data
public class ReturnList<T> {
    List<T> data;
    String code;
    String msg;
}
