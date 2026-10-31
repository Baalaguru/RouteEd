-- Demo catalog used by the learner experience. Each module intentionally has no video URL yet.
INSERT INTO courses (id, organization_id, title, description, status, estimated_duration_days)
VALUES
  ('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Java Foundations', 'Build strong Java fundamentals through practical, bite-sized lessons.', 'PUBLISHED', 14),
  ('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Python Foundations', 'Learn Python syntax, data structures, functions, and files.', 'PUBLISHED', 14)
ON CONFLICT (id) DO NOTHING;

INSERT INTO modules (id, course_id, title, description, content_url, content_text, default_order, difficulty, estimated_minutes, skill_tag)
VALUES
  ('11000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'Java syntax and variables', 'Variables, primitive types, strings, and console output.', NULL, 'Video lesson coming soon.', 1, 'BEGINNER', 25, 'JAVA_BASICS'),
  ('11000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', 'Conditions and loops', 'Control program flow with if statements, for loops, and while loops.', NULL, 'Video lesson coming soon.', 2, 'BEGINNER', 30, 'CONTROL_FLOW'),
  ('11000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000001', 'Methods and object-oriented programming', 'Create methods, classes, objects, and constructors.', NULL, 'Video lesson coming soon.', 3, 'INTERMEDIATE', 40, 'OOP'),
  ('11000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000001', 'Collections with ArrayList', 'Store and process groups of values with ArrayList.', NULL, 'Video lesson coming soon.', 4, 'INTERMEDIATE', 35, 'COLLECTIONS'),
  ('21000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'Python syntax and variables', 'Get comfortable with Python values, strings, and print statements.', NULL, 'Video lesson coming soon.', 1, 'BEGINNER', 20, 'PYTHON_BASICS'),
  ('21000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', 'Lists, tuples, and dictionaries', 'Use Python data structures to organize information.', NULL, 'Video lesson coming soon.', 2, 'BEGINNER', 35, 'DATA_STRUCTURES'),
  ('21000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000001', 'Functions and modules', 'Write reusable functions and import standard modules.', NULL, 'Video lesson coming soon.', 3, 'INTERMEDIATE', 35, 'FUNCTIONS'),
  ('21000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000001', 'Working with files', 'Read and write text files safely with context managers.', NULL, 'Video lesson coming soon.', 4, 'INTERMEDIATE', 30, 'FILES')
ON CONFLICT (id) DO NOTHING;

INSERT INTO assessments (id, course_id, title, type, published)
VALUES
  ('12000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'Java Foundations diagnostic', 'ONBOARDING', TRUE),
  ('22000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'Python Foundations diagnostic', 'ONBOARDING', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO questions (id, assessment_id, prompt, options_json, correct_option, skill_tag, question_type, points, position)
VALUES
  ('13000000-0000-0000-0000-000000000001', '12000000-0000-0000-0000-000000000001', 'Which keyword creates a class in Java?', '["class", "define", "object", "newclass"]'::jsonb, 'class', 'OOP', 'MCQ', 1, 1),
  ('13000000-0000-0000-0000-000000000002', '12000000-0000-0000-0000-000000000001', 'Which collection keeps elements in insertion order?', '["ArrayList", "HashSet", "HashMap", "Stack"]'::jsonb, 'ArrayList', 'COLLECTIONS', 'MCQ', 1, 2),
  ('23000000-0000-0000-0000-000000000001', '22000000-0000-0000-0000-000000000001', 'Which symbol starts a Python comment?', '["#", "//", "--", "/*"]'::jsonb, '#', 'PYTHON_BASICS', 'MCQ', 1, 1),
  ('23000000-0000-0000-0000-000000000002', '22000000-0000-0000-0000-000000000001', 'Which Python type stores key-value pairs?', '["list", "tuple", "dictionary", "set"]'::jsonb, 'dictionary', 'DATA_STRUCTURES', 'MCQ', 1, 2)
ON CONFLICT (id) DO NOTHING;
