// controllers/customerController.js
const Customer = require('../models/Customer');

const findOrCreateCustomer = async (businessId, name, phone) => {
    let customer = await Customer.findOne({ businessId, name, phone });
    if (!customer) {
        customer = new Customer({ businessId, name, phone });
        await customer.save();
    }
    return customer;
};

const getTopCustomers = async (req, res) => {
    try {
      const { businessId } = req.params;
  
      const customers = await Customer.find({ businessId }).populate('invoices');
  
      const topClients = customers.map(customer => {
        const invoiceCount = customer.invoices.length;
        const totalSpent = customer.invoices.reduce((sum, inv) => sum + inv.totalAmount, 0);
  
        return {
          name: customer.name,
          invoiceCount,
          totalSpent
        };
      });
  
      // You can sort by highest spender:
      topClients.sort((a, b) => b.totalSpent - a.totalSpent);
  
      res.status(200).json(topClients);
    } catch (err) {
      res.status(500).json({ error: 'Failed to get top clients' });
    }
  };


const findOrCreateCustomerAPI = async (req, res) => {
    try {
        const { businessId, name, phone } = req.body;
        if (!businessId || !name || !phone) {
            return res.status(400).json({ error: "Missing required fields" });
        }
        const customer = await findOrCreateCustomer(businessId, name, phone);
        res.status(200).json(customer);
    } catch (err) {
        res.status(500).json({ error: "Internal Server Error" });
    }
};

// ✅ NEW: Get all customers by businessId
const getCustomersByBusiness = async (req, res) => {
  try {
      const { businessId } = req.params;

      const customers = await Customer.find({ businessId });

      res.status(200).json(customers);
  } catch (err) {
      res.status(500).json({ error: "Failed to fetch customers" });
  }
};
module.exports = { findOrCreateCustomer, getTopCustomers,findOrCreateCustomerAPI ,getCustomersByBusiness};
