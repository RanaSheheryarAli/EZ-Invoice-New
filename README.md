# EZinvoice - Mobile Billing & Inventory System for Small Businesses

EZinvoice is a mobile application designed to streamline trade invoice billing and product inventory for small business owners. Built using **Kotlin** for the frontend and **Node.js (Express.js)** with **MongoDB** for the backend, this app supports both **online and offline** operations using Room Database (under development).

---

## 🌟 Features Overview

### ✅ Currently Implemented (Server-Side)
- **Product Management**
  - Add new products with name, barcode, price, quantity, etc.
  - Search/filter products by name or barcode.
  - Update and delete product details.
- **Inventory Management**
  - Assign products to categories and subcategories.
  - Add category & subcategory with relational linking.
  - Generate invoices with selected products.
- **Barcode Integration**
  - Scan barcodes to fetch product details automatically.
- **API Structure**
  - All features are connected to a secure and scalable REST API built using Express.js.
  - Data is stored in **MongoDB**, organized by `businessId`.

---

## 🕓 In Progress (Client-Side - Room Database)

The app is being enhanced to support full **offline-first capability** using **Room Database**:

### Planned Room DB Features:
- Store categories, subcategories, and products locally first (with temp IDs).
- Offline product creation and editing.
- Sync all unsynced data to the server manually (via a Sync button).
- Automatically maintain relationships between Category → Subcategory → Product after syncing.

⛔ **Note**: Room database integration is currently **not implemented yet**. Development is ongoing.

---

## 🔧 Tech Stack

### 📱 Frontend
- Language: **Kotlin**
- Architecture: **MVVM**
- Local DB: **Room (planned)**
- Libraries: Retrofit, LiveData, Coroutines, Barcode Scanner

### 🌐 Backend
- **Node.js** with **Express.js**
- **MongoDB** for NoSQL data storage
- RESTful API design
- JSON-based request/response structure
- Hosted locally or on cloud (depending on deployment)

---

## 📦 Folder Structure (Backend)

EZ-Invoice/
├── README.md                    # Project documentation
└── backend/                     # Main backend folder
    ├── server.js                # Entry point of the Node.js backend server
    ├── package.json             # Project metadata and dependencies
    ├── package-lock.json        # Lock file for exact dependency versions

    ├── controllers/             # Business logic for each module
    │   ├── businessController.js
    │   ├── categoryController.js
    │   ├── customerController.js
    │   ├── invoiceController.js
    │   ├── productController.js
    │   ├── reportsController.js
    │   ├── subcategoryController.js
    │   └── userController.js

    ├── routes/                  # API route definitions
    │   ├── businessRoutes.js
    │   ├── categoryRoutes.js
    │   ├── customerRoutes.js
    │   ├── invoiceRoutes.js
    │   ├── productRoutes.js
    │   ├── reportsRoutes.js
    │   ├── subcategoryRoutes.js
    │   └── userRoutes.js

    ├── models/                  # Mongoose schemas for MongoDB collections
    │   ├── Business.js
    │   ├── Category.js
    │   ├── Customer.js
    │   ├── Invoice.js
    │   ├── Product.js
    │   ├── Reports.js
    │   ├── Subcategory.js
    │   └── User.js

    ├── middleware/              # Middleware (e.g.,logo upload, error handling)
    │   ├── auth.js
    │   └── errorHandler.js

    ├── upload/                  # File‑upload directory
    │   └── logo/                # Stores uploaded business logos
    │       └── (uploaded files…)

    └── config/                  # Configuration files
        ├── db.js                # MongoDB connection
        └── config.js            # Environment / app‑level settings

---

## 🚀 Getting Started

Follow these steps to set up and run the EZ-Invoice project locally.

### 🧰 Prerequisites

- [Node.js](https://nodejs.org/) (v14 or higher)
- [MongoDB](https://www.mongodb.com/try/download/community) (running locally or use MongoDB Atlas)
- Android Studio (for mobile app)
- Git (for cloning the repository)

---

### 💻 Backend Setup

1. **Clone the Repository**

```bash
git clone https://github.com/RanaSheheryarAli/EZ-Invoice-New.git
cd EZ-Invoice-New/backend
npm install
node server.js

---

### 📱 Mobile App Setup (Optional for Developers)

1. Open the Android project in **Android Studio** (located in the root or `EZ-Invoice-New` folder).
2. Go to the Retrofit configuration file and set:
   ```kotlin
   BASE_URL = "http://localhost:5000/"
If you're running the app on an Android emulator, localhost will work.

If you're using a physical device, make sure your computer and device are on the same Wi-Fi network.

(⚠️ Required for physical devices only) Run this command inproject folder to allow the device to access your local backend server:

adb reverse tcp:5000 tcp:5000

