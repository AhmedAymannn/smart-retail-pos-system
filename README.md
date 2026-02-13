# Smart Retail POS System

A modern Point of Sale (POS) system built with Spring Boot and JavaFX for retail businesses. This application provides a comprehensive solution for managing sales, inventory, and customer transactions with an intuitive Arabic/English bilingual interface.

## 🏪 Features

- **Sales Management**: Complete POS functionality with barcode scanning
- **Inventory Control**: Product management and stock tracking
- **User Authentication**: Secure login system with role-based access
- **Dashboard Analytics**: Real-time sales and business insights
- **Bilingual Interface**: Arabic/English support
- **Receipt Generation**: Automatic receipt creation for transactions
- **Data Persistence**: H2 database with web console access

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2.2
- **Frontend**: JavaFX 21
- **Database**: H2 (In-memory with file persistence)
- **Build Tool**: Maven
- **Java Version**: 21
- **Additional**: Lombok for boilerplate reduction

## 📁 Project Structure

```
retail/
├── src/main/java/com/smartpos/retail/
│   ├── MainLauncher.java              # Application entry point
│   ├── RetailApplication.java         # Spring Boot + JavaFX integration
│   ├── controllers/                   # UI Controllers
│   │   ├── MainController.java        # Main application shell
│   │   ├── SalesController.java       # POS and sales management
│   │   ├── LoginController.java       # User authentication
│   │   ├── DashboardController.java   # Analytics dashboard
│   │   └── InventoryController.java   # Inventory management
│   ├── service/                       # Service interfaces
│   │   ├── ProductService.java
│   │   ├── CartService.java
│   │   ├── SalesService.java
│   │   └── UserService.java
│   ├── service/impl/                  # Service implementations
│   │   ├── ProductServiceImpl.java
│   │   ├── CartServiceImpl.java
│   │   ├── SalesServiceImpl.java
│   │   └── UserServiceImpl.java
│   └── model/                         # Data models
│       ├── ProductRow.java            # Product representation
│       ├── CartItem.java              # Shopping cart item
│       ├── Receipt.java               # Transaction receipt
│       └── User.java                  # User entity
├── src/main/resources/
│   ├── fxml/                          # JavaFX UI layouts
│   │   ├── main-shell.fxml            # Main application window
│   │   ├── sales-view.fxml            # POS interface
│   │   ├── login-view.fxml            # Login screen
│   │   ├── dashboard-view.fxml        # Analytics dashboard
│   │   ├── inventory-view.fxml        # Inventory management
│   │   └── home-view.fxml             # Welcome screen
│   ├── css/                           # Stylesheets
│   ├── images/                        # UI assets
│   └── application.properties         # Spring configuration
├── data/                              # H2 database files
└── pom.xml                            # Maven configuration
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd retail
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   # Using Maven
   mvn spring-boot:run
   
   # Or using the executable JAR
   java -jar target/retail-0.0.1-SNAPSHOT.jar
   ```

4. **Access the H2 Database Console** (Optional)
   - Open your browser and navigate to: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:file:./data/pos_db`
   - Username: `sa`
   - Password: (leave blank)

## 📖 Usage

### Login
- Launch the application to see the login screen
- Enter valid credentials to access the system

### Sales Process
1. Navigate to the Sales screen from the main menu
2. Scan product barcodes or search manually
3. Review items in the shopping cart
4. Process payment and generate receipt
5. System automatically updates inventory

### Inventory Management
- Add new products with barcode, name, price, and stock
- Update existing product information
- Monitor stock levels and set alerts
- Generate inventory reports

### Dashboard Analytics
- View real-time sales data
- Monitor daily/weekly/monthly performance
- Track top-selling products
- Analyze customer trends

## 🏗️ Architecture

The application follows a layered architecture pattern:

### Presentation Layer
- **FXML Views**: UI layouts and structure
- **Controllers**: Handle user interactions and UI logic

### Service Layer
- **Business Logic**: Core application functionality
- **Data Validation**: Input verification and business rules
- **Calculations**: Pricing, taxes, and totals

### Model Layer
- **Data Models**: POJOs for data representation
- **JavaFX Properties**: UI binding and reactive updates

### Framework Layer
- **Spring Boot**: Dependency injection and application context
- **JavaFX**: UI framework and event handling

## 🔧 Configuration

### Database Configuration
The application uses H2 database with file persistence. Database files are stored in the `data/` directory.

### Application Properties
Key configurations in `src/main/resources/application.properties`:
- H2 console enabled for development
- Web access allowed for database management
- Application name and settings

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

## 📝 Development Notes

### Key Design Patterns
- **Dependency Injection**: Spring manages object creation
- **MVC Pattern**: Separation of concerns in UI layer
- **Service Layer**: Business logic abstraction
- **Factory Pattern**: Spring controller factory for FXML

### Thread Management
- All UI updates occur on JavaFX Application Thread
- Services are stateless and thread-safe
- `Platform.runLater()` ensures UI thread safety

### Future Enhancements
- Repository layer for database operations
- REST API for web/mobile integration
- Advanced reporting and analytics
- Multi-store support
- Cloud synchronization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Review the architecture documentation
- Check the H2 console for database insights

## 🌟 Acknowledgments

- Spring Boot team for the excellent framework
- JavaFX community for UI guidance
- H2 database for lightweight persistence solution
