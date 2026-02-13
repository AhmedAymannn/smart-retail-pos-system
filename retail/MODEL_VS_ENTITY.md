# Model vs Entity - النموذج مقابل الكيان
# Why We Need Both? - لماذا نحتاج كليهما؟

## الفرق الأساسي - Basic Difference

### Model (النموذج) - للعرض
```java
// ProductRow.java - للعرض في JavaFX
public class ProductRow {
    private SimpleStringProperty name;
    private SimpleDoubleProperty price;
    private SimpleIntegerProperty stock;
    // لا يوجد ID
    // لا يوجد annotations قاعدة البيانات
}
```

**الاستخدام:**
- ✅ للتعامل مع UI (JavaFX)
- ✅ يحتوي على JavaFX Properties (لربط UI)
- ✅ مبسط ومباشر
- ❌ لا يحتوي على بيانات قاعدة البيانات

### Entity (الكيان) - لقاعدة البيانات
```java
// Product.java - لقاعدة البيانات
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue
    private Long id; // معرف قاعدة البيانات
    
    @Column(nullable = false)
    private String name;
    
    private Double price;
    private Integer stock;
    
    @CreatedDate
    private LocalDateTime createdAt; // بيانات إضافية
}
```

**الاستخدام:**
- ✅ للتعامل مع قاعدة البيانات
- ✅ يحتوي على JPA annotations (@Entity, @Id, @Column)
- ✅ يحتوي على بيانات إضافية (id, timestamps, relations)
- ❌ لا يحتوي على JavaFX Properties

---

## لماذا نحتاج كليهما؟ - Why We Need Both?

### 1. **Separation of Concerns - فصل الاهتمامات**

```
┌─────────────────────────────────────┐
│  UI Layer (JavaFX)                 │
│  يستخدم: Model (ProductRow)        │
│  - بسيط ومباشر                      │
│  - JavaFX Properties                │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Service Layer                       │
│  يحول: Entity ↔ Model               │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Repository Layer                    │
│  يستخدم: Entity (Product)           │
│  - JPA Annotations                   │
│  - Database Relations                │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Database                            │
└─────────────────────────────────────┘
```

**الفائدة:**
- UI لا يعرف تفاصيل قاعدة البيانات
- Database لا يعرف تفاصيل UI
- كل طبقة تعمل بشكل مستقل

---

### 2. **Different Requirements - متطلبات مختلفة**

#### Model (ProductRow) - متطلبات UI:
```java
public class ProductRow {
    // يحتاج JavaFX Properties للربط مع UI
    private SimpleStringProperty name;
    private SimpleDoubleProperty price;
    
    // لا يحتاج ID (UI لا يحتاجه)
    // لا يحتاج timestamps (UI لا يعرضها)
    // بسيط ومباشر
}
```

#### Entity (Product) - متطلبات Database:
```java
@Entity
public class Product {
    @Id
    private Long id; // قاعدة البيانات تحتاجه
    
    private String name;
    private Double price;
    
    @CreatedDate
    private LocalDateTime createdAt; // للتدقيق
    
    @ManyToOne
    private Category category; // علاقات مع جداول أخرى
    
    // معقد أكثر - يحتوي على كل شيء
}
```

**الفائدة:**
- Model مبسط للعرض
- Entity كامل للبيانات

---

### 3. **JavaFX Properties vs JPA Annotations**

#### Model يحتاج JavaFX Properties:
```java
// ProductRow.java
private SimpleStringProperty name;
private SimpleDoubleProperty price;

// للربط مع UI
nameProperty().bindBidirectional(textField.textProperty());
```

#### Entity يحتاج JPA Annotations:
```java
// Product.java
@Entity
@Table(name = "products")
@Id
@Column(nullable = false)
// للتعامل مع قاعدة البيانات
```

**المشكلة:**
- ❌ لا يمكن دمج JavaFX Properties مع JPA Annotations
- ❌ JavaFX Properties معقدة لقاعدة البيانات
- ❌ JPA Annotations غير مناسبة للعرض

**الحل:**
- ✅ Model للعرض (JavaFX Properties)
- ✅ Entity للبيانات (JPA Annotations)

---

### 4. **Additional Data in Entity - بيانات إضافية في Entity**

#### Entity يحتوي على بيانات إضافية:
```java
@Entity
public class Product {
    @Id
    private Long id; // UI لا يحتاجه
    
    private String name;
    private Double price;
    
    // بيانات إضافية للقاعدة البيانات
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Version
    private Long version; // للـ optimistic locking
    
    @ManyToOne
    private Category category; // علاقة مع جدول آخر
    
    @OneToMany
    private List<SaleItem> saleItems; // علاقة مع مبيعات
}
```

#### Model مبسط:
```java
public class ProductRow {
    // فقط ما يحتاجه UI
    private String name;
    private Double price;
    private Integer stock;
    // لا يوجد id, timestamps, relations
}
```

**الفائدة:**
- Model خفيف وسريع
- Entity كامل ومفصل

---

## مثال عملي - Practical Example

### السيناريو: عرض منتج في الجدول

#### بدون فصل (Bad):
```java
// Product.java - يحاول أن يكون كل شيء
@Entity
public class Product {
    @Id
    private Long id;
    
    // JavaFX Properties (للعرض)
    private SimpleStringProperty name; // ❌ مشكلة!
    
    // JPA Annotations (للقاعدة البيانات)
    @Column(nullable = false) // ❌ تعارض!
    
    // لا يعمل بشكل جيد
}
```

#### مع فصل (Good):
```java
// Entity - للقاعدة البيانات
@Entity
public class Product {
    @Id
    private Long id;
    
    @Column(nullable = false)
    private String name; // ✅ بسيط
    
    private Double price;
}

// Model - للعرض
public class ProductRow {
    private SimpleStringProperty name; // ✅ JavaFX Property
    
    private SimpleDoubleProperty price;
    
    // لا يحتاج id أو annotations
}
```

---

## التدفق الكامل - Complete Flow

### مثال: جلب منتج من قاعدة البيانات وعرضه

```
1. User requests products in InventoryController
   │
2. Controller calls: productService.getAllProducts()
   │
3. Service calls: productRepository.findAll()
   │   └─> Returns: List<Product> (Entity)
   │
4. Service converts Entity → Model
   │   Product → ProductRow
   │   └─> Removes: id, timestamps, relations
   │   └─> Adds: JavaFX Properties
   │
5. Service returns: List<ProductRow> (Model)
   │
6. Controller uses ProductRow in JavaFX TableView
   │   └─> Binds with JavaFX Properties
   │
7. UI displays products
```

### مثال: حفظ منتج جديد

```
1. User fills form in UI
   │
2. Controller creates: ProductRow (Model)
   │
3. Controller calls: productService.createProduct(productRow)
   │
4. Service converts Model → Entity
   │   ProductRow → Product
   │   └─> Adds: id (auto-generated), timestamps
   │
5. Service calls: productRepository.save(product)
   │   └─> Saves to database
   │
6. Repository returns: Product (Entity) with ID
   │
7. Service converts Entity → Model
   │   └─> Returns: ProductRow
   │
8. Controller updates UI
```

---

## مقارنة مباشرة - Direct Comparison

| الجانب | Model (ProductRow) | Entity (Product) |
|--------|-------------------|------------------|
| **الاستخدام** | UI (JavaFX) | Database (JPA) |
| **Properties** | JavaFX Properties | Regular fields |
| **Annotations** | لا يوجد | @Entity, @Id, @Column |
| **ID** | ❌ لا يوجد | ✅ موجود |
| **Timestamps** | ❌ لا يوجد | ✅ موجود |
| **Relations** | ❌ لا يوجد | ✅ موجود |
| **الغرض** | العرض | الحفظ |

---

## متى نستخدم كل واحد؟ - When to Use Each?

### استخدم Model (ProductRow) عندما:
- ✅ تعمل مع UI (JavaFX)
- ✅ تحتاج JavaFX Properties
- ✅ تريد بيانات مبسطة
- ✅ لا تحتاج بيانات قاعدة البيانات

### استخدم Entity (Product) عندما:
- ✅ تعمل مع Repository
- ✅ تحفظ في قاعدة البيانات
- ✅ تحتاج علاقات مع جداول أخرى
- ✅ تحتاج بيانات إضافية (id, timestamps)

---

## مثال من كودك الحالي - Example from Your Code

### Model الحالي (ProductRow):
```java
// ProductRow.java - للعرض
public class ProductRow {
    private SimpleStringProperty name; // JavaFX Property
    private SimpleDoubleProperty price;
    private SimpleIntegerProperty stock;
    
    // لا يوجد id
    // لا يوجد annotations
    // بسيط ومباشر
}
```

**يستخدم في:**
- `InventoryController` - لعرض المنتجات في الجدول
- `SalesController` - لعرض المنتجات في المبيعات
- `DashboardController` - لعرض الإحصائيات

### Entity المستقبلي (Product):
```java
// Product.java - لقاعدة البيانات
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue
    private Long id; // معرف قاعدة البيانات
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(unique = true)
    private String barcode;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Integer stock;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

**سيستخدم في:**
- `ProductRepository` - للتعامل مع قاعدة البيانات
- `ProductServiceImpl` - للتحويل من/إلى Model

---

## دوال التحويل - Conversion Methods

### في Service Implementation:

```java
@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Entity → Model
    private ProductRow convertToProductRow(Product product) {
        return new ProductRow(
            product.getName(),
            product.getBarcode(),
            product.getPrice(),
            product.getStock()
        );
        // لا نحتاج: id, createdAt, updatedAt
    }
    
    // Model → Entity
    private Product convertToEntity(ProductRow productRow) {
        Product product = new Product();
        product.setName(productRow.getName());
        product.setBarcode(productRow.getBarcode());
        product.setPrice(productRow.getPrice());
        product.setStock(productRow.getStock());
        // id سيتم توليده تلقائياً
        // timestamps سيتم تعيينها تلقائياً
        return product;
    }
    
    @Override
    public List<ProductRow> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::convertToProductRow) // Entity → Model
            .collect(Collectors.toList());
    }
    
    @Override
    public ProductRow createProduct(ProductRow productRow) {
        Product product = convertToEntity(productRow); // Model → Entity
        Product saved = productRepository.save(product);
        return convertToProductRow(saved); // Entity → Model
    }
}
```

---

## الخلاصة - Summary

### Model (ProductRow):
- 🎯 **الغرض**: للعرض في UI
- 📦 **المحتوى**: بيانات مبسطة + JavaFX Properties
- 🔌 **الاستخدام**: Controllers, JavaFX UI
- ❌ **لا يحتوي**: id, timestamps, relations

### Entity (Product):
- 🎯 **الغرض**: للحفظ في قاعدة البيانات
- 📦 **المحتوى**: بيانات كاملة + JPA Annotations
- 🔌 **الاستخدام**: Repositories, Database
- ✅ **يحتوي**: id, timestamps, relations

### لماذا نحتاج كليهما؟
1. ✅ **Separation of Concerns** - فصل الاهتمامات
2. ✅ **Different Requirements** - متطلبات مختلفة
3. ✅ **JavaFX vs JPA** - لا يمكن دمجهما
4. ✅ **Simplicity** - Model مبسط للعرض
5. ✅ **Completeness** - Entity كامل للبيانات

---

## القاعدة الذهبية - Golden Rule

> **"Model للعرض، Entity للبيانات"**
> 
> **"Model for Display, Entity for Data"**

- 🖥️ **UI Layer** → يستخدم **Model**
- 💾 **Database Layer** → يستخدم **Entity**
- 🔄 **Service Layer** → يحول بينهما

---

## مثال بسيط - Simple Example

**السؤال:** لماذا لا نستخدم Entity مباشرة في UI؟

**الإجابة:**
```java
// ❌ سيء - استخدام Entity مباشرة
@FXML
private TableView<Product> productsTable; // Entity في UI

// المشاكل:
// 1. Entity يحتوي على id, timestamps (لا نحتاجها في UI)
// 2. Entity لا يحتوي على JavaFX Properties (لا يمكن الربط)
// 3. Entity مرتبط بـ JPA (UI لا يجب أن يعرف قاعدة البيانات)
```

```java
// ✅ جيد - استخدام Model في UI
@FXML
private TableView<ProductRow> productsTable; // Model في UI

// المزايا:
// 1. Model مبسط (فقط ما نحتاجه)
// 2. Model يحتوي على JavaFX Properties (يمكن الربط)
// 3. Model منفصل عن قاعدة البيانات
```

---

**هذا هو السبب!** 🎯
