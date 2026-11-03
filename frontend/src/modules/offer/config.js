export default {
  "title": "Offers",
  "singular": "offer",
  "endpoint": "offers",
  "public": false,
  "fields": [
    {
      "name": "propertyId",
      "label": "Property",
      "type": "property"
    },
    {
      "name": "amount",
      "label": "Amount (LKR)",
      "type": "text",
      "money": true,
      "help": "LKR, for example 12500000.00"
    },
    {
      "name": "message",
      "label": "Message",
      "type": "textarea",
      "optional": true
    }
  ],
  "columns": [
    "propertyId",
    "amount",
    "status"
  ],
  "owner": "buyerId",
  "key": "offer"
};
