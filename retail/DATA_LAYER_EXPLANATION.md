# Data Layer & Repositories - طبقة البيانات والمستودعات
# Current Status & Future Implementation

## الوضع الحالي - Current Status

### ❌ **لا توجد طبقة بيانات حالياً - No Data Layer Currently**

**البنية الحالية:**
```
Controller → Service → In-Memory List (قائمة في الذاكرة)
```

**الكود الحالي:**
```java
// ProductServiceImpl.java
@Service
public class ProductServiceImpl implements ProductService {
    private final List<ProductRow> products; // ❌ في الذاكرة فقط
    
    public ProductServiceImpl() {
        this.products = new ArrayList<>(); // ❌ بيانات مؤقتة
        products.add(new ProductRow("Product 1", ...));
        // ...
    }
}
```

**المشاكل:**
- ❌ البيانات تُفقد عند إغلاق التطبيق
- ❌ لا يوجد حفظ دائم (Persistence)
- ❌ لا يوجد قاعدة بيانات
- ❌ لا يوجد Repositories

---

## أين يجب أن تكون طبقة البيانات؟ - Where Should Data Layer Be?

### البنية المطلوبة - Required Architecture:

```
┌─────────────────────────────────────────┐
│  CONTROLLER LAYER                      │
│  طبقة التحكم                            │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  SERVICE LAYER                          │
│  طبقة الخدمات                            │
│  - ProductService                       │
│  - CartService                          │
│  - SalesService                         │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  REPOSITORY LAYER (Data Access)         │
│  طبقة المستودعات (الوصول للبيانات)      │
│  - ProductRepository                    │
│  - SaleRepository                       │
│  - CustomerRepository                   │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  ENTITY LAYER (Database Models)         │
│  طبقة الكيانات (نماذج قاعدة البيانات)    │
│  - Product (JPA Entity)                 │
│  - Sale (JPA Entity)                    │
│  - SaleItem (JPA Entity)                 │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│  DATABASE                                │
│  قاعدة البيانات                          │
│  - H2 (حالياً)                          │
│  - MySQL/PostgreSQL (مستقبلاً)          │
└─────────────────────────────────────────┘
```

---

## ما هي الـ Repository؟ - What is Repository?

### تعريف - Definition:

**Repository Pattern:**
- طبقة وسيطة بين Service و Database
- تتعامل مع عمليات قاعدة البيانات (CRUD)
- تخفي تفاصيل الوصول للبيانات

### مثال - Example:

```java
// Repository Interface
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Spring Data JPA يوفر تلقائياً:
    // - save()
    // - findAll()
    // - findById()
    // - delete()
    // - count()
    
    // يمكن إضافة دوال مخصصة:
    Product findByBarcode(String barcode);
    List<Product> findByNameContaining(String name);
    List<Product> findByStockLessThan(int threshold);
}
```

**Spring Data JPA يقوم بـ:**
- ✅ إنشاء التنفيذ تلقائياً
- ✅ لا تحتاج كتابة SQL
- ✅ دوال جاهزة للـ CRUD

---

## البنية الحالية vs المستقبلية - Current vs Future

### الحالي (Current):

```
ProductServiceImpl
    │
    ├─> private List<ProductRow> products = new ArrayList<>();
    │   └─> في الذاكرة فقط
    │
    └─> getAllProducts() {
            return new ArrayList<>(products); // من القائمة
        }
```

**المشاكل:**
- ❌ البيانات تُفقد عند إعادة التشغيل
- ❌ لا يوجد حفظ دائم
- ❌ لا يمكن مشاركة البيانات بين المستخدمين

### المستقبلي (Future):

```
ProductServiceImpl
    │
    ├─> @Autowired ProductRepository productRepository;
    │
    └─> getAllProducts() {
            return productRepository.findAll(); // من قاعدة البيانات
        }
```

**المزايا:**
- ✅ البيانات محفوظة دائمياً
- ✅ يمكن مشاركة البيانات
- ✅ استعلامات قوية (Queries)
- ✅ معاملات (Transactions)

---

## كيف تضيف طبقة البيانات؟ - How to Add Data Layer?

### الخطوة 1: إنشاء Entities (نماذج قاعدة البيانات)

**الموقع:** `src/main/java/com/smartpos/retail/entity/`

```java
// Product.java
package com.smartpos.retail.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(unique = true)
    private String barcode;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Integer stock;
}
```

### الخطوة 2: إنشاء Repositories

**الموقع:** `src/main/java/com/smartpos/retail/repository/`

```java
// ProductRepository.java
package com.smartpos.retail.repository;

import com.smartpos.retail.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Spring Data JPA يوفر تلقائياً:
    // - save(Product)
    // - findAll()
    // - findById(Long)
    // - delete(Product)
    // - count()
    
    // دوال مخصصة (Spring ينفذها تلقائياً):
    Optional<Product> findByBarcode(String barcode);
    Optional<Product> findByName(String name);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByStockLessThan(int threshold);
}
```

### الخطوة 3: تحديث Service Implementation

```java
// ProductServiceImpl.java
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public List<ProductRow> getAllProducts() {
        // تحويل من Entity إلى Model
        return productRepository.findAll().stream()
            .map(this::convertToProductRow)
            .collect(Collectors.toList());
    }
    
    @Override
    public ProductRow getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
            .map(this::convertToProductRow)
            .orElse(null);
    }
    
    @Override
    public ProductRow createProduct(ProductRow productRow) {
        Product product = convertToEntity(productRow);
        Product saved = productRepository.save(product);
        return convertToProductRow(saved);
    }
    
    // دوال التحويل بين Entity و Model
    private ProductRow convertToProductRow(Product product) {
        return new ProductRow(
            product.getName(),
            product.getBarcode(),
            product.getPrice(),
            product.getStock()
        );
    }
    
    private Product convertToEntity(ProductRow productRow) {
        Product product = new Product();
        product.setName(productRow.getName());
        product.setBarcode(productRow.getBarcode());
        product.setPrice(productRow.getPrice());
        product.setStock(productRow.getStock());
        return product;
    }
}
```

### الخطوة 4: تحديث application.properties

```properties
# Database Configuration
spring.datasource.url=jdbc:h2:file:./data/retaildb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# H2 Console (للمطورين)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

---

## الفرق بين Entity و Model - Entity vs Model

### Entity (للقاعدة البيانات):
```java
@Entity
public class Product {
    @Id
    private Long id; // معرف قاعدة البيانات
    private String name;
    // ...
}
```

**الاستخدام:**
- ✅ للتعامل مع قاعدة البيانات
- ✅ يحتوي على @Entity, @Id, @Column
- ✅ يستخدم في Repository

### Model/DTO (للعرض):
```java
public class ProductRow {
    private String name; // لا يوجد id
    // ...
}
```

**الاستخدام:**
- ✅ للتعامل مع UI (JavaFX)
- ✅ لا يحتوي على annotations قاعدة البيانات
- ✅ يستخدم في Controllers

**لماذا الفصل؟**
- ✅ فصل الاهتمامات (Separation of Concerns)
- ✅ Entity قد يحتوي على بيانات إضافية (id, timestamps)
- ✅ Model مبسط للعرض

---

## البنية الكاملة بعد الإضافة - Complete Structure After Adding

```
src/main/java/com/smartpos/retail/
│
├── controllers/          (طبقة التحكم)
│   ├── MainController
│   ├── SalesController
│   └── InventoryController
│
├── service/              (طبقة الخدمات)
│   ├── ProductService (Interface)
│   └── impl/
│       └── ProductServiceImpl
│
├── repository/           (طبقة المستودعات) ⭐ جديد
│   ├── ProductRepository
│   ├── SaleRepository
│   └── CustomerRepository
│
├── entity/               (طبقة الكيانات) ⭐ جديد
│   ├── Product
│   ├── Sale
│   └── SaleItem
│
└── model/                (نماذج العرض)
    ├── ProductRow
    └── CartItem
```

---

## التدفق الكامل - Complete Flow

### مثال: إضافة منتج جديد

```
1. User clicks "إضافة منتج" in InventoryController
   │
2. InventoryController.showProductDialog()
   │
3. User fills form and clicks "حفظ"
   │
4. InventoryController calls:
   productService.createProduct(productRow)
   │
5. ProductServiceImpl.createProduct()
   │   ├─> Convert ProductRow → Product (Entity)
   │   └─> productRepository.save(product)
   │       │
   │       └─> Spring Data JPA saves to database
   │           └─> INSERT INTO products ...
   │
6. Database saves the product
   │
7. Repository returns saved Product (with ID)
   │
8. Service converts Product → ProductRow
   │
9. Controller updates UI table
```

---

## ما الذي موجود في pom.xml لكن غير مستخدم؟

```xml
<!-- موجود لكن غير مستخدم حالياً -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
```

**هذه المكتبات:**
- ✅ موجودة في المشروع
- ❌ غير مستخدمة حالياً
- ⏳ جاهزة للاستخدام عند إضافة Repositories

---

## الخلاصة - Summary

### الوضع الحالي:
- ❌ **لا توجد طبقة بيانات**
- ❌ **لا توجد Repositories**
- ❌ **البيانات في الذاكرة فقط**
- ✅ **البنية جاهزة للإضافة**

### ما يجب إضافته:
1. ✅ **Entity Classes** - نماذج قاعدة البيانات
2. ✅ **Repository Interfaces** - مستودعات البيانات
3. ✅ **Database Configuration** - إعدادات قاعدة البيانات
4. ✅ **Update Services** - تحديث الخدمات لاستخدام Repositories

### الفوائد بعد الإضافة:
- ✅ حفظ دائم للبيانات
- ✅ استعلامات قوية
- ✅ معاملات آمنة
- ✅ قابلية التوسع

---

## الخطوات التالية - Next Steps

1. إنشاء Entity classes
2. إنشاء Repository interfaces
3. تحديث application.properties
4. تحديث Service implementations
5. إضافة دوال التحويل (Entity ↔ Model)

**هل تريد أن أضيف طبقة البيانات الآن؟** 🚀
