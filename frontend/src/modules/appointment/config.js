export default {
  "title": "Viewing appointments",
  "singular": "appointment",
  "endpoint": "appointments",
  "public": false,
  "fields": [
    {
      "name": "propertyId",
      "label": "Property",
      "type": "property"
    },
    {
      "name": "viewingDate",
      "label": "Viewing date",
      "type": "date"
    },
    {
      "name": "viewingTime",
      "label": "Viewing time (Sri Lanka)",
      "type": "time"
    },
    {
      "name": "notes",
      "label": "Notes",
      "type": "textarea",
      "optional": true
    }
  ],
  "columns": [
    "propertyId",
    "viewingDate",
    "viewingTime",
    "status"
  ],
  "owner": "buyerId",
  "key": "appointment"
};
