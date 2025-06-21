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


## 🖼️ App Screenshots

### 🔐 Authentication Screens

- **Login**
- ![WhatsApp Image 2025-06-21 at 9 16 36 AM (1)](https://github.com/user-attachments/assets/d0e4901c-f9d1-4430-b4f7-7ccf98cf8013)
- **Signup**
- ![WhatsApp Image 2025-06-21 at 9 16 35 AM (1)](https://github.com/user-attachments/assets/a7f24eca-3318-4c8c-9d54-f21b5b32bda5)
- **Forget Password**
- ![WhatsApp Image 2025-06-21 at 9 16 36 AM (2)](https://github.com/user-attachments/assets/511440a4-2a29-4532-9686-6f68daa067dc)
- **OTP Verification**
- ![image](https://github.com/user-attachments/assets/86a50d5f-97c7-424e-aee7-d32b68d1fbf2)
- ** Reset Password**
- ![WhatsApp Image 2025-06-21 at 9 16 36 AM](https://github.com/user-attachments/assets/eb7bec5b-c616-428b-a894-0fde7b79497a)


---


### 🏠 Main App Flow

- **Home Dashboard**
- ![WhatsApp Image 2025-06-21 at 9 16 38 AM (1)](https://github.com/user-attachments/assets/504aef50-a82d-406f-9a2a-57eb7a9dba86)

---

### 📦 Inventory & Product Management
- **Product List**
- ![WhatsApp Image 2025-06-21 at 9 16 38 AM](https://github.com/user-attachments/assets/3b3d0023-9798-4dfd-ad0a-22086686ec11)
- **Barcode Scanning**
- ![WhatsApp Image 2025-06-18 at 10 09 34 PM](https://github.com/user-attachments/assets/c14280d8-90f9-4ab6-a242-18b81eb00b69)
- **Inventory Overview**
- ![WhatsApp Image 2025-06-21 at 9 16 37 AM](https://github.com/user-attachments/assets/eea455f1-3246-4a19-a919-04b594ac3892)

---

### 🧾 Invoice & Billing'
- **New Invoice Creation**
- ![WhatsApp Image 2025-06-21 at 9 17 18 AM](https://github.com/user-attachments/assets/67fe8015-3b72-4036-b524-9af938797d76)
- **PDF Invoice Preview and sharing to whatapp**
- ![WhatsApp Image 2025-06-18 at 10 09 38 PM](https://github.com/user-attachments/assets/8ddb970d-6345-45a7-8cd9-897efa29c67a)

---

### 📊 Reports & Customers
- **Reports View**
- ![WhatsApp Image 2025-06-21 at 9 13 40 AM](https://github.com/user-attachments/assets/179a9c54-04c3-4287-9c14-6a6f6fa7e651)
- **Customers List**
- ![WhatsApp Image 2025-06-21 at 9 17 17 AM](https://github.com/user-attachments/assets/0bd066d2-6d6e-4e37-b3aa-843edd181fe2)

---

### ⚙️ Settings

- **App Settings**
- ![WhatsApp Image 2025-06-21 at 9 16 38 AM (2)](https://github.com/user-attachments/assets/479121ac-2c8e-4796-a0d5-1c1407b992d6)

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




📱 Mobile App Setup (Optional for Developers)
Open the Android project in Android Studio (located in the root or EZ-Invoice-New folder).

Go to the Retrofit configuration file and set:

BASE_URL = "http://localhost:5000/"



If you're running the app on an Android emulator, localhost will work.

If you're using a physical device, make sure your computer and device are on the same Wi-Fi network.

(⚠️ Required for physical devices only)
Run the following command in the project terminal to allow your physical device to access your local backend server:

adb reverse tcp:5000 tcp:5000

---

