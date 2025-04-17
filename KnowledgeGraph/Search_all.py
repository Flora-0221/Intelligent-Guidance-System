from flask import Flask, request, jsonify, render_template
from neo4j import GraphDatabase
from pyecharts import options as opts
from pyecharts.charts import Graph
import json
from neo4j import GraphDatabase
import json
from flask import Flask, request, jsonify
from pyecharts import options as opts
from pyecharts.charts import Graph
from pyecharts.commons.utils import JsCode
app = Flask(__name__)


class Neo4jConnection:
    def __init__(self, uri, user, password):
        self.__uri = uri
        self.__user = user
        self.__password = password
        self.__driver = None
        try:
            self.__driver = GraphDatabase.driver(
                self.__uri, auth=(self.__user, self.__password))
        except Exception as e:
            print("Failed to create the driver:", e)

    def close(self):
        if self.__driver is not None:
            self.__driver.close()

    def query(self, keyword, parameters=None, db=None):
        session = None
        response = None
        query = """
        MATCH (q:Question)-[r:HAS_CHOICE]->(c:Choice)
        WHERE ANY(word IN split(q.text, " ") WHERE word = $keyword)
        RETURN q AS questionNode, collect(c) AS choiceNodes, q.solution AS solutionNode
        """
        try:
            session = self.__driver.session(
                database=db) if db is not None else self.__driver.session()
            response = list(session.run(
                query, keyword=keyword, parameters=parameters))
        except Exception as e:
            print("Query failed:", e)
        finally:
            if session is not None:
                session.close()
        return response


uri="bolt://127.0.0.1:7687"
user="neo4j"
password="123"
driver = GraphDatabase.driver(uri, auth=(user, password))

conn = Neo4jConnection(uri="bolt://127.0.0.1:7687",
                       user="neo4j", password="123")


def get_data(skill_name):
    query = """
    MATCH (sk:Skill {name: $skill_name})<-[:DEMONSTRATES]-(q:Question)
    WITH sk, q
    LIMIT 20
    OPTIONAL MATCH (q)-[:IS_A]->(t:Task)
    OPTIONAL MATCH (q)-[:HAS_CHOICE]->(ch:Choice)
    OPTIONAL MATCH (q)-[:EXPLAINED_BY]->(l:Lecture)
    RETURN q AS Question, sk AS Skill, t AS Task, collect(ch) AS Choices, l AS Lecture
    """
    with driver.session() as session:
        result = session.run(query, skill_name=skill_name)
        data = [record.data() for record in result]
    return data


def process_neo4j_data(data):
    nodes = []
    links = []
    node_ids = set()

    for item in data:
        question = item['Question']
        question_id = "Q: " + question['text']
        if question_id not in node_ids:
            nodes.append({"name": question_id, "symbolSize": 30,
                         "category": 6, "solution": question.get("solution", "")})
            node_ids.add(question_id)

        skill = item['Skill']
        skill_id = "Skill: " + skill['name']
        if skill_id not in node_ids:
            nodes.append({"name": skill_id, "symbolSize": 50, "category": 4})
            node_ids.add(skill_id)
        links.append({"source": skill_id, "target": question_id})

        task = item.get('Task', {})
        if task:
            task_id = "Task: " + task['type']
            if task_id not in node_ids:
                nodes.append(
                    {"name": task_id, "symbolSize": 30, "category": 5})
                node_ids.add(task_id)
            links.append({"source": question_id, "target": task_id})

        for choice in item.get('Choices', []):
            choice_id = "Choice: " + choice['text']
            if choice_id not in node_ids:
                nodes.append({"name": choice_id, "symbolSize": 15,
                             "category": 7, "isCorrect": choice.get('isCorrect', False)})
                node_ids.add(choice_id)
            links.append({"source": question_id, "target": choice_id})

        lecture = item.get('Lecture', {})
        if lecture:
            lecture_id = "Lecture: " + lecture['content'][:30] + "..."
            if lecture_id not in node_ids:
                nodes.append(
                    {"name": lecture_id, "symbolSize": 35, "category": 8})
                node_ids.add(lecture_id)
            links.append({"source": lecture_id, "target": question_id})

    return nodes, links


def create_graph(nodes, links):
    for link in links:
        source_category = next(
            (item for item in nodes if item["name"] == link['source']), {}).get('category', None)
        target_category = next(
            (item for item in nodes if item["name"] == link['target']), {}).get('category', None)
        if source_category == 0 and target_category == 1:
            link['display_text'] = 'INCLUDES'
        elif source_category == 1 and target_category == 2:
            link['display_text'] = 'BELONGS_TO'
        elif source_category == 2 and target_category == 3:
            link['display_text'] = 'PART_OF'
        elif source_category == 3 and target_category == 4:
            link['display_text'] = 'REQUIRES'
        elif source_category == 4 and target_category == 6:
            link['display_text'] = 'DEMONSTRATES'
        elif source_category == 6 and target_category == 5:
            link['display_text'] = 'IS_A'
        elif source_category == 6 and target_category == 7:
            link['display_text'] = 'HAS_CHOICE'
        elif source_category == 8 and target_category == 6:
            link['display_text'] = 'EXPLAINED_BY'
        else:
            link['display_text'] = 'OTHER_RELATIONSHIP'
    categories = [
        {"name": "Grade"},
        {"name": "Subject"},
        {"name": "Topic"},
        {"name": "Category"},
        {"name": "Skill"},
        {"name": "Task"},
        {"name": 'Question'},
        {"name": 'Choice'},
        {"name": 'Lecture'},
    ]

    graph = Graph(init_opts=opts.InitOpts(width="100%", height="800px"))
    graph.add(
        "",
        nodes=nodes,
        links=links,
        categories=categories,
        layout="force",
        repulsion=5000,
        gravity=0.2,
        edge_length=100,
        label_opts=opts.LabelOpts(position="right"),
        linestyle_opts=opts.LineStyleOpts(curve=0.3),
        edge_label=opts.LabelOpts(
            is_show=True,
            position="middle",
            formatter=JsCode(
                "function(data){return data.data.display_text || '';}"),
        ),

    )
    graph.set_global_opts(
        legend_opts=opts.LegendOpts(
            orient="vertical", pos_left="4%", pos_top="20%"),
        tooltip_opts=opts.TooltipOpts(formatter=JsCode("""
            function (params) {
                if (params.dataType === 'node') {
                    if (params.data.category === 8) {                    
                        var textWithBreaks = params.data.name.replace(/\\n/g, '<br>');
                        return textWithBreaks;
                    } else if(params.data.category === 7) {
                        var correctness = params.data.isCorrect ? 'Correct' : 'Incorrect';
                        return params.data.name + ' is  (' + correctness + ')';
                    } else if(params.data.category === 6) {                               
                        return 'solution: ' + params.data.solution;
                    } else {
                        return params.data.shortText ? params.data.shortText : params.data.name;
                    }
                } else if (params.dataType === 'edge') {
                    return params.data.display_text || '';
                }
            }
        """)),
    )
    # return graph.render('educational_content_relationships111.html')
    return jsonify({
        'graphHtml': graph.render_embed(),
    })


@app.route('/generate-graph-skill', methods=['POST'])
def generate_graph_skill():
    request_data = json.loads(request.json['skillName'])
    skill_name = request_data['skillName']
    print(skill_name)
    raw_data = get_data(skill_name)
    nodes, links = process_neo4j_data(raw_data)
    return create_graph(nodes, links)


@app.route('/generate-graph-kw', methods=['POST'])
def generate_graph_kw():
    # keyword = request.form['keyword']  # 从前端获取关键字
    # Initialize the Neo4j connection
    request_data = json.loads(request.json['keyword'])
    keyword = request_data['keyword']
    print(keyword)

    # uri = "bolt://127.0.0.1:7687"
    # user = "neo4j"
    # password = "123"
    # conn = Neo4jConnection(uri, user, password)

    # Execute the query with the specified keyword
    result = conn.query(keyword)

    # Convert the query results into detailed_info
    detailed_info = []
    for record in result:
        node_properties = record["questionNode"]._properties
        question_text = node_properties.get("text", "Question text not found")
        choice_nodes = record["choiceNodes"]
        choices = [c._properties.get(
            "text", "Choice text not found") for c in choice_nodes]
        solution_text = record.get("solutionNode", "Solution not found")
        detailed_info.append({
            "question": question_text,
            "choices": choices,
            "solution": solution_text
        })

    # Close the Neo4j connection
    conn.close()

    # Visualization setup
    nodes = [{"name": keyword, "category": 0,
              "symbolSize": 70, "itemStyle": {"color": "#ff7f0e"}}]
    links = []
    categories = [{"name": "Keyword"}, {"name": "Question"},
                  {"name": "Choice"}, {"name": "Solution"}]

    for i, item in enumerate(detailed_info, start=1):
        question_node_name = f"Q{i}"
        solution_node_name = f"S{i}"

        nodes.append({
            "name": question_node_name,
            "category": 1,
            "symbolSize": 50,
            "itemStyle": {"color": "#1f77b4"},
            "tooltip": item["question"]
        })

        nodes.append({
            "name": solution_node_name,
            "category": 3,
            "symbolSize": 40,
            "itemStyle": {"color": "#d62728"},
            "tooltip": item["solution"]
        })

        links.append(
            {"source": keyword, "target": question_node_name, "value": "Keyword"})
        links.append({"source": question_node_name,
                     "target": solution_node_name, "value": "Question to Solution"})

        for choice_text in item["choices"]:
            choice_node_name = f"C{i}-{item['choices'].index(choice_text)}"
            nodes.append({
                "name": choice_node_name,
                "category": 2,
                "symbolSize": 40,
                "itemStyle": {"color": "#2ca02c"},
                "tooltip": choice_text
            })
            links.append({"source": question_node_name,
                         "target": choice_node_name, "value": "Question to Choice"})

    graph = Graph()
    graph.add("", nodes, links, categories, repulsion=5000, edge_label=opts.LabelOpts(
        is_show=True, formatter="{c}"), layout="force")
    graph.set_global_opts(legend_opts=opts.LegendOpts(is_show=False))

    return jsonify({
        'graphHtml': graph.render_embed(),
    })
    # graph.render('educational_content_relationships111.html')


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8888)
