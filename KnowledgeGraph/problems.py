import pandas as pd
from neo4j import GraphDatabase

class Neo4jImporter:
    def __init__(self, uri, user, password):
        self.driver = GraphDatabase.driver(uri, auth=(user, password))
    
    def close(self):
        self.driver.close()
    
    def import_data_from_excel(self, excel_file):
        df = pd.read_excel(excel_file)
        with self.driver.session() as session:
            for index, row in df.iterrows():
                session.execute_write(self._add_data, row)
    
    def _add_data(self, tx, row):
        choices_text = row['Choices'].split(', ') if row['Choices'] else []

        data = {
            'grade': row['Grade'],
            'subject': row['Subject'],
            'topic': row['Topic'],
            'category': row['Category'],
            'skill': row['Skill'],
            'task': row['Task'],
            'question': row['Question'],
            'solution': row.get('Solution', None),
            'lecture': row.get('Lecture', None),
            'choices': choices_text,
            'correctIndex': int(row['Answer']) if pd.notnull(row['Answer']) else None
        }
        if pd.isna(row.get('Lecture')):
            data['lecture'] = None
        query = """
        MERGE (g:Grade {name: $grade})
        MERGE (s:Subject {name: $subject})
        MERGE (g)-[:INCLUDES]->(s)
        MERGE (t:Topic {name: $topic})
        MERGE (t)-[:BELONGS_TO]->(s)
        MERGE (c:Category {name: $category})
        MERGE (c)-[:PART_OF]->(t)
        MERGE (sk:Skill {name: $skill})
        MERGE (c)-[:REQUIRES]->(sk)
        MERGE (task:Task {type: $task})
        CREATE (q:Question {text: $question})
        SET q.solution = COALESCE($solution, '')
        MERGE (q)-[:DEMONSTRATES]->(sk)
        MERGE (q)-[:IS_A]->(task)
        WITH q, $choices as choices, $correctIndex as correctIndex
        UNWIND range(0, size(choices) - 1) as i
        MERGE (ch:Choice {text: choices[i], questionId: id(q), isCorrect: i = correctIndex})
        MERGE (q)-[:HAS_CHOICE]->(ch)
        FOREACH (_ IN CASE WHEN $lecture IS NOT NULL THEN [1] ELSE [] END | 
            MERGE (l:Lecture {content: $lecture})
            MERGE (q)-[:EXPLAINED_BY]->(l))
        """
        tx.run(query, data)

# Example usage
if __name__ == "__main__":
    # Your local neo4j account password.
    importer = Neo4jImporter("bolt://localhost:7687", "neo4j", "123")
    # This is a relative path, if there is a mistake, please use an absolute path.
    importer.import_data_from_excel("problems.xlsx")
    importer.close()
