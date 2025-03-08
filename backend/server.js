const express = require('express');
const mongoose = require('mongoose');
const userRoutes = require('./routes/userRoutes');

// Initialize Express app
const app = express();
const port = 5000;

// MongoDB connection URI (replace with your actual MongoDB URI)
const mongoURI = "mongodb://localhost:27017/ezinvoice";

// Middleware to parse JSON data
app.use(express.json());

// Use user routes
app.use('/', userRoutes); // All user routes (signup, signin)

// MongoDB connection and server start
async function startServer() {
  try {
    await mongoose.connect(mongoURI);
    console.log("✅ MongoDB connected successfully");

    app.listen(port, () => {
      console.log(`🚀 Server running on port ${port}`);
    });
  } catch (err) {
    console.error("❌ Failed to connect to MongoDB:", err);
  }
}

startServer();
