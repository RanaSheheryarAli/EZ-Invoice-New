const bcrypt = require('bcryptjs');
const User = require('../models/User');
const jwt = require('jsonwebtoken');

// Signup Controller
const signup = async (req, res) => {
  const { username, email, password,businessID } = req.body;

  if (!username || !email || !password ) {
    return res.status(400).json({ message: 'All fields are required' });
  }

  try {
    const existingUser = await User.findOne({ email });

    if (existingUser) {
      return res.status(400).json({ message: 'Email already registered' });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const newUser = new User({ username, email, password: hashedPassword,businessID:businessID });
    await newUser.save();

    res.status(201).send({ message: 'User created successfully' ,newUser});
  } catch (error) {
    res.status(500).send({ message: 'Error creating user', error });
  }
};

const signin = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ message: 'Email and password are required' });
  }

  try {
    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }

    // ✅ Generate JWT Token
    const token = jwt.sign({ id: user._id, email: user.email }, 'your_secret_key', {
      expiresIn: '7d',
    });

    // ✅ Send Safe User Data (Exclude Password)
    res.status(200).json({success: 'Signin successful',
      user: {
        id: user._id,
        sussess:true,
        username: user.username,
        email: user.email,
        businessID:user.businessID
      },
      token,
    });
  } catch (error) {
    res.status(500).json({ message: 'Error signing in', error });
  }
};


module.exports = {
  signup,
  signin
};
