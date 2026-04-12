const express = require('express');
const mongoose = require('mongoose');
const path = require('path');

const userRoutes = require('./routes/userRoutes');
const businessRoutes = require('./routes/businessRoutes');
const CategoryRoutes = require('./routes/categoryRoutes');
const SubCategoryRoutes = require('./routes/subcategoryRoutes');
const ProductsRoutes = require('./routes/productRoutes');
const invoiceRoutes = require('./routes/invoiceRoutes'); // ✅ new
const customerRoutes = require('./routes/customerRoutes'); // ✅ new
const reportRoutes = require('./routes/reportRoutes');


const app = express();
const port = 5000;

// MongoDB connection URI
const mongoURI = "mongodb://localhost:27017/ezinvoice";

// Middleware
app.use(express.json());

// ✅ Serve static files for logos
app.use('/uploads/logos', express.static(path.join(__dirname, 'uploads/logos')));

// ✅ Register routes
app.use('/api/auth', userRoutes);
app.use('/api/businesses', businessRoutes);
app.use('/api/categories', CategoryRoutes);
app.use('/api/subcategories', SubCategoryRoutes);
app.use('/api/products', ProductsRoutes);
app.use('/api/invoices', invoiceRoutes);      
app.use('/api/customers', customerRoutes);    

app.use('/api/reports', reportRoutes);




// MongoDB connection and server start
async function startServer() {
  try {
    await mongoose.connect(mongoURI);
    console.log("MongoDB connected successfully");

    app.listen(port, () => {
      console.log(`Server running on port ${port}`);
    });
  } catch (err) {
    console.error("Failed to connect to MongoDB:", err);
  }
}

startServer();
