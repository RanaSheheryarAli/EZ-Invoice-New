// controllers/businessController.js
const Business = require('../models/Business');
const mongoose = require("mongoose");

const createBusiness = async (req, res) => {
    try {
        const {
            userId, name, ownername, gstin, address, email,
            contact, country, currency, numberformate, dateformate, signature,categoryIds
        } = req.body;

        if (!userId || !name || !ownername || !address || !email || !contact || !country || !currency || !numberformate || !dateformate || !signature) {
            return res.status(400).json({ error: "All fields are required!" });
        }

        const business = new Business({
            userId, name, ownername, gstin, address, email,
            contact, country, currency, numberformate, dateformate, signature,categoryIds
        });

        await business.save();
        res.status(201).json({ message: "Business added successfully", business });
    } catch (error) {
        res.status(500).json({ error: "Server error", details: error.message });
    }
};
const getBusiness = async (req, res) => {
    try {
        const { userId } = req.params;

        // Validate if userId is provided
        if (!userId) {
            return res.status(400).json({ error: "User ID is required" });
        }

        // Fetch business details for the given user ID
        const business = await Business.findOne({ userId }).populate("categoryIds");

        if (!business) {
            return res.status(404).json({ error: "Business not found for this user" });
        }

        res.status(200).json(business);
    } catch (error) {
        console.error("Error fetching business:", error);
        res.status(500).json({ error: "Internal server error" });
    }
};
const updateBusiness = async (req, res) => {
    try {
        const { userId } = req.params;

        // Validate if userId is provided
        if (!userId) {
            return res.status(400).json({ error: "User ID is required" });
        }

        // Find and update the business using userId
        const updatedBusiness = await Business.findOneAndUpdate(
            { userId: userId },  
            req.body,
            { new: true } 
        );

        if (!updatedBusiness) {
            return res.status(404).json({ error: "Business not found" });
        }

        res.status(200).json({ message: "Business updated successfully", updatedBusiness });
    } catch (error) {
        console.error("Update Business Error:", error);
        res.status(500).json({ error: "Server error" });
    }
};

const deleteBusiness = async (req, res) => {
    try {
        const { userId } = req.params;

        // Validate if userId is provided
        if (!userId) {
            return res.status(400).json({ error: "User ID is required" });
        }

        // Convert userId to ObjectId
        const objectId = new mongoose.Types.ObjectId(userId);

        // Find and delete all businesses associated with the userId
        const result = await Business.deleteMany({ userId: objectId });

        if (result.deletedCount === 0) {
            return res.status(404).json({ error: "No businesses found for this user" });
        }

        res.status(200).json({ message: `${result.deletedCount} businesses deleted successfully` });
    } catch (error) {
        console.error("Delete Business Error:", error);
        res.status(500).json({ error: "Server error" });
    }
};



module.exports = {
    createBusiness,
    getBusiness,
    updateBusiness,
    deleteBusiness
    
  };
