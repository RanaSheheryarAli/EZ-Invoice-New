const mongoose = require('mongoose');

// Define the user schema
const userSchema = new mongoose.Schema({
  username: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  businessID:{type:String,require:true}
});

// Create the User model based on the schema
const User = mongoose.model('User', userSchema);

module.exports = User;
