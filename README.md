# eBank Bot — Microservices bancaires & assistant IA

## 1. Présentation
eBank Bot est une application bancaire construite sur une architecture microservices et dotée d'un assistant conversationnel basé sur l'IA.
Le backend est composé de services Spring Boot indépendants, responsables de la gestion des clients, de la gestion des comptes bancaires, de la découverte de services, du routage des API et des interactions assistées par l'IA.
L'assistant utilise Spring AI et OpenAI pour traiter des questions en langage naturel. Grâce au Model Context Protocol (MCP), il peut interagir avec les services clients et bancaires pour consulter des informations et exécuter certaines opérations.
L'assistant est également intégré à Discord, ce qui permet de dialoguer avec le chatbot directement depuis un serveur ou en message privé.
Le projet est développé de manière incrémentale. Le backend et les intégrations du chatbot sont implémentés et testés ; le frontend Angular constitue la prochaine étape.

---

## 2. Objectifs du projet
* Mettre en place un backend modulaire avec des microservices Spring Boot.
* Séparer la gestion des clients et celle des comptes bancaires dans des services indépendants.
* Activer la découverte de services avec Netflix Eureka.
* Centraliser l'accès aux services avec Spring Cloud Gateway.
* Implémenter la communication inter-services avec OpenFeign.
* Améliorer la résilience grâce à des mécanismes de circuit breaker.
* Intégrer un assistant IA avec Spring AI et OpenAI.
* Exposer les capacités métier à l'assistant via des outils MCP.
* Conserver l'historique des conversations avec la mémoire de chat de Spring AI.
* Fournir un accès au chatbot via REST et Discord.
* Préparer le backend à l'intégration d'un frontend Angular.

---

## 3. Architecture
Chaque service a une responsabilité précise. Le frontend Angular est prévu, et les routes exactes de la Gateway dépendent des mappings configurés.

### Composants principaux
| Composant | Port | Responsabilité |
| :--- | :--- | :--- |
| **Customer Service** | 8056 | Opérations métier liées aux clients |
| **eBank Service** | 8057 | Opérations métier liées aux comptes bancaires |
| **eBank Bot** | 8058 | Assistant IA, endpoint de chat et intégrations chatbot |
| **Eureka Discovery Server** | 8761 | Enregistrement et découverte des services |
| **Spring Cloud Gateway** | 9999 | Point d'entrée centralisé des API |
| **Frontend Angular** | À définir | Interface utilisateur — prévu |

---

## 4. Stack technique

### Backend
* **Java** — langage principal.
* **Spring Boot** — développement applicatif et injection de dépendances.
* **Spring Web** — endpoints HTTP et REST.
* **Spring Data JPA** — persistance, là où elle est configurée.
* **Spring Cloud Netflix Eureka** — découverte de services.
* **Spring Cloud Gateway** — routage centralisé des requêtes.
* **Spring Cloud OpenFeign** — communication HTTP déclarative entre services.
* **Circuit Breaker** — résilience des appels inter-services.

### IA et chatbot
* **Spring AI** — intégration avec les modèles de langage.
* **OpenAI GPT-4o** — modèle utilisé par l'assistant.
* **Model Context Protocol (MCP)** — expose les opérations métier comme outils accessibles à l'IA.
* **Spring AI ChatMemory** — gestion de l'historique des conversations.
* **Discord** — interaction avec le chatbot via un bot Discord.
* **Telegram** — intégration disponible lorsque la configuration du bot correspondant est activée.

### Outils de développement
* **Maven** — gestion des dépendances et build.
* **Git** — gestion de versions.
* **IntelliJ IDEA** — environnement de développement.

---

## 5. Les microservices

### 5.1 Customer Service
* **Port** : 8056
* Ce service gère les opérations métier liées aux clients. Ses outils MCP sont :
  * `getAllCustomers` : Récupère la liste des clients
  * `findCustomerById(Long)` : Récupère un client par son identifiant
  * `saveCustomer(Customer)` : Enregistre un client
* **Endpoint MCP** : `http://localhost:8056/mcp`
* **API REST exposée** :
  * `GET /customers` : Liste des clients
  * `POST /customers` : Création d'un client
  * `GET /customers/{id}` : Détail d'un client
* Accès via la Gateway : `http://localhost:9999/CUSTOMER-SERVICE/customers`

### 5.2 eBank Service
* **Port** : 8057
* Ce service gère les comptes bancaires indépendamment du Customer Service. Ses outils MCP sont :
  * `getAllAccounts` : Récupère la liste des comptes bancaires
  * `findAccountById(Long)` : Récupère un compte par son identifiant
  * `saveAccount(Account)` : Enregistre un compte bancaire
* **Endpoint MCP** : `http://localhost:8057/mcp`
* **API REST exposée** :
  * `GET /accounts` : Liste des comptes bancaires
  * `POST /accounts` : Création d'un compte
  * `GET /accounts/{id}` : Détail d'un compte
* Accès via la Gateway : `http://localhost:9999/EBANK-SERVICE/accounts`

### 5.3 Eureka Discovery Server
* **Port** : 8761
* Eureka assure l'enregistrement et la découverte des services : au lieu de s'appuyer exclusivement sur des adresses codées en dur, les services enregistrés se découvrent via le registre.
* Tableau de bord : `http://localhost:8761/`

### 5.4 Spring Cloud Gateway
* **Port** : 9999
* Point d'entrée centralisé pour les requêtes vers les services backend. Ses responsabilités :
  * Router les requêtes vers le microservice approprié.
  * Centraliser l'accès aux routes de services configurées.
  * S'intégrer à l'architecture de découverte de services.
* Les services sont accessibles via leur nom Eureka, par exemple `/CUSTOMER-SERVICE/**` et `/EBANK-SERVICE/**`.

### 5.5 eBank Bot
* **Port** : 8058
* Interface conversationnelle entre les utilisateurs, le modèle d'IA et les microservices bancaires. Ses responsabilités :
  * Recevoir des questions en langage naturel.
  * Envoyer les prompts au ChatClient de Spring AI.
  * Utiliser un prompt système pour définir le rôle de l'assistant.
  * Conserver l'historique via la mémoire de chat.
  * Se connecter au Customer Service et à l'eBank Service via MCP.
  * Retourner la réponse générée via HTTP.
  * Traiter les messages reçus via Discord.
* **Connexions MCP** :
  * Customer MCP : `http://localhost:8056/mcp`
  * eBank MCP : `http://localhost:8057/mcp`

---

## 6. Assistant IA et intégration MCP

### 6.1 Spring AI ChatClient
Le chatbot utilise le `ChatClient` de Spring AI pour communiquer avec le modèle OpenAI configuré.
Le prompt système définit l'assistant comme un assistant bancaire centré sur les clients et les comptes bancaires, et lui demande de ne pas inventer d'informations lorsque le contexte nécessaire n'est pas disponible.

### 6.2 Model Context Protocol
MCP fournit un moyen standardisé pour une application d'IA d'accéder aux capacités exposées par d'autres services. Le flux d'intégration est le suivant :
1. L'utilisateur envoie une question.
2. L'eBank Bot transmet le prompt au modèle d'IA.
3. L'assistant peut utiliser les outils MCP mis à disposition par l'application.
4. Le microservice concerné exécute l'opération métier demandée.
5. Le résultat de l'outil est renvoyé à l'assistant.
6. L'assistant formule une réponse pour l'utilisateur.

### 6.3 Mémoire de conversation
La mémoire de chat de Spring AI sert à conserver le contexte des conversations.
Chaque requête doit inclure un `conversationId` identifiant la conversation dont l'historique doit être récupéré. Réutiliser le même identifiant permet de conserver l'historique ; un identifiant différent sépare les historiques.

---

## 7. API HTTP

### Endpoint de chat
Le chatbot expose un endpoint `GET` pour tester les interactions conversationnelles.
* **URL de base** : `http://localhost:8058`
* **Route** : `GET /chat`
* **Paramètres de requête** :
  * `query` (non obligatoire) : Question en langage naturel ; valeur par défaut : *Bonjour*.
  * `conversationId` (non obligatoire) : Identifiant de conversation ; valeur par défaut : *default*.

**Exemples de requêtes** :
* `http://localhost:8058/chat?query=Bonjour&conversationId=test-1`
* `http://localhost:8058/chat?query=Quels%20sont%20les%20clients%20disponibles%3F&conversationId=test-1`



## 8. Intégration Discord
L'intégration Discord permet de communiquer avec l'assistant IA depuis un serveur Discord ou en message privé.
Le gestionnaire de messages suit ces étapes :
1. Il reçoit un événement de message Discord.
2. Il ignore les messages écrits par des bots.
3. Il extrait le texte du message.
4. Il utilise l'identifiant du canal Discord comme identifiant de conversation.
5. Il appelle l'agent IA eBank.
6. Il renvoie la réponse générée dans le canal Discord.

---

