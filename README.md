# 📘 Book Store SOA & Microservices Project

<p align="center">
  <b>GlobalBooks Inc. SOA migration project</b><br>
  Transforming a monolithic system into a modern SOA with 4 microservices.
</p>

---

## 📖 Project Overview

This project demonstrates how **GlobalBooks Inc.** migrated from a large monolithic application to a modern **Service-Oriented Architecture (SOA)**. It includes four independent services:

- 📚 **Catalog Service** - Book catalog management and search functionality
- 📦 **Orders Service** - Order processing and tracking
- 💳 **Payment Service** - Payment processing and transaction management
- 🚚 **Shipping Service** - Shipping calculations and logistics

The architecture showcases the transition from a tightly-coupled monolithic system to loosely-coupled microservices, enabling better scalability, maintainability, and independent deployment capabilities.

---

## 🚀 Services Architecture

| Service | Protocol | Technology | Port | Description |
|---------|----------|------------|------|-------------|
| **Catalog Service** | SOAP | Java + JAX-WS | `8080` | Manages book catalog, search, and inventory |
| **Orders Service** | REST | Spring Boot | `8081` | Handles order creation, processing, and tracking |
| **Payment Service** | REST | Spring Boot | `8082` | Manages payment processing and transactions |
| **Shipping Service** | REST | Spring Boot | `8083` | Calculates shipping costs and logistics |

---

## 🛠️ Tech Stack

### Backend Technologies
- ☕ **Java 17+** - Core programming language
- 🍃 **Spring Boot 3.1+** - Application framework
- 📡 **JAX-WS** - SOAP web services
- 🔄 **Apache ODE** - Business process orchestration
- 📨 **RabbitMQ** - Message broker for async communication

### Database & Storage
- 🗄️ **PostgreSQL** - Primary database for all services
- 💾 **Database per Service** pattern implementation

### Security
- 🔐 **WS-Security** - SOAP service security
- 🔑 **OAuth2 + JWT** - REST API authentication and authorization
- 🛡️ **Spring Security** - Application security framework

### DevOps & Deployment
- 🐳 **Docker** - Containerization
- 🐙 **Docker Compose** - Multi-container orchestration
- ☸️ **Kubernetes** (optional) - Cloud deployment

### Documentation & Testing
- 📊 **Swagger/OpenAPI** - API documentation
- 🧪 **SOAP UI** - SOAP service testing
- 📮 **Postman** - REST API testing

---

## 📁 Project Structure

```
Book_Store-SOA-Microservices/
├── catalog-service/                # SOAP-based catalog service
│   ├── src/
│   │   ├── main/java/
│   │   └── main/resources/
│   ├── pom.xml
│   └── Dockerfile
├── orders-service/                 # REST-based orders service
│   ├── src/
│   │   ├── main/java/
│   │   └── main/resources/
│   ├── pom.xml
│   └── Dockerfile
├── payment-service/                # REST-based payment service
│   ├── src/
│   │   ├── main/java/
│   │   └── main/resources/
│   ├── pom.xml
│   └── Dockerfile
├── shipping-service/               # REST-based shipping service
│   ├── src/
│   │   ├── main/java/
│   │   └── main/resources/
│   ├── pom.xml
│   └── Dockerfile
├── docker-compose.yml              # Service orchestration
├── init-databases.sql              # Database initialization scripts
├── .env                           # Environment variables
├── .gitignore                     # Git ignore file
└── README.md                      # This documentation
```

---

## 🚀 Quick Start

### Prerequisites
Make sure you have the following installed:
- ☕ **Java 17+**
- 📦 **Maven 3.8+**
- 🐳 **Docker** and **Docker Compose**
- 🗄️ **PostgreSQL 14+**
- 📨 **RabbitMQ 3.11+**

### Installation Steps

1. **Clone the repository:**
```bash
git clone https://github.com/Chamath-Lasindu/Book_Store-SOA-Microservices.git
cd Book_Store-SOA-Microservices
```

2. **Set up environment variables:**
```bash
cp .env.example .env
# Edit .env file with your configuration
```

3. **Start all services with Docker Compose:**
```bash
docker-compose up -d
```

4. **Initialize databases:**
```bash
docker-compose exec postgres psql -U postgres -f /docker-entrypoint-initdb.d/init-databases.sql
```

5. **Verify services are running:**
```bash
docker-compose ps
```

### Manual Setup (Development)

If you prefer to run services individually:

1. **Start PostgreSQL and RabbitMQ:**
```bash
docker-compose up -d postgres rabbitmq
```

2. **Build all services:**
```bash
mvn clean install
```

3. **Start each service:**
```bash
# Terminal 1 - Catalog Service
cd catalog-service
mvn spring-boot:run

# Terminal 2 - Orders Service
cd orders-service
mvn spring-boot:run

# Terminal 3 - Payment Service
cd payment-service
mvn spring-boot:run

# Terminal 4 - Shipping Service
cd shipping-service
mvn spring-boot:run
```

---

## 📚 API Endpoints

### Service URLs

| Service | Type | URL | Documentation |
|---------|------|-----|---------------|
| **Catalog** | SOAP | `http://localhost:8080/ws/v1/catalog?wsdl` | WSDL Document |
| **Orders** | REST | `http://localhost:8081/api/v1/orders` | [Swagger UI](http://localhost:8082/swagger-ui.html) |
| **Payments** | REST | `http://localhost:8082/api/v1/payments` | [Swagger UI](http://localhost:8083/swagger-ui.html) |
| **Shipping** | REST | `http://localhost:8083/api/v1/shipping` | [Swagger UI](http://localhost:8084/swagger-ui.html) |

### Sample API Calls

#### Orders Service (REST)
```bash
# Create a new order
curl -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "12345",
    "items": [
      {"bookId": "book-001", "quantity": 2, "price": 29.99}
    ]
  }'

# Get order by ID
curl -X GET http://localhost:8081/api/v1/orders/1
```

#### Payment Service (REST)
```bash
# Process payment
curl -X POST http://localhost:8082/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-123",
    "amount": 59.98,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "4111111111111111"
  }'
```

---

## 🧪 Testing

### SOAP Services
Use **SOAP UI** for testing the Catalog service:
1. Import WSDL: `http://localhost:8080/ws/v1/catalog?wsdl`
2. Create test requests for available operations
3. Test with sample data

### REST Services
Use **Postman** or **curl** for REST services:

1. **Import Postman Collection:**
```bash
# Collection available at: ./postman/BookStore-SOA.postman_collection.json
```

2. **Health Checks:**
```bash
curl http://localhost:8081/actuator/health  # Orders
curl http://localhost:8082/actuator/health  # Payments  
curl http://localhost:8083/actuator/health  # Shipping
```

### Integration Testing
```bash
# Run all integration tests
mvn test

# Run specific service tests
cd orders-service && mvn test
cd payment-service && mvn test
cd shipping-service && mvn test
```

---

## 🚀 Deployment

### Local Development
Use Docker Compose for local development:
```bash
docker-compose up -d
```

### Production Deployment

#### Docker Swarm
```bash
docker stack deploy -c docker-compose.prod.yml bookstore
```

#### Kubernetes
```bash
kubectl apply -f k8s/
```

#### AWS/Cloud Deployment
1. Push images to container registry
2. Update environment configurations
3. Deploy using your preferred orchestration platform

### Environment Configuration

Create environment-specific configuration files:
- `.env.development`
- `.env.staging`
- `.env.production`

---

## 🔧 Configuration

### Database Configuration
Each service uses its own database schema:
- `catalog_db` - Catalog service
- `orders_db` - Orders service  
- `payments_db` - Payment service
- `shipping_db` - Shipping service

### Message Queue Configuration
RabbitMQ exchanges and queues:
- `bookstore.orders` - Order events
- `bookstore.payments` - Payment events
- `bookstore.shipping` - Shipping events

### Security Configuration
- JWT secret keys in environment variables
- Database credentials in secure configuration
- API rate limiting enabled

---

## 📊 Monitoring & Observability

### Health Checks
- Service health endpoints: `/actuator/health`
- Database connectivity monitoring
- Message queue connection status

### Logging
- Centralized logging with structured format
- Log levels configurable per service
- Request/response logging for debugging

### Metrics
- Application metrics via Micrometer
- Custom business metrics
- Performance monitoring

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding standards
- Write unit and integration tests
- Update documentation for API changes
- Use conventional commits

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Chamath Lasindu**
- GitHub: [@Chamath-Lasindu](https://github.com/Chamath-Lasindu)


---

## 🙏 Acknowledgments

- GlobalBooks Inc. for the business case study
- Spring Boot team for excellent framework
- Docker community for containerization tools
- Open source contributors

---

## 📞 Support

If you have any questions or need help with setup:
1. Check the [Issues](https://github.com/Chamath-Lasindu/Book_Store-SOA-Microservices/issues) page
2. Create a new issue with detailed description
3. Contact the maintainer

---

<p align="center">
  <b>Happy Coding! 🚀</b>
</p>
