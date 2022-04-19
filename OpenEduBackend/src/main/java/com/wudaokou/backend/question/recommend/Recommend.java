package com.wudaokou.backend.question.recommend;

import com.wudaokou.backend.history.Course;
import com.wudaokou.backend.history.HistoryRepository;
import com.wudaokou.backend.login.Customer;
import com.wudaokou.backend.question.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Recommend {
    WebClient webClient = WebClient.create("http://open.edukg.cn/opedukg/api/typeOpen/open/questionListByUriName");

    String openEduId;

    public Recommend(String openEduId) {
        this.openEduId = openEduId;
    }

    public List<Question> recommend(Customer customer,
                                    Course course,
                                    final int totalCount,
                                    UserQuestionRepository userQuestionRepository,
                                    HistoryRepository historyRepository){
        List<Question> questions = new LinkedList<>();
        List<String> labels = new LinkedList<>();
        // 1个错题，1个易错知识点，2个高频访问知识点，1个随机知识点
        final int wrongQuestionCount = (int) (totalCount * 0.2);  // 错题
        final int wrongEntityCount = (int) (totalCount * 0.2);  // 易错知识点
        final int frequentEntityCount = (int) (totalCount * 0.4);  // 高频访问知识点

        // 错题
        List<UserQuestion> userQuestions = userQuestionRepository
                .findByUserQuestionId_CustomerAndUserQuestionId_Question_Course(customer, course);
        userQuestions.sort(Comparator.comparingDouble(UserQuestion::recommendationValue).reversed()); //倒序

        for(int i = 0; i < wrongQuestionCount && i < userQuestions.size(); i++)
            addIfValid(questions, userQuestions.get(i).getUserQuestionId().getQuestion());

        // 易错知识点
        for(int i = 0; i < wrongEntityCount && i < userQuestions.size(); i++)
            labels.add(userQuestions.get(i).getUserQuestionId().getQuestion().getLabel());

        // 高频知识点
        List<String> topFrequentNamesOfEntity = historyRepository
                .findTopFrequentNameOfEntity(customer, course,
                        PageRequest.of(0, wrongQuestionCount + wrongEntityCount + frequentEntityCount));
        while( !topFrequentNamesOfEntity.isEmpty() &&
                questions.size() + labels.size() < wrongQuestionCount + wrongEntityCount + frequentEntityCount)
            labels.add(topFrequentNamesOfEntity.remove(0));

        // 随机知识点
        final String[] randomNames = SubjectKeywords.getMap().get(course);
        Random rand = new Random();
        while(labels.size() < totalCount - questions.size()){
            String label = randomNames[rand.nextInt(randomNames.length)];
            if(!labels.contains(label))
                labels.add(label);
        }

//        Logger logger = LoggerFactory.getLogger(QuestionController.class);
//        logger.info("labels: "+ labels);

        // 请求
        List<QuestionResponse> responses = fetchQuestions(labels, course).collectList().block();

        assert responses != null;

//        for(QuestionResponse o : responses)
//            logger.info("response: "+o.getData().toString());

        for (QuestionResponse res : responses) {
            List<Question> qs = res.getData();
            if (qs == null)
                throw new WebClientResponseException("OpenEdu Not Logged In", 500, "Server Error", null, null, null);
            if (qs.isEmpty()) continue;
            Question q;
            do {
                q = qs.get(rand.nextInt(qs.size()));
            } while (containsQuestion(questions, q));
            addIfValid(questions, q);
//            logger.info("added " + q.toString());
        }

        for(int j = responses.size() - 1; j >= 0 && questions.size() < totalCount; j--){
            List<Question> qsBackward = new ArrayList<>(responses.get(j).getData());
            Collections.shuffle(qsBackward);
            for(Question qBackward : qsBackward){
                if(questions.size() == totalCount) break;
                if(containsQuestion(questions, qBackward)) continue;
                addIfValid(questions, qBackward);
//                logger.info("last label added " + qBackward.toString());
            }
        }

        Collections.shuffle(questions);
        return questions;
    }

    private boolean containsQuestion(List<Question> questions, Question question){
        for(Question q : questions)
            if(Objects.equals(q.getId(), question.getId())) return true;
        return false;
    }

    private void addIfValid(List<Question> questions, Question question){
        String answer = question.getQAnswer();
        String body = question.getQBody();
        if(answer.isEmpty() || body.isEmpty()) return;
        if(answer.startsWith("答案"))
            answer = answer.replaceFirst("答案", "");
        if(!List.of("A", "B", "C", "D").contains(answer)) return;
        question.setQAnswer(answer);
        questions.add(question);
    }

    public Mono<QuestionResponse> getQuestion(Pair<Integer, String> indexedName, Course course){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("id", openEduId)
                        .queryParam("uriName", indexedName.getSecond())
                        .build())
                .retrieve()
                .bodyToMono(QuestionResponse.class)
                .map(qr -> {
                    qr.setData(
                            qr.getData().stream().peek(
                                    q -> {
                                        q.setLabel(indexedName.getSecond());
                                        q.setCourse(course);
                                    }
                            ).collect(Collectors.toList())
                    );
                    qr.setIndex(indexedName.getFirst());
                    return qr;
                });
    }

    public Flux<QuestionResponse> fetchQuestions(List<String> names, Course course){
        List<Pair<Integer, String>> indexedNames = IntStream.range(0, names.size())
                .mapToObj(i -> Pair.of(i, names.get(i)))
                .collect(Collectors.toList());

        return Flux.fromIterable(indexedNames)
                .parallel()
                .runOn(Schedulers.boundedElastic())
                .flatMap(name -> getQuestion(name, course))
                .ordered(Comparator.comparingInt(QuestionResponse::getIndex));
    }
}
