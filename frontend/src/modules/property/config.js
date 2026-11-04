export default {
  "title": "Property listings",
  "singular": "property",
  "endpoint": "properties",
  "public": true,
  "fields": [
    {
      "name": "title",
      "label": "Title",
      "type": "text",
      "max": 150
    },
    {
      "name": "description",
      "label": "Description",
      "type": "textarea"
    },
    {
      "name": "city",
      "label": "City",
      "type": "text",
      "max": 100
    },
    {
      "name": "address",
      "label": "Address",
      "type": "text",
      "max": 255
    },
    {
      "name": "price",
      "label": "Price (LKR)",
      "type": "text",
      "money": true,
      "help": "LKR, for example 12500000.00"
    },
    {
      "name": "propertyType",
      "label": "Property type",
      "options": [
        "HOUSE",
        "APARTMENT",
        "LAND"
      ]
    },
    {
      "name": "status",
      "label": "Availability",
      "options": [
        "AVAILABLE",
        "UNAVAILABLE"
      ]
    }
  ],
  "columns": [
    "title",
    "city",
    "price",
    "status"
  ],
  "owner": "ownerId",
  "key": "property"
};
