package sdu.diploma.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sdu.diploma.userservice.dto.PsychTestResponse;
import sdu.diploma.userservice.dto.TakeTestRequest;
import sdu.diploma.userservice.dto.TestStatsResponse;
import sdu.diploma.userservice.dto.UserTestResultResponse;
import sdu.diploma.userservice.entity.PsychologicalTest;
import sdu.diploma.userservice.entity.TestAnswerOption;
import sdu.diploma.userservice.entity.TestQuestion;
import sdu.diploma.userservice.entity.UserTestResult;
import sdu.diploma.userservice.exception.BusinessException;
import sdu.diploma.userservice.repository.PsychologicalTestRepository;
import sdu.diploma.userservice.repository.UserTestResultRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PsychologicalTestService {

    private final PsychologicalTestRepository testRepository;
    private final UserTestResultRepository resultRepository;

    @Transactional(readOnly = true)
    public List<PsychTestResponse> getAllActiveTests() {
        return testRepository.findAllByActiveTrue().stream()
                .map(this::toTestResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PsychTestResponse getTest(Long testId) {
        return testRepository.findById(testId)
                .map(this::toTestResponse)
                .orElseThrow(() -> new BusinessException("Test not found: " + testId));
    }

    public UserTestResultResponse takeTest(Long userId, TakeTestRequest request) {
        PsychologicalTest test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new BusinessException("Test not found"));

        Map<Long, Long> answers = request.getAnswers();

        int totalStress = 0, totalMotivation = 0, totalProductivity = 0;
        int answeredCount = 0;

        for (TestQuestion question : test.getQuestions()) {
            Long selectedOptionId = answers.get(question.getId());
            if (selectedOptionId == null) continue;

            TestAnswerOption selectedOption = question.getOptions().stream()
                    .filter(o -> o.getId().equals(selectedOptionId))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("Invalid answer option: " + selectedOptionId));

            totalStress += selectedOption.getStressScore();
            totalMotivation += selectedOption.getMotivationScore();
            totalProductivity += selectedOption.getProductivityScore();
            answeredCount++;
        }

        if (answeredCount == 0) {
            throw new BusinessException("No valid answers provided");
        }

        int stressLevel = totalStress / answeredCount;
        int motivationLevel = totalMotivation / answeredCount;
        int productivityLevel = totalProductivity / answeredCount;

        List<String> recommendations = buildRecommendations(stressLevel, motivationLevel, productivityLevel);

        UserTestResult result = UserTestResult.builder()
                .userId(userId)
                .test(test)
                .stressLevel(stressLevel)
                .motivationLevel(motivationLevel)
                .productivityLevel(productivityLevel)
                .recommendations(String.join("|", recommendations))
                .build();

        result = resultRepository.save(result);
        return toResultResponse(result, recommendations);
    }

    @Transactional(readOnly = true)
    public List<UserTestResultResponse> getUserResults(Long userId) {
        return resultRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(r -> toResultResponse(r, parseRecommendations(r.getRecommendations())))
                .toList();
    }

    @Transactional(readOnly = true)
    public TestStatsResponse getUserStats(Long userId) {
        Double avgStress = resultRepository.avgStressLevel(userId);
        Double avgMotivation = resultRepository.avgMotivationLevel(userId);
        Double avgProductivity = resultRepository.avgProductivityLevel(userId);
        long totalTests = resultRepository.countByUserId(userId);

        String overallStatus = determineOverallStatus(avgStress, avgMotivation, avgProductivity);

        return TestStatsResponse.builder()
                .avgStressLevel(avgStress != null ? Math.round(avgStress * 10.0) / 10.0 : 0.0)
                .avgMotivationLevel(avgMotivation != null ? Math.round(avgMotivation * 10.0) / 10.0 : 0.0)
                .avgProductivityLevel(avgProductivity != null ? Math.round(avgProductivity * 10.0) / 10.0 : 0.0)
                .totalTestsTaken((int) totalTests)
                .overallStatus(overallStatus)
                .build();
    }

    private List<String> buildRecommendations(int stress, int motivation, int productivity) {
        List<String> recs = new ArrayList<>();
        if (stress > 70) recs.add("Your stress level is high. Consider meditation or breathing exercises.");
        if (stress > 50) recs.add("Try to balance your workload and take regular breaks.");
        if (motivation < 40) recs.add("Set smaller, achievable goals to boost your motivation.");
        if (motivation > 70) recs.add("Great motivation! Channel it into your most important goals.");
        if (productivity < 40) recs.add("Focus on one task at a time using time-blocking technique.");
        if (productivity > 70) recs.add("You are highly productive. Consider mentoring others.");
        if (recs.isEmpty()) recs.add("You are in a balanced state. Keep up the good work!");
        return recs;
    }

    private String determineOverallStatus(Double stress, Double motivation, Double productivity) {
        if (stress == null) return "No data";
        double score = (100 - stress) * 0.3 + (motivation != null ? motivation : 50) * 0.35 + (productivity != null ? productivity : 50) * 0.35;
        if (score >= 70) return "EXCELLENT";
        if (score >= 50) return "GOOD";
        if (score >= 30) return "AVERAGE";
        return "NEEDS_IMPROVEMENT";
    }

    private List<String> parseRecommendations(String recs) {
        if (recs == null || recs.isBlank()) return List.of();
        return Arrays.asList(recs.split("\\|"));
    }

    private PsychTestResponse toTestResponse(PsychologicalTest test) {
        List<PsychTestResponse.QuestionResponse> questions = test.getQuestions().stream()
                .map(q -> PsychTestResponse.QuestionResponse.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .orderIndex(q.getOrderIndex())
                        .options(q.getOptions().stream()
                                .map(o -> PsychTestResponse.OptionResponse.builder()
                                        .id(o.getId())
                                        .optionText(o.getOptionText())
                                        .build())
                                .toList())
                        .build())
                .toList();

        return PsychTestResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .questions(questions)
                .build();
    }

    private UserTestResultResponse toResultResponse(UserTestResult result, List<String> recommendations) {
        return UserTestResultResponse.builder()
                .id(result.getId())
                .testId(result.getTest().getId())
                .testTitle(result.getTest().getTitle())
                .stressLevel(result.getStressLevel())
                .motivationLevel(result.getMotivationLevel())
                .productivityLevel(result.getProductivityLevel())
                .recommendations(recommendations)
                .createdAt(result.getCreatedAt())
                .build();
    }
}
