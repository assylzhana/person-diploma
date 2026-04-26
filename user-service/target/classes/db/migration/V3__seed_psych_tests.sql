INSERT INTO psychological_tests (title, description, active)
VALUES
    ('Stress & Productivity Assessment', 'Evaluate your current stress level, motivation, and productivity.', true),
    ('Daily Wellbeing Check', 'A quick check on your daily mental wellbeing and energy levels.', true);

-- Test 1 questions
INSERT INTO test_questions (test_id, question_text, order_index)
VALUES
    (1, 'How would you describe your current workload?', 1),
    (1, 'How well did you sleep last night?', 2),
    (1, 'How motivated do you feel about your goals today?', 3),
    (1, 'How focused are you able to be during work?', 4);

-- Question 1 options
INSERT INTO test_answer_options (question_id, option_text, stress_score, motivation_score, productivity_score)
VALUES
    (1, 'Very heavy - I feel overwhelmed', 90, 20, 20),
    (1, 'Heavy but manageable', 60, 50, 50),
    (1, 'Moderate - I can handle it', 40, 65, 65),
    (1, 'Light - I have capacity for more', 10, 85, 85);

-- Question 2 options
INSERT INTO test_answer_options (question_id, option_text, stress_score, motivation_score, productivity_score)
VALUES
    (2, 'Very poorly - less than 4 hours', 85, 25, 25),
    (2, 'Poorly - 4-6 hours, restless', 65, 40, 40),
    (2, 'Okay - 6-7 hours', 40, 60, 60),
    (2, 'Well - 7-9 hours, refreshed', 15, 85, 85);

-- Question 3 options
INSERT INTO test_answer_options (question_id, option_text, stress_score, motivation_score, productivity_score)
VALUES
    (3, 'Not motivated at all', 70, 10, 20),
    (3, 'Slightly motivated', 50, 35, 40),
    (3, 'Moderately motivated', 30, 65, 60),
    (3, 'Highly motivated', 10, 90, 85);

-- Question 4 options
INSERT INTO test_answer_options (question_id, option_text, stress_score, motivation_score, productivity_score)
VALUES
    (4, 'Very distracted - cannot focus', 80, 20, 15),
    (4, 'Somewhat distracted', 55, 45, 40),
    (4, 'Mostly focused', 30, 65, 70),
    (4, 'Fully focused and in flow', 10, 90, 95);

-- Test 2 questions
INSERT INTO test_questions (test_id, question_text, order_index)
VALUES
    (2, 'How do you feel emotionally today?', 1),
    (2, 'What is your energy level right now?', 2),
    (2, 'How satisfied are you with your progress this week?', 3);

-- Test 2 question 1 options
INSERT INTO test_answer_options (question_id, option_text, stress_score, motivation_score, productivity_score)
VALUES
    (5, 'Very stressed or anxious', 90, 20, 20),
    (5, 'A bit down or tired', 60, 40, 40),
    (5, 'Neutral', 40, 55, 55),
    (5, 'Happy and energetic', 10, 90, 85);

-- Test 2 question 2 options
INSERT INTO test_answer_options (question_id, option_text, stress_score, motivation_score, productivity_score)
VALUES
    (6, 'Very low - exhausted', 85, 15, 15),
    (6, 'Low - feeling sluggish', 65, 35, 35),
    (6, 'Moderate', 35, 60, 60),
    (6, 'High - feeling great', 10, 90, 90);

-- Test 2 question 3 options
INSERT INTO test_answer_options (question_id, option_text, stress_score, motivation_score, productivity_score)
VALUES
    (7, 'Not satisfied at all', 70, 20, 20),
    (7, 'Slightly satisfied', 50, 45, 40),
    (7, 'Moderately satisfied', 30, 65, 65),
    (7, 'Very satisfied', 10, 90, 90);
