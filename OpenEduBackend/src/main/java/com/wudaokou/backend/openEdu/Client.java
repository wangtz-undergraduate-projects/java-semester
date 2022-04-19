package com.wudaokou.backend.openEdu;

import com.wudaokou.backend.openEdu.response.Instance;
import com.wudaokou.backend.openEdu.response.ReturnList;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Component
public class Client {
    WebClient webClient = WebClient.create("http://open.edukg.cn/opedukg/api/typeOpen/open");
    public Mono<ReturnList<Instance>> instanceList(String course, String searchKey, String id){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/instanceList")
                        .queryParam("course", course)
                        .queryParam("searchKey", searchKey)
                        .queryParam("id", id)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
