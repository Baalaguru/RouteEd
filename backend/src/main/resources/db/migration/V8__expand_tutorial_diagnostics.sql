-- Expand each starter diagnostic to six questions so personalization has a meaningful skill signal.
INSERT INTO questions (id, assessment_id, prompt, options_json, correct_option, skill_tag, question_type, points, position)
VALUES
  ('13000000-0000-0000-0000-000000000003', '12000000-0000-0000-0000-000000000001', 'Which type is used for a whole number in Java?', '["int", "String", "boolean", "double[]"]'::jsonb, 'int', 'JAVA_BASICS', 'MCQ', 1, 3),
  ('13000000-0000-0000-0000-000000000004', '12000000-0000-0000-0000-000000000001', 'Which loop is commonly used when the number of iterations is known?', '["for", "switch", "try", "class"]'::jsonb, 'for', 'CONTROL_FLOW', 'MCQ', 1, 4),
  ('13000000-0000-0000-0000-000000000005', '12000000-0000-0000-0000-000000000001', 'What does the new keyword do in Java?', '["Creates an object", "Deletes a class", "Imports a package", "Stops a loop"]'::jsonb, 'Creates an object', 'OOP', 'MCQ', 1, 5),
  ('13000000-0000-0000-0000-000000000006', '12000000-0000-0000-0000-000000000001', 'Which method adds an item to an ArrayList?', '["add", "append", "push", "insertAfter"]'::jsonb, 'add', 'COLLECTIONS', 'MCQ', 1, 6),
  ('23000000-0000-0000-0000-000000000003', '22000000-0000-0000-0000-000000000001', 'Which function displays text in Python?', '["print", "echo", "show", "console"]'::jsonb, 'print', 'PYTHON_BASICS', 'MCQ', 1, 3),
  ('23000000-0000-0000-0000-000000000004', '22000000-0000-0000-0000-000000000001', 'Which brackets create a Python list?', '["[]", "{}", "()", "<>"]'::jsonb, '[]', 'DATA_STRUCTURES', 'MCQ', 1, 4),
  ('23000000-0000-0000-0000-000000000005', '22000000-0000-0000-0000-000000000001', 'Which keyword defines a Python function?', '["def", "func", "function", "lambda"]'::jsonb, 'def', 'FUNCTIONS', 'MCQ', 1, 5),
  ('23000000-0000-0000-0000-000000000006', '22000000-0000-0000-0000-000000000001', 'Which mode opens a file for writing in Python?', '["w", "r", "xonly", "read"]'::jsonb, 'w', 'FILES', 'MCQ', 1, 6)
ON CONFLICT (id) DO NOTHING;
