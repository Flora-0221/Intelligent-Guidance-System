# 🌟 EduNavi: Intelligent Guidance System for Next-Generation Learning

EduNavi is an AI-powered intelligent education platform designed to revolutionize digital learning by combining personalized questioning, automated grading, adaptive learning paths, and an engaging community experience. Built on the principles of constructivist education, it empowers students to actively build knowledge through interaction, feedback, and collaboration.

<p align="center">
  <img width="422" alt="Main Page" src="https://github.com/user-attachments/assets/96868a2b-d60c-4ade-bfd7-abf46bac30c2">
</p>

## 🚀 Project Vision

As digitalization reshapes global education, EduNavi addresses the urgent need for scalable yet personalized learning environments. By leveraging cutting-edge technologies like LSTM, P-Tuning, Bagging ensemble learning, and Neo4j-based knowledge graphs, EduNavi delivers intelligent guidance and real-time evaluation with high accuracy and adaptability.

## 🧠 Core Features

### 1. **Intelligent Questioning & Grading System**
- Multi-dimensional question selection (difficulty, type, quantity)
- Real-time performance evaluation and AI-generated analysis
- Personalized question recommendations based on learner performance
- Achieves **82.13%** evaluation accuracy via LSTM + P-Tuning + Bagging

<p align="center">
  <img width="522" alt="Answering Pages" src="https://github.com/user-attachments/assets/8017166c-045a-4d22-ad91-3ee989415e2e">
</p>

### 2. **Personalized Learning Navigation**
- Visualized knowledge graph powered by **Neo4j**
- Interactive learning path planning based on knowledge acquisition status
- Customized learning suggestions grounded in constructivist pedagogy

<p align="center">
  <img src="https://github.com/user-attachments/assets/563bb967-0037-46e0-928f-de6e934ffd92" alt="Knowledge Graph Main Page" width="49%"/>
  <img src="https://github.com/user-attachments/assets/95cc49c5-8741-4ff5-8d32-48c9ae4347f8" alt="Knowledge Graph Searching Page" width="49%"/>
</p>


### 3. **Automated Grading Module**
- Subjective answer evaluation using deep learning (LSTM/CNN)
- Fine-tuned models optimized with Reinforcement Learning from Human Feedback (RLHF)
- Improves grading robustness and reduces training complexity

### 4. **Community Support Center**
- Blog & Q&A sections for collaborative learning
- Support for **Markdown** editing with real-time preview
- Interactive features: nested comments, likes, and topic-based navigation

<p align="center">
  <img src="https://github.com/user-attachments/assets/4b082051-5c1b-49ec-b909-34ebade1caaa" alt="Community Page 1" width="49%"/>
  <img src="https://github.com/user-attachments/assets/db2daffd-aeca-4d76-af95-4d013674ffb7" alt="Community Page 2" width="49%"/>
</p>


### 5. **User Management Center**
- Role-based access control (RBAC)
- Learning data analytics & progress visualization
- Personalized dashboard with statistics, scores, and recommendations
- 
<p align="center">
  <img src="https://github.com/user-attachments/assets/d966d9fb-d1f9-453f-bbb4-2a943232fe23" alt="User Management Pages" width="50%"/>
</p>



## 🛠️ Technology Stack

| Layer        | Technology Used                  |
|-------------|----------------------------------|
| **Frontend** | Vue.js + Element-UI              |
| **Backend**  | Spring Boot                      |
| **Proxy**    | Nginx                            |
| **Database** | MySQL + Redis + Neo4j            |
| **Model Layer** | LSTM + P-Tuning + Bagging + RLHF |
| **Data Access** | MyBatis Plus                  |

> The project follows the **MVC (Model-View-Controller)** architecture for a clean and maintainable codebase.

## 📈 Architecture Overview

- **View Layer:** Vue.js + Element-UI for a responsive and intuitive UI
- **Controller Layer:** Spring Boot RESTful API handling client-server interactions
- **Model Layer:** Redis caching, business logic clustering, and data persistence with MyBatis Plus + MySQL
- **Graph Services:** Neo4j for managing and querying knowledge relationships
- **AI Engine:** Fine-tuned LSTM with P-Tuning and Bagging for scalable and accurate grading

## 🌍 Vision for the Future

EduNavi is committed to promoting **educational equity** by democratizing access to intelligent learning tools. We strive to build a robust digital learning ecosystem that adapts to each learner’s needs while fostering community-driven knowledge sharing.

---

## 🤝 Contribution

We welcome contributions from the community to improve and expand EduNavi. Feel free to fork the repository, raise issues, or open pull requests!!!

---

> Empowering learners through intelligence, guidance, and community — EduNavi leads the future of smart education.
