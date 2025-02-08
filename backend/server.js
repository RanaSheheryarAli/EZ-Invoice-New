const express = require('express');
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const User = require('./models/User');

// Initialize Express app
const app = express();
const port = 5000;

// MongoDB connection URI (replace with your actual MongoDB URI)
const mongoURI = "mongodb://localhost:27017/ezinvoice"; // Example URI

// Middleware to parse JSON data
app.use(express.json());

// MongoDB connection and server start
async function startServer() {
  try {
    // Connecting to MongoDB using mongoose
    await mongoose.connect(mongoURI);
    console.log("MongoDB connected successfully");

    // Start the Express server after successful MongoDB connection
    app.listen(port, () => {
      console.log(`Server running on port ${port}`);
    });
  } catch (err) {
    console.error("Failed to connect to MongoDB:", err);
  }
}



app.post('/signup', async (req, res) => {
    console.log("Received Request Body:", req.body); // Debugging line
  
    const { username, email, password } = req.body;
  
    if (!username || !email || !password) {
      return res.status(400).json({ message: 'All fields are required from backend node' });
    }
  
    try {
      const existingUser = await User.findOne({ email });
      if (existingUser) {
        return res.status(400).json({ message: 'Email already registered' });
      }
  
      const hashedPassword = await bcrypt.hash(password, 10);
      const newUser = new User({ username, email, password: hashedPassword });
  
      await newUser.save();
      res.status(201).json({ message: 'User created successfully' });
    } catch (err) {
      console.error(err);
      res.status(500).json({ message: 'Error creating user' });
    }
  });
  

// Call the function to connect to MongoDB and start the server
startServer();
