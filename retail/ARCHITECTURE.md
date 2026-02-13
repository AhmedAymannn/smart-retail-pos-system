# Architecture & Flow Documentation
# توثيق البنية والتدفق

## Application Architecture - بنية التطبيق

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│                                                  │
├─────────────────────────────────────────────────────────────┤
│  FXML Views (UI)          │  JavaFX Controllers            │
│                           │
│  - main-shell.fxml        │  - MainController              │
│  - sales-view.fxml        │  - SalesController              │
│  - home-view.fxml         │                                │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    SERVICE LAYER                             │
│                                                 │
├─────────────────────────────────────────────────────────────┤
│  Service Interfaces        │  Service Implementations       │
│                            │
│  - ProductService          │  - ProductServiceImpl          │
│  - CartService             │  - CartServiceImpl             │
│  - SalesService            │  - SalesServiceImpl             │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    MODEL LAYER                              │
│                                              │
├─────────────────────────────────────────────────────────────┤
│  Data Models (DTOs)                                         │
│                                            │
│  - ProductRow                                              │
│  - CartItem                                                │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    FRAMEWORK LAYER                          │
│                                                │
├─────────────────────────────────────────────────────────────┤
│  Spring Boot Context       │  JavaFX Application            │
│                             │
│  - Dependency Injection    │  - UI Thread Management       │
│  - Bean Management         │  - Event Handling             │
└─────────────────────────────────────────────────────────────┘
```

---

## Layer Details - 

### 1. PRESENTATION LAYER - 

#### 1.1 FXML Views (UI Components)
**Location:** `src/main/resources/fxml/`

**Files:**
- `main-shell.fxml` - Main application shell with sidebar navigation
- `sales-view.fxml` - Sales/POS interface for cashier
- `home-view.fxml` - Welcome/home screen

**Responsibilities:**
- Define UI layout and structure
- Bind UI components to controller methods
- Define visual styling and layout

#### 1.2 Controllers
**Location:** `src/main/java/com/smartpos/retail/controllers/`

**MainController:**
- Manages main application shell
- Handles navigation between views
- Loads FXML views dynamically
- Injected with: `ApplicationContext` (Spring)

**SalesController:**
- Manages sales/POS interface
- Handles barcode scanning
- Manages shopping cart UI
- Processes checkout
- Injected with: `ProductService`, `CartService`, `SalesService`, `ApplicationContext`

**Responsibilities:**
- Handle user interactions (button clicks, keyboard input)
- Update UI based on business logic results
- Display data from services
- Validate user input
- Show alerts and messages

---

### 2. SERVICE LAYER - طبقة الخدمات

#### 2.1 Service Interfaces
**Location:** `src/main/java/com/smartpos/retail/service/`

**ProductService:**
```java
- getAllProducts() - Get all products
- searchProducts(String) - Search products
- getProductByName(String) - Find product by name
- getProductByBarcode(String) - Find product by barcode
- hasSufficientStock(String, int) - Check stock availability
- getAvailableStock(String) - Get stock quantity
```

**CartService:**
```java
- addToCart(ProductRow, int, List<CartItem>) - Add/update cart item
- removeFromCart(List<CartItem>, String) - Remove item from cart
- clearCart(List<CartItem>) - Clear all items
- calculateSubtotal(List<CartItem>) - Calculate subtotal
- calculateTax(double, double) - Calculate tax
- calculateTotal(List<CartItem>, double) - Calculate total
- canAddToCart(ProductRow, int, List<CartItem>) - Validate cart addition
```

**SalesService:**
```java
- processSale(List<CartItem>, double) - Process checkout
- canProcessCheckout(List<CartItem>) - Validate checkout
```

#### 2.2 Service Implementations
**Location:** `src/main/java/com/smartpos/retail/service/impl/`

**Responsibilities:**
- Implement business logic
- Validate business rules
- Handle calculations
- Prepare data for presentation
- (Future: Interact with database repositories)

**Annotations:**
- `@Service` - Marks class as Spring service bean
- `@Autowired` - Injects dependencies

---

### 3. MODEL LAYER -

**Location:** `src/main/java/com/smartpos/retail/model/`

**ProductRow:**
- Represents a product in the UI
- Properties: name, barcode, price, stock
- Uses JavaFX properties for UI binding

**CartItem:**
- Represents an item in shopping cart
- Properties: productName, quantity, price, total
- Auto-calculates total when quantity changes

**Responsibilities:**
- Hold data structure
- Provide data transfer between layers
- Support UI binding with JavaFX properties

---

### 4. FRAMEWORK LAYER - 

#### 4.1 Spring Boot
**RetailApplication:**
- `@SpringBootApplication` - Main Spring Boot application
- Manages Spring context lifecycle
- Integrates with JavaFX

**Responsibilities:**
- Dependency Injection (DI)
- Bean management
- Service discovery and wiring
- Application context management

#### 4.2 JavaFX
**Application Lifecycle:**
- `init()` - Initialize Spring context
- `start()` - Initialize JavaFX UI
- `stop()` - Cleanup resources

**Responsibilities:**
- UI rendering
- Event handling
- Thread management (JavaFX Application Thread)

---

## Complete Flow Examples -

### Flow 1: Application Startup 

```
1. MainLauncher.main()
   └─> Application.launch(RetailApplication.class)
       │
       ├─> RetailApplication.init()
       │   └─> SpringApplicationBuilder.run()
       │       └─> Spring scans for @Service, @Controller beans
       │           ├─> Creates ProductServiceImpl
       │           ├─> Creates CartServiceImpl
       │           ├─> Creates SalesServiceImpl
       │           ├─> Creates MainController
       │           └─> Creates SalesController
       │
       └─> RetailApplication.start(Stage)
           ├─> Load main-shell.fxml
           ├─> Spring injects MainController
           │   └─> MainController.initialize()
           │       └─> showHomeView()
           │           └─> Load home-view.fxml
           └─> Display window
```

### Flow 2: Navigate to Sales View - 

```
User clicks "مبيعات" button
   │
   ▼
MainController.showSalesView()
   │
   ├─> loadView("/fxml/sales-view.fxml")
   │   │
   │   ├─> FXMLLoader loads FXML
   │   │
   │   └─> Spring injects SalesController
   │       │
   │       └─> SalesController.initialize()
   │           ├─> setupDateLabel()
   │           ├─> initializeCart()
   │           │   └─> Setup cart table columns
   │           │
   │           └─> setupBarcodeScanner()
   │               └─> Add Enter key listener
   │
   └─> Display sales view
       └─> Auto-focus barcode field
```

### Flow 3: Scan Barcode & Add to Cart - 

```
Cashier scans barcode: "1234567890123"
   │
   ▼
Barcode scanner sends: "1234567890123" + Enter key
   │
   ▼
SalesController.handleBarcodeInput(KeyEvent)
   │
   ├─> Check if Enter key pressed
   │
   └─> processBarcode("1234567890123")
       │
       ├─> ProductService.getProductByBarcode("1234567890123")
       │   │
       │   └─> ProductServiceImpl.getProductByBarcode()
       │       └─> Search products list
       │           └─> Return ProductRow or null
       │
       ├─> If not found, try ProductService.getProductByName()
       │
       ├─> If still null:
       │   └─> Show error: "المنتج غير موجود"
       │
       └─> If found:
           │
           ├─> CartService.addToCart(product, 1, cartItems)
           │   │
           │   └─> CartServiceImpl.addToCart()
           │       ├─> Validate product != null
           │       ├─> Validate quantity > 0
           │       ├─> Check if product already in cart
           │       │   ├─> If yes: Update quantity
           │       │   └─> If no: Create new CartItem
           │       ├─> Validate stock availability
           │       └─> Return CartItem
           │
           ├─> Update UI:
           │   ├─> updateTotals()
           │   │   └─> CartService.calculateSubtotal()
           │   │   └─> CartService.calculateTax()
           │   │   └─> CartService.calculateTotal()
           │   │
           │   ├─> updateRecentScans(product)
           │   │   └─> Add to recent scans display
           │   │
           │   └─> Show success: "تم الإضافة: Product 1"
           │
           └─> Clear barcode field & refocus
```

### Flow 4: Checkout Process - 

```
User clicks "الدفع" button
   │
   ▼
SalesController.handleCheckout()
   │
   ├─> SalesService.canProcessCheckout(cartItems)
   │   └─> SalesServiceImpl.canProcessCheckout()
   │       └─> Check if cart is not empty
   │
   ├─> If empty:
   │   └─> Show alert: "السلة فارغة"
   │
   └─> If not empty:
       │
       ├─> CartService.calculateTotal(cartItems, TAX_RATE)
       │   └─> Calculate final total
       │
       ├─> Show confirmation dialog
       │   └─> "إتمام البيع؟ الإجمالي: 50.00 ر.س"
       │
       ├─> If user confirms:
       │   │
       │   └─> SalesService.processSale(cartItems, TAX_RATE)
       │       │
       │       └─> SalesServiceImpl.processSale()
       │           ├─> Validate cart not empty
       │           ├─> Calculate totals
       │           ├─> Generate transaction ID (UUID)
       │           ├─> (Future: Save to database)
       │           ├─> (Future: Update product stock)
       │           └─> Return transaction ID
       │
       ├─> Show success message
       │   └─> "تم إتمام البيع - رقم المعاملة: xxx"
       │
       ├─> CartService.clearCart(cartItems)
       │   └─> Clear all items
       │
       └─> Reset UI & refocus barcode field
```

---

## Dependency Injection Flow - تدفق حقن التبعيات

### Spring Bean Creation Order:

```
1. Spring Boot starts
   │
   ├─> Scans for @Service, @Controller annotations
   │
   ├─> Creates ProductServiceImpl (@Service)
   │   └─> No dependencies
   │
   ├─> Creates CartServiceImpl (@Service)
   │   └─> No dependencies
   │
   ├─> Creates SalesServiceImpl (@Service)
   │   └─> @Autowired CartService
   │       └─> Injects CartServiceImpl
   │
   ├─> Creates MainController (@Controller)
   │   └─> Constructor: ApplicationContext
   │       └─> Spring injects ApplicationContext
   │
   └─> Creates SalesController (@Controller)
       └─> Constructor: ApplicationContext, ProductService, CartService, SalesService
           ├─> Spring injects ApplicationContext
           ├─> Spring injects ProductServiceImpl
           ├─> Spring injects CartServiceImpl
           └─> Spring injects SalesServiceImpl
```

### Controller Factory Pattern:

```java
// In RetailApplication.start()
loader.setControllerFactory(springContext::getBean);

// When FXML loads:
// 1. FXML finds fx:controller="com.smartpos.retail.controllers.SalesController"
// 2. Calls springContext.getBean(SalesController.class)
// 3. Spring returns the already-created SalesController instance
// 4. Spring has already injected all dependencies
```

---

## Data Flow - تدفق البيانات

### Request Flow (Top to Bottom):
```
User Action
    │
    ▼
Controller (Handles UI event)
    │
    ▼
Service Interface (Defines contract)
    │
    ▼
Service Implementation (Business logic)
    │
    ▼
Model (Data structure)
    │
    ▼
Response Flow (Bottom to Top)
```

### Example: Adding Product to Cart

```
1. UI Event: Barcode scanned
   │
2. SalesController.processBarcode()
   │   └─> Receives barcode string
   │
3. ProductService.getProductByBarcode()
   │   └─> Returns ProductRow model
   │
4. CartService.addToCart()
   │   └─> Receives ProductRow
   │   └─> Returns CartItem model
   │
5. SalesController updates UI
   │   └─> Uses CartItem to update table
   │   └─> Uses CartItem to calculate totals
   │
6. UI displays updated cart
```

---

## Key Design Patterns - أنماط التصميم الرئيسية

### 1. **Dependency Injection (DI)**
- Spring manages object creation and dependencies
- Controllers receive services via constructor injection
- Services receive other services via @Autowired

### 2. **Service Layer Pattern**
- Separates business logic from presentation
- Controllers delegate to services
- Services are testable independently

### 3. **Model-View-Controller (MVC)**
- **Model**: ProductRow, CartItem
- **View**: FXML files
- **Controller**: MainController, SalesController

### 4. **Factory Pattern**
- Spring's controller factory creates controllers
- FXML loader uses factory to get Spring-managed beans

### 5. **Observer Pattern**
- JavaFX properties notify UI of changes
- Event handlers respond to user actions

---

## Thread Management - إدارة الخيوط

### JavaFX Application Thread:
- All UI updates must be on JavaFX thread
- `Platform.runLater()` ensures thread safety
- Used for: focusing fields, updating UI after async operations

### Spring Thread Safety:
- Services are stateless (no shared mutable state)
- Each request gets its own service instance (singleton scope)
- Controllers are created per view load

---

## Future Enhancements - التحسينات المستقبلية

### Missing Layers (To be added):

1. **Repository Layer** (Data Access)
   ```
   - ProductRepository extends JpaRepository
   - SaleRepository extends JpaRepository
   - CustomerRepository extends JpaRepository
   ```

2. **Entity Layer** (Database Models)
   ```
   - Product (JPA Entity)
   - Sale (JPA Entity)
   - SaleItem (JPA Entity)
   - Customer (JPA Entity)
   ```

3. **DTO Layer** (Data Transfer Objects)
   ```
   - ProductDTO
   - SaleDTO
   - For API communication
   ```

### Current vs Future Flow:

**Current:**
```
Controller → Service → In-Memory Data
```

**Future:**
```
Controller → Service → Repository → Database
```

---

## Summary - الملخص

### Layer Responsibilities:

| Layer | Responsibility | Example |
|-------|--------------|---------|
| **Presentation** | UI, User interaction | SalesController, FXML |
| **Service** | Business logic | CartService, ProductService |
| **Model** | Data structure | ProductRow, CartItem |
| **Framework** | DI, Lifecycle | Spring, JavaFX |

### Communication Flow:

```
User → Controller → Service → Model → Service → Controller → User
```

### Key Principles:

1. **Separation of Concerns**: Each layer has specific responsibility
2. **Dependency Inversion**: Controllers depend on service interfaces
3. **Single Responsibility**: Each class has one job
4. **Testability**: Services can be tested without UI
